package org.jeecg.modules.recruitment.xgsExport.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.jeecg.modules.recruitment.positions.entity.XgsPositionApply;
import org.jeecg.modules.recruitment.positions.service.IXgsPositionApplyService;
import org.jeecg.modules.recruitment.security.XgsRecruitmentAuthUtil;
import org.jeecg.modules.recruitment.xgsExport.service.IXgsResumeExportService;
import org.jeecg.modules.recruitment.xgsExport.vo.XgsResumeExportVO;
import org.jeecg.modules.recruitment.xgsResume.entity.XgsResumeBase;
import org.jeecg.modules.recruitment.xgsResume.service.IXgsResumeBaseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
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
            // B-003：仅本人或具备审核/导出权限者可导出
            XgsRecruitmentAuthUtil.assertCanExportApply(positionApply);

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
//            int replacedCount = replacePlaceholders(document, dataMap);
//            log.info("成功替换{}个占位符", replacedCount);
            
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
//        const isPT = computed(() => positionType.value === '普通岗位');
//const isBSH = computed(() => positionType.value === '博士后岗位');
//const isFG = computed(() => positionType.value === '副高级以上岗位');
//const isTJ = computed(() => positionType.value === '人才派遣岗位');
        // 模板文件必须是Office 2007+格式（.docx）
        switch (positionType) {
            case "普通岗位":
                return "export_template_PT.docx";
            case "博士后岗位":
                return "export_template_BSH.docx";
            case "副高级以上岗位":
                return "export_template_FG.docx";
            case "人才派遣岗位":
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
     * 替换段落中的Word书签（简化版本，不使用Saxon）
     * 
     * @param paragraph 段落对象
     * @param dataMap 数据映射
     * @return 替换的书签数量
     */
    private int replaceParagraphBookmarks(XWPFParagraph paragraph, Map<String, Object> dataMap) {
        int replacedCount = 0;
        
        try {
            CTP ctp = paragraph.getCTP();
            
            // 获取段落中的所有书签
            CTBookmark[] bookmarks = ctp.getBookmarkStartArray();
            if (bookmarks == null || bookmarks.length == 0) {
                return 0;
            }
            
            // 遍历每个书签
            for (CTBookmark bookmark : bookmarks) {
                String bookmarkName = bookmark.getName();
                
                // 从dataMap中查找书签对应的值
                Object value = dataMap.get(bookmarkName);
                if (value == null) {
                    log.debug("未找到书签 [{}] 对应的数据", bookmarkName);
                    continue;
                }
                
                String replacementText = value.toString();
                
                // 使用改进的方式替换书签内容，确保只显示替换后的内容
                boolean replaced = replaceBookmarkContentCompletely(ctp, bookmark, replacementText);
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
     * 简化版书签内容替换（不使用XPath/Saxon）
     * 直接遍历XML元素查找并替换
     * 
     * @param ctp 段落的CTP对象
     * @param bookmark 书签对象
     * @param replacementText 替换文本
     * @return 是否替换成功
     */
    private boolean replaceBookmarkContentSimple(CTP ctp, CTBookmark bookmark, String replacementText) {
        try {
            long bookmarkId = bookmark.getId().longValue();
            boolean replaced = false;
            
            // 获取段落的所有子元素（包括书签和文本run）
            org.apache.xmlbeans.XmlObject[] children = ctp.selectChildren(
                new javax.xml.namespace.QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "*")
            );
            
            boolean inBookmark = false;
            
            for (org.apache.xmlbeans.XmlObject child : children) {
                // 检查是否是书签开始标记
                if (child instanceof CTBookmark) {
                    CTBookmark bm = (CTBookmark) child;
                    if (bm.getId().longValue() == bookmarkId) {
                        inBookmark = true;
                        continue;
                    }
                }
                
                // 检查是否是书签结束标记
                if (child instanceof CTMarkupRange) {
                    CTMarkupRange markupRange = (CTMarkupRange) child;
                    if (markupRange.getId().longValue() == bookmarkId) {
                        break; // 到达书签结束位置
                    }
                }
                
                // 如果在书签范围内，且是文本run，则替换
                if (inBookmark && child instanceof CTR) {
                    CTR run = (CTR) child;
                    CTText[] textArray = run.getTArray();
                    
                    if (textArray != null && textArray.length > 0) {
                        // 替换第一个文本节点
                        textArray[0].setStringValue(replacementText);
                        replaced = true;
                        
                        // 删除其他文本节点
                        for (int i = textArray.length - 1; i > 0; i--) {
                            run.removeT(i);
                        }
                        break;
                    }
                }
            }
            
            // 如果没有找到文本，尝试在书签后插入
            if (!replaced) {
                replaced = insertTextAfterBookmark(ctp, bookmark, replacementText);
            }
            
            return replaced;
        } catch (Exception e) {
            log.error("替换书签内容失败: {}", bookmark.getName(), e);
            return false;
        }
    }
    
    /**
     * 完全替换书签内容，保留书签标记
     * 
     * @param ctp 段落的CTP对象
     * @param bookmark 书签对象
     * @param replacementText 替换文本
     * @return 是否替换成功
     */
    private boolean replaceBookmarkContentCompletely(CTP ctp, CTBookmark bookmark, String replacementText) {
        try {
            long bookmarkId = bookmark.getId().longValue();
            String bookmarkName = bookmark.getName();
            
            // 第一步：找到书签开始和结束标记在XML中的位置
            int bookmarkStartPos = -1;
            int bookmarkEndPos = -1;
            
            // 使用XmlCursor定位书签开始位置
            org.apache.xmlbeans.XmlCursor startCursor = bookmark.newCursor();
            bookmarkStartPos = getElementPosition(ctp, startCursor);
            startCursor.dispose();
            
            // 找到书签结束标记
            CTMarkupRange bookmarkEnd = null;
            CTMarkupRange[] bookmarkEnds = ctp.getBookmarkEndArray();
            for (CTMarkupRange end : bookmarkEnds) {
                if (end.getId().longValue() == bookmarkId) {
                    bookmarkEnd = end;
                    org.apache.xmlbeans.XmlCursor endCursor = end.newCursor();
                    bookmarkEndPos = getElementPosition(ctp, endCursor);
                    endCursor.dispose();
                    break;
                }
            }
            
            if (bookmarkEnd == null || bookmarkStartPos == -1 || bookmarkEndPos == -1) {
                log.warn("未找到书签 [{}] 的完整标记位置", bookmarkName);
                return false;
            }
            
            log.debug("书签 [{}] 位置范围: {} - {}", bookmarkName, bookmarkStartPos, bookmarkEndPos);
            
            // 第二步：找到书签范围内的所有Run，并清空它们的文本内容
            java.util.List<Integer> runsInBookmark = new java.util.ArrayList<>();
            
            for (int i = 0; i < ctp.sizeOfRArray(); i++) {
                CTR run = ctp.getRArray(i);
                org.apache.xmlbeans.XmlCursor runCursor = run.newCursor();
                int runPos = getElementPosition(ctp, runCursor);
                runCursor.dispose();
                
                // 如果Run在书签范围内，记录下来
                if (runPos > bookmarkStartPos && runPos < bookmarkEndPos) {
                    runsInBookmark.add(i);
                }
            }
            
            log.debug("书签 [{}] 范围内找到 {} 个Run", bookmarkName, runsInBookmark.size());
            
            // 第三步：清空书签范围内所有Run的文本内容
            for (int runIndex : runsInBookmark) {
                if (runIndex >= 0 && runIndex < ctp.sizeOfRArray()) {
                    CTR run = ctp.getRArray(runIndex);
                    // 清空所有文本节点
                    int textCount = run.sizeOfTArray();
                    for (int j = textCount - 1; j >= 0; j--) {
                        run.removeT(j);
                    }
                }
            }
            
            // 第四步：在书签开始位置后插入新的Run和文本
            // 找到第一个合适的插入位置（书签开始标记之后）
            int insertRunPosition = -1;
            for (int i = 0; i < ctp.sizeOfRArray(); i++) {
                CTR run = ctp.getRArray(i);
                org.apache.xmlbeans.XmlCursor runCursor = run.newCursor();
                int runPos = getElementPosition(ctp, runCursor);
                runCursor.dispose();
                
                if (runPos > bookmarkStartPos) {
                    insertRunPosition = i;
                    break;
                }
            }
            
            // 如果在书签范围内找到了Run，在第一个Run中插入文本
            if (!runsInBookmark.isEmpty() && runsInBookmark.get(0) < ctp.sizeOfRArray()) {
                CTR firstRun = ctp.getRArray(runsInBookmark.get(0));
                CTText newText = firstRun.addNewT();
                newText.setStringValue(replacementText);
                log.debug("在书签 [{}] 的第一个Run中插入文本: [{}]", bookmarkName, replacementText);
            } else if (insertRunPosition >= 0) {
                // 如果书签内没有Run，在书签后创建新Run
                CTR newRun = ctp.insertNewR(insertRunPosition);
                CTText newText = newRun.addNewT();
                newText.setStringValue(replacementText);
                log.debug("在书签 [{}] 后创建新Run并插入文本: [{}]", bookmarkName, replacementText);
            } else {
                // 如果无法确定位置，在段落末尾添加
                CTR newRun = ctp.addNewR();
                CTText newText = newRun.addNewT();
                newText.setStringValue(replacementText);
                log.debug("在段落末尾为书签 [{}] 添加文本: [{}]", bookmarkName, replacementText);
            }
            
            log.info("成功替换书签 [{}] 的内容为 [{}]（保留书签标记）", bookmarkName, replacementText);
            return true;
            
        } catch (Exception e) {
            log.error("替换书签内容失败: {}", bookmark.getName(), e);
            return false;
        }
    }
    
    /**
     * 获取XML元素在父元素中的位置索引
     * 
     * @param parent 父元素
     * @param cursor 元素的XmlCursor
     * @return 位置索引，未找到返回-1
     */
    private int getElementPosition(org.apache.xmlbeans.XmlObject parent, org.apache.xmlbeans.XmlCursor cursor) {
        try {
            org.apache.xmlbeans.XmlCursor parentCursor = parent.newCursor();
            parentCursor.toFirstChild();
            
            int position = 0;
            do {
                if (cursorsEqual(parentCursor, cursor)) {
                    parentCursor.dispose();
                    return position;
                }
                position++;
            } while (parentCursor.toNextSibling());
            
            parentCursor.dispose();
            return -1;
        } catch (Exception e) {
            log.error("获取元素位置失败", e);
            return -1;
        }
    }
    
    /**
     * 比较两个XmlCursor是否指向同一个元素
     * 
     * @param cursor1 第一个cursor
     * @param cursor2 第二个cursor
     * @return 是否相同
     */
    private boolean cursorsEqual(org.apache.xmlbeans.XmlCursor cursor1, org.apache.xmlbeans.XmlCursor cursor2) {
        try {
            return cursor1.getObject() == cursor2.getObject();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 在书签后插入文本（简化版本）
     * 
     * @param ctp 段落的CTP对象
     * @param bookmark 书签对象
     * @param text 要插入的文本
     * @return 是否插入成功
     */
    private boolean insertTextAfterBookmark(CTP ctp, CTBookmark bookmark, String text) {
        try {
            long bookmarkId = bookmark.getId().longValue();
            
            // 查找书签在CTP中的位置
            int bookmarkIndex = -1;
            CTBookmark[] bookmarks = ctp.getBookmarkStartArray();
            for (int i = 0; i < bookmarks.length; i++) {
                if (bookmarks[i].getId().longValue() == bookmarkId) {
                    bookmarkIndex = i;
                    break;
                }
            }
            
            if (bookmarkIndex >= 0) {
                // 在书签后创建新的run并添加文本
                CTR newRun = ctp.addNewR();
                CTText ctText = newRun.addNewT();
                ctText.setStringValue(text);
                
                log.debug("在书签 [{}] 后插入文本", bookmark.getName());
                return true;
            }
            
            return false;
        } catch (Exception e) {
            log.error("在书签位置插入文本失败: {}", bookmark.getName(), e);
            return false;
        }
    }
}

