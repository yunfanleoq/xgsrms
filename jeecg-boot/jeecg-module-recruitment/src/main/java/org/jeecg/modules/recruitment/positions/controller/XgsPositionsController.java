package org.jeecg.modules.recruitment.positions.controller;

import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.jeecg.modules.recruitment.positions.entity.XgsFirstHtml;
import org.jeecg.modules.recruitment.positions.entity.XgsPositionApply;
import org.jeecg.modules.recruitment.positions.entity.XgsPositions;
import org.jeecg.modules.recruitment.positions.service.IXgsPositionApplyService;
import org.jeecg.modules.recruitment.positions.service.IXgsPositionsService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.recruitment.xgsInviteToInterview.entity.XgsInviteToInterview;
import org.jeecg.modules.recruitment.xgsInviteToInterview.service.IXgsInviteToInterviewService;
import org.jeecg.modules.recruitment.xgsMyresume.entity.XgsMyresume;
import org.jeecg.modules.recruitment.xgsMyresume.service.IXgsMyresumeService;
import org.jeecg.modules.recruitment.xgsResume.entity.XgsResumeBase;
import org.jeecg.modules.recruitment.xgsResume.service.IXgsResumeBaseService;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: 招聘岗位列表
 * @Author: jeecg-boot
 * @Date:   2024-12-20
 * @Version: V1.0
 */
@Api(tags="招聘岗位列表")
@RestController
@RequestMapping("/positions/xgsPositions")
@Slf4j
public class XgsPositionsController extends JeecgController<XgsPositions, IXgsPositionsService> {
	@Autowired
	private IXgsPositionsService xgsPositionsService;

	@Autowired
	private IXgsPositionApplyService xgsPositionApplyService;

	@Autowired
	private IXgsInviteToInterviewService xgsInviteToInterviewService;

	@Autowired
	private IXgsResumeBaseService xgsResumeBaseService;

	 @Autowired
	 private IXgsMyresumeService xgsMyresumeService;
	 
	 @Autowired
	 private ISysBaseAPI sysBaseAPI;
	
	/**
	 * 分页列表查询
	 *
	 * @param xgsPositions
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "招聘岗位列表-分页列表查询")
	@ApiOperation(value="招聘岗位列表-分页列表查询", notes="招聘岗位列表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<XgsPositions>> queryPageList(XgsPositions xgsPositions,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<XgsPositions> queryWrapper = QueryGenerator.initQueryWrapper(xgsPositions, req.getParameterMap());
        
        // 添加关键词搜索：同时搜索职位名称和部门名称
        String keyword = req.getParameter("keyword");
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 先根据关键词从部门表查询匹配的部门ID列表
            List<SysDepartModel> allDepts = sysBaseAPI.getAllSysDepart();
            List<String> matchedDeptIds = allDepts.stream()
                .filter(dept -> dept.getDepartName() != null && dept.getDepartName().contains(keyword))
                .map(SysDepartModel::getId)
                .collect(Collectors.toList());
            
            // 构建查询条件：职位名称 LIKE keyword OR 部门ID IN (匹配的部门ID列表)
            queryWrapper.and(wrapper -> {
                wrapper.like("position_name", keyword);
                if (!matchedDeptIds.isEmpty()) {
                    wrapper.or().in("dept", matchedDeptIds);
                }
            });
        }
        
        // 过滤掉已删除的记录
        queryWrapper.isNull("deleted");
		Page<XgsPositions> page = new Page<XgsPositions>(pageNo, pageSize);
		IPage<XgsPositions> pageList = xgsPositionsService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 * 获取有招聘岗位的部门列表
	 *
	 * @param req
	 * @return
	 */
	@ApiOperation(value="获取有招聘岗位的部门列表", notes="获取有招聘岗位的部门列表")
	@GetMapping(value = "/getDeptList")
	public Result<List<Map<String, Object>>> getDeptList(HttpServletRequest req) {
		QueryWrapper<XgsPositions> queryWrapper = new QueryWrapper<>();
		
		// 只查询招聘中的岗位
		queryWrapper.eq("status", "招聘中");
		
		// 过滤已删除的记录
		queryWrapper.isNull("deleted");
		
		// 使用 select distinct 去重获取部门ID
		queryWrapper.select("DISTINCT dept");
		queryWrapper.isNotNull("dept");

		// 查询有招聘岗位的部门ID列表
		List<XgsPositions> list = xgsPositionsService.list(queryWrapper);
		
		// 提取所有部门ID并去重
		List<String> deptIds = list.stream()
			.map(XgsPositions::getDept)
			.filter(deptId -> deptId != null && !deptId.trim().isEmpty())
			.distinct()
			.collect(Collectors.toList());

		// 如果没有部门ID，返回空列表
		if (deptIds.isEmpty()) {
			return Result.OK(new ArrayList<>());
		}

		// 获取所有部门信息
		List<SysDepartModel> sysDeparts = sysBaseAPI.getAllSysDepart();
		
		// 创建部门ID到部门对象的映射，便于快速查找
		Map<String, SysDepartModel> departMap = sysDeparts.stream()
			.collect(Collectors.toMap(SysDepartModel::getId, dept -> dept, (d1, d2) -> d1));
		
		// 遍历部门ID列表，结合sysDeparts获取部门名称
		List<Map<String, Object>> deptList = deptIds.stream()
			.map(deptId -> {
				Map<String, Object> map = new HashMap<>();
				SysDepartModel depart = departMap.get(deptId);
				if (depart != null) {
					// 在系统部门表中找到了对应的部门信息
					map.put("id", depart.getId());
					map.put("departName", depart.getDepartName());
				} else {
					// 找不到部门信息，使用dept作为id和departName
					map.put("id", deptId);
					map.put("departName", deptId);
				}
				return map;
			})
			.collect(Collectors.toList());
		
		return Result.OK(deptList);
	}
	
	/**
	 *   添加
	 *
	 * @param xgsPositions
	 * @return
	 */
	@AutoLog(value = "招聘岗位列表-添加")
	@ApiOperation(value="招聘岗位列表-添加", notes="招聘岗位列表-添加")
	@RequiresPermissions("positions:xgs_positions:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody XgsPositions xgsPositions) {
		xgsPositionsService.save(xgsPositions);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param xgsPositions
	 * @return
	 */
	@AutoLog(value = "招聘岗位列表-编辑")
	@ApiOperation(value="招聘岗位列表-编辑", notes="招聘岗位列表-编辑")
	@RequiresPermissions("positions:xgs_positions:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody XgsPositions xgsPositions) {
		xgsPositionsService.updateById(xgsPositions);
		return Result.OK("编辑成功!");
	}

	 /**
	  *  申请
	  *
	  * @param xgsPositions
	  * @return
	  */
	 @AutoLog(value = "招聘岗位列表-申请")
	 @ApiOperation(value="招聘岗位列表-申请", notes="招聘岗位列表-申请")
	 @RequiresPermissions("positions:xgs_positions:edit")
	 @RequestMapping(value = "/apply", method = {RequestMethod.PUT,RequestMethod.POST})
	 public Result<String> apply(@RequestBody XgsPositions xgsPositions) {
		 xgsPositionsService.updateById(xgsPositions);
		 return Result.OK("申请成功!");
	 }
	 /**
	  *  审核
	  *
	  * @param xgsPositions
	  * @return
	  */
	 @AutoLog(value = "招聘岗位列表-审核")
	 @ApiOperation(value="招聘岗位列表-审核", notes="招聘岗位列表-审核")
	 @RequiresPermissions("positions:xgs_positions:edit")
	 @RequestMapping(value = "/check", method = {RequestMethod.PUT,RequestMethod.POST})
	 public Result<String> check(@RequestBody XgsPositions xgsPositions) {
		 xgsPositionsService.updateById(xgsPositions);
		 return Result.OK("审核成功!");
	 }

	 /**
	  *  发布
	  *
	  * @param xgsPositions
	  * @return
	  */
	 @AutoLog(value = "招聘岗位列表-发布")
	 @ApiOperation(value="招聘岗位列表-发布", notes="招聘岗位列表-发布")
	 @RequiresPermissions("positions:xgs_positions:edit")
	 @RequestMapping(value = "/post", method = {RequestMethod.PUT,RequestMethod.POST})
	 public Result<String> post(@RequestBody XgsPositions xgsPositions) {
		 xgsPositionsService.updateById(xgsPositions);
		 return Result.OK("发布成功!");
	 }
	/**
	 *   通过id删除（逻辑删除）- 只允许删除草稿状态的岗位
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "招聘岗位列表-通过id删除")
	@ApiOperation(value="招聘岗位列表-通过id删除", notes="招聘岗位列表-通过id删除")
	@RequiresPermissions("positions:xgs_positions:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		// 逻辑删除实现
		XgsPositions xgsPositions = xgsPositionsService.getById(id);
		if(xgsPositions != null) {
			// 只允许删除草稿状态的岗位
			if(!"草稿".equals(xgsPositions.getStatus())) {
				return Result.error("只能删除草稿状态的岗位!");
			}
			
			xgsPositions.setDeleted("Y");
			xgsPositionsService.updateById(xgsPositions);
			return Result.OK("删除成功!");
		}
		return Result.error("未找到对应岗位!");
	}
	
	/**
	 *  批量删除（逻辑删除）- 只删除草稿状态的岗位
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "招聘岗位列表-批量删除")
	@ApiOperation(value="招聘岗位列表-批量删除", notes="招聘岗位列表-批量删除")
	@RequiresPermissions("positions:xgs_positions:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		// 逻辑删除实现 - 只删除草稿状态的岗位
		String[] idArray = ids.split(",");
		int successCount = 0;
		int totalCount = idArray.length;
		
		for(String id : idArray) {
			XgsPositions xgsPositions = xgsPositionsService.getById(id);
			// 只删除草稿状态的岗位
			if(xgsPositions != null && "草稿".equals(xgsPositions.getStatus())) {
				xgsPositions.setDeleted("Y");
				xgsPositionsService.updateById(xgsPositions);
				successCount++;
			}
		}
		
		if(successCount == 0) {
			return Result.error("没有可删除的草稿状态岗位!");
		} else if(successCount < totalCount) {
			return Result.OK("部分删除成功! 只有草稿状态的岗位被删除，共删除 " + successCount + " 条记录");
		} else {
			return Result.OK("批量删除成功!");
		}
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "招聘岗位列表-通过id查询")
	@ApiOperation(value="招聘岗位列表-通过id查询", notes="招聘岗位列表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<XgsPositions> queryById(@RequestParam(name="id",required=true) String id) {
		XgsPositions xgsPositions = xgsPositionsService.getById(id);
		if(xgsPositions==null || "Y".equals(xgsPositions.getDeleted())) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(xgsPositions);
	}

	 @ApiOperation(value="招聘岗位列表-通过id查询", notes="招聘岗位列表-通过id查询")
	 @GetMapping(value = "/getPageById")
	 public Result<IPage<XgsPositions>> getPageById(@RequestParam(name="id",required=true) String id) {
		 IPage<XgsPositions> page =  new Page<>(1,1);
		 XgsPositions xgsPositions = xgsPositionsService.getById(id);
		 if(xgsPositions==null || "Y".equals(xgsPositions.getDeleted())) {
			 return Result.error("未找到对应数据");
		 }
		 page.setRecords(Collections.singletonList(xgsPositions));
		 return Result.OK(page);
	 }

    /**
    * 导出excel
    *
    * @param request
    * @param xgsPositions
    */
    @RequiresPermissions("positions:xgs_positions:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, XgsPositions xgsPositions) {
        // 获取当前登录用户
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String roleCode = loginUser.getRoleCode();
        
        // 只有 admin 和 depart_position_manager 角色可以导出全部数据
        if (!"admin".equals(roleCode) && !"depart_position_manager".equals(roleCode)) {
            // 其他角色只能导出自己创建的数据
            xgsPositions.setCreateBy(loginUser.getUsername());
        }
        
        // 设置排除已删除的记录
        xgsPositions.setDeleted("N");
        
        return super.exportXls(request, xgsPositions, XgsPositions.class, "招聘岗位列表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("positions:xgs_positions:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, XgsPositions.class);
    }

	 /**
	  *   获取首页头部信息
	  *
	  * @param
	  * @return
	  */
	 @AutoLog(value = "获取首页头部信息")
	 @ApiOperation(value="获取首页头部信息", notes="获取首页头部信息")
	 @PostMapping(value = "/first_text")
	 public Result<XgsFirstHtml> firstText(String identity,
									 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
									 HttpServletRequest req) {
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 if(sysUser.getRoleCode().equals("admin")){
			 //岗位数量（所有招聘中的岗位数量）
			 XgsPositions xgsPositions = new XgsPositions();
			 xgsPositions.setStatus("招聘中");
			 QueryWrapper<XgsPositions> queryWrapper = QueryGenerator.initQueryWrapper(xgsPositions, req.getParameterMap());
			 // 过滤掉已删除的记录
			 queryWrapper.isNull("deleted");
			 Page<XgsPositions> page = new Page<XgsPositions>(pageNo, pageSize);
			 IPage<XgsPositions> positionsList = xgsPositionsService.page(page, queryWrapper);

			 //招聘数量（所有需要招聘完成（面试通过）的数量）
			 XgsInviteToInterview xgsInviteToInterview = new XgsInviteToInterview();
			 xgsInviteToInterview.setInviteStatus("已通过");
			 QueryWrapper<XgsInviteToInterview> queryWrapperXgsInviteToInterview = QueryGenerator.initQueryWrapper(xgsInviteToInterview, req.getParameterMap());
			 Page<XgsInviteToInterview> pageXgsInviteToInterview = new Page<XgsInviteToInterview>(pageNo, pageSize);
			 IPage<XgsInviteToInterview> pageList = xgsInviteToInterviewService.page(pageXgsInviteToInterview, queryWrapperXgsInviteToInterview);

			 //审核数量（所有需要审核的简历数量）
			 XgsPositionApply xgsPositionApply = new XgsPositionApply();
			 xgsPositionApply.setApplyStatus("审核中");
			 QueryWrapper<XgsPositionApply> queryWrapperPositionApply = QueryGenerator.initQueryWrapper(xgsPositionApply, req.getParameterMap());
			 Page<XgsPositionApply> pagePositionApply = new Page<XgsPositionApply>(pageNo, pageSize);
			 IPage<XgsPositionApply> pageListXgsPositionApply = xgsPositionApplyService.page(pagePositionApply, queryWrapperPositionApply);

			 //简历数量（所有需要简历的数量）
			 XgsResumeBase xgsResumeBase = new XgsResumeBase();
			 QueryWrapper<XgsResumeBase> queryWrapperXgsResumeBase = QueryGenerator.initQueryWrapper(xgsResumeBase, req.getParameterMap());
			 Page<XgsResumeBase> pageXgsResumeBase = new Page<XgsResumeBase>(pageNo, pageSize);
			 IPage<XgsResumeBase> pageListXgsResumeBase = xgsResumeBaseService.page(pageXgsResumeBase, queryWrapperXgsResumeBase);

			 XgsFirstHtml xgsFirstHtml = new XgsFirstHtml((int) positionsList.getTotal(), (int) pageList.getTotal(), (int) pageListXgsPositionApply.getTotal(), (int) pageListXgsResumeBase.getTotal());

			 return Result.OK(xgsFirstHtml);

		 }else {
			 //获取id
			 String userId = sysUser.getId();

			 //申请数量（提交过的申请数量）
			 XgsPositionApply xgsPositionApply = new XgsPositionApply();
			 xgsPositionApply.setCreateBy(sysUser.getUsername());
			 QueryWrapper<XgsPositionApply> queryWrapperPositionApply = QueryGenerator.initQueryWrapper(xgsPositionApply, req.getParameterMap());
			 Page<XgsPositionApply> pagePositionApply = new Page<XgsPositionApply>(pageNo, pageSize);
			 IPage<XgsPositionApply> pageListXgsPositionApply = xgsPositionApplyService.page(pagePositionApply, queryWrapperPositionApply);

			 //岗位数量（等待面试的数量）
			 xgsPositionApply = new XgsPositionApply();
			 xgsPositionApply.setCreateBy(sysUser.getUsername());
			 xgsPositionApply.setApprovalNode("初审完成");
			 queryWrapperPositionApply = QueryGenerator.initQueryWrapper(xgsPositionApply, req.getParameterMap());
			 pagePositionApply = new Page<>(pageNo, pageSize);
			 IPage<XgsPositionApply> pageListXgsPositionApplyOK = xgsPositionApplyService.page(pagePositionApply, queryWrapperPositionApply);

			 // 面试的数量
			 XgsInviteToInterview xgsInviteToInterview = new XgsInviteToInterview();
			 xgsInviteToInterview.setCandidate(sysUser.getId());
			 QueryWrapper<XgsInviteToInterview> queryWrapper = QueryGenerator.initQueryWrapper(xgsInviteToInterview, req.getParameterMap());
			 Page<XgsInviteToInterview> page = new Page<>(pageNo, pageSize);
			 IPage<XgsInviteToInterview> inviteToInterviewIPage = xgsInviteToInterviewService.page(page, queryWrapper);

			 //提交审核数量（审核中的申请数量）
			 xgsPositionApply = new XgsPositionApply();
			 xgsPositionApply.setCreateBy(sysUser.getUsername());
			 xgsPositionApply.setApplyStatus("审核中");
			 queryWrapperPositionApply = QueryGenerator.initQueryWrapper(xgsPositionApply, req.getParameterMap());
			 pagePositionApply = new Page<>(pageNo, pageSize);
			 IPage<XgsPositionApply> pageListXgsPositionApplyWaiting = xgsPositionApplyService.page(pagePositionApply, queryWrapperPositionApply);

			 //我的简历数量（简历数量）
			 XgsMyresume xgsMyresume = new XgsMyresume();
			 xgsMyresume.setCreateBy(sysUser.getUsername());
			 QueryWrapper<XgsMyresume> queryWrapperMyresume = QueryGenerator.initQueryWrapper(xgsMyresume, req.getParameterMap());
			 Page<XgsMyresume> pageMyresume = new Page<XgsMyresume>(pageNo, pageSize);
			 IPage<XgsMyresume> pageListXgsMyresume = xgsMyresumeService.page(pageMyresume, queryWrapperMyresume);

			 XgsFirstHtml xgsFirstHtml = new XgsFirstHtml((int) pageListXgsPositionApply.getTotal(), (int) inviteToInterviewIPage.getTotal(), (int) pageListXgsPositionApplyWaiting.getTotal(), (int) pageListXgsMyresume.getTotal());

			 return Result.OK(xgsFirstHtml);
		 }
	 }
}
