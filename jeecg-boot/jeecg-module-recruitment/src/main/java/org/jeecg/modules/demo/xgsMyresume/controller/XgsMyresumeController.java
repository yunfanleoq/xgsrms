package org.jeecg.modules.demo.xgsMyresume.controller;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.CommonUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.xgsMyresume.entity.XgsMyresume;
import org.jeecg.modules.demo.xgsMyresume.service.IXgsMyresumeService;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
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
 * @Description: 简历列表
 * @Author: jeecg-boot
 * @Date:   2025-01-02
 * @Version: V1.0
 */
@Api(tags="简历列表")
@RestController
@RequestMapping("/xgsMyresume/xgsMyresume")
@Slf4j
public class XgsMyresumeController extends JeecgController<XgsMyresume, IXgsMyresumeService> {
	 @Autowired
	 private IXgsMyresumeService xgsMyresumeService;
	 @Autowired
	 private ISysBaseAPI sysBaseApi;

	 @Value("${jeecg.path.upload}")
	 private String upLoadPath;

	 @Value(value="${jeecg.uploadType}")
	 private String uploadType;

	/**
	 * 分页列表查询
	 *
	 * @param xgsMyresume
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "简历列表-分页列表查询")
	@ApiOperation(value="简历列表-分页列表查询", notes="简历列表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<XgsMyresume>> queryPageList(XgsMyresume xgsMyresume,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<XgsMyresume> queryWrapper = QueryGenerator.initQueryWrapper(xgsMyresume, req.getParameterMap());
		Page<XgsMyresume> page = new Page<XgsMyresume>(pageNo, pageSize);
		// 控制权限
		LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		Set<String> roles = sysBaseApi.getUserRoleSetById(loginUser.getId());
		//打印日志
		log.info("get loginUser info: {}", loginUser);
		if (roles.contains("admin")) {
			queryWrapper.eq("create_by", loginUser.getUsername());
			// show all
		} else {
			queryWrapper.eq("create_by", loginUser.getUsername());

		}
		// 显示queryWrapper 的具体内容

		log.info("get queryWrapper info: {}", queryWrapper);
		IPage<XgsMyresume> pageList = xgsMyresumeService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param xgsMyresume
	 * @return
	 */
	@AutoLog(value = "简历列表-添加")
	@ApiOperation(value="简历列表-添加", notes="简历列表-添加")
	@RequiresPermissions("xgsMyresume:xgs_myresume:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody XgsMyresume xgsMyresume) {
		xgsMyresumeService.save(xgsMyresume);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param xgsMyresume
	 * @return
	 */
	@AutoLog(value = "简历列表-编辑")
	@ApiOperation(value="简历列表-编辑", notes="简历列表-编辑")
	@RequiresPermissions("xgsMyresume:xgs_myresume:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody XgsMyresume xgsMyresume) {
		xgsMyresumeService.updateById(xgsMyresume);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "简历列表-通过id删除")
	@ApiOperation(value="简历列表-通过id删除", notes="简历列表-通过id删除")
	@RequiresPermissions("xgsMyresume:xgs_myresume:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		xgsMyresumeService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "简历列表-批量删除")
	@ApiOperation(value="简历列表-批量删除", notes="简历列表-批量删除")
	@RequiresPermissions("xgsMyresume:xgs_myresume:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.xgsMyresumeService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "简历列表-通过id查询")
	@ApiOperation(value="简历列表-通过id查询", notes="简历列表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<XgsMyresume> queryById(@RequestParam(name="id",required=true) String id) {
		XgsMyresume xgsMyresume = xgsMyresumeService.getById(id);
		if(xgsMyresume==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(xgsMyresume);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param xgsMyresume
    */
    @RequiresPermissions("xgsMyresume:xgs_myresume:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, XgsMyresume xgsMyresume) {
        return super.exportXls(request, xgsMyresume, XgsMyresume.class, "简历列表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("xgsMyresume:xgs_myresume:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, XgsMyresume.class);
    }

	 /**
	  * 通过excel导入数据
	  *
	  * @param request
	  * @param response
	  * @return
	  */
	 @RequestMapping(value = "/uploadPdf", method = RequestMethod.POST)
	 public Result<?> uploadPdf(HttpServletRequest request, HttpServletResponse response) {
		 // 上传文件
		 MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		 Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		 for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			 MultipartFile upFile = entity.getValue();// 获取上传文件对象
			 String upFileName = upFile.getOriginalFilename();
			 String upFileType = upFileName.substring(upFileName.lastIndexOf("."));
			 if(!".pdf".equals(upFileType)) {
				 return Result.error("请上传pdf格式的文件！");
			 }

			 String bizPath = "xgsMyResume";
			 try {
				 String fileName = null;
				 File file = new File(upLoadPath + File.separator + bizPath + File.separator);
				 if (!file.exists()) {
					 file.mkdirs();// 创建文件根目录
				 }
				 String orgName = upFile.getOriginalFilename();// 获取文件名
				 orgName = CommonUtils.getFileName(orgName);
				 if (orgName.indexOf(".") != -1) {
					 fileName = orgName.substring(0, orgName.lastIndexOf(".")) + "_" + System.currentTimeMillis() + orgName.substring(orgName.indexOf("."));
				 } else {
					 fileName = orgName + "_" + System.currentTimeMillis();
				 }
				 String savePath = file.getPath() + File.separator + fileName;
				 File savefile = new File(savePath);
				 FileCopyUtils.copy(upFile.getBytes(), savefile);
				 String dbpath = null;
				 if (oConvertUtils.isNotEmpty(bizPath)) {
					 dbpath = bizPath + File.separator + fileName;
				 } else {
					 dbpath = fileName;
				 }
				 if (dbpath.contains("\\")) {
					 dbpath = dbpath.replace("\\", "/");
				 }

				 LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

				 XgsMyresume xgsMyresume = new XgsMyresume();
				 xgsMyresume.setName(loginUser.getRealname());
				 xgsMyresume.setResumeFile(dbpath);
				 xgsMyresumeService.save(xgsMyresume);
				 xgsMyresumeService.analysisResume(xgsMyresume);
			 } catch (IOException e) {
				 log.error(e.getMessage(), e);
			 }
		 }

		 return Result.OK("上传成功！");
	 }

}
