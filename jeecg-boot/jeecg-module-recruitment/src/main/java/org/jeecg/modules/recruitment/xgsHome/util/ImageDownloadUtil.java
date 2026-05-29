package org.jeecg.modules.recruitment.xgsHome.util;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.filter.SsrfFileTypeFilter;
import org.jeecg.common.util.filter.SsrfUrlGuard;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

/**
 * 轮播图保存到 {@code jeecg.path.upload} 下，相对路径固定为 {@code upload/carousel/文件名}，
 * 与 {@link org.jeecg.modules.recruitment.xgsHome.controller.XgsHomeController#getCarouselImage} 解析规则一致。
 * 旧版曾写入 JVM 当前工作目录，导致与接口读路径不一致、生产 404。
 */
@Slf4j
@Component
public class ImageDownloadUtil {

    /** 相对 jeecg.path.upload 的子目录，与 {@link #resolveSafeCarouselImageFile(String)} 一致 */
    private static final String CAROUSEL_REL = "upload/carousel";

    @Value("${jeecg.path.upload}")
    private String jeecgUploadPath;

    /**
     * 下载图片并保存到服务器本地
     *
     * @param imageUrl 图片URL地址
     * @return 相对 jeecg.path.upload 的路径，正斜杠（如 upload/carousel/uuid.png）
     */
    public String downloadAndSaveImage(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            log.error("图片URL为空");
            return null;
        }

        HttpURLConnection connection = null;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            SsrfUrlGuard.validateHttpUrl(imageUrl);

            Path carouselDir = carouselDir();
            Files.createDirectories(carouselDir);

            String fileExtension = getFileExtension(imageUrl);
            String fileName = UUID.randomUUID().toString() + fileExtension;
            Path localPath = carouselDir.resolve(fileName);
            File localFile = localPath.toFile();

            URL url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
                outputStream = new FileOutputStream(localFile);

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                String rel = CAROUSEL_REL + "/" + fileName;
                log.info("图片下载成功: {} -> {}", imageUrl, localPath.toAbsolutePath());
                return rel;
            } else {
                log.error("图片下载失败，HTTP响应码: {}, URL: {}", responseCode, imageUrl);
                return null;
            }

        } catch (JeecgBootException e) {
            log.warn("图片 URL 被拒绝: {} — {}", imageUrl, e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("下载图片时发生异常: {}", imageUrl, e);
            return null;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException e) {
                log.error("关闭流时发生异常", e);
            }
        }
    }

    private static String getFileExtension(String url) {
        if (url == null || url.isEmpty()) {
            return ".jpg";
        }

        String urlWithoutParams = url.split("\\?")[0];

        int lastDotIndex = urlWithoutParams.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < urlWithoutParams.length() - 1) {
            String extension = urlWithoutParams.substring(lastDotIndex);
            if (extension.matches("\\.(jpg|jpeg|png|gif|bmp|webp)")) {
                return extension;
            }
        }

        return ".jpg";
    }

    /**
     * 保存Base64编码的图片到本地
     *
     * @param base64Data Base64编码的图片数据（可以包含data:image/png;base64,前缀）
     * @return 相对 jeecg.path.upload 的路径，正斜杠
     */
    public String saveBase64Image(String base64Data) {
        if (base64Data == null || base64Data.trim().isEmpty()) {
            log.error("Base64图片数据为空");
            return null;
        }

        FileOutputStream outputStream = null;

        try {
            Path carouselDir = carouselDir();
            Files.createDirectories(carouselDir);

            String base64String = base64Data;
            String fileExtension = ".jpg";

            if (base64Data.contains(",")) {
                String[] parts = base64Data.split(",");
                if (parts.length == 2) {
                    String mimeType = parts[0];
                    if (mimeType.contains("image/png")) {
                        fileExtension = ".png";
                    } else if (mimeType.contains("image/jpeg") || mimeType.contains("image/jpg")) {
                        fileExtension = ".jpg";
                    } else if (mimeType.contains("image/gif")) {
                        fileExtension = ".gif";
                    } else if (mimeType.contains("image/webp")) {
                        fileExtension = ".webp";
                    }
                    base64String = parts[1];
                }
            }

            String fileName = UUID.randomUUID().toString() + fileExtension;
            Path localPath = carouselDir.resolve(fileName);
            File localFile = localPath.toFile();

            byte[] imageBytes = java.util.Base64.getDecoder().decode(base64String);
            outputStream = new FileOutputStream(localFile);
            outputStream.write(imageBytes);

            String rel = CAROUSEL_REL + "/" + fileName;
            log.info("Base64图片保存成功: {}", localPath.toAbsolutePath());
            return rel;

        } catch (Exception e) {
            log.error("保存Base64图片时发生异常", e);
            return null;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                log.error("关闭流时发生异常", e);
            }
        }
    }

    /**
     * 删除本地图片（支持库中存的相对路径 upload/carousel/xxx）
     */
    public boolean deleteLocalImage(String localPath) {
        if (localPath == null || localPath.trim().isEmpty()) {
            return false;
        }

        try {
            String normalized = localPath.replace("\\", "/").trim();
            if (normalized.contains("..")) {
                return false;
            }
            Path basePath = Paths.get(jeecgUploadPath).toAbsolutePath().normalize();
            Path candidate = Paths.get(normalized);
            if (!candidate.isAbsolute()) {
                candidate = basePath.resolve(candidate);
            }
            candidate = candidate.toAbsolutePath().normalize();
            if (!candidate.startsWith(basePath)) {
                log.warn("拒绝删除越界路径: {}", localPath);
                return false;
            }
            File file = candidate.toFile();
            if (file.exists() && file.isFile()) {
                boolean deleted = file.delete();
                if (deleted) {
                    log.info("删除本地图片: {}", candidate);
                }
                return deleted;
            }
        } catch (Exception e) {
            log.error("删除本地图片失败: {}", localPath, e);
        }

        return false;
    }

    private Path carouselDir() {
        return Paths.get(jeecgUploadPath).resolve("upload").resolve("carousel").normalize();
    }

    /**
     * 将接口传入的路径解析为 {@code jeecg.path.upload/upload/carousel} 下的真实文件，防止路径遍历与任意文件读取。
     * <p>允许形态：仅文件名（如 {@code uuid.png}），或相对上传根目录的完整路径（如 {@code upload/carousel/uuid.png}）。</p>
     *
     * @param imagePathParam 请求参数 imagePath
     * @return 存在且为普通文件时返回该文件，否则 empty
     */
    public Optional<File> resolveSafeCarouselImageFile(String imagePathParam) {
        if (imagePathParam == null || imagePathParam.trim().isEmpty()) {
            return Optional.empty();
        }
        String normalized = imagePathParam.replace("\\", "/").trim();
        try {
            SsrfFileTypeFilter.checkPathTraversal(normalized);
        } catch (Exception e) {
            log.warn("轮播图路径非法: {}", imagePathParam);
            return Optional.empty();
        }

        Path uploadRoot = Paths.get(jeecgUploadPath).toAbsolutePath().normalize();
        Path carouselRoot = uploadRoot.resolve("upload").resolve("carousel").normalize();

        Path candidate;
        if (normalized.startsWith(CAROUSEL_REL + "/")) {
            candidate = uploadRoot.resolve(normalized).normalize();
        } else if (!normalized.contains("/")) {
            candidate = carouselRoot.resolve(normalized).normalize();
        } else {
            log.warn("轮播图路径格式不支持: {}", imagePathParam);
            return Optional.empty();
        }

        if (!candidate.startsWith(carouselRoot)) {
            log.warn("轮播图路径越界: {}", imagePathParam);
            return Optional.empty();
        }

        String nameForTypeCheck = candidate.getFileName().toString();
        try {
            SsrfFileTypeFilter.checkDownloadFileType(nameForTypeCheck);
        } catch (Exception e) {
            log.warn("轮播图文件类型不允许: {}", imagePathParam);
            return Optional.empty();
        }

        File file = candidate.toFile();
        if (!file.exists() || !file.isFile()) {
            return Optional.empty();
        }
        return Optional.of(file);
    }
}
