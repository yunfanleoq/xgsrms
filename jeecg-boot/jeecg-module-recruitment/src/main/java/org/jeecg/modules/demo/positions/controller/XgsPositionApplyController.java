package org.jeecg.modules.demo.positions.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.positions.entity.XgsPositionApply;
import org.jeecg.modules.demo.positions.service.IXgsPositionApplyService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.demo.positions.vo.XgsPositionApplyVO;
import org.jeecg.modules.recruitment.position.service.IXgsFlowOpinionsService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
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
	@GetMapping(value = "/list")
	public Result<IPage<XgsPositionApply>> queryPageList(XgsPositionApply xgsPositionApply,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		String approvalNode = req.getParameter("approvalNode");
		String approvalStatus = req.getParameter("approvalStatusValue");
		xgsPositionApply.setApprovalNode(null);
		Page<XgsPositionApply> page = new Page<XgsPositionApply>(pageNo, pageSize);
		IPage<XgsPositionApply> pageList = null;
        if (IXgsFlowOpinionsService.NODE_DEPT.equals(approvalNode)) {
			QueryWrapper<XgsPositionApply> queryWrapper = QueryGenerator.initQueryWrapper(xgsPositionApply, req.getParameterMap());
			if ("1".equals(approvalStatus)) {
				queryWrapper.eq("approval_node", IXgsFlowOpinionsService.NODE_DEPT);
				queryWrapper.eq("approval_status", IXgsFlowOpinionsService.APPROVAL_STATUS_DEPT_TODO);
			} else if ("2".equals(approvalStatus)) {
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
        } else {
			QueryWrapper<XgsPositionApply> queryWrapper = QueryGenerator.initQueryWrapper(xgsPositionApply, req.getParameterMap());
			if ("1".equals(approvalStatus)) {
				queryWrapper.eq("approval_node", IXgsFlowOpinionsService.NODE_HR);
				queryWrapper.eq("approval_status", IXgsFlowOpinionsService.APPROVAL_STATUS_HR_TODO);
			} else if ("2".equals(approvalStatus)) {
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
		 xgsPositionApplyService.doPositionApply(xgsPositionApplyVO);
		 return Result.OK("在线申请添加岗位信息成功！");
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
