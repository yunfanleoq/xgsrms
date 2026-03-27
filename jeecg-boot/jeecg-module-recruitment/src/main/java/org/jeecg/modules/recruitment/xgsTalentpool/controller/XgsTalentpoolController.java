package org.jeecg.modules.recruitment.xgsTalentpool.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.modules.recruitment.xgsResume.entity.XgsResumeBase;
import org.jeecg.modules.recruitment.xgsResume.service.IXgsResumeBaseService;
import org.jeecg.modules.recruitment.xgsTalentpool.entity.XgsTalentpool;
import org.jeecg.modules.recruitment.xgsTalentpool.service.IXgsTalentpoolService;

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
import org.springframework.beans.BeanUtils;
 /**
 * @Description: 人才库
 * @Author: Liuyl
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
	 @Autowired
	 private IXgsResumeBaseService xgsResumeBaseService;
	
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
	@RequiresPermissions("xgsTalentpool:xgs_talentpool:list")
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
	  * 批量添加
	  *
	  * @param xgsTalentpoolList
	  * @return
	  */
	 @ApiOperation(value = "人才库-批量从简历表添加", notes = "人才库-批量从简历表添加")
	 @PostMapping(value = "/addBatchFromResume")
	 public Result<String> addBatchFromResume(@RequestBody Map<String, List<String>> idsMap) {
		 List<String> ids = idsMap.get("ids");

		 // 校验简历 ID 是否为空
		 if (ids == null || ids.isEmpty()) {
			 return Result.error("没有传递任何简历 ID");
		 }

		 // 根据简历 ID 获取简历信息
		 List<XgsResumeBase> resumeBaseList = xgsResumeBaseService.listByIds(ids);
		 if (resumeBaseList.isEmpty()) {
			 return Result.error("未找到相关简历信息");
		 }

		 // 将简历信息转为人才库需要的对象（只复制存在的字段）
		 List<XgsTalentpool> talentpoolList = resumeBaseList.stream().map(resume -> {
			 XgsTalentpool talentpool = new XgsTalentpool();

			 // 使用 BeanUtils.copyProperties 自动复制匹配的字段
			 BeanUtils.copyProperties(resume, talentpool);

			 // 返回填充完成的对象
			 return talentpool;
		 }).collect(Collectors.toList());

		 // 批量插入人才库
		 xgsTalentpoolService.saveBatch(talentpoolList);

		 return Result.OK("添加到人才库成功！");
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
