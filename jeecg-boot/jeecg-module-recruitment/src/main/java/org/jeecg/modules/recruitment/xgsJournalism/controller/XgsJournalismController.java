package org.jeecg.modules.recruitment.xgsJournalism.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.config.shiro.IgnoreAuth;
import org.jeecg.modules.recruitment.xgsJournalism.entity.XgsJournalism;
import org.jeecg.modules.recruitment.security.RecruitmentPortalAuthUtil;
import org.jeecg.modules.recruitment.security.RecruitmentPublicDataSanitizer;
import org.jeecg.modules.recruitment.xgsJournalism.service.IXgsJournalismService;

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
 * @Description: 新闻内容
 * @Author: Liuyl
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

	@Autowired
	private ISysBaseAPI sysBaseAPI;

	@Autowired
	private RedisUtil redisUtil;

	/** 与前端门户、同步任务中「已发布」状态一致 */
	private static final String JOURNALISM_STATE_PUBLISHED = "已发布";
	
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
	public Result<IPage<XgsJournalism>> queryPageList(XgsJournalism xgsJournalism,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<XgsJournalism> queryWrapper = QueryGenerator.initQueryWrapper(xgsJournalism, req.getParameterMap());
		Page<XgsJournalism> page = new Page<XgsJournalism>(pageNo, pageSize);
		IPage<XgsJournalism> pageList = xgsJournalismService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	 @ApiOperation(value="新闻内容-分页列表查询2", notes="新闻内容-分页列表查询2")
	 @IgnoreAuth
	 @GetMapping(value = "/listForHomeNews")
	 public Result<IPage<XgsJournalism>> listForHome(XgsJournalism xgsJournalism,
													   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
													   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
													   HttpServletRequest req) {
		 Map<String, String[]> safeParams = new HashMap<>(req.getParameterMap());
		 safeParams.remove("state");
		 QueryWrapper<XgsJournalism> queryWrapper = QueryGenerator.initQueryWrapper(xgsJournalism, safeParams);
		 queryWrapper.eq("state", JOURNALISM_STATE_PUBLISHED);
		 Page<XgsJournalism> page = new Page<XgsJournalism>(pageNo, pageSize);
		 queryWrapper.orderByDesc("create_time");
		 queryWrapper.orderByAsc("news_sort");
		 IPage<XgsJournalism> pageList = xgsJournalismService.page(page, queryWrapper);
		 RecruitmentPublicDataSanitizer.stripXgsJournalismList(pageList.getRecords());
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
	@IgnoreAuth
	@GetMapping(value = "/queryById")
	public Result<XgsJournalism> queryById(@RequestParam(name="id",required=true) String id, HttpServletRequest req) {
		XgsJournalism xgsJournalism = xgsJournalismService.getById(id);
		if(xgsJournalism==null) {
			return Result.error("未找到对应数据");
		}
		LoginUser portalUser = RecruitmentPortalAuthUtil.tryGetLoginUser(req, sysBaseAPI, redisUtil);
		boolean privileged = RecruitmentPortalAuthUtil.isRecruitmentPrivileged(portalUser);
		if (!privileged && !JOURNALISM_STATE_PUBLISHED.equals(xgsJournalism.getState())) {
			return Result.error("未找到对应数据");
		}
		if (!privileged) {
			RecruitmentPublicDataSanitizer.stripXgsJournalism(xgsJournalism);
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
