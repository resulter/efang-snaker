package com.efangtec.project.service;

import com.efangtec.workflow.engine.entity.Order;
import com.efangtec.workflow.service.SnakerEngineFacets;
import org.apache.commons.lang.StringUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ApplyService {
    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;
    @Autowired
    private SnakerEngineFacets facets;

    public String startProcess(String processId,String name,String operator,String msg){
        if(StringUtils.isNotEmpty(processId)) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("msg",msg);
            Order orderId = facets.startAndExecute(processId, operator, params);
            Map<String,String> p = new HashMap<>();
            p.put("orderId",orderId.getId());
            p.put("name",name);
            sqlSessionTemplate.insert("ApplyMapper.insertWithOrder",p);
        }
        return "";
    }
}
