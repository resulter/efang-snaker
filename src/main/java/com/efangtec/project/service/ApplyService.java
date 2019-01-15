package com.efangtec.project.service;

import com.efangtec.workflow.engine.DBAccess;
import com.efangtec.workflow.engine.access.QueryFilter;
import com.efangtec.workflow.engine.entity.Order;
import com.efangtec.workflow.engine.entity.Task;
import com.efangtec.workflow.service.SnakerEngineFacets;
import org.apache.commons.lang.StringUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApplyService {
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;
    @Autowired
    private SnakerEngineFacets facets;
    @Autowired
    DBAccess access;

    @Transactional
    public String startProcess(String processId,String name,String operator,String msg){
        if(StringUtils.isNotEmpty(processId)) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("msg",msg);
            Order order = facets.startAndExecute(processId, operator, params);
            List<Task> activeTasks = facets.getEngine().query().getActiveTasks(new QueryFilter().setOrderId(order.getId()));
            String taskId = activeTasks.get(0).getId();
            Map<String,String> p = new HashMap<>();
            p.put("orderId",order.getId());
            p.put("name",name);
            p.put("taskId",taskId);
            p.put("msg",msg);
            sqlSessionTemplate.insert("ApplyMapper.insertTwo",p);
            sqlSessionTemplate.insert("ApplyMapper.insertWithOrder",p);
        }
        return "";
    }
}