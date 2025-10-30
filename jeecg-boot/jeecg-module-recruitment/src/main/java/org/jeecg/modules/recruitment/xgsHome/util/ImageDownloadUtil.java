package org.jeecg.modules.recruitment.xgsHome.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * @Description: 图片下载工具类
 * @Author: System
 * @Date: 2025-10-15
 * @Version: V1.0
 */
@Slf4j
@Component
public class ImageDownloadUtil {

    // 图片保存的根目录，从配置文件读取，默认为upload/images
    private static String uploadPath = "upload/carousel";

    /**
     * 下载图片并保存到服务器本地
     *
     * @param imageUrl 图片URL地址
     * @return 保存后的本地相对路径
     */
    public static String downloadAndSaveImage(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            log.error("图片URL为空");
            return null;
        }

        HttpURLConnection connection = null;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            // 创建保存目录
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                log.info("创建图片保存目录: {}", uploadDir.toAbsolutePath());
            }

            // 生成唯一文件名
            String fileExtension = getFileExtension(imageUrl);
            String fileName = UUID.randomUUID().toString() + fileExtension;
            String filePath = uploadPath + File.separator + fileName;
            File localFile = new File(filePath);

            // 下载图片
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

                log.info("图片下载成功: {} -> {}", imageUrl, filePath);
                return filePath;
            } else {
                log.error("图片下载失败，HTTP响应码: {}, URL: {}", responseCode, imageUrl);
                return null;
            }

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

    /**
     * 从URL中提取文件扩展名
     *
     * @param url 图片URL
     * @return 文件扩展名，如.jpg, .png等
     */
    private static String getFileExtension(String url) {
        if (url == null || url.isEmpty()) {
            return ".jpg"; // 默认扩展名
        }

        // 移除URL参数
        String urlWithoutParams = url.split("\\?")[0];

        // 提取扩展名
        int lastDotIndex = urlWithoutParams.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < urlWithoutParams.length() - 1) {
            String extension = urlWithoutParams.substring(lastDotIndex);
            // 验证扩展名是否为常见图片格式
            if (extension.matches("\\.(jpg|jpeg|png|gif|bmp|webp)")) {
                return extension;
            }
        }

        return ".jpg"; // 默认返回.jpg
    }

    /**
     * 保存Base64编码的图片到本地
     *
     * @param base64Data Base64编码的图片数据（可以包含data:image/png;base64,前缀）
     * @return 保存后的本地相对路径
     */
    public static String saveBase64Image(String base64Data) {
        if (base64Data == null || base64Data.trim().isEmpty()) {
            log.error("Base64图片数据为空");
            return null;
        }

        FileOutputStream outputStream = null;

        try {
            // 创建保存目录
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                log.info("创建图片保存目录: {}", uploadDir.toAbsolutePath());
            }

            // 处理Base64数据，移除前缀
            String base64String = base64Data;
            String fileExtension = ".jpg"; // 默认扩展名
            
            if (base64Data.contains(",")) {
                String[] parts = base64Data.split(",");
                if (parts.length == 2) {
                    // 提取MIME类型
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

            // 生成唯一文件名
            String fileName = UUID.randomUUID().toString() + fileExtension;
            String filePath = uploadPath + File.separator + fileName;
            File localFile = new File(filePath);

            // 解码Base64并保存
            byte[] imageBytes = java.util.Base64.getDecoder().decode(base64String);
            outputStream = new FileOutputStream(localFile);
            outputStream.write(imageBytes);

            log.info("Base64图片保存成功: {}", filePath);
            return filePath;

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
     * 删除本地图片文件
     *
     * @param localPath 本地图片路径
     * @return 是否删除成功
     */
    public static boolean deleteLocalImage(String localPath) {
        if (localPath == null || localPath.trim().isEmpty()) {
            return false;
        }

        try {
            File file = new File(localPath);
            if (file.exists() && file.isFile()) {
                boolean deleted = file.delete();
                if (deleted) {
                    log.info("删除本地图片: {}", localPath);
                }
                return deleted;
            }
        } catch (Exception e) {
            log.error("删除本地图片失败: {}", localPath, e);
        }

        return false;
    }
}

