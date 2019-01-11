package com.efangtec.workflow;

import com.efangtec.workflow.engine.entity.Task;
import com.efangtec.workflow.engine.scheduling.JobCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Administrator on 2018-11-06.
 */
public class TestCallback implements JobCallback {
    private static final Logger log = LoggerFactory.getLogger(TestCallback.class);
    public void callback(String taskId, List<Task> newTasks) {
        log.info("callback taskId=" + taskId);
        log.info("newTasks=" + newTasks);
    }
}
