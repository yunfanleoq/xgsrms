package org.jeecg.modules.demo.xgsIntroduce.controller;

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
import org.jeecg.config.shiro.IgnoreAuth;
import org.jeecg.modules.demo.xgsIntroduce.entity.XgsIntroduce;
import org.jeecg.modules.demo.xgsIntroduce.service.IXgsIntroduceService;

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
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Description: 机构概况
 * @Author: jeecg-boot
 * @Date:   2024-12-22
 * @Version: V1.0
 */
@Api(tags="机构概况")
@RestController
@RequestMapping("/xgsIntroduce/xgsIntroduce")
@Slf4j
public class XgsIntroduceController extends JeecgController<XgsIntroduce, IXgsIntroduceService> {
	@Autowired
	private IXgsIntroduceService xgsIntroduceService;
	
	/**
	 * 分页列表查询
	 *
	 * @param xgsIntroduce
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "机构概况-分页列表查询")
	@ApiOperation(value="机构概况-分页列表查询", notes="机构概况-分页列表查询")
	@GetMapping(value = "/list")
	@IgnoreAuth
	public Result<IPage<XgsIntroduce>> queryPageList(XgsIntroduce xgsIntroduce,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<XgsIntroduce> queryWrapper = QueryGenerator.initQueryWrapper(xgsIntroduce, req.getParameterMap());
		Page<XgsIntroduce> page = new Page<XgsIntroduce>(pageNo, pageSize);
		IPage<XgsIntroduce> pageList = xgsIntroduceService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param xgsIntroduce
	 * @return
	 */
	@AutoLog(value = "机构概况-添加")
	@ApiOperation(value="机构概况-添加", notes="机构概况-添加")
	@RequiresPermissions("xgsIntroduce:xgs_introduce:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody XgsIntroduce xgsIntroduce) {
		xgsIntroduceService.save(xgsIntroduce);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param xgsIntroduce
	 * @return
	 */
	@AutoLog(value = "机构概况-编辑")
	@ApiOperation(value="机构概况-编辑", notes="机构概况-编辑")
	@RequiresPermissions("xgsIntroduce:xgs_introduce:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody XgsIntroduce xgsIntroduce) {
		xgsIntroduceService.updateById(xgsIntroduce);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "机构概况-通过id删除")
	@ApiOperation(value="机构概况-通过id删除", notes="机构概况-通过id删除")
	@RequiresPermissions("xgsIntroduce:xgs_introduce:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		xgsIntroduceService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "机构概况-批量删除")
	@ApiOperation(value="机构概况-批量删除", notes="机构概况-批量删除")
	@RequiresPermissions("xgsIntroduce:xgs_introduce:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.xgsIntroduceService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "机构概况-通过id查询")
	@ApiOperation(value="机构概况-通过id查询", notes="机构概况-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<XgsIntroduce> queryById(@RequestParam(name="id",required=true) String id) {
		XgsIntroduce xgsIntroduce = xgsIntroduceService.getById(id);
		if(xgsIntroduce==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(xgsIntroduce);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param xgsIntroduce
    */
    @RequiresPermissions("xgsIntroduce:xgs_introduce:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, XgsIntroduce xgsIntroduce) {
        return super.exportXls(request, xgsIntroduce, XgsIntroduce.class, "机构概况");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("xgsIntroduce:xgs_introduce:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, XgsIntroduce.class);
    }

}
