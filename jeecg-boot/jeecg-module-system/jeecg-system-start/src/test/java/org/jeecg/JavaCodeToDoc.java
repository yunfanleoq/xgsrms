package org.jeecg;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class JavaCodeToDoc {
    public static void main(String[] args) {
        String sourceDirectory = "D:\\zktw\\xgszp\\RMS\\jeecg-boot\\jeecg-module-recruitment\\src\\main\\java\\org\\jeecg\\modules\\recruitment";
        String desktopPath = "D:\\zktw\\xgszp\\Desktop";
        String outputFilePath = desktopPath + "\\java_code.doc";

        try {
            // 修改部分：创建一个新的 POIFSFileSystem 对象来初始化 HWPFDocument
            POIFSFileSystem fs = new POIFSFileSystem();
            HWPFDocument document = new HWPFDocument(fs);
            Range range = document.getRange();

            // 遍历指定目录下的所有 Java 文件
            File directory = new File(sourceDirectory);
            if (directory.exists() && directory.isDirectory()) {
                processDirectory(directory, range);
            }

            // 将文档内容写入到文件
            try (FileOutputStream out = new FileOutputStream(outputFilePath)) {
                document.write(out);
            }
            System.out.println("Java 代码已保存到 " + outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processDirectory(File directory, Range range) throws IOException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 递归处理子目录
                    processDirectory(file, range);
                } else if (file.getName().endsWith(".java")) {
                    // 读取 Java 文件内容
                    Path filePath = Paths.get(file.getAbsolutePath());
                    List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
                    StringBuilder content = new StringBuilder();
                    content.append("File: ").append(file.getAbsolutePath()).append("\n");
                    for (String line : lines) {
                        content.append(line).append("\n");
                    }
                    content.append("\n").append(StringUtils.repeat("=", 80)).append("\n\n");

                    // 将内容添加到文档中
                    range.insertAfter(content.toString());
                }
            }
        }
    }
}