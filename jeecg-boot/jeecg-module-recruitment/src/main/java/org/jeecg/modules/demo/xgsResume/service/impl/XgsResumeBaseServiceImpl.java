package org.jeecg.modules.demo.xgsResume.service.impl;

import io.swagger.annotations.ApiModelProperty;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.demo.positions.entity.XgsPositionApply;
import org.jeecg.modules.demo.positions.mapper.XgsPositionApplyMapper;
import org.jeecg.modules.demo.xgsResume.entity.XgsResumeBase;
import org.jeecg.modules.demo.xgsResume.entity.XgsResumeWorks;
import org.jeecg.modules.demo.xgsResume.entity.XgsResumeEdus;
import org.jeecg.modules.demo.xgsResume.entity.XgsResumeHome;
import org.jeecg.modules.demo.xgsResume.mapper.XgsResumeWorksMapper;
import org.jeecg.modules.demo.xgsResume.mapper.XgsResumeEdusMapper;
import org.jeecg.modules.demo.xgsResume.mapper.XgsResumeHomeMapper;
import org.jeecg.modules.demo.xgsResume.mapper.XgsResumeBaseMapper;
import org.jeecg.modules.demo.xgsResume.service.IXgsResumeBaseService;
import org.jeecg.modules.demo.xgsResume.vo.XgsResumeBasePage;
import org.jeecg.modules.recruitment.position.controller.XgsFlowOpinionsController;
import org.jeecg.modules.recruitment.position.entity.XgsFlowOpinions;
import org.jeecg.modules.recruitment.position.mapper.XgsFlowOpinionsMapper;
import org.jeecg.modules.recruitment.position.service.IXgsFlowOpinionsService;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;

/**
 * @Description: 基本信息
 * @Author: jeecg-boot
 * @Date:   2024-12-25
 * @Version: V1.0
 */
@Service
public class XgsResumeBaseServiceImpl extends ServiceImpl<XgsResumeBaseMapper, XgsResumeBase> implements IXgsResumeBaseService {

	@Autowired
	private XgsResumeBaseMapper xgsResumeBaseMapper;
	@Autowired
	private XgsResumeWorksMapper xgsResumeWorksMapper;
	@Autowired
	private XgsResumeEdusMapper xgsResumeEdusMapper;
	@Autowired
	private XgsResumeHomeMapper xgsResumeHomeMapper;
	@Autowired
	private XgsPositionApplyMapper positionApplyMapper;
	@Autowired
	private XgsFlowOpinionsMapper flowOpinionsMapper;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(XgsResumeBase xgsResumeBase, List<XgsResumeWorks> xgsResumeWorksList,List<XgsResumeEdus> xgsResumeEdusList,List<XgsResumeHome> xgsResumeHomeList) {
		xgsResumeBaseMapper.insert(xgsResumeBase);
		if(xgsResumeWorksList!=null && xgsResumeWorksList.size()>0) {
			for(XgsResumeWorks entity:xgsResumeWorksList) {
				//外键设置
				entity.setBaseId(xgsResumeBase.getId());
				xgsResumeWorksMapper.insert(entity);
			}
		}
		if(xgsResumeEdusList!=null && xgsResumeEdusList.size()>0) {
			for(XgsResumeEdus entity:xgsResumeEdusList) {
				//外键设置
				entity.setBaseId(xgsResumeBase.getId());
				xgsResumeEdusMapper.insert(entity);
			}
		}
		if(xgsResumeHomeList!=null && xgsResumeHomeList.size()>0) {
			for(XgsResumeHome entity:xgsResumeHomeList) {
				//外键设置
				entity.setBaseId(xgsResumeBase.getId());
				xgsResumeHomeMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(XgsResumeBase xgsResumeBase,List<XgsResumeWorks> xgsResumeWorksList,List<XgsResumeEdus> xgsResumeEdusList,List<XgsResumeHome> xgsResumeHomeList) {
		xgsResumeBaseMapper.updateById(xgsResumeBase);
		
		//1.先删除子表数据
		xgsResumeWorksMapper.deleteByMainId(xgsResumeBase.getId());
		xgsResumeEdusMapper.deleteByMainId(xgsResumeBase.getId());
		xgsResumeHomeMapper.deleteByMainId(xgsResumeBase.getId());
		
		//2.子表数据重新插入
		if(xgsResumeWorksList!=null && xgsResumeWorksList.size()>0) {
			for(XgsResumeWorks entity:xgsResumeWorksList) {
				//外键设置
				entity.setBaseId(xgsResumeBase.getId());
				xgsResumeWorksMapper.insert(entity);
			}
		}
		if(xgsResumeEdusList!=null && xgsResumeEdusList.size()>0) {
			for(XgsResumeEdus entity:xgsResumeEdusList) {
				//外键设置
				entity.setBaseId(xgsResumeBase.getId());
				xgsResumeEdusMapper.insert(entity);
			}
		}
		if(xgsResumeHomeList!=null && xgsResumeHomeList.size()>0) {
			for(XgsResumeHome entity:xgsResumeHomeList) {
				//外键设置
				entity.setBaseId(xgsResumeBase.getId());
				xgsResumeHomeMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		xgsResumeWorksMapper.deleteByMainId(id);
		xgsResumeEdusMapper.deleteByMainId(id);
		xgsResumeHomeMapper.deleteByMainId(id);
		xgsResumeBaseMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			xgsResumeWorksMapper.deleteByMainId(id.toString());
			xgsResumeEdusMapper.deleteByMainId(id.toString());
			xgsResumeHomeMapper.deleteByMainId(id.toString());
			xgsResumeBaseMapper.deleteById(id);
		}
	}

	@Override
	@Transactional
	public void saveMainWithJob(XgsResumeBasePage xgsResumeBasePage, List<XgsResumeWorks> xgsResumeWorksList, List<XgsResumeEdus> xgsResumeEdusList, List<XgsResumeHome> xgsResumeHomeList) {
		XgsResumeBase xgsResumeBase = new XgsResumeBase();
		BeanUtils.copyProperties(xgsResumeBasePage, xgsResumeBase);
		saveMain(xgsResumeBase, xgsResumeWorksList, xgsResumeEdusList, xgsResumeHomeList);
		// 登录用户
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		// 岗位信息
		XgsPositionApply positionApply = new XgsPositionApply();
		positionApply.setUserId(sysUser.getId());
		positionApply.setUserName(sysUser.getRealname());
		positionApply.setResumeId(xgsResumeBase.getId());
		positionApply.setPositionName(xgsResumeBasePage.getApplyPositionName());
		positionApply.setPositionDept(xgsResumeBasePage.getApplyPositionDept());
		positionApply.setPositionType(xgsResumeBasePage.getApplyPositionType());
		positionApply.setApprovalNode(IXgsFlowOpinionsService.NODE_DEPT); // 提交到 部门审核
		positionApply.setApprovalStatus(IXgsFlowOpinionsService.APPROVAL_STATUS_GOING); // 内部审核状态
		positionApply.setStatus(IXgsFlowOpinionsService.APPROVAL_STATUS_DEPT_TODO); // 用户看到的审核状态
		positionApplyMapper.insert(positionApply);
		// 增加流程记录
		XgsFlowOpinions flowOpinions = new XgsFlowOpinions();
		flowOpinions.setApprovalNode(IXgsFlowOpinionsService.NODE_USER);
		flowOpinions.setApprovalStatus(IXgsFlowOpinionsService.APPROVAL_STATUS_SUBMIT);
		flowOpinions.setOpinions(IXgsFlowOpinionsService.APPROVAL_STATUS_SUBMIT);
		flowOpinionsMapper.insert(flowOpinions);
	}
}
