package org.jeecg.modules.demo.xgsResume.service.impl;

import org.jeecg.modules.demo.xgsResume.entity.XgsResumeWorks;
import org.jeecg.modules.demo.xgsResume.mapper.XgsResumeWorksMapper;
import org.jeecg.modules.demo.xgsResume.service.IXgsResumeWorksService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 工作经历
 * @Author: jeecg-boot
 * @Date:   2024-12-25
 * @Version: V1.0
 */
@Service
public class XgsResumeWorksServiceImpl extends ServiceImpl<XgsResumeWorksMapper, XgsResumeWorks> implements IXgsResumeWorksService {
	
	@Autowired
	private XgsResumeWorksMapper xgsResumeWorksMapper;
	
	@Override
	public List<XgsResumeWorks> selectByMainId(String mainId) {
		return xgsResumeWorksMapper.selectByMainId(mainId);
	}
}
