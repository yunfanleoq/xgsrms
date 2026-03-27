package org.jeecg.modules.recruitment.positions.controller;

import java.util.Arrays;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.recruitment.positions.entity.XgsPositionApply;
import org.jeecg.modules.recruitment.positions.service.IXgsPositionApplyService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.recruitment.positions.vo.XgsPositionApplyVO;
import org.jeecg.modules.recruitment.position.service.IXgsFlowOpinionsService;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: 岗位申请
 * @Author: jeecg-boot
 * @Date:   2024-12-24
 * @Version: V1.0
 */
@Api(tags="岗位申请")
@RestController
@RequestMapping("/positions/xgsPositionApply")
@Slf4j
public class XgsPositionApplyController extends JeecgController<XgsPositionApply, IXgsPositionApplyService> {
	@Autowired
	private IXgsPositionApplyService xgsPositionApplyService;

	 /**
	  * 分页列表查询 我的申请列表
	  *
	  * @param xgsPositionApply
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	 //@AutoLog(value = "岗位申请-分页列表查询")
	 @ApiOperation(value="岗位申请-分页列表查询", notes="岗位申请-分页列表查询")
	 @GetMapping(value = "/listMine")
	 public Result<IPage<XgsPositionApply>> listMine(XgsPositionApply xgsPositionApply,
														  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
														  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
														  HttpServletRequest req) {
		 String approvalNode = req.getParameter("approvalNode");
		 String approvalStatus = req.getParameter("approvalStatusValue");
		 //获取当前用户
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 xgsPositionApply.setApprovalNode(null);
		 Page<XgsPositionApply> page = new Page<XgsPositionApply>(pageNo, pageSize);
		 IPage<XgsPositionApply> pageList = null;
		 QueryWrapper<XgsPositionApply> queryWrapper = QueryGenerator.initQueryWrapper(xgsPositionApply, req.getParameterMap());
		 queryWrapper.eq("create_by", sysUser.getUsername());
		 pageList = xgsPositionApplyService.page(page, queryWrapper);
		 return Result.OK(pageList);
	 }

	/**
	 * 分页列表查询
	 *
	 * @param xgsPositionApply
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "岗位申请-分页列表查询")
	@ApiOperation(value="岗位申请-分页列表查询", notes="岗位申请-分页列表查询")
	@RequiresPermissions("positions:xgs_position_apply:list")
	@GetMapping(value = "/list")
	public Result<IPage<XgsPositionApply>> queryPageList(XgsPositionApply xgsPositionApply,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		String approvalNode = req.getParameter("approvalNode"); //
		String approvalStatus = req.getParameter("approvalStatusValue"); // 已审核，未审核
		
		// 添加调试日志
		log.info("查询岗位申请列表 - approvalNode: {}, approvalStatus: {}", approvalNode, approvalStatus);
		
		xgsPositionApply.setApprovalNode(null);
		Page<XgsPositionApply> page = new Page<>(pageNo, pageSize);
		IPage<XgsPositionApply> pageList = null;
		
        if (IXgsFlowOpinionsService.NODE_DEPT.equals(approvalNode)) { // 部门审核
			QueryWrapper<XgsPositionApply> queryWrapper = QueryGenerator.initQueryWrapper(xgsPositionApply, req.getParameterMap());
			if ("1".equals(approvalStatus)) { // 未审核
				queryWrapper.eq("approval_node", IXgsFlowOpinionsService.NODE_DEPT);
				queryWrapper.eq("approval_status", IXgsFlowOpinionsService.APPROVAL_STATUS_DEPT_TODO);
			} else if ("2".equals(approvalStatus)) { // 已审核
				queryWrapper.and(wrapper -> wrapper
								.eq("approval_node", IXgsFlowOpinionsService.NODE_USER) // 申请人
								.in("apply_status", IXgsFlowOpinionsService.APPROVAL_STATUS_SUBMIT, IXgsFlowOpinionsService.APPROVAL_STATUS_DEPT_NOT_PASS))
						.or(wrapper -> wrapper
								.eq("approval_node", IXgsFlowOpinionsService.NODE_DEPT) // 部门审核
								.in("apply_status",IXgsFlowOpinionsService.APPROVAL_STATUS_DEPT_PASS, IXgsFlowOpinionsService.APPROVAL_STATUS_DEPT_NOT_PASS))
						.or(wrapper -> wrapper
								.eq("approval_node", IXgsFlowOpinionsService.NODE_HR) // HR 审核
								.in("apply_status",IXgsFlowOpinionsService.APPROVAL_STATUS_HR_TODO,IXgsFlowOpinionsService.APPROVAL_STATUS_DEPT_PASS, IXgsFlowOpinionsService.APPROVAL_STATUS_DEPT_NOT_PASS))
						.or(wrapper -> wrapper
								.eq("approval_node", IXgsFlowOpinionsService.NODE_END) // 初审通过
								.in("apply_status",IXgsFlowOpinionsService.APPROVAL_STATUS_HR_PASS));

			} else {
				queryWrapper.eq("approval_status", "-1");
			}
			pageList = xgsPositionApplyService.page(page, queryWrapper);
			log.info("部门审核查询结果 - 总数: {}", pageList != null ? pageList.getTotal() : 0);
        }
		else if (IXgsFlowOpinionsService.NODE_HR.equals(approvalNode)) { // HR 审核
			QueryWrapper<XgsPositionApply> queryWrapper = QueryGenerator.initQueryWrapper(xgsPositionApply, req.getParameterMap());
			if ("1".equals(approvalStatus)) { // 未审核
				queryWrapper.eq("approval_node", IXgsFlowOpinionsService.NODE_HR);
				queryWrapper.eq("approval_status", IXgsFlowOpinionsService.APPROVAL_STATUS_HR_TODO);
			} else if ("2".equals(approvalStatus)) { // 已审核
				queryWrapper.in("approval_node",
						IXgsFlowOpinionsService.NODE_USER,
						IXgsFlowOpinionsService.NODE_DEPT,
						IXgsFlowOpinionsService.NODE_HR,
						IXgsFlowOpinionsService.NODE_END
						);
				queryWrapper.in("approval_status",
						IXgsFlowOpinionsService.APPROVAL_STATUS_DEPT_PASS,
						IXgsFlowOpinionsService.APPROVAL_STATUS_DEPT_NOT_PASS,
						IXgsFlowOpinionsService.APPROVAL_STATUS_HR_TODO,
						IXgsFlowOpinionsService.APPROVAL_STATUS_HR_PASS,
						IXgsFlowOpinionsService.APPROVAL_STATUS_HR_NOT_PASS
				);
			} else {
				queryWrapper.eq("approval_status", "-1");
			}
			pageList = xgsPositionApplyService.page(page, queryWrapper);
			log.info("HR审核查询结果 - 总数: {}", pageList != null ? pageList.getTotal() : 0);
		}
		// 处理"待人力处查看"节点
		else if (IXgsFlowOpinionsService.NODE_HR_PENDING_REVIEW.equals(approvalNode)) {
			QueryWrapper<XgsPositionApply> queryWrapper = QueryGenerator.initQueryWrapper(xgsPositionApply, req.getParameterMap());
			if ("1".equals(approvalStatus)) { // 待人力处查看
				// 查询审批环节为"待人力处查看"的记录
				queryWrapper.eq("approval_node", IXgsFlowOpinionsService.NODE_HR_PENDING_REVIEW);
				log.info("查询待人力处查看的记录");
			} else if ("2".equals(approvalStatus)) { // 已查看（人力处已处理过的）
				// 查询人力处已处理过的记录，包括进入部门审核和退回申请人的
				queryWrapper.and(wrapper -> wrapper
								.eq("approval_node", IXgsFlowOpinionsService.NODE_DEPT) // 进入部门审核
								.or()
								.eq("approval_node", IXgsFlowOpinionsService.NODE_USER)) // 退回申请人
						.and(wrapper -> wrapper
								.ne("approval_node", IXgsFlowOpinionsService.NODE_HR_PENDING_REVIEW)); // 排除待人力处查看状态
				log.info("查询人力处已处理的记录");
			} else {
				queryWrapper.eq("approval_status", "-1");
			}
			pageList = xgsPositionApplyService.page(page, queryWrapper);
			log.info("待人力处查看查询结果 - 总数: {}", pageList != null ? pageList.getTotal() : 0);
		}
		else {
			// 如果没有匹配的审批环节，记录警告日志
			log.warn("未识别的审批环节: {}", approvalNode);
		}
		
		// 如果查询结果为空，记录日志
		if (pageList == null || pageList.getTotal() == 0) {
			log.info("查询结果为空 - approvalNode: {}, approvalStatus: {}", approvalNode, approvalStatus);
		}
		
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param xgsPositionApply
	 * @return
	 */
	@AutoLog(value = "岗位申请-添加")
	@ApiOperation(value="岗位申请-添加", notes="岗位申请-添加")
//	@RequiresPermissions("positions:xgs_position_apply:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody XgsPositionApply xgsPositionApply) {
		xgsPositionApplyService.save(xgsPositionApply);
		return Result.OK("添加成功！");
	}

	 /**
	  *   在线申请添加岗位信息
	  *
	  * @param xgsPositionApplyVO
	  * @return
	  */
	 @AutoLog(value = "岗位申请-在线申请添加岗位信息")
	 @ApiOperation(value="岗位申请-在线申请添加岗位信息", notes="岗位申请-在线申请添加岗位信息")
	 @PostMapping(value = "/doPositionApply")
	 public Result<String> doPositionApply(@RequestBody XgsPositionApplyVO xgsPositionApplyVO) {
		 return xgsPositionApplyService.doPositionApply(xgsPositionApplyVO);
//		 return Result.OK("在线申请添加岗位信息成功！");
	 }

	 /**
	  *   在线申请添加岗位信息
	  *
	  * @param xgsPositionApplyVO
	  * @return
	  */
	 @AutoLog(value = "岗位申请-获取岗位信息")
	 @ApiOperation(value="岗位申请-获取岗位信息", notes="岗位申请-获取岗位信息")
	 @PostMapping(value = "/getPositionApply")
	 public Result<XgsPositionApplyVO> getPositionApply(@RequestBody XgsPositionApplyVO xgsPositionApplyVO) {
		 XgsPositionApplyVO resumtInfo = xgsPositionApplyService.getPositionApply(xgsPositionApplyVO);
		 return Result.OK("获取岗位信息成功！", resumtInfo);
	 }

	 /**
	  *   在线申请添加岗位信息 检查岗位是否已申请过了
	  *
	  * @param xgsPositionApplyVO
	  * @return
	  */
	 @AutoLog(value = "岗位申请-检查岗位是否已申请过了")
	 @ApiOperation(value="岗位申请-检查岗位是否已申请过了", notes="岗位申请-检查岗位是否已申请过了")
	 @PostMapping(value = "/checkHasApplied")
	 public Result<Boolean> checkHasApplied(@RequestBody XgsPositionApplyVO xgsPositionApplyVO) {
		 Boolean result = xgsPositionApplyService.checkHasApplied(xgsPositionApplyVO);
		 return Result.OK(result);
	 }

	 @AutoLog(value = "岗位申请-根据岗位ID检查岗位是否已申请过了")
	 @ApiOperation(value="岗位申请-根据岗位ID检查岗位是否已申请过了", notes="岗位申请-根据岗位ID检查岗位是否已申请过了")
	 @PostMapping(value = "/checkApplyByPosId")
	 public Result<XgsPositionApplyVO> checkApplyByPosId(@RequestBody XgsPositionApplyVO xgsPositionApplyVO) {
		 return xgsPositionApplyService.checkApplyByPosId(xgsPositionApplyVO);
	 }

	/**
	 *  编辑
	 *
	 * @param xgsPositionApply
	 * @return
	 */
	@AutoLog(value = "岗位申请-编辑")
	@ApiOperation(value="岗位申请-编辑", notes="岗位申请-编辑")
	@RequiresPermissions("positions:xgs_position_apply:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody XgsPositionApply xgsPositionApply) {
		xgsPositionApplyService.updateById(xgsPositionApply);
		return Result.OK("编辑成功!");
	}


	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "岗位申请-通过id删除")
	@ApiOperation(value="岗位申请-通过id删除", notes="岗位申请-通过id删除")
	@RequiresPermissions("positions:xgs_position_apply:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		xgsPositionApplyService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "岗位申请-批量删除")
	@ApiOperation(value="岗位申请-批量删除", notes="岗位申请-批量删除")
	@RequiresPermissions("positions:xgs_position_apply:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.xgsPositionApplyService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "岗位申请-通过id查询")
	@ApiOperation(value="岗位申请-通过id查询", notes="岗位申请-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<XgsPositionApply> queryById(@RequestParam(name="id",required=true) String id) {
		XgsPositionApply xgsPositionApply = xgsPositionApplyService.getById(id);
		if(xgsPositionApply==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(xgsPositionApply);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param xgsPositionApply
    */
    @RequiresPermissions("positions:xgs_position_apply:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, XgsPositionApply xgsPositionApply) {
        return super.exportXls(request, xgsPositionApply, XgsPositionApply.class, "岗位申请");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("positions:xgs_position_apply:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, XgsPositionApply.class);
    }

}
