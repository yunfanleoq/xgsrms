package org.jeecg.modules.demo.positions.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.modules.demo.positions.entity.XgsPositionApply;
import org.jeecg.modules.demo.positions.mapper.XgsPositionApplyMapper;
import org.jeecg.modules.demo.positions.service.IXgsPositionApplyService;
import org.jeecg.modules.demo.positions.vo.XgsPositionApplyVO;
import org.jeecg.modules.demo.xgsResume.entity.XgsResumeBase;
import org.jeecg.modules.demo.xgsResume.entity.XgsResumeEdus;
import org.jeecg.modules.demo.xgsResume.entity.XgsResumeHome;
import org.jeecg.modules.demo.xgsResume.entity.XgsResumeWorks;
import org.jeecg.modules.demo.xgsResume.mapper.XgsResumeBaseMapper;
import org.jeecg.modules.demo.xgsResume.mapper.XgsResumeEdusMapper;
import org.jeecg.modules.demo.xgsResume.mapper.XgsResumeHomeMapper;
import org.jeecg.modules.demo.xgsResume.mapper.XgsResumeWorksMapper;
import org.jeecg.modules.recruitment.position.entity.XgsFlowOpinions;
import org.jeecg.modules.recruitment.position.mapper.XgsFlowOpinionsMapper;
import org.jeecg.modules.recruitment.position.service.IXgsFlowOpinionsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 岗位申请
 * @Author: jeecg-boot
 * @Date:   2024-12-24
 * @Version: V1.0
 */
@Service
public class XgsPositionApplyServiceImpl extends ServiceImpl<XgsPositionApplyMapper, XgsPositionApply> implements IXgsPositionApplyService {

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

    /**
     *
     * @param xgsPositionApplyVO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void doPositionApply(XgsPositionApplyVO xgsPositionApplyVO) {

        XgsResumeBase xgsResumeBase = new XgsResumeBase();
        BeanUtils.copyProperties(xgsPositionApplyVO, xgsResumeBase);
        xgsResumeBaseMapper.insert(xgsResumeBase);
        List<XgsResumeWorks> xgsResumeWorksList = xgsPositionApplyVO.getXgsResumeWorksList();
        if(xgsResumeWorksList!=null && xgsResumeWorksList.size()>0) {
            for(XgsResumeWorks entity:xgsResumeWorksList) {
                //外键设置
                entity.setBaseId(xgsResumeBase.getId());
                xgsResumeWorksMapper.insert(entity);
            }
        }
        List<XgsResumeEdus> xgsResumeEdusList = xgsPositionApplyVO.getXgsResumeEdusList();
        if(xgsResumeEdusList!=null && xgsResumeEdusList.size()>0) {
            for(XgsResumeEdus entity:xgsResumeEdusList) {
                //外键设置
                entity.setBaseId(xgsResumeBase.getId());
                xgsResumeEdusMapper.insert(entity);
            }
        }
        List<XgsResumeHome> xgsResumeHomeList = xgsPositionApplyVO.getXgsResumeHomeList();
        if(xgsResumeHomeList!=null && xgsResumeHomeList.size()>0) {
            for(XgsResumeHome entity:xgsResumeHomeList) {
                //外键设置
                entity.setBaseId(xgsResumeBase.getId());
                xgsResumeHomeMapper.insert(entity);
            }
        }

        XgsPositionApply xgsPositionApply = xgsPositionApplyVO.getXgsPositionApply();
        xgsPositionApply.setResumeId(xgsResumeBase.getId());
        xgsPositionApply.setResumeName(xgsResumeBase.getResumeName());
//        xgsPositionApply.setPositionName(xgsResumeBase.getApplyPositionName());
//        xgsPositionApply.setPositionDept(xgsResumeBase.getApplyPositionDept());
//        xgsPositionApply.setPositionType(xgsResumeBase.getApplyPositionType());
        xgsPositionApply.setApprovalNode(IXgsFlowOpinionsService.NODE_DEPT); // 提交到 部门审核
        xgsPositionApply.setApprovalStatus(IXgsFlowOpinionsService.APPROVAL_STATUS_DEPT_TODO); // 内部审核状态
        xgsPositionApply.setStatus(IXgsFlowOpinionsService.APPROVAL_STATUS_GOING); // 用户看到的审核状态
        save(xgsPositionApply);
        // 增加流程记录
        XgsFlowOpinions flowOpinions = new XgsFlowOpinions();
        flowOpinions.setApprovalNode(IXgsFlowOpinionsService.NODE_USER);
        flowOpinions.setApprovalStatus(IXgsFlowOpinionsService.APPROVAL_STATUS_SUBMIT);
        flowOpinions.setOpinions(IXgsFlowOpinionsService.APPROVAL_STATUS_SUBMIT);
        flowOpinionsMapper.insert(flowOpinions);
    }

    @Override
    public Boolean checkHasApplied(XgsPositionApplyVO xgsPositionApplyVO) {
        XgsPositionApply xgsPositionApply = xgsPositionApplyVO.getXgsPositionApply();
        QueryWrapper<XgsPositionApply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("create_by", xgsPositionApply.getCreateBy());
        queryWrapper.eq("position_type", xgsPositionApply.getPositionType());
        queryWrapper.eq("position_dept", xgsPositionApply.getPositionDept());
        long count = count(queryWrapper);
        return count > 0;
    }
}
