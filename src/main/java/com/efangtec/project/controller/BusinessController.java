package com.efangtec.project.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.efangtec.project.entity.BaseParam;
import com.efangtec.project.entity.BsActorTask;
import com.efangtec.project.entity.One;
import com.efangtec.project.entity.Two;
import com.efangtec.workflow.engine.DBAccess;
import com.efangtec.workflow.engine.access.QueryFilter;
import com.efangtec.workflow.engine.entity.Process;
import com.efangtec.workflow.engine.entity.Task;
import com.efangtec.workflow.engine.model.ProcessModel;
import com.efangtec.workflow.engine.model.TaskModel;
import com.efangtec.workflow.service.SnakerEngineFacets;
import org.apache.commons.lang.StringUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/business")
public class BusinessController {
    @Autowired
    DBAccess access;
    @Autowired
    private SnakerEngineFacets facets;
    @Autowired
    SqlSessionTemplate sqlSessionTemplate;

    @RequestMapping(value = "/detail")
    public JSONObject getbusinessDate(String taskId) {
        JSONObject result = new JSONObject();
        Task task = access.queryObject(Task.class, "select * from wf_task where id=?", new Object[]{taskId});
        One one = access.queryObject(One.class, "select * from ap_one where order_id=?", task.getOrderId());
        String variable = task.getVariable();
        JSONObject jsonObject = JSON.parseObject(variable);
        jsonObject.put("name", one.getName());
        System.out.println(task);
        return jsonObject;
    }

    @RequestMapping(value = "/opt")
    @ResponseBody
    public JSONObject doSth(String name, String opt, String expr, String taskId, String operator, String processId, String orderId) {
        JSONObject jsonObject = new JSONObject();
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotEmpty(expr)) {
            params.put(expr, opt);
            params.put("name", name);
        }
        facets.execute(taskId, operator, params);
        Task task = access.queryObject(Task.class, "select * from wf_task where order_id=? limit 1", new Object[]{orderId});
        if (ObjectUtils.isEmpty(task)) {
            jsonObject.put("code", 100);
        } else {
            jsonObject.put("code", 200);
            jsonObject.put("id", task.getId());
        }
        Map<String, Object> p = new HashMap<>();
        p.put("orderId", orderId);
        p.put("taskId", taskId);
        p.put("name", "name");
        p.put("msg", null);
        sqlSessionTemplate.insert("ApplyMapper.insertTwo", p);
        return jsonObject;
    }

    /**
     * 执行一步
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/receiveTest", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    @Transactional
    public JSONObject receiveTest(@RequestBody BaseParam param) {
        Map<String, Object> build = param.build();
        String expr = (String) build.get("expr");
        String opt = (String) build.get("opt");
        String name = (String) build.get("name");
        String taskId = (String) build.get("taskId");
        String operator = (String) build.get("operator");
        String orderId = (String) build.get("orderId");
        String processId = (String) build.get("processId");
        //本次会签操作人
        String select = (String) build.get("select");
        //接收多选的操作人
        String selector = (String) build.get("selector");
        Process process = facets.getEngine().process().getProcessById(processId);
        ProcessModel processModel = process.getModel();
        List<TaskModel> models = process.getModel().getModels(TaskModel.class);
        Task taskLocal = access.queryObject(Task.class, "select * from wf_task where id = ?", new Object[]{taskId});

        Boolean isAssignee = false;
        String nextAssigneeName = "";
        //如果下一个节点是会签节点
        for (int i = 0; i < models.size() - 1; i++) {
            if (models.get(i).getName().equals(taskLocal.getTaskName())) {

                if (models.get(i + 1).getPerformType().equals("ALL")) {
                    isAssignee = true;
                    break;
                } else {
                    isAssignee = false;
                }
            }else {
                isAssignee = false;
            }
        }

        String[] operators = selector.split("-");
        Map<String, Object> params = new HashMap<>();
        if (StringUtils.isNotEmpty(expr)) {
            params.put(expr, opt);
            params.put("name", name);
        }
        //以下用来获取当前会签节点的参与人
        if (!isAssignee) {
            String assignee = "";//本次会签名称
            for (int i = 0; i < models.size(); i++) {
                if (models.get(i).getName().equals(taskLocal.getTaskName())) {
                    assignee = StringUtils.isNotEmpty(models.get(i).getAssignee()) ? models.get(i).getAssignee() : "";
                }
            }
            params.put(assignee, select);
        } else {
            for (int i = 0; i < models.size(); i++) {
                if (models.get(i).getName().equals(taskLocal.getTaskName())) {
                    nextAssigneeName = models.get(i + 1).getAssignee();
                    break;
                }
            }
            //下一个节点的会签assignee
            params.put(nextAssigneeName, operators);
        }
        JSONObject jsonObject = new JSONObject();

        List<Task> tasks = facets.execute(taskId, operator, params);
        sqlSessionTemplate.delete("ApplyMapper.deleteActorTaskByTaskId", taskId);
        Task task = access.queryObject(Task.class, "select * from wf_task where order_id=? limit 1", new Object[]{orderId});
        if (ObjectUtils.isEmpty(task)) {
            jsonObject.put("code", 100);
        } else {
            jsonObject.put("code", 200);
            jsonObject.put("id", task.getId());
        }
        Map<String, Object> p = new HashMap<>();
        p.put("orderId", orderId);
        p.put("taskId", taskId);
        p.put("name", "name");
        p.put("msg", JSON.toJSONString(build));
        sqlSessionTemplate.insert("ApplyMapper.insertTwo", p);
        if(isAssignee) {
            List<BsActorTask> bsActorTasks = new ArrayList<>();
            for (int i = 0; i < operators.length; i++) {
                BsActorTask bsActorTask = new BsActorTask(Integer.parseInt(operators[i]), tasks.get(i).getId(), orderId);
                bsActorTasks.add(bsActorTask);
            }
            sqlSessionTemplate.insert("ApplyMapper.insertActorTaskBatch", bsActorTasks);
        }
        return jsonObject;
    }

}
