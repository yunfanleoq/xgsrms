package org.jeecg.modules.recruitment.xgsMyresume.service;

import org.jeecg.modules.recruitment.xgsMyresume.entity.XgsMyresume;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 简历列表
 * @Author: Liuyl
 * @Date:   2025-01-02
 * @Version: V1.0
 */
public interface IXgsMyresumeService extends IService<XgsMyresume> {

    /**
     * 分析简历
     * @param xgsMyresume
     */
    void analysisResume(XgsMyresume xgsMyresume);
}
