package com.efangtec.workflow.condition;

import com.efangtec.workflow.engine.model.NodeModel;
import com.efangtec.workflow.engine.scheduling.IReminder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.efangtec.workflow.engine.entity.Process;

import java.util.Map;

/**
 * 提醒实例类
 */
public class SnakerReminder implements IReminder {

    private static Logger logger = LoggerFactory.getLogger(SnakerReminder.class);

    public void remind(Process process, String orderId, String taskId, NodeModel nodeModel, Map<String, Object> data) {
        logger.info("orderId:"+orderId);
        logger.info("taskId:"+taskId);
        logger.info("process:"+process.getDisplayName());
        logger.info("nodeModel:"+nodeModel.getDisplayName());
        for(String key:data.keySet()){
            logger.info(key+":"+data.get(key));
        }
    }


}
