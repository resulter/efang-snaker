package com.efangtec.project.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.efangtec.project.entity.BaseParam;
import com.efangtec.project.entity.One;
import com.efangtec.project.entity.Two;
import com.efangtec.workflow.engine.DBAccess;
import com.efangtec.workflow.engine.access.QueryFilter;
import com.efangtec.workflow.engine.entity.Task;
import com.efangtec.workflow.service.SnakerEngineFacets;
import org.apache.commons.lang.StringUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

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
    public JSONObject doSth(String name, String opt,String expr,String taskId,String operator,String processId,String orderId) {
        JSONObject jsonObject = new JSONObject();
        Map<String,Object> params = new HashMap<>();
        if (StringUtils.isNotEmpty(expr)){
            params.put(expr,opt);
            params.put("name",name);
        }
        facets.execute(taskId,operator,params);
        Task task = access.queryObject(Task.class, "select * from wf_task where order_id=? limit 1", new Object[]{orderId});
        if(ObjectUtils.isEmpty(task)){
            jsonObject.put("code",100);
        }else {
            jsonObject.put("code",200);
            jsonObject.put("id", task.getId());
        }
        Map<String,Object> p = new HashMap<>();
        p.put("orderId",orderId);
        p.put("taskId",taskId);
        p.put("name","name");
        p.put("msg",null);
        sqlSessionTemplate.insert("ApplyMapper.insertTwo",p);
        return jsonObject;
    }
    @RequestMapping(value = "/receiveTest",method = {RequestMethod.POST,RequestMethod.GET})
    @ResponseBody
    public void  receiveTest(@RequestBody BaseParam param){
        Map<String, Object> build = param.build();
    }

}
