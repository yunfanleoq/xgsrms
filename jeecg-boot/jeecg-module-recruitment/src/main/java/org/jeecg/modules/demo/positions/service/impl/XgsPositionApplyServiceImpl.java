package org.jeecg.modules.demo.positions.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.demo.positions.entity.XgsPositionApply;
import org.jeecg.modules.demo.positions.mapper.XgsPositionApplyMapper;
import org.jeecg.modules.demo.positions.service.IXgsPositionApplyService;
import org.jeecg.modules.demo.positions.vo.XgsPositionApplyVO;
import org.jeecg.modules.recruitment.xgsResume.entity.XgsResumeBase;
import org.jeecg.modules.recruitment.xgsResume.entity.XgsResumeEdus;
import org.jeecg.modules.recruitment.xgsResume.entity.XgsResumeHome;
import org.jeecg.modules.recruitment.xgsResume.entity.XgsResumeWorks;
import org.jeecg.modules.recruitment.xgsResume.mapper.XgsResumeBaseMapper;
import org.jeecg.modules.recruitment.xgsResume.mapper.XgsResumeEdusMapper;
import org.jeecg.modules.recruitment.xgsResume.mapper.XgsResumeHomeMapper;
import org.jeecg.modules.recruitment.xgsResume.mapper.XgsResumeWorksMapper;
import org.jeecg.modules.recruitment.xgsResume.vo.XgsResumeBasePage;
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
    public Result<String> doPositionApply(XgsPositionApplyVO xgsPositionApplyVO) {
        Result<String> result = new Result<>();
        XgsPositionApply xgsPositionApply = xgsPositionApplyVO.getXgsPositionApply();
        if (xgsPositionApply != null && StringUtils.isNotEmpty(xgsPositionApply.getPositionId())) {
            xgsPositionApplyVO.setPositionId(xgsPositionApply.getPositionId());
        }
        Result<XgsPositionApplyVO> xgsPositionApplyVOResult = checkApplyByPosId(xgsPositionApplyVO);
        if (!xgsPositionApplyVOResult.isSuccess()) {
            result.setSuccess(false);
            result.setMessage(xgsPositionApplyVOResult.getMessage());
            result.setResult(xgsPositionApplyVOResult.getMessage());
            return result;
        }
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        XgsResumeBase xgsResumeBase = new XgsResumeBase();
        XgsResumeBasePage xgsResumeBasePage = xgsPositionApplyVO.getXgsResumeBasePage();
        BeanUtils.copyProperties(xgsResumeBasePage, xgsResumeBase);
        xgsResumeBaseMapper.insert(xgsResumeBase);
        List<XgsResumeWorks> xgsResumeWorksList = xgsResumeBasePage.getXgsResumeWorksList();
        if(xgsResumeWorksList!=null && xgsResumeWorksList.size()>0) {
            for(XgsResumeWorks entity:xgsResumeWorksList) {
                //外键设置
                entity.setBaseId(xgsResumeBase.getId());
                xgsResumeWorksMapper.insert(entity);
            }
        }
        List<XgsResumeEdus> xgsResumeEdusList = xgsResumeBasePage.getXgsResumeEdusList();
        if(xgsResumeEdusList!=null && xgsResumeEdusList.size()>0) {
            for(XgsResumeEdus entity:xgsResumeEdusList) {
                //外键设置
                entity.setBaseId(xgsResumeBase.getId());
                xgsResumeEdusMapper.insert(entity);
            }
        }
        List<XgsResumeHome> xgsResumeHomeList = xgsResumeBasePage.getXgsResumeHomeList();
        if(xgsResumeHomeList!=null && xgsResumeHomeList.size()>0) {
            for(XgsResumeHome entity:xgsResumeHomeList) {
                //外键设置
                entity.setBaseId(xgsResumeBase.getId());
                xgsResumeHomeMapper.insert(entity);
            }
        }

        // 处理4个新增的子表列表，将其转为JSON字符串保存
        // 工作主要业绩
        if(xgsResumeBasePage.getXgsResumeResearchResultList() != null && xgsResumeBasePage.getXgsResumeResearchResultList().size() > 0) {
            xgsResumeBase.setResearchResult(JSON.toJSONString(xgsResumeBasePage.getXgsResumeResearchResultList()));
        }
        // 应聘岗位陈述
        if(xgsResumeBasePage.getXgsResumePositionDescriptionList() != null && xgsResumeBasePage.getXgsResumePositionDescriptionList().size() > 0) {
            xgsResumeBase.setPositionDescription(JSON.toJSONString(xgsResumeBasePage.getXgsResumePositionDescriptionList()));
        }
        // 研究方向与专长
        if(xgsResumeBasePage.getXgsResumeResearchDirectionList() != null && xgsResumeBasePage.getXgsResumeResearchDirectionList().size() > 0) {
            xgsResumeBase.setResearchDirection(JSON.toJSONString(xgsResumeBasePage.getXgsResumeResearchDirectionList()));
        }
        // 论文专著专利
        if(xgsResumeBasePage.getXgsResumeResearchPaperList() != null && xgsResumeBasePage.getXgsResumeResearchPaperList().size() > 0) {
            xgsResumeBase.setResearchPaper(JSON.toJSONString(xgsResumeBasePage.getXgsResumeResearchPaperList()));
        }
        // 更新简历主表数据
        xgsResumeBaseMapper.updateById(xgsResumeBase);

        xgsPositionApply.setUserId(loginUser.getId());
        xgsPositionApply.setPositionId(xgsPositionApplyVO.getPositionId());
        xgsPositionApply.setResumeId(xgsResumeBase.getId());
        xgsPositionApply.setResumeName(xgsResumeBase.getResumeName());
//        xgsPositionApply.setPositionName(xgsResumeBase.getApplyPositionName());
//        xgsPositionApply.setPositionDept(xgsResumeBase.getApplyPositionDept());
//        xgsPositionApply.setPositionType(xgsResumeBase.getApplyPositionType());
        xgsPositionApply.setApprovalNode(IXgsFlowOpinionsService.NODE_HR_PENDING_REVIEW); // 提交到 待查看
        xgsPositionApply.setApprovalStatus(IXgsFlowOpinionsService.APPROVAL_STATUS_HR_PENDING_REVIEW); // 待人力处查看
        xgsPositionApply.setStatus(IXgsFlowOpinionsService.APPROVAL_STATUS_PENDING_REVIEW); // 用户看到的审核状态为待查看
        save(xgsPositionApply);
        // 增加流程记录
        XgsFlowOpinions flowOpinions = new XgsFlowOpinions();
        flowOpinions.setApprovalNode(IXgsFlowOpinionsService.NODE_HR_PENDING_REVIEW);
        flowOpinions.setApprovalStatus(IXgsFlowOpinionsService.APPROVAL_STATUS_HR_PENDING_REVIEW);
        flowOpinions.setOpinions(IXgsFlowOpinionsService.APPROVAL_STATUS_HR_PENDING_REVIEW);
        flowOpinionsMapper.insert(flowOpinions);
        result.setMessage("在线申请添加岗位信息成功！");
        result.setResult("在线申请添加岗位信息成功！");
        return result;
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

    @Override
    public Result<XgsPositionApplyVO> checkApplyByPosId(XgsPositionApplyVO xgsPositionApplyVO) {
        Result<XgsPositionApplyVO> result = new Result<>();
        if (StringUtils.isEmpty(xgsPositionApplyVO.getPositionId())) {
            result.setSuccess(false);
            result.setMessage("岗位ID不能为空");
            return result;
        }
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        QueryWrapper<XgsPositionApply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("create_by", sysUser.getUsername());
        queryWrapper.eq("position_id", xgsPositionApplyVO.getPositionId());
        long count = count(queryWrapper);
        if (count > 0) {
            result.setSuccess(false);
            result.setMessage("您已申请过该岗位");
            return result;
        }
        return result;
    }

    @Override
    public XgsPositionApplyVO getPositionApply(XgsPositionApplyVO xgsPositionApplyVO) {
        XgsPositionApply xgsPositionApply = xgsPositionApplyVO.getXgsPositionApply();
        if (xgsPositionApply == null || xgsPositionApply.getId() == null) {
            return null;
        }
        XgsPositionApply positionApply = getById(xgsPositionApply.getId());
        String resumeId = positionApply.getResumeId();
        XgsResumeBase xgsResumeBase = xgsResumeBaseMapper.selectById(positionApply.getResumeId());
        List<XgsResumeWorks> xgsResumeWorksList = xgsResumeWorksMapper.selectByMainId(resumeId);
        List<XgsResumeEdus> xgsResumeEdusList = xgsResumeEdusMapper.selectByMainId(resumeId);
        List<XgsResumeHome> xgsResumeHomeList = xgsResumeHomeMapper.selectByMainId(resumeId);

        // 返回简历信息
        XgsResumeBasePage xgsResumeBasePage = new XgsResumeBasePage();
        BeanUtils.copyProperties(xgsResumeBase, xgsResumeBasePage);
        xgsResumeBasePage.setXgsResumeEdusList(xgsResumeEdusList);
        xgsResumeBasePage.setXgsResumeWorksList(xgsResumeWorksList);
        xgsResumeBasePage.setXgsResumeHomeList(xgsResumeHomeList);

        // 将JSON字符串转换为列表，供前端显示
        if(StringUtils.isNotEmpty(xgsResumeBase.getResearchResult())) {
            xgsResumeBasePage.setXgsResumeResearchResultList(JSON.parseArray(xgsResumeBase.getResearchResult(), Object.class));
        }
        if(StringUtils.isNotEmpty(xgsResumeBase.getPositionDescription())) {
            xgsResumeBasePage.setXgsResumePositionDescriptionList(JSON.parseArray(xgsResumeBase.getPositionDescription(), Object.class));
        }
        if(StringUtils.isNotEmpty(xgsResumeBase.getResearchDirection())) {
            xgsResumeBasePage.setXgsResumeResearchDirectionList(JSON.parseArray(xgsResumeBase.getResearchDirection(), Object.class));
        }
        if(StringUtils.isNotEmpty(xgsResumeBase.getResearchPaper())) {
            xgsResumeBasePage.setXgsResumeResearchPaperList(JSON.parseArray(xgsResumeBase.getResearchPaper(), Object.class));
        }

        // 返回申请信息
        xgsPositionApplyVO.setXgsPositionApply(positionApply);
        xgsPositionApplyVO.setXgsResumeBasePage(xgsResumeBasePage);
        return xgsPositionApplyVO;
    }
}
