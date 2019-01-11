package com.efangtec.workflow.engine.scheduling.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class FayaoJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //发短信通知领药
        // 执行流程
        //删除spring_job 数据
    }
}
