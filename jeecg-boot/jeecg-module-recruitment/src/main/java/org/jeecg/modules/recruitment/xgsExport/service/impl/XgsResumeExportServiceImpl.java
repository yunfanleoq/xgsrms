package org.jeecg.modules.recruitment.xgsExport.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import org.jeecg.modules.demo.positions.entity.XgsPositionApply;
import org.jeecg.modules.demo.positions.service.IXgsPositionApplyService;
import org.jeecg.modules.recruitment.xgsExport.service.IXgsResumeExportService;
import org.jeecg.modules.recruitment.xgsExport.vo.XgsResumeExportVO;
import org.jeecg.modules.recruitment.xgsResume.entity.XgsResumeBase;
import org.jeecg.modules.recruitment.xgsResume.service.IXgsResumeBaseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: 简历导出服务实现
 * @Author: System
 * @Date: 2025-01-23
 * @Version: V1.0
 */
@Slf4j
@Service
public class XgsResumeExportServiceImpl implements IXgsResumeExportService {

    @Autowired
    private IXgsPositionApplyService positionApplyService;

    @Autowired
    private IXgsResumeBaseService resumeBaseService;

    @Override
    public void exportResumeToWord(String applyId, HttpServletResponse response) throws Exception {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        
        try {
            // 1. 根据applyId查询岗位申请信息
            XgsPositionApply positionApply = positionApplyService.getById(applyId);
            if (positionApply == null) {
                throw new RuntimeException("未找到对应的岗位申请记录");
            }

            String resumeId = positionApply.getResumeId();
            String positionType = positionApply.getPositionType();
            String userName = positionApply.getUserName();
            String positionName = positionApply.getPositionName();

            // 2. 根据岗位类型选择Word模板
            String templateFileName = getTemplateFileName(positionType);
            String templatePath = "template/" + templateFileName;
            log.info("开始使用auto-poi导出Word文档，模板: {}", templatePath);

            // 3. 根据resumeId查询简历数据
            XgsResumeBase resumeBase = resumeBaseService.getById(resumeId);
            if (resumeBase == null) {
                throw new RuntimeException("未找到对应的简历记录");
            }

            // 4. 将简历数据转换为导出VO并获取数据Map
            XgsResumeExportVO exportVO = new XgsResumeExportVO();
            BeanUtils.copyProperties(resumeBase, exportVO);
            
            // 5. 准备模板数据（使用Map传递数据）
            Map<String, Object> dataMap = exportVO.getBookmarkMap();
            log.info("准备导出数据，共{}个字段", dataMap.size());
            
            // 打印前10个字段用于调试
            int count = 0;
            for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
                if (count++ < 10) {
                    log.info("字段 [{}] = [{}]", entry.getKey(), entry.getValue());
                }
            }

            // 6. 读取模板文件
            inputStream = this.getClass().getClassLoader().getResourceAsStream(templatePath);
            if (inputStream == null) {
                log.error("无法找到模板文件: {}", templatePath);
                throw new RuntimeException("无法找到模板文件: " + templatePath);
            }
            log.info("成功读取模板文件，准备替换占位符");

            // 7. 使用Apache POI读取Word文档并手动替换占位符
            XWPFDocument document = new XWPFDocument(inputStream);
            log.info("成功加载Word文档，开始替换内容");
            
            // 8. 替换文档中的所有占位符 {{变量名}}
            int replacedCount = replacePlaceholders(document, dataMap);
            log.info("成功替换{}个占位符", replacedCount);
            
            // 9. 替换Word内置书签
            int bookmarkReplacedCount = replaceBookmarks(document, dataMap);
            log.info("成功替换{}个Word书签", bookmarkReplacedCount);

            // 10. 生成文件名（导出为.docx格式）
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String fileName = String.format("简历_%s_%s_%s.docx", 
                userName != null ? userName : "未知", 
                positionName != null ? positionName : "未知岗位",
                sdf.format(new Date()));
            
            // 11. 设置响应头并输出
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            response.setHeader("Content-Disposition", 
                "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));

            outputStream = response.getOutputStream();
            document.write(outputStream);
            
            outputStream.flush();
            
            if (document != null) {
                document.close();
            }

            log.info("成功导出简历Word文档: {}", fileName);

        } catch (Exception e) {
            log.error("导出简历Word文档失败: applyId={}, error={}", applyId, e.getMessage(), e);
            throw e;
        } finally {
            // 关闭流
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                log.error("关闭流失败", e);
            }
        }
    }

    /**
     * 根据岗位类型获取模板文件名
     */
    private String getTemplateFileName(String positionType) {
        if (positionType == null) {
            throw new RuntimeException("岗位类型为空");
        }
        
        // 模板文件必须是Office 2007+格式（.docx）
        switch (positionType) {
            case "普通岗位":
                return "export_template_PT.docx";
            case "博士后岗位":
                return "export_template_BSH.docx";
            case "副高级岗位":
                return "export_template_FG.docx";
            case "专家推荐岗位":
                return "export_template_TJ.docx";
            default:
                throw new RuntimeException("不支持的岗位类型: " + positionType + "。支持的类型：普通岗位、博士后岗位、副高级岗位、专家推荐岗位");
        }
    }

    /**
     * 替换Word文档中的所有占位符 {{变量名}}
     * 
     * @param document Word文档对象
     * @param dataMap 数据映射
     * @return 替换的占位符数量
     */
    private int replacePlaceholders(XWPFDocument document, Map<String, Object> dataMap) {
        int replacedCount = 0;
        
        // 1. 替换段落中的占位符
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            replacedCount += replaceParagraphPlaceholders(paragraph, dataMap);
        }
        
        // 2. 替换表格中的占位符
        for (XWPFTable table : document.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph paragraph : cell.getParagraphs()) {
                        replacedCount += replaceParagraphPlaceholders(paragraph, dataMap);
                    }
                }
            }
        }
        
        // 3. 替换页眉中的占位符
        for (XWPFHeader header : document.getHeaderList()) {
            for (XWPFParagraph paragraph : header.getParagraphs()) {
                replacedCount += replaceParagraphPlaceholders(paragraph, dataMap);
            }
        }
        
        // 4. 替换页脚中的占位符
        for (XWPFFooter footer : document.getFooterList()) {
            for (XWPFParagraph paragraph : footer.getParagraphs()) {
                replacedCount += replaceParagraphPlaceholders(paragraph, dataMap);
            }
        }
        
        return replacedCount;
    }

    /**
     * 替换段落中的占位符
     * 
     * @param paragraph 段落对象
     * @param dataMap 数据映射
     * @return 替换的占位符数量
     */
    private int replaceParagraphPlaceholders(XWPFParagraph paragraph, Map<String, Object> dataMap) {
        int replacedCount = 0;
        
        // 获取段落的完整文本
        String text = paragraph.getText();
        if (text == null || text.isEmpty()) {
            return 0;
        }
        
        // 查找所有占位符 {{变量名}}
        Pattern pattern = Pattern.compile("\\{\\{([^}]+)\\}\\}");
        Matcher matcher = pattern.matcher(text);
        
        if (!matcher.find()) {
            return 0; // 没有找到占位符
        }
        
        // 重置matcher
        matcher.reset();
        
        // 构建替换后的文本
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String placeholder = matcher.group(1); // 获取占位符名称（不包括{{}}）
            Object value = dataMap.get(placeholder);
            String replacement = value != null ? value.toString() : "";
            
            // 转义特殊字符
            replacement = Matcher.quoteReplacement(replacement);
            matcher.appendReplacement(result, replacement);
            
            replacedCount++;
            log.debug("替换占位符: {{{}}} -> [{}]", placeholder, value != null ? value : "");
        }
        matcher.appendTail(result);
        
        // 如果有替换，则更新段落内容
        if (replacedCount > 0) {
            // 清除段落中的所有runs
            int runsSize = paragraph.getRuns().size();
            for (int i = runsSize - 1; i >= 0; i--) {
                paragraph.removeRun(i);
            }
            
            // 创建新的run并设置替换后的文本
            XWPFRun newRun = paragraph.createRun();
            newRun.setText(result.toString());
            
            // 尝试保留原有的字体格式（如果存在）
            if (runsSize > 0) {
                // 从第一个run复制格式（如果存在）
                // 这里可以根据需要进一步优化格式保留逻辑
            }
        }
        
        return replacedCount;
    }

    /**
     * 替换Word文档中的所有内置书签
     * 使用Word"插入→书签"功能创建的书签
     * 
     * @param document Word文档对象
     * @param dataMap 数据映射（书签名->值）
     * @return 替换的书签数量
     */
    private int replaceBookmarks(XWPFDocument document, Map<String, Object> dataMap) {
        int replacedCount = 0;
        
        try {
            // 1. 替换正文段落中的书签
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                replacedCount += replaceParagraphBookmarks(paragraph, dataMap);
            }
            
            // 2. 替换表格中的书签
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph paragraph : cell.getParagraphs()) {
                            replacedCount += replaceParagraphBookmarks(paragraph, dataMap);
                        }
                    }
                }
            }
            
            // 3. 替换页眉中的书签
            for (XWPFHeader header : document.getHeaderList()) {
                for (XWPFParagraph paragraph : header.getParagraphs()) {
                    replacedCount += replaceParagraphBookmarks(paragraph, dataMap);
                }
            }
            
            // 4. 替换页脚中的书签
            for (XWPFFooter footer : document.getFooterList()) {
                for (XWPFParagraph paragraph : footer.getParagraphs()) {
                    replacedCount += replaceParagraphBookmarks(paragraph, dataMap);
                }
            }
        } catch (Exception e) {
            log.error("替换Word书签失败", e);
        }
        
        return replacedCount;
    }

    /**
     * 替换段落中的Word书签
     * 
     * @param paragraph 段落对象
     * @param dataMap 数据映射
     * @return 替换的书签数量
     */
    private int replaceParagraphBookmarks(XWPFParagraph paragraph, Map<String, Object> dataMap) {
        int replacedCount = 0;
        
        try {
            // 获取段落中的所有书签
            CTBookmark[] bookmarks = paragraph.getCTP().getBookmarkStartArray();
            
            if (bookmarks == null || bookmarks.length == 0) {
                return 0;
            }
            
            for (CTBookmark bookmark : bookmarks) {
                String bookmarkName = bookmark.getName();
                
                // 从dataMap中查找书签对应的值
                Object value = dataMap.get(bookmarkName);
                if (value == null) {
                    // 如果没有找到对应的值，跳过（不报错）
                    log.debug("未找到书签 [{}] 对应的数据", bookmarkName);
                    continue;
                }
                
                String replacementText = value.toString();
                
                // 查找书签所在的位置并替换内容
                boolean replaced = replaceBookmarkContent(paragraph, bookmark, replacementText);
                if (replaced) {
                    replacedCount++;
                    log.debug("替换书签: [{}] -> [{}]", bookmarkName, replacementText);
                }
            }
        } catch (Exception e) {
            log.error("替换段落书签失败", e);
        }
        
        return replacedCount;
    }

    /**
     * 替换单个书签的内容
     * 
     * @param paragraph 段落对象
     * @param bookmark 书签对象
     * @param replacementText 替换文本
     * @return 是否替换成功
     */
    private boolean replaceBookmarkContent(XWPFParagraph paragraph, CTBookmark bookmark, String replacementText) {
        try {
            // 获取书签的ID
            long bookmarkId = bookmark.getId().longValue();
            
            // 使用XmlCursor遍历段落的XML内容
            XmlCursor cursor = paragraph.getCTP().newCursor();
            cursor.selectPath("declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' "
                    + ".//w:bookmarkStart[@w:id='" + bookmarkId + "']");
            
            if (!cursor.toNextSelection()) {
                cursor.dispose();
                return false;
            }
            
            // 移动到书签开始位置的下一个元素
            cursor.toNextSibling();
            
            // 查找书签范围内的文本节点
            boolean textFound = false;
            while (cursor.toNextSibling()) {
                XmlObject obj = cursor.getObject();
                
                // 检查是否到达书签结束位置
                if (obj instanceof org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkupRange) {
                    org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkupRange markupRange = 
                        (org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkupRange) obj;
                    if (markupRange.getId().longValue() == bookmarkId) {
                        break; // 到达书签结束位置
                    }
                }
                
                // 查找并替换文本
                if (obj instanceof org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR) {
                    org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR run = 
                        (org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR) obj;
                    
                    CTText[] textArray = run.getTArray();
                    if (textArray != null && textArray.length > 0) {
                        // 只替换第一个文本节点的内容
                        textArray[0].setStringValue(replacementText);
                        textFound = true;
                        
                        // 删除其他文本节点（如果有多个）
                        for (int i = textArray.length - 1; i > 0; i--) {
                            run.removeT(i);
                        }
                        break;
                    }
                }
            }
            
            cursor.dispose();
            
            // 如果没有找到文本节点，尝试在书签位置插入新的文本
            if (!textFound) {
                insertTextAtBookmark(paragraph, bookmark, replacementText);
                return true;
            }
            
            return textFound;
        } catch (Exception e) {
            log.error("替换书签内容失败: {}", bookmark.getName(), e);
            return false;
        }
    }

    /**
     * 在书签位置插入文本（当书签内没有文本时）
     * 
     * @param paragraph 段落对象
     * @param bookmark 书签对象
     * @param text 要插入的文本
     */
    private void insertTextAtBookmark(XWPFParagraph paragraph, CTBookmark bookmark, String text) {
        try {
            long bookmarkId = bookmark.getId().longValue();
            
            XmlCursor cursor = paragraph.getCTP().newCursor();
            cursor.selectPath("declare namespace w='http://schemas.openxmlformats.org/wordprocessingml/2006/main' "
                    + ".//w:bookmarkStart[@w:id='" + bookmarkId + "']");
            
            if (cursor.toNextSelection()) {
                // 在书签开始位置之后插入新的run
                XWPFRun run = paragraph.createRun();
                run.setText(text);
                
                // 注意：这种方式会在段落末尾添加文本
                // 如果需要精确控制位置，可能需要更复杂的XML操作
                log.debug("在书签 [{}] 位置插入文本", bookmark.getName());
            }
            
            cursor.dispose();
        } catch (Exception e) {
            log.error("在书签位置插入文本失败: {}", bookmark.getName(), e);
        }
    }
}

