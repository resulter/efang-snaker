/* Copyright 2013-2015 www.snakerflow.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.efangtec.workflow.engine.scheduling.quartz;

import java.util.Map;

import com.efangtec.workflow.engine.core.ServiceContext;
import com.efangtec.workflow.engine.scheduling.IReminder;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 提醒的job
 * @author yuqs
 * @since 1.4
 */
public class ReminderJob extends AbstractJob {
	private static final Logger log = LoggerFactory.getLogger(ReminderJob.class);
	public void exec(com.efangtec.workflow.engine.entity.Process process, String orderId,
			String taskId,  com.efangtec.workflow.engine.model.NodeModel nodeModel, Map<String, Object> data)
			throws JobExecutionException {
		log.info("\nReminderJob execute taskId:{}\n", taskId);
        IReminder reminder = ServiceContext.find(IReminder.class);
        if(reminder != null) {
            reminder.remind(process, orderId, taskId, nodeModel, data);
        }
	}
}
