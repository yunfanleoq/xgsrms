package org.jeecg.modules.recruitment.resume.service;

import org.jeecg.modules.recruitment.resume.entity.XgsUserResumeFile;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 用户简历文件分析
 * @Author: Liuyl
 * @Date:   2025-01-01
 * @Version: V1.0
 */
public interface IXgsUserResumeFileService extends IService<XgsUserResumeFile> {

    /**
     * 分析简历
     * @param xgsUserResumeFile
     * @return
     */
    XgsUserResumeFile analysisResume(XgsUserResumeFile xgsUserResumeFile);
}
