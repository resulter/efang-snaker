package com.efangtec.project;

import com.efangtec.project.entity.BaseParam;
import com.efangtec.project.service.ApplyService;
import com.efangtec.workflow.engine.DBAccess;
import com.efangtec.workflow.engine.access.Page;
import com.efangtec.workflow.engine.access.QueryFilter;
import com.efangtec.workflow.engine.entity.*;
import com.efangtec.workflow.engine.entity.Process;
import com.efangtec.workflow.engine.helper.StringHelper;
import com.efangtec.workflow.engine.model.TaskModel;
import com.efangtec.workflow.service.SnakerEngineFacets;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TestController {
    @Autowired
    private SnakerEngineFacets facets;
    @Autowired
    private ApplyService applyService;
    @Autowired
    private DBAccess access;

    @RequestMapping(value = "/greeting")
    public ModelAndView test(ModelAndView mv, String s) {
        mv.setViewName("/greeting");
        mv.addObject("title", "欢迎使用Thymeleaf!");
        mv.addObject("s", s);
        return mv;
    }

    @RequestMapping(value = "/toProcess")
    public ModelAndView toProcess(ModelAndView mv, String s, Page<Process> page) {
        mv.setViewName("/process");
        mv.addObject("title", "流程列表");
        mv.addObject("s", s);
        QueryFilter filter = new QueryFilter();
        facets.getEngine().process().getProcesss(page, filter);
        mv.addObject("page", page);
        return mv;
    }

    /**
     * 启动流程
     *
     * @param processId
     * @return
     */
    @RequestMapping(value = "/startProcess")
    public String startProcess(@RequestBody BaseParam param) {
        Map<String, Object> build = param.build();
        String operator = (String) build.get("operator");
        String processId = (String) build.get("processId");

        applyService.startProcess(processId, operator, param.build());
        return "redirect:/toProcess";
    }

    @RequestMapping(value = "/toOrder")
    public ModelAndView toOrder(ModelAndView mv, String s, Page<HistoryOrder> page) {
        mv.setViewName("/order");
        mv.addObject("title", "实例列表");
        mv.addObject("s", s);
        QueryFilter filter = new QueryFilter();
        facets.getEngine().query().getHistoryOrders(page, new QueryFilter());
        mv.addObject("page", page);
        return mv;
    }

    @RequestMapping(value = "toActive")
    public ModelAndView homeTaskList(ModelAndView mv, Page<WorkItem> page) {
        mv.setViewName("/active");
        List<WorkItem> majorWorks = facets.getEngine()
                .query()
                .getWorkItems(page, new QueryFilter()
//                        .setOperators(assignees)
                        .setTaskType(TaskModel.TaskType.Major.ordinal()));

        mv.addObject("page", page);
        return mv;
    }

    @RequestMapping(value = "toActiveDetail", method = RequestMethod.GET)
    public ModelAndView toActiveDetail(ModelAndView mv, String processId, String orderId) {
        mv.setViewName("/active-detail");
        mv.addObject("processId", processId);
        mv.addObject("orderId", orderId);
        HistoryOrder order = facets.getEngine().query().getHistOrder(orderId);
        mv.addObject("order", order);
        List<HistoryTask> tasks = facets.getEngine().query().getHistoryTasks(new QueryFilter().setOrderId(orderId));
        mv.addObject("tasks", tasks);
        return mv;
    }

    @RequestMapping(value = "toOrderDetail", method = RequestMethod.GET)
    public ModelAndView toOrderDetail(ModelAndView mv, String processId, String orderId, String taskId) {
        mv.setViewName("/order-detail");
        mv.addObject("processId", processId);
        mv.addObject("orderId", orderId);
        mv.addObject("taskId", taskId);
//        HistoryTask task = access.queryObject(HistoryTask.class, "SELECT * FROM `wf_hist_task` WHERE order_Id =? ORDER BY finish_Time desc LIMIT 1", new Object[]{orderId});

        mv.addObject("taskName", facets.getEngine().query().getTask(taskId).getTaskName());

        HistoryOrder order = facets.getEngine().query().getHistOrder(orderId);
        mv.addObject("order", order);
        List<HistoryTask> tasks = facets.getEngine().query().getHistoryTasks(new QueryFilter().setOrderId(orderId));
        mv.addObject("tasks", tasks);
        return mv;
    }

    @RequestMapping(value = "/excute")
    @ResponseBody
    public void excuteTask(String taskId) {
        facets.execute(taskId, "testOperator", null);
    }
}
