package org.jeecg.modules.recruitment.xgsHome.service;

import org.jeecg.modules.recruitment.xgsHome.entity.XgsHome;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 首页
 * @Author: Liuyl
 * @Date:   2025-01-21
 * @Version: V1.0
 */
public interface IXgsHomeService extends IService<XgsHome> {
    boolean syncHomeContentFromAPI() throws Exception;
}
