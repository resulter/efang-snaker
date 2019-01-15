package com.efangtec.workflow.condition;

import com.efangtec.workflow.engine.SnakerInterceptor;
import com.efangtec.workflow.engine.core.*;
import com.efangtec.workflow.engine.entity.Task;
import com.efangtec.workflow.engine.scheduling.IScheduler;
import com.efangtec.workflow.engine.scheduling.JobEntity;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * CreateTaskHandler
 * https://wenku.baidu.com/view/8b06cd90aa00b52acec7ca24.html
 * https://wenku.baidu.com/view/b82d5a1a360cba1aa911da2e.html
 * Created by Administrator on 2018-11-10.
 */
public class CustomHandler   implements SnakerInterceptor {
    private IScheduler scheduler;
    /**
     * 是否调度
     */
    private boolean isScheduled = true;

    public void schedule(String id, Task task, Date startDate, int jobType, Map<String, Object> args) {
        try {
            JobEntity entity = new JobEntity(id, task, startDate, args);
            entity.setModelName(task.getTaskName());
            entity.setJobType(jobType);
            schedule(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void schedule(JobEntity entity) {
        if (scheduler == null) {
            scheduler = ServiceContext.getContext().find(IScheduler.class);
        }
        if (scheduler != null) {
            scheduler.schedule(entity);
        } else {
            isScheduled = false;
        }
    }

    @Override
    public void intercept(Execution execution) {
    }
}
