package org.jeecg.modules.demo.xgsResume.service.impl;

import org.jeecg.modules.demo.xgsResume.entity.XgsResumeEdus;
import org.jeecg.modules.demo.xgsResume.mapper.XgsResumeEdusMapper;
import org.jeecg.modules.demo.xgsResume.service.IXgsResumeEdusService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 教育经历
 * @Author: jeecg-boot
 * @Date:   2024-12-25
 * @Version: V1.0
 */
@Service
public class XgsResumeEdusServiceImpl extends ServiceImpl<XgsResumeEdusMapper, XgsResumeEdus> implements IXgsResumeEdusService {
	
	@Autowired
	private XgsResumeEdusMapper xgsResumeEdusMapper;
	
	@Override
	public List<XgsResumeEdus> selectByMainId(String mainId) {
		return xgsResumeEdusMapper.selectByMainId(mainId);
	}
}
