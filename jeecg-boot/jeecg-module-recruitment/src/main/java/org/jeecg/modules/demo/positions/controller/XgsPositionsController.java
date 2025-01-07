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
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.positions.entity.XgsPositions;
import org.jeecg.modules.demo.positions.service.IXgsPositionsService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

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
		Page<XgsPositions> page = new Page<XgsPositions>(pageNo, pageSize);
		IPage<XgsPositions> pageList = xgsPositionsService.page(page, queryWrapper);
		return Result.OK(pageList);
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
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "招聘岗位列表-通过id删除")
	@ApiOperation(value="招聘岗位列表-通过id删除", notes="招聘岗位列表-通过id删除")
	@RequiresPermissions("positions:xgs_positions:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		xgsPositionsService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "招聘岗位列表-批量删除")
	@ApiOperation(value="招聘岗位列表-批量删除", notes="招聘岗位列表-批量删除")
	@RequiresPermissions("positions:xgs_positions:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.xgsPositionsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
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
		if(xgsPositions==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(xgsPositions);
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

}
