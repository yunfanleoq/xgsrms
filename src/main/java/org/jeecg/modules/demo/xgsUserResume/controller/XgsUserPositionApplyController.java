package org.jeecg.modules.demo.xgsUserResume.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.common.util.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.config.JeecgBaseConfig;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.PropertyUtils;
import org.jeecg.modules.demo.xgsUserResume.entity.XgsUserPositionApply;
import org.jeecg.modules.demo.xgsUserResume.service.IXgsUserPositionApplyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

 /**
 * @Description: 岗位申请
 * @Author: jeecg-boot
 * @Date:   2025-01-02
 * @Version: V1.0
 */
@Api(tags="岗位申请")
@RestController
@RequestMapping("/xgsUserResume/xgsUserPositionApply")
@Slf4j
public class XgsUserPositionApplyController extends JeecgController<XgsUserPositionApply, IXgsUserPositionApplyService> {
	@Autowired
	private IXgsUserPositionApplyService xgsUserPositionApplyService;
	@Autowired
	private JeecgBaseConfig jeecgBaseConfig;
	
	/**
	 * 分页列表查询
	 *
	 * @param xgsUserPositionApply
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "岗位申请-分页列表查询")
	@ApiOperation(value="岗位申请-分页列表查询", notes="岗位申请-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<XgsUserPositionApply>> queryPageList(XgsUserPositionApply xgsUserPositionApply,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<XgsUserPositionApply> queryWrapper = QueryGenerator.initQueryWrapper(xgsUserPositionApply, req.getParameterMap());
		Page<XgsUserPositionApply> page = new Page<XgsUserPositionApply>(pageNo, pageSize);
		IPage<XgsUserPositionApply> pageList = xgsUserPositionApplyService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param xgsUserPositionApply
	 * @return
	 */
	@AutoLog(value = "岗位申请-添加")
	@ApiOperation(value="岗位申请-添加", notes="岗位申请-添加")
	@RequiresPermissions("xgsUserResume:xgs_position_apply:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody XgsUserPositionApply xgsUserPositionApply) {
		xgsUserPositionApplyService.save(xgsUserPositionApply);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param xgsUserPositionApply
	 * @return
	 */
	@AutoLog(value = "岗位申请-编辑")
	@ApiOperation(value="岗位申请-编辑", notes="岗位申请-编辑")
	@RequiresPermissions("xgsUserResume:xgs_position_apply:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody XgsUserPositionApply xgsUserPositionApply) {
		xgsUserPositionApplyService.updateById(xgsUserPositionApply);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "岗位申请-通过id删除")
	@ApiOperation(value="岗位申请-通过id删除", notes="岗位申请-通过id删除")
	@RequiresPermissions("xgsUserResume:xgs_position_apply:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		xgsUserPositionApplyService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "岗位申请-批量删除")
	@ApiOperation(value="岗位申请-批量删除", notes="岗位申请-批量删除")
	@RequiresPermissions("xgsUserResume:xgs_position_apply:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.xgsUserPositionApplyService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "岗位申请-通过id查询")
	@ApiOperation(value="岗位申请-通过id查询", notes="岗位申请-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<XgsUserPositionApply> queryById(@RequestParam(name="id",required=true) String id) {
		XgsUserPositionApply xgsUserPositionApply = xgsUserPositionApplyService.getById(id);
		if(xgsUserPositionApply==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(xgsUserPositionApply);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param xgsUserPositionApply
    */
    @RequiresPermissions("xgsUserResume:xgs_position_apply:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, XgsUserPositionApply xgsUserPositionApply) {
        // 获取列配置参数
        String columnsParam = request.getParameter("columns");
        List<String> exportColumns = new ArrayList<>();
        
        if (oConvertUtils.isNotEmpty(columnsParam)) {
            try {
                // 解析前端传递的列配置
                ObjectMapper objectMapper = new ObjectMapper();
                List<Map<String, String>> columnsList = objectMapper.readValue(columnsParam, 
                    new TypeReference<List<Map<String, String>>>() {});
                
                // 提取列键
                for (Map<String, String> col : columnsList) {
                    if (col.containsKey("key")) {
                        exportColumns.add(col.get("key"));
                    }
                }
            } catch (Exception e) {
                log.warn("解析列配置失败，使用默认导出: {}", e.getMessage());
            }
        }
        
        // 如果没有指定列，使用默认导出
        if (exportColumns.isEmpty()) {
            return super.exportXls(request, xgsUserPositionApply, XgsUserPositionApply.class, "岗位申请");
        }
        
        // 自定义列导出
        return exportXlsWithColumns(request, xgsUserPositionApply, exportColumns, "岗位申请");
    }
    
    /**
     * 自定义列导出
     */
    private ModelAndView exportXlsWithColumns(HttpServletRequest request, XgsUserPositionApply xgsUserPositionApply, 
                                            List<String> exportColumns, String title) {
        // Step.1 组装查询条件
        QueryWrapper<XgsUserPositionApply> queryWrapper = QueryGenerator.initQueryWrapper(xgsUserPositionApply, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        // 过滤选中数据
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            queryWrapper.in("id", selectionList);
        }
        
        // Step.2 获取导出数据
        List<XgsUserPositionApply> exportList = xgsUserPositionApplyService.list(queryWrapper);

        // Step.3 过滤数据，只保留指定的列
        List<Map<String, Object>> filteredData = new ArrayList<>();
        for (XgsUserPositionApply item : exportList) {
            Map<String, Object> row = new HashMap<>();
            for (String column : exportColumns) {
                try {
                    Object value = PropertyUtils.getProperty(item, column);
                    row.put(column, value);
                } catch (Exception e) {
                    log.warn("获取字段 {} 值失败: {}", column, e.getMessage());
                    row.put(column, "");
                }
            }
            filteredData.add(row);
        }

        // Step.4 创建自定义导出视图
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, title);
        mv.addObject(NormalExcelConstants.CLASS, Map.class);
        
        ExportParams exportParams = new ExportParams(title + "报表", "导出人:" + sysUser.getRealname(), title);
        exportParams.setImageBasePath(jeecgBaseConfig.getPath().getUpload());
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, filteredData);
        
        return mv;
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("xgsUserResume:xgs_position_apply:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, XgsUserPositionApply.class);
    }

}
