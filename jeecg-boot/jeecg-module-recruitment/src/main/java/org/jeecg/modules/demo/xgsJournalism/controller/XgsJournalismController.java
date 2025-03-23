package org.jeecg.modules.demo.xgsJournalism.controller;

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
import org.jeecg.modules.demo.xgsJournalism.entity.XgsJournalism;
import org.jeecg.modules.demo.xgsJournalism.service.IXgsJournalismService;

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
 * @Description: 新闻内容
 * @Author: jeecg-boot
 * @Date:   2024-12-23
 * @Version: V1.0
 */
@Api(tags="新闻内容")
@RestController
@RequestMapping("/xgsJournalism/xgsJournalism")
@Slf4j
public class XgsJournalismController extends JeecgController<XgsJournalism, IXgsJournalismService> {
	@Autowired
	private IXgsJournalismService xgsJournalismService;
	
	/**
	 * 分页列表查询
	 *
	 * @param xgsJournalism
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "新闻内容-分页列表查询")
	@ApiOperation(value="新闻内容-分页列表查询", notes="新闻内容-分页列表查询")
	@GetMapping(value = "/list")
	@IgnoreAuth
	public Result<IPage<XgsJournalism>> queryPageList(XgsJournalism xgsJournalism,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<XgsJournalism> queryWrapper = QueryGenerator.initQueryWrapper(xgsJournalism, req.getParameterMap());
		Page<XgsJournalism> page = new Page<XgsJournalism>(pageNo, pageSize);
		IPage<XgsJournalism> pageList = xgsJournalismService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	 @ApiOperation(value="新闻内容-分页列表查询", notes="新闻内容-分页列表查询")
	 @GetMapping(value = "/listForHome")
	 @IgnoreAuth
	 public Result<IPage<XgsJournalism>> listForHome(XgsJournalism xgsJournalism,
													   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
													   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
													   HttpServletRequest req) {
		 QueryWrapper<XgsJournalism> queryWrapper = QueryGenerator.initQueryWrapper(xgsJournalism, req.getParameterMap());
		 Page<XgsJournalism> page = new Page<XgsJournalism>(pageNo, pageSize);
		 queryWrapper.orderByDesc("create_time");
		 queryWrapper.orderByAsc("news_sort");
		 IPage<XgsJournalism> pageList = xgsJournalismService.page(page, queryWrapper);
		 return Result.OK(pageList);
	 }
	
	/**
	 *   添加
	 *
	 * @param xgsJournalism
	 * @return
	 */
	@AutoLog(value = "新闻内容-添加")
	@ApiOperation(value="新闻内容-添加", notes="新闻内容-添加")
	@RequiresPermissions("xgsJournalism:xgs_journalism:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody XgsJournalism xgsJournalism) {
		xgsJournalismService.save(xgsJournalism);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param xgsJournalism
	 * @return
	 */
	@AutoLog(value = "新闻内容-编辑")
	@ApiOperation(value="新闻内容-编辑", notes="新闻内容-编辑")
	@RequiresPermissions("xgsJournalism:xgs_journalism:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody XgsJournalism xgsJournalism) {
		xgsJournalismService.updateById(xgsJournalism);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "新闻内容-通过id删除")
	@ApiOperation(value="新闻内容-通过id删除", notes="新闻内容-通过id删除")
	@RequiresPermissions("xgsJournalism:xgs_journalism:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		xgsJournalismService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "新闻内容-批量删除")
	@ApiOperation(value="新闻内容-批量删除", notes="新闻内容-批量删除")
	@RequiresPermissions("xgsJournalism:xgs_journalism:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.xgsJournalismService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "新闻内容-通过id查询")
	@ApiOperation(value="新闻内容-通过id查询", notes="新闻内容-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<XgsJournalism> queryById(@RequestParam(name="id",required=true) String id) {
		XgsJournalism xgsJournalism = xgsJournalismService.getById(id);
		if(xgsJournalism==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(xgsJournalism);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param xgsJournalism
    */
    @RequiresPermissions("xgsJournalism:xgs_journalism:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, XgsJournalism xgsJournalism) {
        return super.exportXls(request, xgsJournalism, XgsJournalism.class, "新闻内容");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("xgsJournalism:xgs_journalism:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, XgsJournalism.class);
    }

}
