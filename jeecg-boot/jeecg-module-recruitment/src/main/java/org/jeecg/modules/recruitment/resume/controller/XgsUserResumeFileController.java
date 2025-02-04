package org.jeecg.modules.recruitment.resume.controller;

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
import org.jeecg.modules.recruitment.resume.entity.XgsUserResumeFile;
import org.jeecg.modules.recruitment.resume.service.IXgsUserResumeFileService;

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
 * @Description: 用户简历文件分析
 * @Author: jeecg-boot
 * @Date:   2025-01-01
 * @Version: V1.0
 */
@Api(tags="用户简历文件分析")
@RestController
@RequestMapping("/resume/xgsUserResumeFile")
@Slf4j
public class XgsUserResumeFileController extends JeecgController<XgsUserResumeFile, IXgsUserResumeFileService> {
	@Autowired
	private IXgsUserResumeFileService xgsUserResumeFileService;
	
	/**
	 * 分页列表查询
	 *
	 * @param xgsUserResumeFile
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "用户简历文件分析-分页列表查询")
	@ApiOperation(value="用户简历文件分析-分页列表查询", notes="用户简历文件分析-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<XgsUserResumeFile>> queryPageList(XgsUserResumeFile xgsUserResumeFile,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<XgsUserResumeFile> queryWrapper = QueryGenerator.initQueryWrapper(xgsUserResumeFile, req.getParameterMap());
		Page<XgsUserResumeFile> page = new Page<XgsUserResumeFile>(pageNo, pageSize);
		IPage<XgsUserResumeFile> pageList = xgsUserResumeFileService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param xgsUserResumeFile
	 * @return
	 */
	@AutoLog(value = "用户简历文件分析-添加")
	@ApiOperation(value="用户简历文件分析-添加", notes="用户简历文件分析-添加")
	@RequiresPermissions("resume:xgs_user_resume_file:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody XgsUserResumeFile xgsUserResumeFile) {
		xgsUserResumeFileService.save(xgsUserResumeFile);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param xgsUserResumeFile
	 * @return
	 */
	@AutoLog(value = "用户简历文件分析-编辑")
	@ApiOperation(value="用户简历文件分析-编辑", notes="用户简历文件分析-编辑")
	@RequiresPermissions("resume:xgs_user_resume_file:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody XgsUserResumeFile xgsUserResumeFile) {
		xgsUserResumeFileService.updateById(xgsUserResumeFile);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "用户简历文件分析-通过id删除")
	@ApiOperation(value="用户简历文件分析-通过id删除", notes="用户简历文件分析-通过id删除")
	@RequiresPermissions("resume:xgs_user_resume_file:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		xgsUserResumeFileService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "用户简历文件分析-批量删除")
	@ApiOperation(value="用户简历文件分析-批量删除", notes="用户简历文件分析-批量删除")
	@RequiresPermissions("resume:xgs_user_resume_file:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.xgsUserResumeFileService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "用户简历文件分析-通过id查询")
	@ApiOperation(value="用户简历文件分析-通过id查询", notes="用户简历文件分析-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<XgsUserResumeFile> queryById(@RequestParam(name="id",required=true) String id) {
		XgsUserResumeFile xgsUserResumeFile = xgsUserResumeFileService.getById(id);
		if(xgsUserResumeFile==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(xgsUserResumeFile);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param xgsUserResumeFile
    */
    @RequiresPermissions("resume:xgs_user_resume_file:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, XgsUserResumeFile xgsUserResumeFile) {
        return super.exportXls(request, xgsUserResumeFile, XgsUserResumeFile.class, "用户简历文件分析");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("resume:xgs_user_resume_file:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, XgsUserResumeFile.class);
    }

	 /**
	  *  分析简历文件
	  *
	  * @param xgsUserResumeFile
	  * @return
	  */
	 @AutoLog(value = "用户简历文件分析-分析简历文件")
	 @ApiOperation(value="用户简历文件分析-分析简历文件", notes="用户简历文件分析-分析简历文件")
//	 @RequiresPermissions("resume:xgs_user_resume_file:analysis")
	 @RequestMapping(value = "/analysisResume", method = {RequestMethod.PUT,RequestMethod.POST})
	 public Result<?> analysisResume(@RequestBody XgsUserResumeFile xgsUserResumeFile) {
		 XgsUserResumeFile resumeFile = xgsUserResumeFileService.analysisResume(xgsUserResumeFile);
		 return Result.OK("分析简历文件完成!", resumeFile);
	 }

}
