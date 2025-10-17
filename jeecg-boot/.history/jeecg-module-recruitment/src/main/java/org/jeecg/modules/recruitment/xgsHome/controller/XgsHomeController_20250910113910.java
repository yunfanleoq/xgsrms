package org.jeecg.modules.recruitment.xgsHome.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.recruitment.xgsHome.entity.XgsHome;
import org.jeecg.modules.recruitment.xgsHome.service.IXgsHomeService;

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
 * @Description: 首页
 * @Author: Liuyl
 * @Date:   2025-01-21
 * @Version: V1.0
 */
@Api(tags="首页")
@RestController
@RequestMapping("/xgsHome/xgsHome")
@Slf4j
public class XgsHomeController extends JeecgController<XgsHome, IXgsHomeService> {
	@Autowired
	private IXgsHomeService xgsHomeService;
	
	/**
	 * 分页列表查询
	 *
	 * @param xgsHome
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "首页-分页列表查询")
	@ApiOperation(value="首页-分页列表查询", notes="首页-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<XgsHome>> queryPageList(XgsHome xgsHome,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<XgsHome> queryWrapper = QueryGenerator.initQueryWrapper(xgsHome, req.getParameterMap());
		Page<XgsHome> page = new Page<XgsHome>(pageNo, pageSize);
		IPage<XgsHome> pageList = xgsHomeService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	 /**
	  * 轮播图查询
	  * @param xgsHome
	  * @param pageNo
	  * @param pageSize
	  * @param req
	  * @return
	  */
	 @ApiOperation(value="首页-轮播图查询", notes="首页-轮播图查询")
	 @GetMapping(value = "/listForHome")
	 public Result<IPage<XgsHome>> listForHome(XgsHome xgsHome,
												 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
												 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
												 HttpServletRequest req) {
		 QueryWrapper<XgsHome> queryWrapper = QueryGenerator.initQueryWrapper(xgsHome, req.getParameterMap());
		 Page<XgsHome> page = new Page<XgsHome>(pageNo, pageSize);
		 queryWrapper.orderByDesc("create_time");
		 queryWrapper.orderByAsc("img_num");
		 IPage<XgsHome> pageList = xgsHomeService.page(page, queryWrapper);
		 return Result.OK(pageList);
	 }
	
	/**
	 *   添加
	 *
	 * @param xgsHome
	 * @return
	 */
	@AutoLog(value = "首页-添加")
	@ApiOperation(value="首页-添加", notes="首页-添加")
	@RequiresPermissions("xgsHome:xgs_home:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody XgsHome xgsHome) {
		xgsHomeService.save(xgsHome);
		return Result.OK("添加成功！");
	}

	 /**
	  * 自动同步首页数据接口
	  */
	 @ApiOperation(value="自动同步首页数据", notes="从外部API同步首页数据")
	 @PostMapping(value = "/syncHomeContent")
	 public Result<String> syncHomeContent() {
		 try {
			 boolean success = xgsHomeService.syncHomeContentFromAPI(); // 调用服务层方法进行同步
			 if (success) {
				 return Result.OK("同步成功！");
			 } else {
				 return Result.error("同步失败，请检查外部API是否可用");
			 }
		 } catch (Exception e) {
			 log.error("同步数据时发生错误", e);
			 return Result.error("同步失败，系统异常");
		 }
	 }
	
	/**
	 *  编辑
	 *
	 * @param xgsHome
	 * @return
	 */
	@AutoLog(value = "首页-编辑")
	@ApiOperation(value="首页-编辑", notes="首页-编辑")
	@RequiresPermissions("xgsHome:xgs_home:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody XgsHome xgsHome) {
		xgsHomeService.updateById(xgsHome);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "首页-通过id删除")
	@ApiOperation(value="首页-通过id删除", notes="首页-通过id删除")
	@RequiresPermissions("xgsHome:xgs_home:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		xgsHomeService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "首页-批量删除")
	@ApiOperation(value="首页-批量删除", notes="首页-批量删除")
	@RequiresPermissions("xgsHome:xgs_home:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.xgsHomeService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "首页-通过id查询")
	@ApiOperation(value="首页-通过id查询", notes="首页-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<XgsHome> queryById(@RequestParam(name="id",required=true) String id) {
		XgsHome xgsHome = xgsHomeService.getById(id);
		if(xgsHome==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(xgsHome);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param xgsHome
    */
    @RequiresPermissions("xgsHome:xgs_home:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, XgsHome xgsHome) {
        return super.exportXls(request, xgsHome, XgsHome.class, "首页");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("xgsHome:xgs_home:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, XgsHome.class);
    }

}
