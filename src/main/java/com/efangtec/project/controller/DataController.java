package com.efangtec.project.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.efangtec.project.entity.*;
import com.efangtec.workflow.engine.DBAccess;
import com.efangtec.workflow.engine.access.QueryFilter;
import com.efangtec.workflow.engine.entity.HistoryTask;
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
@RequestMapping(value = "/data")
public class DataController {
    @Autowired
    DBAccess access;
    @Autowired
    private SnakerEngineFacets facets;
    @Autowired
    SqlSessionTemplate sqlSessionTemplate;

    /**
     * 执行一步
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/executeOneStep", method = {RequestMethod.POST, RequestMethod.GET})
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
        //根据选择的操作人和orderId删除这个任务，该表的作用是获取会签待办人列表
        Map<String,String> deleteParam = new HashMap<>();
        deleteParam.put("orderId",orderId);
        deleteParam.put("select",select);
        sqlSessionTemplate.delete("ApplyMapper.deleteActorTaskByOrderIdAndSelect", deleteParam);
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

    /**
     * 获取表格数据，回显之前节点的信息
     * @param orderId
     * @return
     */
    @RequestMapping(value = "/getFormData")
    @ResponseBody
    public JSONObject getFormData(String orderId) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
//        List<HistoryTask> historyTasks = facets.getEngine().query().getHistoryTasks(new QueryFilter().setOrderId(orderId));
        //这个查询用来按照tackName进行了分组，主要是解决会签时同一步可能有多个task，暂定200，原则上不会有这个多节点
        List<HistoryTask> historyTasks = access.queryList(HistoryTask.class,"SELECT * from (SELECT * FROM `wf_hist_task` where order_Id =?  ORDER BY id desc  LIMIT 200 ) as b GROUP BY task_Name",new Object[]{orderId});
        //当前任务，如果有多个则可能是会签任务
        List<Task> tasks = access.queryList(Task.class, "select * from wf_task where order_Id =?", new Object[]{orderId});

        for (HistoryTask historyTask:historyTasks) {
            JSONObject jo = new JSONObject();
            jo.put("name",historyTask.getTaskName());
            jo.put("id",historyTask.getId());
            jo.put("orderId",orderId);
            Two two = access.queryObject(Two.class, "select * from ap_two where task_id = ?", new Object[]{historyTask.getId()});
            jo.put("formData",two);
            jo.put("tasks",tasks.toString());
            jo.put("taskSize",tasks.size());
            jsonArray.add(jo);
        }
        jsonObject.put("data",jsonArray);
        return jsonObject;
    }

    /**
     * 获取会签全部参与人列表
     * @return
     */
    @RequestMapping("/getOperatorList")
    @ResponseBody
    public JSONObject getOperatorList(){
        JSONObject jsonObject = new JSONObject();
        List<BsActor> bsActors = access.queryList(BsActor.class, "select * from bs_actor", new Object[]{});
        jsonObject.put("list",bsActors);
        return jsonObject;
    }

    /**
     * 获取会签待参与人列表
     * @param orderId
     * @return
     */
    @RequestMapping("/getOperatorListDoing")
    @ResponseBody
    public JSONObject getOperatorListDoing(String orderId){
        JSONObject jsonObject = new JSONObject();
        List<BsActor> result = new ArrayList<>();
        List<BsActor> bsActors = access.queryList(BsActor.class, "select * from bs_actor", new Object[]{});
        List<BsActorTask> bsActorTasks = access.queryList(BsActorTask.class, "select * from bs_actor_task where order_id=?", new Object[]{orderId});
        for (int i = 0; i < bsActorTasks.size(); i++) {
            for (int j = 0; j < bsActors.size(); j++) {
                if(bsActors.get(j).getId() == bsActorTasks.get(i).getActorId()){
                    result.add(bsActors.get(j));
                }
            }
        }
        jsonObject.put("list",result);
        return jsonObject;
    }

}
