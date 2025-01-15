package org.jeecg.modules.demo.xgsResume.controller;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jeecg.modules.demo.xgsResume.vo.XgsResumeBasePage;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.vo.LoginUser;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.xgsResume.entity.XgsResumeWorks;
import org.jeecg.modules.demo.xgsResume.entity.XgsResumeEdus;
import org.jeecg.modules.demo.xgsResume.entity.XgsResumeHome;
import org.jeecg.modules.demo.xgsResume.entity.XgsResumeTJ;
import org.jeecg.modules.demo.xgsResume.vo.XgsResumeTJPage;
import org.jeecg.modules.demo.xgsResume.service.IXgsResumeTJService;
import org.jeecg.modules.demo.xgsResume.service.IXgsResumeWorksService;
import org.jeecg.modules.demo.xgsResume.service.IXgsResumeEdusService;
import org.jeecg.modules.demo.xgsResume.service.IXgsResumeHomeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;
 /**
 * @Description: 基本信息-推荐
 * @Author: jeecg-boot
 * @Date:   2024-12-25
 * @Version: V1.0
 */
@Api(tags="基本信息-推荐")
@RestController
@RequestMapping("/xgsResume/xgsResumeTJ")
@Slf4j
public class XgsResumeTJController {
	@Autowired
	private IXgsResumeTJService xgsResumeTJService;
	@Autowired
	private IXgsResumeWorksService xgsResumeWorksService;
	@Autowired
	private IXgsResumeEdusService xgsResumeEdusService;
	@Autowired
	private IXgsResumeHomeService xgsResumeHomeService;
	
	/**
	 * 分页列表查询
	 *
	 * @param xgsResumeTJ
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "基本信息-推荐-分页列表查询")
	@ApiOperation(value="基本信息-推荐-分页列表查询", notes="基本信息-推荐-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<XgsResumeTJ>> queryPageList(XgsResumeTJ xgsResumeTJ,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<XgsResumeTJ> queryWrapper = QueryGenerator.initQueryWrapper(xgsResumeTJ, req.getParameterMap());
		Page<XgsResumeTJ> page = new Page<XgsResumeTJ>(pageNo, pageSize);
		IPage<XgsResumeTJ> pageList = xgsResumeTJService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param xgsResumeTJPage
	 * @return
	 */
	@AutoLog(value = "基本信息-推荐-添加")
	@ApiOperation(value="基本信息-推荐-添加", notes="基本信息-推荐-添加")
    @RequiresPermissions("xgsResume:xgs_resume_base:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody XgsResumeTJPage xgsResumeTJPage) {
		XgsResumeTJ xgsResumeTJ = new XgsResumeTJ();
		BeanUtils.copyProperties(xgsResumeTJPage, xgsResumeTJ);
		xgsResumeTJService.saveMain(xgsResumeTJ, xgsResumeTJPage.getXgsResumeWorksList(),xgsResumeTJPage.getXgsResumeEdusList(),xgsResumeTJPage.getXgsResumeHomeList());
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param xgsResumeTJPage
	 * @return
	 */
	@AutoLog(value = "基本信息-推荐-编辑")
	@ApiOperation(value="基本信息-推荐-编辑", notes="基本信息-推荐-编辑")
    @RequiresPermissions("xgsResume:xgs_resume_base:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody XgsResumeTJPage xgsResumeTJPage) {
		XgsResumeTJ xgsResumeTJ = new XgsResumeTJ();
		BeanUtils.copyProperties(xgsResumeTJPage, xgsResumeTJ);
		XgsResumeTJ xgsResumeTJEntity = xgsResumeTJService.getById(xgsResumeTJ.getId());
		if(xgsResumeTJEntity==null) {
			return Result.error("未找到对应数据");
		}
		xgsResumeTJService.updateMain(xgsResumeTJ, xgsResumeTJPage.getXgsResumeWorksList(),xgsResumeTJPage.getXgsResumeEdusList(),xgsResumeTJPage.getXgsResumeHomeList());
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "基本信息-推荐-通过id删除")
	@ApiOperation(value="基本信息-推荐-通过id删除", notes="基本信息-推荐-通过id删除")
    @RequiresPermissions("xgsResume:xgs_resume_base:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		xgsResumeTJService.delMain(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "基本信息-推荐-批量删除")
	@ApiOperation(value="基本信息-推荐-批量删除", notes="基本信息-推荐-批量删除")
    @RequiresPermissions("xgsResume:xgs_resume_base:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.xgsResumeTJService.delBatchMain(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功！");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "基本信息-推荐-通过id查询")
	@ApiOperation(value="基本信息-推荐-通过id查询", notes="基本信息-推荐-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<XgsResumeTJ> queryById(@RequestParam(name="id",required=true) String id) {
		XgsResumeTJ xgsResumeTJ = xgsResumeTJService.getById(id);
		if(xgsResumeTJ==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(xgsResumeTJ);

	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "工作经历通过主表ID查询")
	@ApiOperation(value="工作经历主表ID查询", notes="工作经历-通主表ID查询")
	@GetMapping(value = "/queryXgsResumeWorksByMainId")
	public Result<List<XgsResumeWorks>> queryXgsResumeWorksListByMainId(@RequestParam(name="id",required=true) String id) {
		List<XgsResumeWorks> xgsResumeWorksList = xgsResumeWorksService.selectByMainId(id);
		return Result.OK(xgsResumeWorksList);
	}
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "教育经历通过主表ID查询")
	@ApiOperation(value="教育经历主表ID查询", notes="教育经历-通主表ID查询")
	@GetMapping(value = "/queryXgsResumeEdusByMainId")
	public Result<List<XgsResumeEdus>> queryXgsResumeEdusListByMainId(@RequestParam(name="id",required=true) String id) {
		List<XgsResumeEdus> xgsResumeEdusList = xgsResumeEdusService.selectByMainId(id);
		return Result.OK(xgsResumeEdusList);
	}
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "家庭状况通过主表ID查询")
	@ApiOperation(value="家庭状况主表ID查询", notes="家庭状况-通主表ID查询")
	@GetMapping(value = "/queryXgsResumeHomeByMainId")
	public Result<List<XgsResumeHome>> queryXgsResumeHomeListByMainId(@RequestParam(name="id",required=true) String id) {
		List<XgsResumeHome> xgsResumeHomeList = xgsResumeHomeService.selectByMainId(id);
		return Result.OK(xgsResumeHomeList);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param xgsResumeTJ
    */
    @RequiresPermissions("xgsResume:xgs_resume_base:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, XgsResumeTJ xgsResumeTJ) {
      // Step.1 组装查询条件查询数据
      QueryWrapper<XgsResumeTJ> queryWrapper = QueryGenerator.initQueryWrapper(xgsResumeTJ, request.getParameterMap());
      LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

      //配置选中数据查询条件
       String selections = request.getParameter("selections");
       if(oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            queryWrapper.in("id",selectionList);
       }
       //Step.2 获取导出数据
       List<XgsResumeTJ> xgsResumeTJList = xgsResumeTJService.list(queryWrapper);

      // Step.3 组装pageList
      List<XgsResumeTJPage> pageList = new ArrayList<XgsResumeTJPage>();
      for (XgsResumeTJ main : xgsResumeTJList) {
          XgsResumeTJPage vo = new XgsResumeTJPage();
          BeanUtils.copyProperties(main, vo);
          List<XgsResumeWorks> xgsResumeWorksList = xgsResumeWorksService.selectByMainId(main.getId());
          vo.setXgsResumeWorksList(xgsResumeWorksList);
          List<XgsResumeEdus> xgsResumeEdusList = xgsResumeEdusService.selectByMainId(main.getId());
          vo.setXgsResumeEdusList(xgsResumeEdusList);
          List<XgsResumeHome> xgsResumeHomeList = xgsResumeHomeService.selectByMainId(main.getId());
          vo.setXgsResumeHomeList(xgsResumeHomeList);
          pageList.add(vo);
      }

      // Step.4 AutoPoi 导出Excel
      ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
      mv.addObject(NormalExcelConstants.FILE_NAME, "基本信息-推荐列表");
      mv.addObject(NormalExcelConstants.CLASS, XgsResumeTJPage.class);
      mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("基本信息-推荐数据", "导出人:"+sysUser.getRealname(), "基本信息-推荐"));
      mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
      return mv;
    }

    /**
    * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("xgsResume:xgs_resume_base:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
      for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
          // 获取上传文件对象
          MultipartFile file = entity.getValue();
          ImportParams params = new ImportParams();
          params.setTitleRows(2);
          params.setHeadRows(1);
          params.setNeedSave(true);
          try {
              List<XgsResumeTJPage> list = ExcelImportUtil.importExcel(file.getInputStream(), XgsResumeTJPage.class, params);
              for (XgsResumeTJPage page : list) {
                  XgsResumeTJ po = new XgsResumeTJ();
                  BeanUtils.copyProperties(page, po);
                  xgsResumeTJService.saveMain(po, page.getXgsResumeWorksList(),page.getXgsResumeEdusList(),page.getXgsResumeHomeList());
              }
              return Result.OK("文件导入成功！数据行数:" + list.size());
          } catch (Exception e) {
              log.error(e.getMessage(),e);
              return Result.error("文件导入失败:"+e.getMessage());
          } finally {
              try {
                  file.getInputStream().close();
              } catch (IOException e) {
                  e.printStackTrace();
              }
          }
      }
      return Result.OK("文件导入失败！");
    }

	 /**
	  *   申请工作岗位
	  *
	  * @param xgsResumeBasePage
	  * @return
	  */
	 @AutoLog(value = "基本信息-申请工作岗位")
	 @ApiOperation(value="基本信息-申请工作岗位", notes="基本信息-申请工作岗位")
	 @PostMapping(value = "/applyJob")
	 public Result<String> applyJob(@RequestBody XgsResumeBasePage xgsResumeBasePage) {
		 xgsResumeTJService.saveMainWithJob(xgsResumeBasePage, xgsResumeBasePage.getXgsResumeWorksList(),xgsResumeBasePage.getXgsResumeEdusList(),xgsResumeBasePage.getXgsResumeHomeList());
		 return Result.OK("岗位申请已提交，请保持关注！");
	 }
}
