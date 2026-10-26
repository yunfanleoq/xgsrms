package org.jeecg.modules.recruitment.ontology;

import org.apache.shiro.authz.UnauthorizedException;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.recruitment.security.XgsRecruitmentAuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 招聘本体与智能体工作流入口。
 */
@RestController
@RequestMapping("/recruitment/ontology")
public class RmsOntologyController {

    @Autowired
    private RmsAgentWorkflowService workflowService;

    @GetMapping("/schema")
    public Result<Map<String, Object>> schema() {
        XgsRecruitmentAuthUtil.currentUser();
        return Result.OK(RmsHireOntologyCatalog.schema());
    }

    @GetMapping("/board")
    public Result<Map<String, Object>> board(@RequestParam(name = "myDept", defaultValue = "false") boolean myDept) {
        if (!workflowService.canViewBoard()) {
            return Result.error("只有招聘办理人员可以查看办理看板");
        }
        return Result.OK(workflowService.board(myDept));
    }

    @PostMapping("/actions/run")
    public Result<Map<String, Object>> run(@RequestBody RmsAgentActionRequest request) {
        try {
            return Result.OK(workflowService.run(request));
        } catch (IllegalArgumentException | UnauthorizedException ex) {
            return Result.error(ex.getMessage());
        }
    }
}
