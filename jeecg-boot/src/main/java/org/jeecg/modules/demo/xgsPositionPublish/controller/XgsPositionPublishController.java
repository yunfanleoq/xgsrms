package org.jeecg.modules.demo.xgsPositionPublish.controller;

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
import org.jeecg.modules.demo.xgsPositionPublish.entity.XgsPositionPublish;
import org.jeecg.modules.demo.xgsPositionPublish.service.IXgsPositionPublishService;

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
 * @Description: 岗位发布
 * @Author: jeecg-boot
 * @Date:   2024-12-31
 * @Version: V1.0
 */
@Api(tags="岗位发布")
@RestController
@RequestMapping("/xgsPositionPublish/xgsPositionPublish")
@Slf4j
public class XgsPositionPublishController extends JeecgController<XgsPositionPublish, IXgsPositionPublishService> {
	@Autowired
	private IXgsPositionPublishService xgsPositionPublishService;
	
	/**
	 * 分页列表查询
	 *
	 * @param xgsPositionPublish
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "岗位发布-分页列表查询")
	@ApiOperation(value="岗位发布-分页列表查询", notes="岗位发布-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<XgsPositionPublish>> queryPageList(XgsPositionPublish xgsPositionPublish,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<XgsPositionPublish> queryWrapper = QueryGenerator.initQueryWrapper(xgsPositionPublish, req.getParameterMap());
		Page<XgsPositionPublish> page = new Page<XgsPositionPublish>(pageNo, pageSize);
		IPage<XgsPositionPublish> pageList = xgsPositionPublishService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param xgsPositionPublish
	 * @return
	 */
	@AutoLog(value = "岗位发布-添加")
	@ApiOperation(value="岗位发布-添加", notes="岗位发布-添加")
	@RequiresPermissions("xgsPositionPublish:xgs_position_publish:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody XgsPositionPublish xgsPositionPublish) {
		xgsPositionPublishService.save(xgsPositionPublish);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param xgsPositionPublish
	 * @return
	 */
	@AutoLog(value = "岗位发布-编辑")
	@ApiOperation(value="岗位发布-编辑", notes="岗位发布-编辑")
	@RequiresPermissions("xgsPositionPublish:xgs_position_publish:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody XgsPositionPublish xgsPositionPublish) {
		xgsPositionPublishService.updateById(xgsPositionPublish);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "岗位发布-通过id删除")
	@ApiOperation(value="岗位发布-通过id删除", notes="岗位发布-通过id删除")
	@RequiresPermissions("xgsPositionPublish:xgs_position_publish:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		xgsPositionPublishService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "岗位发布-批量删除")
	@ApiOperation(value="岗位发布-批量删除", notes="岗位发布-批量删除")
	@RequiresPermissions("xgsPositionPublish:xgs_position_publish:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.xgsPositionPublishService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "岗位发布-通过id查询")
	@ApiOperation(value="岗位发布-通过id查询", notes="岗位发布-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<XgsPositionPublish> queryById(@RequestParam(name="id",required=true) String id) {
		XgsPositionPublish xgsPositionPublish = xgsPositionPublishService.getById(id);
		if(xgsPositionPublish==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(xgsPositionPublish);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param xgsPositionPublish
    */
    @RequiresPermissions("xgsPositionPublish:xgs_position_publish:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, XgsPositionPublish xgsPositionPublish) {
        return super.exportXls(request, xgsPositionPublish, XgsPositionPublish.class, "岗位发布");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("xgsPositionPublish:xgs_position_publish:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, XgsPositionPublish.class);
    }

}
