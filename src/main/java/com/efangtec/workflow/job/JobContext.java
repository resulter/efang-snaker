package com.efangtec.workflow.job;

import java.util.*;
import java.util.Map.Entry;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


/**
 * 任务上下文 启动时,将任务保存起来
 * 
 * @author sonny
 *
 */
public class JobContext implements ApplicationContextAware, InitializingBean {
	private ApplicationContext applicationContext;

	private static Map<String, ScheduleJob> jobMap = new HashMap<String, ScheduleJob>();
	
	//简历数据同步   10秒同步一次   正式环境调整为   凌晨 1点到8点   每5分钟一次 0 0/5 1-8 * * ?  】【   0/10 * * * * ?
	private static final String SYNC_RESUME_TRIGGER_CRON = "0 0/5 * * * ?";
	//索引数据同步  5秒同步一次   正式环境调整为   30分钟一次                                   0 0/30 * * * ?   】【      0/7 * * * * ?
	private static final String SYNC_INDEX_TRIGGER_CRON = "0/5 * * * * ?";
	//职位、简历推送  30秒同步一次   正式环境调整为   每天早上9点一次                   0 0 9 * * ?      】【     0/30 * * * * ?
	private static final String PUSH_TRIGGER_CRON = "0 0 9 * * ?";
	
	/**
	 * 添加任务
	 * 
	 * @param scheduleJob
	 */
	public static void addJob(ScheduleJob scheduleJob) {
		jobMap.put(scheduleJob.getJobName(), scheduleJob);
	}

	/**
	 * 获取全部任务列表
	 * 
	 * @return
	 */
	public static List<ScheduleJob> getAllJob() {
		List<ScheduleJob> ret = new ArrayList<ScheduleJob>();
		for (Entry<String, ScheduleJob> entry : jobMap.entrySet()) {
			ret.add(entry.getValue());
		}
		return ret;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {

//addJob(new ScheduleJob("10003", "resume_education", "sync", "1", SYNC_RESUME_TRIGGER_CRON, "教育经历数据同步", this.applicationContext.getBean(EducationService.class)));


	}


	public  static String  getStr(){
		Date now = new Date();
		Date afterDate = new Date(now .getTime() + 60000);
		int year = afterDate.getYear();
		int month = afterDate.getMonth();
		int day = afterDate.getDay();
		int hours = afterDate.getHours();
		int minutes = afterDate.getMinutes();
		int seconds = afterDate.getSeconds();
		String s = "";
		s +=seconds + " ";
		return  null;
	}

}
