package org.jeecg;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.StrUtil;
import org.apache.poi.xwpf.usermodel.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JavaFilesToDoc {

    /**
     * 将指定目录下的所有.java文件合并到一个Word文档中
     * @param sourceDirPath 源代码目录路径
     * @param outputDocPath 输出的Word文档路径
     */
    public static void mergeJavaFilesToDoc(String sourceDirPath, String outputDocPath) {
        // 创建Word文档对象
        XWPFDocument document = new XWPFDocument();
        
        // 遍历目录获取所有.java文件
        List<File> javaFiles = FileUtil.loopFiles(sourceDirPath, pathname -> 
            pathname.getName().endsWith(".java"));
        
        if (javaFiles.isEmpty()) {
            System.out.println("在目录 " + sourceDirPath + " 中未找到任何.java文件");
            return;
        }
        
        // 按文件名排序
        javaFiles.sort((f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
        
        for (File javaFile : javaFiles) {
            // 添加文件名作为标题
            addHeading(document, javaFile.getName(), 1);
            
            // 读取文件内容
            String content = FileUtil.readString(javaFile, StandardCharsets.UTF_8);
            
            // 添加内容到文档
            addCodeContent(document, content);
            
            // 添加分页符(可选)
            document.createParagraph().setPageBreak(true);
        }
        
        // 保存Word文档
        try (FileOutputStream out = new FileOutputStream(outputDocPath)) {
            document.write(out);
            System.out.println("成功将 " + javaFiles.size() + " 个.java文件合并到: " + outputDocPath);
        } catch (IOException e) {
            System.err.println("保存Word文档失败: " + e.getMessage());
        }
    }
    
    /**
     * 添加标题到Word文档
     */
    private static void addHeading(XWPFDocument document, String text, int level) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(true);
        run.setFontSize(16);
        paragraph.setStyle("Heading" + level);
    }
    
    /**
     * 添加代码内容到Word文档(使用等宽字体)
     */
    private static void addCodeContent(XWPFDocument document, String content) {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        
        // 设置等宽字体适合显示代码
        run.setFontFamily("Courier New");
        run.setFontSize(10);
        
        // 处理换行和缩进
        String[] lines = content.split("\\r?\\n");
        for (String line : lines) {
            run.setText(line);
            run.addBreak();
        }
    }
    
    public static void main(String[] args) {
        // 示例用法

        System.out.println("后端代码：");
        String sourceDir = "D:\\zktw\\xgszp\\RMS\\jeecg-boot\\jeecg-module-recruitment\\src\\main\\java\\org\\jeecg\\modules\\recruitment";
        String outputDoc = "D:\\zktw\\xgszp\\后端代码.doc";

        mergeJavaFilesToDoc(sourceDir, outputDoc);
        System.out.println("............end");

    }
}