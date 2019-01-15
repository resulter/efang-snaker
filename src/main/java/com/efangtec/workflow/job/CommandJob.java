package com.efangtec.workflow.job;


import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 联动命令处理
 * Created by shuishou on 2018/8/24.
 */
@DisallowConcurrentExecution
public class CommandJob  implements Job {
    private static Logger logger = LoggerFactory.getLogger(CommandJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        System.err.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
    }
}
