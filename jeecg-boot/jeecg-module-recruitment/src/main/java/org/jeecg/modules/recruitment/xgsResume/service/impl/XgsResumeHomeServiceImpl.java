package org.jeecg.modules.recruitment.xgsResume.service.impl;

import org.jeecg.modules.recruitment.xgsResume.entity.XgsResumeHome;
import org.jeecg.modules.recruitment.xgsResume.mapper.XgsResumeHomeMapper;
import org.jeecg.modules.recruitment.xgsResume.service.IXgsResumeHomeService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 家庭状况
 * @Author: Liuyl
 * @Date:   2024-12-25
 * @Version: V1.0
 */
@Service
public class XgsResumeHomeServiceImpl extends ServiceImpl<XgsResumeHomeMapper, XgsResumeHome> implements IXgsResumeHomeService {
	
	@Autowired
	private XgsResumeHomeMapper xgsResumeHomeMapper;
	
	@Override
	public List<XgsResumeHome> selectByMainId(String mainId) {
		return xgsResumeHomeMapper.selectByMainId(mainId);
	}
}
