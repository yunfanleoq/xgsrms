package org.jeecg.modules.recruitment.positions.service.impl;

import java.util.Collection;

import org.jeecg.modules.recruitment.positions.entity.XgsPositions;
import org.jeecg.modules.recruitment.positions.mapper.XgsPositionsMapper;
import org.jeecg.modules.recruitment.positions.service.IXgsPositionsService;
import org.jeecg.modules.recruitment.positions.util.XgsPositionInputSanitizer;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 招聘岗位列表
 * @Author: jeecg-boot
 * @Date:   2024-12-20
 * @Version: V1.0
 */
@Service
public class XgsPositionsServiceImpl extends ServiceImpl<XgsPositionsMapper, XgsPositions> implements IXgsPositionsService {

	@Override
	public boolean save(XgsPositions entity) {
		XgsPositionInputSanitizer.sanitize(entity);
		return super.save(entity);
	}

	@Override
	public boolean saveBatch(Collection<XgsPositions> entityList, int batchSize) {
		if (entityList != null) {
			for (XgsPositions e : entityList) {
				XgsPositionInputSanitizer.sanitize(e);
			}
		}
		return super.saveBatch(entityList, batchSize);
	}

	@Override
	public boolean updateById(XgsPositions entity) {
		XgsPositionInputSanitizer.sanitize(entity);
		return super.updateById(entity);
	}
}
