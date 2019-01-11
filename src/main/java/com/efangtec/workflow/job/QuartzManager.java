package com.efangtec.workflow.job;


import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * Created by shuishou on 2018/8/23.
 */
@Service
public class QuartzManager implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(QuartzManager.class);

    private   ApplicationContext applicationContext;




    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext =applicationContext;
    }

    /**
     * 根据设备编号，添加任务
     * @param resId
     * @param clazz
     * @throws SchedulerException
     */
    public   void addJob(Long  resId,Class<?> clazz) throws SchedulerException {
    }



    public void  deleteJob(ScheduleJob job){
            try {
                JobKey jobKey = new JobKey(job.getJobName(), job.getJobGroup());
            }catch (Exception e) {
                e.printStackTrace();
            }
    }
    /**
     * //withMisfireHandlingInstructionDoNothing
     * @param job
     * @throws SchedulerException
     */
    public   void addJob(ScheduleJob job) throws SchedulerException {
    }
}
