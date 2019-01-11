package com.efangtec.workflow.conroller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.efangtec.project.entity.One;
import com.efangtec.workflow.engine.DBAccess;
import com.efangtec.workflow.engine.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/business")
public class BusinessController {
    @Autowired
    DBAccess access;

    @RequestMapping(value = "/detail")
    public JSONObject getbusinessDate(String taskId ){
        JSONObject result = new JSONObject();
        Task task = access.queryObject(Task.class, "select * from wf_task where id=?", new Object[]{taskId});
        One one = access.queryObject(One.class, "select * from ap_one where order_id=?", task.getOrderId());
        String variable = task.getVariable();
        JSONObject jsonObject = JSON.parseObject(variable);
        jsonObject.put("name",one.getName());
        System.out.println(task);
        return jsonObject;
    }
}
