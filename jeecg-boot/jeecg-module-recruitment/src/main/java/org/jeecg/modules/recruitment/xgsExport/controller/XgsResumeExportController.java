package org.jeecg.modules.recruitment.xgsExport.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.shiro.authz.UnauthorizedException;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.recruitment.xgsExport.service.IXgsResumeExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 简历导出控制器
 * @Author: System
 * @Date: 2025-01-23
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "简历导出")
@RestController
@RequestMapping("/xgsExport")
public class XgsResumeExportController {

    @Autowired
    private IXgsResumeExportService exportService;

    /**
     * 导出简历到Word文档
     * 
     * @param applyId 岗位申请ID
     * @param response HTTP响应对象
     */
    @ApiOperation(value = "导出简历Word文档", notes = "根据岗位申请ID导出简历到Word文档")
//    @RequiresPermissions("xgsUserResume:xgs_position_apply:exportXls")
    @GetMapping("/exportResumeWord")
    public void exportResumeWord(
            @RequestParam(name = "applyId", required = true) String applyId,
            HttpServletResponse response) {
        try {
            exportService.exportResumeToWord(applyId, response);
        } catch (UnauthorizedException e) {
            log.warn("导出Word文档无权限: applyId={}", applyId);
            try {
                response.reset();
                response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            } catch (Exception ex) {
                log.error("写入403响应失败", ex);
            }
        } catch (Exception e) {
            log.error("导出Word文档失败: applyId={}", applyId, e);
            try {
                response.reset();
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(Result.error("导出失败: " + e.getMessage()).toString());
            } catch (Exception ex) {
                log.error("写入错误响应失败", ex);
            }
        }
    }

    /**
     * 测试模板文件是否可用
     */
    @ApiOperation(value = "测试Word模板文件", notes = "测试所有Word模板文件是否可用（仅 dev）")
    @Profile("dev")
    @GetMapping("/testTemplates")
    public Result<?> testTemplates() {
        Map<String, Object> result = new HashMap<>();
        // jeecgframework的WordExportUtil只支持.docx格式
        String[] templates = {
            "export_template_PT.docx",
            "export_template_BSH.docx",
            "export_template_FG.docx",
            "export_template_TJ.docx"
        };

        for (String template : templates) {
            Map<String, Object> templateInfo = new HashMap<>();
            String templatePath = "template/" + template;
            
            try {
                InputStream rawInputStream = this.getClass().getClassLoader().getResourceAsStream(templatePath);
                if (rawInputStream == null) {
                    templateInfo.put("status", "文件不存在");
                    templateInfo.put("error", "无法找到模板文件");
                } else {
                    BufferedInputStream inputStream = new BufferedInputStream(rawInputStream);
                    
                    // 读取文件头判断文件类型
                    inputStream.mark(8);
                    byte[] header = new byte[8];
                    int bytesRead = inputStream.read(header);
                    inputStream.reset();
                    
                    int available = inputStream.available();
                    templateInfo.put("fileSize", available + " bytes");
                    
                    // 判断文件类型
                    String fileType = detectFileType(header);
                    templateInfo.put("detectedType", fileType);
                    
                    if (available == 0) {
                        templateInfo.put("status", "文件为空");
                    } else if (fileType.equals("ZIP/DOCX")) {
                        try {
                            XWPFDocument doc = new XWPFDocument(inputStream);
                            templateInfo.put("status", "✅ 正常");
                            templateInfo.put("paragraphs", doc.getParagraphs().size());
                            templateInfo.put("tables", doc.getTables().size());
                            doc.close();
                        } catch (Exception e) {
                            templateInfo.put("status", "❌ 格式错误");
                            templateInfo.put("error", e.getMessage());
                        }
                    } else if (fileType.equals("DOC")) {
                        templateInfo.put("status", "❌ 错误格式");
                        templateInfo.put("error", "这是旧版.doc格式，需要转换为.docx格式");
                        templateInfo.put("solution", "用Word打开后另存为.docx格式");
                    } else {
                        templateInfo.put("status", "❌ 未知格式");
                        templateInfo.put("error", "文件格式不是Word文档");
                        templateInfo.put("headerHex", bytesToHex(header));
                    }
                    inputStream.close();
                }
            } catch (Exception e) {
                templateInfo.put("status", "读取失败");
                templateInfo.put("error", e.getMessage());
                log.error("测试模板文件失败: " + template, e);
            }
            
            result.put(template, templateInfo);
        }

        return Result.OK(result);
    }

    /**
     * 检测文件类型
     */
    private String detectFileType(byte[] header) {
        if (header.length < 4) {
            return "未知";
        }
        
        // DOCX文件是ZIP格式，文件头是 PK (0x50 0x4B)
        if (header[0] == 0x50 && header[1] == 0x4B) {
            return "ZIP/DOCX";
        }
        
        // DOC文件头是 0xD0 0xCF 0x11 0xE0
        if (header[0] == (byte)0xD0 && header[1] == (byte)0xCF && 
            header[2] == 0x11 && header[3] == (byte)0xE0) {
            return "DOC";
        }
        
        // PDF文件头
        if (header[0] == 0x25 && header[1] == 0x50 && header[2] == 0x44 && header[3] == 0x46) {
            return "PDF";
        }
        
        return "未知";
    }

    /**
     * 字节数组转十六进制字符串
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}

