package org.jeecg.modules.recruitment.xgsInviteToInterview.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.demo.positions.entity.XgsPositions;
import org.jeecg.modules.demo.positions.service.IXgsPositionsService;
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
import org.springframework.beans.BeanUtils;
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
	 @Autowired
	 private IXgsPositionsService positionsService;
	 @Autowired
	 private ISysBaseAPI sysBaseAPI;

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
		pageList.convert(interview -> {
			XgsInviteToInterviewVO vo = new XgsInviteToInterviewVO();
			BeanUtils.copyProperties(interview, vo);
			String positionId = interview.getPositionId();
			XgsPositions position = positionsService.getById(positionId);
			if (position != null) {
				vo.setPositionName(position.getPositionName());
				vo.setPositionType(position.getCategory());
				if (position.getDept() != null) {
					List<JSONObject> jsonObjects = sysBaseAPI.queryDepartsByIds(position.getDept());
					if (jsonObjects.size() > 0) {
						JSONObject jsonObject = jsonObjects.get(0);
						vo.setPositionDept(jsonObject.getString("departName"));
					}
				}
			}
			return vo;
		});
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
		 pageList.convert(interview -> {
			 XgsInviteToInterviewVO vo = new XgsInviteToInterviewVO();
			 BeanUtils.copyProperties(interview, vo);
			 String positionId = interview.getPositionId();
			 XgsPositions position = positionsService.getById(positionId);
			 if (position != null) {
				 vo.setPositionName(position.getPositionName());
				 vo.setPositionType(position.getCategory());
				 if (position.getDept() != null) {
					 List<JSONObject> jsonObjects = sysBaseAPI.queryDepartsByIds(position.getDept());
					 if (jsonObjects.size() > 0) {
						 JSONObject jsonObject = jsonObjects.get(0);
						 vo.setPositionDept(jsonObject.getString("departName"));
					 }
				 }
			 }
			 return vo;
		 });
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
//		xgsInviteToInterviewService.removeById(id);
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
//		this.xgsInviteToInterviewService.removeByIds(Arrays.asList(ids.split(",")));
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
	  *   添加
	  *
	  * @param xgsInviteToInterview
	  * @return
	  */
	 @AutoLog(value = "面试邀请-已撤回邀请")
	 @ApiOperation(value="面试邀请-已撤回邀请", notes="面试邀请-已撤回邀请")
	 @RequiresPermissions("xgsInviteToInterview:xgs_invite_to_interview:add")
	 @PostMapping(value = "/cancelInvite")
	 public Result<String> cancelInvite(@RequestBody XgsInviteToInterview xgsInviteToInterview) {
		 XgsInviteToInterview invite = xgsInviteToInterviewService.getById(xgsInviteToInterview.getId());
		 invite.setInviteStatus("已撤回邀请");
		 xgsInviteToInterviewService.updateById(invite);
		 return Result.OK("已撤回邀请成功！");
	 }

	 /**
	  *   添加
	  *
	  * @param xgsInviteToInterview
	  * @return
	  */
	 @AutoLog(value = "面试邀请-面试通过")
	 @ApiOperation(value="面试邀请-面试通过", notes="面试邀请-面试通过")
	 @RequiresPermissions("xgsInviteToInterview:xgs_invite_to_interview:add")
	 @PostMapping(value = "/interviewPass")
	 public Result<String> interviewPass(@RequestBody XgsInviteToInterview xgsInviteToInterview) {
		 XgsInviteToInterview invite = xgsInviteToInterviewService.getById(xgsInviteToInterview.getId());
		 invite.setInterviewResult("面试通过");
		 xgsInviteToInterviewService.updateById(invite);
		 return Result.OK("面试通过操作成功！");
	 }

	 /**
	  *   添加
	  *
	  * @param xgsInviteToInterview
	  * @return
	  */
	 @AutoLog(value = "面试邀请-面试未通过")
	 @ApiOperation(value="面试邀请-面试未通过", notes="面试邀请-面试未通过")
	 @RequiresPermissions("xgsInviteToInterview:xgs_invite_to_interview:add")
	 @PostMapping(value = "/interviewFail")
	 public Result<String> interviewFail(@RequestBody XgsInviteToInterview xgsInviteToInterview) {
		 XgsInviteToInterview invite = xgsInviteToInterviewService.getById(xgsInviteToInterview.getId());
		 invite.setInterviewResult("面试未通过");
		 xgsInviteToInterviewService.updateById(invite);
		 return Result.OK("添加成功！");
	 }

	 /**
	  *   被面试人 接受邀请
	  *
	  * @param xgsInviteToInterview
	  * @return
	  */
	 @AutoLog(value = "面试邀请-接受邀请")
	 @ApiOperation(value="面试邀请-接受邀请", notes="面试邀请-接受邀请")
	 @RequiresPermissions("xgsInviteToInterview:xgs_invite_to_interview:add")
	 @PostMapping(value = "/invitePass")
	 public Result<String> invitePass(@RequestBody XgsInviteToInterview xgsInviteToInterview) {
		 XgsInviteToInterview invite = xgsInviteToInterviewService.getById(xgsInviteToInterview.getId());
		 invite.setInviteResult("接受邀请");
		 xgsInviteToInterviewService.updateById(invite);
		 return Result.OK("接受邀请操作成功！");
	 }

	 /**
	  *   拒绝邀请
	  *
	  * @param xgsInviteToInterview
	  * @return
	  */
	 @AutoLog(value = "面试邀请-拒绝邀请")
	 @ApiOperation(value="面试邀请-拒绝邀请", notes="面试邀请-拒绝邀请")
	 @RequiresPermissions("xgsInviteToInterview:xgs_invite_to_interview:add")
	 @PostMapping(value = "/inviteRefuse")
	 public Result<String> inviteRefuse(@RequestBody XgsInviteToInterview xgsInviteToInterview) {
		 XgsInviteToInterview invite = xgsInviteToInterviewService.getById(xgsInviteToInterview.getId());
		 invite.setInviteResult("拒绝邀请");
		 xgsInviteToInterviewService.updateById(invite);
		 return Result.OK("拒绝邀请操作成功！");
	 }

}
