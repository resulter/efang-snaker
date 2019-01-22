package com.efangtec.project.service;

import com.alibaba.fastjson.JSON;
import com.efangtec.project.entity.BsActorTask;
import com.efangtec.workflow.engine.DBAccess;
import com.efangtec.workflow.engine.access.QueryFilter;
import com.efangtec.workflow.engine.entity.HistoryTask;
import com.efangtec.workflow.engine.entity.Order;
import com.efangtec.workflow.engine.entity.Task;
import com.efangtec.workflow.service.SnakerEngineFacets;
import org.apache.commons.lang.StringUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    /**
     *
     * @param processId
     * @param operator
     * @param params
     * @param s 会签变量名
     * @return
     */
    @Transactional
    public String startProcess(String processId,String operator,Map<String,Object> params,String s){
        if(StringUtils.isNotEmpty(processId)) {

            String selector = (String) params.get("selector");
            String[] operators = selector.split("-");
            params.put(s,operators);
            Order order = facets.startAndExecute(processId, operator, params);
            List<HistoryTask> historyTasks = facets.getEngine().query().getHistoryTasks(new QueryFilter().setOrderId(order.getId()));
            String taskId = historyTasks.get(0).getId();
            Map<String,Object> p = new HashMap<>();
            p.put("orderId",order.getId());
            p.put("name","name");
            p.put("taskId",taskId);
            p.put("msg", JSON.toJSONString(params));
            sqlSessionTemplate.insert("ApplyMapper.insertTwo",p);
            sqlSessionTemplate.insert("ApplyMapper.insertWithOrder",p);
            List<Task> tasks = facets.getEngine().query().getActiveTasks(new QueryFilter().setOrderId(order.getId()));
            List<BsActorTask> bsActorTasks = new ArrayList<>();
            for (int i = 0; i < operators.length; i++) {
                BsActorTask bsActorTask = new BsActorTask(Integer.parseInt(operators[i]),tasks.get(i).getId(),order.getId());
                bsActorTasks.add(bsActorTask);
            }
            sqlSessionTemplate.insert("ApplyMapper.insertActorTaskBatch", bsActorTasks);
        }
        return "";
    }
}
