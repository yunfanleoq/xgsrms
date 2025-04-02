package org.jeecg.modules.recruitment.xgsInviteToInterview.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.recruitment.xgsInterview.entity.XgsInterview;
import org.jeecg.modules.recruitment.xgsInterview.service.IXgsInterviewService;
import org.jeecg.modules.recruitment.xgsInviteToInterview.entity.XgsInviteToInterview;
import org.jeecg.modules.recruitment.xgsInviteToInterview.service.IXgsInviteToInterviewService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.recruitment.xgsInviteToInterview.vo.XgsInviteToInterviewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: 面试邀请
 * @Author: Liuyl
 * @Date:   2025-01-06
 * @Version: V1.0
 */
@Api(tags="面试邀请")
@RestController
@RequestMapping("/xgsInviteToInterview/xgsInviteToInterview")
@Slf4j
public class XgsInviteToInterviewController extends JeecgController<XgsInviteToInterview, IXgsInviteToInterviewService> {
	@Autowired
	private IXgsInviteToInterviewService xgsInviteToInterviewService;
	@Autowired
	private IXgsInterviewService interviewService;
	
	/**
	 * 分页列表查询
	 *
	 * @param xgsInviteToInterview
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "面试邀请-分页列表查询")
	@ApiOperation(value="面试邀请-分页列表查询", notes="面试邀请-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<XgsInviteToInterview>> queryPageList(XgsInviteToInterview xgsInviteToInterview,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
//        customeRuleMap.put("status", QueryRuleEnum.LIKE_WITH_OR);
//        customeRuleMap.put("inviteStatus", QueryRuleEnum.LIKE_WITH_OR);
		xgsInviteToInterview.setCandidateId(sysUser.getId());
        QueryWrapper<XgsInviteToInterview> queryWrapper = QueryGenerator.initQueryWrapper(xgsInviteToInterview, req.getParameterMap(),customeRuleMap);
		Page<XgsInviteToInterview> page = new Page<XgsInviteToInterview>(pageNo, pageSize);
		IPage<XgsInviteToInterview> pageList = xgsInviteToInterviewService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	 /**
	  * 我的面试列表页面
	  * @param xgsInviteToInterview
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	 //@AutoLog(value = "面试邀请-我的面试列表页面")
	 @ApiOperation(value="面试邀请-我的面试列表页面", notes="面试邀请-我的面试列表页面")
	 @GetMapping(value = "/myInterviewList")
	 public Result<IPage<XgsInviteToInterview>> myInterviewList(XgsInviteToInterview xgsInviteToInterview,
															  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
															  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
															  HttpServletRequest req) {
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 // 自定义查询规则
		 Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
		 // 自定义多选的查询规则为：LIKE_WITH_OR
//        customeRuleMap.put("status", QueryRuleEnum.LIKE_WITH_OR);
//        customeRuleMap.put("inviteStatus", QueryRuleEnum.LIKE_WITH_OR);
		 xgsInviteToInterview.setCandidateId(sysUser.getId());
//		 xgsInviteToInterview.setStatus("待面试");
		 xgsInviteToInterview.setInviteStatus("已发送邀请");
		 QueryWrapper<XgsInviteToInterview> queryWrapper = QueryGenerator.initQueryWrapper(xgsInviteToInterview, req.getParameterMap(),customeRuleMap);
		 Page<XgsInviteToInterview> page = new Page<XgsInviteToInterview>(pageNo, pageSize);
		 IPage<XgsInviteToInterview> pageList = xgsInviteToInterviewService.page(page, queryWrapper);
		 return Result.OK(pageList);
	 }
	
	/**
	 *   添加
	 *
	 * @param xgsInviteToInterview
	 * @return
	 */
	@AutoLog(value = "面试邀请-添加")
	@ApiOperation(value="面试邀请-添加", notes="面试邀请-添加")
	@RequiresPermissions("xgsInviteToInterview:xgs_invite_to_interview:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody XgsInviteToInterview xgsInviteToInterview) {
		xgsInviteToInterviewService.save(xgsInviteToInterview);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param inviteToInterviewVO
	 * @return
	 */
	@AutoLog(value = "面试邀请-编辑")
	@ApiOperation(value="面试邀请-编辑", notes="面试邀请-编辑")
//	@RequiresPermissions("xgsInviteToInterview:xgs_invite_to_interview:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody XgsInviteToInterviewVO inviteToInterviewVO) {
		xgsInviteToInterviewService.addInterview(inviteToInterviewVO);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "面试邀请-通过id删除")
	@ApiOperation(value="面试邀请-通过id删除", notes="面试邀请-通过id删除")
	@RequiresPermissions("xgsInviteToInterview:xgs_invite_to_interview:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		xgsInviteToInterviewService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "面试邀请-批量删除")
	@ApiOperation(value="面试邀请-批量删除", notes="面试邀请-批量删除")
	@RequiresPermissions("xgsInviteToInterview:xgs_invite_to_interview:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.xgsInviteToInterviewService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "面试邀请-通过id查询")
	@ApiOperation(value="面试邀请-通过id查询", notes="面试邀请-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<XgsInviteToInterview> queryById(@RequestParam(name="id",required=true) String id) {
		XgsInviteToInterview xgsInviteToInterview = xgsInviteToInterviewService.getById(id);
		if(xgsInviteToInterview==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(xgsInviteToInterview);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param xgsInviteToInterview
    */
    @RequiresPermissions("xgsInviteToInterview:xgs_invite_to_interview:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, XgsInviteToInterview xgsInviteToInterview) {
        return super.exportXls(request, xgsInviteToInterview, XgsInviteToInterview.class, "面试邀请");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("xgsInviteToInterview:xgs_invite_to_interview:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, XgsInviteToInterview.class);
    }

}
