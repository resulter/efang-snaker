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

import java.util.List;
import java.util.Map;

import com.efangtec.workflow.engine.SnakerEngine;
import com.efangtec.workflow.engine.entity.Task;
import com.efangtec.workflow.engine.helper.StringHelper;
import com.efangtec.workflow.engine.scheduling.IScheduler;
import com.efangtec.workflow.engine.scheduling.JobCallback;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 自动执行的job
 *
 * @author yuqs
 * @since 1.4
 */
public class ExecutorJob extends AbstractJob {
    private static final Logger log = LoggerFactory.getLogger(ExecutorJob.class);

    /**
     * 根据传递的执行参数，自动执行任务
     */
    public void exec(com.efangtec.workflow.engine.entity.Process process, String orderId,
                     String taskId, com.efangtec.workflow.engine.model.NodeModel nodeModel, Map<String, Object> data)
            throws JobExecutionException {
        log.info("ExecutorJob execute taskId:{}", taskId);
        if (nodeModel == null || !(nodeModel instanceof com.efangtec.workflow.engine.model.TaskModel)) {
            log.debug("节点模型为空，或不是任务模型，则不满足执行条件");
            return;
        }
        com.efangtec.workflow.engine.model.TaskModel tm = (com.efangtec.workflow.engine.model.TaskModel) nodeModel;
        List<Task> tasks = null;
        if (StringHelper.isNotEmpty(tm.getAutoExecute())
                && tm.getAutoExecute().equalsIgnoreCase("Y")) {
            tasks = engine.executeTask(taskId, SnakerEngine.AUTO, data);
            schedule().delete(IScheduler.TYPE_REMINDER + taskId);
        }
        callback(tm.getCallbackObject(), taskId, tasks);
    }

    /**
     * 回调类执行
     *
     * @param jobCallback 回调类
     * @param taskId      任务id
     * @param tasks       新产生的任务列表
     */
    private void callback(JobCallback jobCallback, String taskId, List<Task> tasks) {
        if (jobCallback == null) return;
        jobCallback.callback(taskId, tasks);
    }
}
