package org.jeecg.modules.demo.xgsTalentpool.controller;

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
import org.jeecg.modules.demo.xgsTalentpool.entity.XgsTalentpool;
import org.jeecg.modules.demo.xgsTalentpool.service.IXgsTalentpoolService;

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
 * @Description: 人才库
 * @Author: jeecg-boot
 * @Date:   2025-01-02
 * @Version: V1.0
 */
@Api(tags="人才库")
@RestController
@RequestMapping("/xgsTalentpool/xgsTalentpool")
@Slf4j
public class XgsTalentpoolController extends JeecgController<XgsTalentpool, IXgsTalentpoolService> {
	@Autowired
	private IXgsTalentpoolService xgsTalentpoolService;
	
	/**
	 * 分页列表查询
	 *
	 * @param xgsTalentpool
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "人才库-分页列表查询")
	@ApiOperation(value="人才库-分页列表查询", notes="人才库-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<XgsTalentpool>> queryPageList(XgsTalentpool xgsTalentpool,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        // 自定义查询规则
        Map<String, QueryRuleEnum> customeRuleMap = new HashMap<>();
        // 自定义多选的查询规则为：LIKE_WITH_OR
        customeRuleMap.put("sex", QueryRuleEnum.LIKE_WITH_OR);
        QueryWrapper<XgsTalentpool> queryWrapper = QueryGenerator.initQueryWrapper(xgsTalentpool, req.getParameterMap(),customeRuleMap);
		Page<XgsTalentpool> page = new Page<XgsTalentpool>(pageNo, pageSize);
		IPage<XgsTalentpool> pageList = xgsTalentpoolService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param xgsTalentpool
	 * @return
	 */
	@AutoLog(value = "人才库-添加")
	@ApiOperation(value="人才库-添加", notes="人才库-添加")
	@RequiresPermissions("xgsTalentpool:xgs_talentpool:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody XgsTalentpool xgsTalentpool) {
		xgsTalentpoolService.save(xgsTalentpool);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param xgsTalentpool
	 * @return
	 */
	@AutoLog(value = "人才库-编辑")
	@ApiOperation(value="人才库-编辑", notes="人才库-编辑")
	@RequiresPermissions("xgsTalentpool:xgs_talentpool:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody XgsTalentpool xgsTalentpool) {
		xgsTalentpoolService.updateById(xgsTalentpool);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "人才库-通过id删除")
	@ApiOperation(value="人才库-通过id删除", notes="人才库-通过id删除")
	@RequiresPermissions("xgsTalentpool:xgs_talentpool:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		xgsTalentpoolService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "人才库-批量删除")
	@ApiOperation(value="人才库-批量删除", notes="人才库-批量删除")
	@RequiresPermissions("xgsTalentpool:xgs_talentpool:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.xgsTalentpoolService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "人才库-通过id查询")
	@ApiOperation(value="人才库-通过id查询", notes="人才库-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<XgsTalentpool> queryById(@RequestParam(name="id",required=true) String id) {
		XgsTalentpool xgsTalentpool = xgsTalentpoolService.getById(id);
		if(xgsTalentpool==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(xgsTalentpool);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param xgsTalentpool
    */
    @RequiresPermissions("xgsTalentpool:xgs_talentpool:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, XgsTalentpool xgsTalentpool) {
        return super.exportXls(request, xgsTalentpool, XgsTalentpool.class, "人才库");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("xgsTalentpool:xgs_talentpool:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, XgsTalentpool.class);
    }

}
