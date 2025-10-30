package org.jeecg.modules.recruitment.positions.controller;

import java.util.Arrays;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.config.shiro.IgnoreAuth;
import org.jeecg.modules.recruitment.positions.entity.XgsFavoriteJob;
import org.jeecg.modules.recruitment.positions.entity.XgsPositionApply;
import org.jeecg.modules.recruitment.positions.service.IXgsFavoriteJobService;
import org.jeecg.modules.recruitment.positions.service.IXgsPositionApplyService;

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
 * @Description: 职位收藏
 * @Author: jeecg-boot
 * @Date:   2024-12-23
 * @Version: V1.0
 */
@Api(tags="职位收藏")
@RestController
@RequestMapping("/positions/xgsFavoriteJob")
@Slf4j
public class XgsFavoriteJobController extends JeecgController<XgsFavoriteJob, IXgsFavoriteJobService> {
	@Autowired
	private IXgsFavoriteJobService xgsFavoriteJobService;

	@Autowired
	private IXgsPositionApplyService xgsPositionApplyService;


	 /**
	  * 分页列表查询 我的申请列表
	  *
	  * @param xgsFavoriteJob
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	 //@AutoLog(value = "岗位申请-分页列表查询")
	 @ApiOperation(value="岗位申请-分页列表查询", notes="岗位申请-分页列表查询")
	 @GetMapping(value = "/listMine")
	 public Result<IPage<XgsFavoriteJob>> listMine(XgsFavoriteJob xgsFavoriteJob,
												  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
												  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
												  HttpServletRequest req) {
		 //获取当前用户
		 LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		 Page<XgsFavoriteJob> page = new Page<XgsFavoriteJob>(pageNo, pageSize);
		 IPage<XgsFavoriteJob> pageList = null;
		 QueryWrapper<XgsFavoriteJob> queryWrapper = QueryGenerator.initQueryWrapper(xgsFavoriteJob, req.getParameterMap());
		 queryWrapper.eq("create_by", sysUser.getUsername());
		 pageList = xgsFavoriteJobService.page(page, queryWrapper);
		 
		 // 为每个收藏的岗位检查是否已申请
		 List<XgsFavoriteJob> records = pageList.getRecords();
		 for (XgsFavoriteJob job : records) {
			 // 检查用户是否已申请该岗位
			 QueryWrapper<XgsPositionApply> applyQueryWrapper = new QueryWrapper<>();
			 applyQueryWrapper.eq("create_by", sysUser.getUsername());
			 applyQueryWrapper.eq("position_id", job.getPositionId());
			 long count = xgsPositionApplyService.count(applyQueryWrapper);
			 // 设置是否已申请标记（通过自定义字段）
			 job.setHasApplied(count > 0 ? "1" : "0");
		 }
		 
		 return Result.OK(pageList);
	 }

	/**
	 * 分页列表查询
	 *
	 * @param xgsFavoriteJob
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "职位收藏-分页列表查询")
	@ApiOperation(value="职位收藏-分页列表查询", notes="职位收藏-分页列表查询")
	@GetMapping(value = "/list")
	@IgnoreAuth
	public Result<IPage<XgsFavoriteJob>> queryPageList(XgsFavoriteJob xgsFavoriteJob,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<XgsFavoriteJob> queryWrapper = QueryGenerator.initQueryWrapper(xgsFavoriteJob, req.getParameterMap());
		Page<XgsFavoriteJob> page = new Page<XgsFavoriteJob>(pageNo, pageSize);
		IPage<XgsFavoriteJob> pageList = xgsFavoriteJobService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param xgsFavoriteJob
	 * @return
	 */
	@AutoLog(value = "职位收藏-添加")
	@ApiOperation(value="职位收藏-添加", notes="职位收藏-添加")
	@RequiresPermissions("positions:xgs_favorite_job:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody XgsFavoriteJob xgsFavoriteJob) {
		xgsFavoriteJobService.save(xgsFavoriteJob);
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param xgsFavoriteJob
	 * @return
	 */
	@AutoLog(value = "职位收藏-编辑")
	@ApiOperation(value="职位收藏-编辑", notes="职位收藏-编辑")
	@RequiresPermissions("positions:xgs_favorite_job:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody XgsFavoriteJob xgsFavoriteJob) {
		xgsFavoriteJobService.updateById(xgsFavoriteJob);
		return Result.OK("编辑成功!");
	}

	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "职位收藏-通过id删除")
	@ApiOperation(value="职位收藏-通过id删除", notes="职位收藏-通过id删除")
	@RequiresPermissions("positions:xgs_favorite_job:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		xgsFavoriteJobService.removeById(id);
		return Result.OK("删除成功!");
	}

	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "职位收藏-批量删除")
	@ApiOperation(value="职位收藏-批量删除", notes="职位收藏-批量删除")
	@RequiresPermissions("positions:xgs_favorite_job:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.xgsFavoriteJobService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}

	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "职位收藏-通过id查询")
	@ApiOperation(value="职位收藏-通过id查询", notes="职位收藏-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<XgsFavoriteJob> queryById(@RequestParam(name="id",required=true) String id) {
		XgsFavoriteJob xgsFavoriteJob = xgsFavoriteJobService.getById(id);
		if(xgsFavoriteJob==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(xgsFavoriteJob);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param xgsFavoriteJob
    */
    @RequiresPermissions("positions:xgs_favorite_job:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, XgsFavoriteJob xgsFavoriteJob) {
        return super.exportXls(request, xgsFavoriteJob, XgsFavoriteJob.class, "职位收藏");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("positions:xgs_favorite_job:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, XgsFavoriteJob.class);
    }

}
