package org.jeecg.modules.recruitment.positions.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.recruitment.positions.entity.XgsPositionApply;
import org.jeecg.modules.recruitment.positions.service.IXgsPositionApplyService;
import org.jeecg.modules.recruitment.security.XgsRecruitmentAuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
* @Description: 岗位申请
* @Author: jeecg-boot
* @Date:   2024-12-24
* @Version: V1.0
*/
@Api(tags="岗位申请")
@RestController
@RequestMapping("/positions/xgsPositionApply2")
@Slf4j
public class XgsPositionApply2Controller extends JeecgController<XgsPositionApply, IXgsPositionApplyService> {
   @Autowired
   private IXgsPositionApplyService xgsPositionApplyService;

    /**
     * 分页列表查询 我的申请列表
     *
     * @param xgsPositionApply
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "岗位申请-分页列表查询")
    @ApiOperation(value="岗位申请-分页列表查询", notes="岗位申请-分页列表查询")
    @GetMapping(value = "/listMine")
    public Result<IPage<XgsPositionApply>> listMine(XgsPositionApply xgsPositionApply,
                                                         @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                         @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                                         HttpServletRequest req) {
        //获取当前用户
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        xgsPositionApply.setApprovalNode(null);
        Page<XgsPositionApply> page = new Page<XgsPositionApply>(pageNo, pageSize);
        IPage<XgsPositionApply> pageList = null;
        QueryWrapper<XgsPositionApply> queryWrapper = QueryGenerator.initQueryWrapper(xgsPositionApply, req.getParameterMap());
        queryWrapper.eq("create_by", sysUser.getUsername());
        pageList = xgsPositionApplyService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

   /**
    * 分页列表查询
    *
    * @param xgsPositionApply
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   //@AutoLog(value = "岗位申请-分页列表查询")
   @ApiOperation(value="岗位申请-分页列表查询", notes="岗位申请-分页列表查询")
   @RequiresPermissions("positions:xgs_position_apply:list")
   @GetMapping(value = "/list")
   public Result<IPage<XgsPositionApply>> queryPageList(XgsPositionApply xgsPositionApply,
                                  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                  HttpServletRequest req) {
       QueryWrapper<XgsPositionApply> queryWrapper = QueryGenerator.initQueryWrapper(xgsPositionApply, req.getParameterMap());
       Page<XgsPositionApply> page = new Page<XgsPositionApply>(pageNo, pageSize);
       IPage<XgsPositionApply> pageList = xgsPositionApplyService.page(page, queryWrapper);

       return Result.OK(pageList);
   }

   /**
    *   添加
    *
    * @param xgsPositionApply
    * @return
    */
   @AutoLog(value = "岗位申请-添加")
   @ApiOperation(value="岗位申请-添加", notes="岗位申请-添加")
//	@RequiresPermissions("positions:xgs_position_apply:add")
   @PostMapping(value = "/add")
   public Result<String> add(@RequestBody XgsPositionApply xgsPositionApply) {
       xgsPositionApplyService.save(xgsPositionApply);
       return Result.OK("添加成功！");
   }

   /**
    *  编辑
    *
    * @param xgsPositionApply
    * @return
    */
   @AutoLog(value = "岗位申请-编辑")
   @ApiOperation(value="岗位申请-编辑", notes="岗位申请-编辑")
   @RequiresPermissions("positions:xgs_position_apply:edit")
   @RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
   public Result<String> edit(@RequestBody XgsPositionApply xgsPositionApply) {
       XgsPositionApply existing = xgsPositionApplyService.getById(xgsPositionApply.getId());
       if (existing == null) {
           return Result.error("未找到对应数据");
       }
       XgsRecruitmentAuthUtil.assertCanReadApply(existing);
       xgsPositionApplyService.updateById(xgsPositionApply);
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
   @RequiresPermissions("positions:xgs_position_apply:delete")
   @DeleteMapping(value = "/delete")
   public Result<String> delete(@RequestParam(name="id",required=true) String id) {
       XgsPositionApply existing = xgsPositionApplyService.getById(id);
       if (existing == null) {
           return Result.error("未找到对应数据");
       }
       XgsRecruitmentAuthUtil.assertCanReadApply(existing);
       xgsPositionApplyService.removeById(id);
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
   @RequiresPermissions("positions:xgs_position_apply:deleteBatch")
   @DeleteMapping(value = "/deleteBatch")
   public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
       for (String id : ids.split(",")) {
           XgsPositionApply existing = xgsPositionApplyService.getById(id);
           if (existing != null) {
               XgsRecruitmentAuthUtil.assertCanReadApply(existing);
           }
       }
       this.xgsPositionApplyService.removeByIds(Arrays.asList(ids.split(",")));
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
   public Result<XgsPositionApply> queryById(@RequestParam(name="id",required=true) String id) {
       XgsPositionApply xgsPositionApply = xgsPositionApplyService.getById(id);
       if(xgsPositionApply==null) {
           return Result.error("未找到对应数据");
       }
       XgsRecruitmentAuthUtil.assertCanReadApply(xgsPositionApply);
       return Result.OK(xgsPositionApply);
   }

   /**
   * 导出excel
   *
   * @param request
   * @param xgsPositionApply
   */
   @RequiresPermissions("positions:xgs_position_apply:exportXls")
   @RequestMapping(value = "/exportXls")
   public ModelAndView exportXls(HttpServletRequest request, XgsPositionApply xgsPositionApply) {
       return super.exportXls(request, xgsPositionApply, XgsPositionApply.class, "岗位申请");
   }

   /**
     * 通过excel导入数据
   *
   * @param request
   * @param response
   * @return
   */
   @RequiresPermissions("positions:xgs_position_apply:importExcel")
   @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
   public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
       return super.importExcel(request, response, XgsPositionApply.class);
   }

}
