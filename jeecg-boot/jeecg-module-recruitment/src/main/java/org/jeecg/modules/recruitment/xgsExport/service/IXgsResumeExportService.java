package org.jeecg.modules.recruitment.xgsExport.service;

import javax.servlet.http.HttpServletResponse;

/**
 * @Description: 简历导出服务接口
 * @Author: System
 * @Date: 2025-01-23
 * @Version: V1.0
 */
public interface IXgsResumeExportService {

    /**
     * 导出简历到Word文档
     * 
     * @param applyId 岗位申请ID
     * @param response HTTP响应对象
     * @throws Exception 导出异常
     */
    void exportResumeToWord(String applyId, HttpServletResponse response) throws Exception;
}

