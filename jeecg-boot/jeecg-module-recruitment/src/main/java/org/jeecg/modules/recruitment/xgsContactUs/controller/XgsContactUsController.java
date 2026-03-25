package org.jeecg.modules.recruitment.xgsContactUs.controller;

import java.util.Arrays;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.config.shiro.IgnoreAuth;
import org.jeecg.modules.recruitment.xgsContactUs.entity.XgsContactUs;
import org.jeecg.modules.recruitment.security.RecruitmentPortalAuthUtil;
import org.jeecg.modules.recruitment.security.RecruitmentPublicDataSanitizer;
import org.jeecg.modules.recruitment.xgsContactUs.service.IXgsContactUsService;

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
 * @Description: 联系我们
 * @Author: Liuyl
 * @Date:   2024-12-24
 * @Version: V1.0
 */
@Api(tags="联系我们")
@RestController
@RequestMapping("/xgsContactUs/xgsContactUs")
@Slf4j
public class XgsContactUsController extends JeecgController<XgsContactUs, IXgsContactUsService> {
	@Autowired
	private IXgsContactUsService xgsContactUsService;

	@Autowired
	private ISysBaseAPI sysBaseAPI;

	@Autowired
	private RedisUtil redisUtil;
	
	/**
	 * 分页列表查询
	 *
	 * @param xgsContactUs
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "联系我们-分页列表查询")
	@ApiOperation(value="联系我们-分页列表查询", notes="联系我们-分页列表查询")
	@GetMapping(value = "/list")
	@IgnoreAuth
	public Result<IPage<XgsContactUs>> queryPageList(XgsContactUs xgsContactUs,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		LoginUser portalUser = RecruitmentPortalAuthUtil.tryGetLoginUser(req, sysBaseAPI, redisUtil);
		boolean privileged = RecruitmentPortalAuthUtil.isRecruitmentPrivileged(portalUser);
        QueryWrapper<XgsContactUs> queryWrapper = QueryGenerator.initQueryWrapper(xgsContactUs, req.getParameterMap());
		Page<XgsContactUs> page = new Page<XgsContactUs>(pageNo, pageSize);
		IPage<XgsContactUs> pageList = xgsContactUsService.page(page, queryWrapper);
		if (!privileged) {
			RecruitmentPublicDataSanitizer.stripXgsContactUsList(pageList.getRecords());
		}
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param xgsContactUs
	 * @return
	 */
	@AutoLog(value = "联系我们-添加")
	@ApiOperation(value="联系我们-添加", notes="联系我们-添加")
	@RequiresPermissions("xgsContactUs:xgs_contact_us:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody XgsContactUs xgsContactUs) {
		xgsContactUsService.save(xgsContactUs);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param xgsContactUs
	 * @return
	 */
	@AutoLog(value = "联系我们-编辑")
	@ApiOperation(value="联系我们-编辑", notes="联系我们-编辑")
	@RequiresPermissions("xgsContactUs:xgs_contact_us:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody XgsContactUs xgsContactUs) {
		xgsContactUsService.updateById(xgsContactUs);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "联系我们-通过id删除")
	@ApiOperation(value="联系我们-通过id删除", notes="联系我们-通过id删除")
	@RequiresPermissions("xgsContactUs:xgs_contact_us:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		xgsContactUsService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "联系我们-批量删除")
	@ApiOperation(value="联系我们-批量删除", notes="联系我们-批量删除")
	@RequiresPermissions("xgsContactUs:xgs_contact_us:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.xgsContactUsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "联系我们-通过id查询")
	@ApiOperation(value="联系我们-通过id查询", notes="联系我们-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<XgsContactUs> queryById(@RequestParam(name="id",required=true) String id) {
		XgsContactUs xgsContactUs = xgsContactUsService.getById(id);
		if(xgsContactUs==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(xgsContactUs);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param xgsContactUs
    */
    @RequiresPermissions("xgsContactUs:xgs_contact_us:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, XgsContactUs xgsContactUs) {
        return super.exportXls(request, xgsContactUs, XgsContactUs.class, "联系我们");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("xgsContactUs:xgs_contact_us:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, XgsContactUs.class);
    }

}
