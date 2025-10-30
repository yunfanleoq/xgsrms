package org.jeecg.modules.recruitment.position.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.modules.recruitment.positions.entity.XgsPositionApply;
import org.jeecg.modules.recruitment.positions.service.IXgsPositionApplyService;
import org.jeecg.modules.recruitment.xgsResume.entity.XgsResumeBase;
import org.jeecg.modules.recruitment.xgsResume.service.IXgsResumeBaseService;
import org.jeecg.modules.recruitment.position.entity.XgsFlowOpinions;
import org.jeecg.modules.recruitment.position.service.IXgsFlowOpinionsService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: 审批办理过程表
 * @Author: Liuyl
 * @Date:   2025-01-09
 * @Version: V1.0
 */
@Api(tags="审批办理过程表")
@RestController
@RequestMapping("/resume/xgsFlowOpinions")
@Slf4j
public class XgsFlowOpinionsController extends JeecgController<XgsFlowOpinions, IXgsFlowOpinionsService> {
	 @Autowired
	 private IXgsResumeBaseService resumeBaseService;
	 @Autowired
	 private IXgsPositionApplyService xgsPositionApplyService;
	 @Autowired
	 private IXgsFlowOpinionsService xgsFlowOpinionsService;
	
	/**
	 * 分页列表查询
	 *
	 * @param xgsFlowOpinions
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "审批办理过程表-分页列表查询")
	@ApiOperation(value="审批办理过程表-分页列表查询", notes="审批办理过程表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<XgsFlowOpinions>> queryPageList(XgsFlowOpinions xgsFlowOpinions,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		String positionApplyId = req.getParameter("positionApplyId");
		log.info("审批办理过程表-分页列表查询 查询参数 positionApplyId: {}", positionApplyId);
		
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("approvalNode", QueryRuleEnum.LIKE_WITH_OR);
        customeRuleMap.put("approvalStatus", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<XgsFlowOpinions> queryWrapper = QueryGenerator.initQueryWrapper(xgsFlowOpinions, req.getParameterMap(),customeRuleMap);
		Page<XgsFlowOpinions> page = new Page<XgsFlowOpinions>(pageNo, pageSize);
		
		// 只有当positionApplyId不为空时才添加查询条件
		if (positionApplyId != null && !positionApplyId.isEmpty()) {
			queryWrapper.eq("parent_id", positionApplyId);
		} else {
			// 如果没有传递positionApplyId，返回空结果
			log.warn("审批办理过程表-分页列表查询：positionApplyId参数为空，返回空结果");
			return Result.OK(new Page<>(pageNo, pageSize));
		}
		
		IPage<XgsFlowOpinions> pageList = xgsFlowOpinionsService.page(page, queryWrapper);
		log.info("审批办理过程表-分页列表查询 查询结果数量: {}", pageList.getRecords().size());
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param xgsFlowOpinions
	 * @return
	 */
	@AutoLog(value = "审批办理过程表-添加")
	@ApiOperation(value="审批办理过程表-添加", notes="审批办理过程表-添加")
//	@RequiresPermissions("resume:xgs_flow_opinions:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody XgsFlowOpinions xgsFlowOpinions) {
		String resultMessage = xgsFlowOpinionsService.add(xgsFlowOpinions);
		return Result.OK(resultMessage);
	}
	
	/**
	 *  编辑
	 *
	 * @param xgsFlowOpinions
	 * @return
	 */
	@AutoLog(value = "审批办理过程表-编辑")
	@ApiOperation(value="审批办理过程表-编辑", notes="审批办理过程表-编辑")
//	@RequiresPermissions("resume:xgs_flow_opinions:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody XgsFlowOpinions xgsFlowOpinions) {
		xgsFlowOpinionsService.updateById(xgsFlowOpinions);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "审批办理过程表-通过id删除")
	@ApiOperation(value="审批办理过程表-通过id删除", notes="审批办理过程表-通过id删除")
	@RequiresPermissions("resume:xgs_flow_opinions:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		xgsFlowOpinionsService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "审批办理过程表-批量删除")
	@ApiOperation(value="审批办理过程表-批量删除", notes="审批办理过程表-批量删除")
	@RequiresPermissions("resume:xgs_flow_opinions:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.xgsFlowOpinionsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "审批办理过程表-通过id查询")
	@ApiOperation(value="审批办理过程表-通过id查询", notes="审批办理过程表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<XgsFlowOpinions> queryById(@RequestParam(name="id",required=true) String id) {
		XgsFlowOpinions xgsFlowOpinions = xgsFlowOpinionsService.getById(id);
		if(xgsFlowOpinions==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(xgsFlowOpinions);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param xgsFlowOpinions
    */
    @RequiresPermissions("resume:xgs_flow_opinions:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, XgsFlowOpinions xgsFlowOpinions) {
        return super.exportXls(request, xgsFlowOpinions, XgsFlowOpinions.class, "审批办理过程表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("resume:xgs_flow_opinions:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, XgsFlowOpinions.class);
    }

	 /**
	  * 通过id查询简历数据
	  *
	  * @param id
	  * @return
	  */
	 //@AutoLog(value = "审批办理过程表-通过id查询")
	 @ApiOperation(value="审批办理过程-通过id查询简历数据", notes="审批办理过程-通过id查询简历数据")
	 @GetMapping(value = "/getResumeData")
	 public Result<XgsResumeBase> getResumeData(@RequestParam(name="id",required=true) String id) {
		 XgsResumeBase xgsResumeBase = resumeBaseService.getById(id);
		 if(xgsResumeBase==null) {
			 return Result.error("未找到对应数据");
		 }
		 return Result.OK(xgsResumeBase);
	 }

	 @ApiOperation(value="审批办理过程表-通过申请id查询", notes="审批办理过程表-通过申请id查询")
	 @GetMapping(value = "/getDepartRejectByApplyId")
	 public Result<XgsFlowOpinions> getDepartRejectByApplyId(@RequestParam(name="applyId", required=true) String applyId) {
		 QueryWrapper<XgsFlowOpinions> queryWrapper = new QueryWrapper<>();
		 XgsFlowOpinions xgsFlowOpinions = null;
		 Page<XgsFlowOpinions> page = new Page<XgsFlowOpinions>(1, 1);
		 queryWrapper.eq("parent_id", applyId);
		 queryWrapper.eq("approval_node", "部门审核");
//		 queryWrapper.eq("approval_status", "驳回");
		 queryWrapper.orderByDesc("create_time");
		 Page<XgsFlowOpinions> list = xgsFlowOpinionsService.page(page, queryWrapper);
		 if (list.getRecords().size() > 0) {
			 xgsFlowOpinions = list.getRecords().get(0);
		 }
		 if(xgsFlowOpinions==null) {
			 return Result.error(200, "未找到对应数据");
		 }
		 return Result.OK(xgsFlowOpinions);
	 }

	 @AutoLog(value = "审批办理过程表-申请人提交")
	 @ApiOperation(value="审批办理过程表-申请人提交", notes="审批办理过程表-申请人提交")
	 @RequestMapping(value = "/submitByApplyId")
	 public Result<String> submitByApplyId(@RequestParam(name="applyId", required=true) String applyId) {
//		 XgsFlowOpinions xgsFlowOpinions = new XgsFlowOpinions();
//		 xgsFlowOpinions.setParentId(applyId);
//		 xgsFlowOpinions.setApprovalNode(IXgsFlowOpinionsService.NODE_DEPT);
//		 xgsFlowOpinions.setApprovalStatus(IXgsFlowOpinionsService.APPROVAL_STATUS_DEPT_TODO);
//		 xgsFlowOpinionsService.save(xgsFlowOpinions);
		 XgsPositionApply xgsPositionApply = xgsPositionApplyService.getById(applyId);
		 xgsPositionApply.setApprovalNode(IXgsFlowOpinionsService.NODE_DEPT);
		 xgsPositionApply.setStatus(IXgsFlowOpinionsService.APPROVAL_STATUS_GOING);
		 xgsPositionApply.setApprovalStatus(IXgsFlowOpinionsService.APPROVAL_STATUS_DEPT_TODO);
		 xgsPositionApply.setApplyStatus(IXgsFlowOpinionsService.APPROVAL_STATUS_DEPT_TODO);
		 xgsPositionApplyService.updateById(xgsPositionApply);
		 return Result.OK("提交成功!");
	 }


}
