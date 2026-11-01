package org.jeecg.modules.recruitment.resume.parse;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.jeecg.common.exception.JeecgBootException;
import org.springframework.stereotype.Component;
import org.xml.sax.ContentHandler;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Apache Tika 本地抽 PDF/文本内容（默认简历解析链路，不依赖智谱 ClientV4）。
 */
@Slf4j
@Component
public class LocalResumeTikaParser {

    /**
     * 抽取文件纯文本；handler 长度不限制（-1）。
     */
    public String extractText(Path filePath) {
        if (filePath == null || !Files.isRegularFile(filePath)) {
            throw new JeecgBootException("简历文件不存在或不可读：" + filePath);
        }
        Parser parser = new AutoDetectParser();
        ContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();
        try (InputStream in = Files.newInputStream(filePath)) {
            parser.parse(in, handler, metadata, context);
        } catch (Exception e) {
            log.warn("Tika 解析失败: {}", filePath, e);
            throw new JeecgBootException("本地解析简历失败（Tika）：" + e.getMessage());
        }
        String text = handler.toString();
        if (text == null || text.trim().isEmpty()) {
            throw new JeecgBootException("未能从简历中提取到文本（可能是扫描件，本期不支持 OCR）");
        }
        return text.trim();
    }
}
