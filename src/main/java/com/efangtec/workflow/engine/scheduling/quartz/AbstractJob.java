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

import com.efangtec.workflow.engine.SnakerEngine;
import com.efangtec.workflow.engine.core.ServiceContext;
import com.efangtec.workflow.engine.helper.AssertHelper;
import com.efangtec.workflow.engine.helper.StringHelper;
import com.efangtec.workflow.engine.model.ProcessModel;
import com.efangtec.workflow.engine.scheduling.IScheduler;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 抽象的job类
 *
 * @author yuqs
 * @since 1.4
 */
public abstract class AbstractJob implements Job {
    private static final Logger log = LoggerFactory.getLogger(AbstractJob.class);
    /**
     * 调度器接口
     */
    private IScheduler scheduler;
    /**
     * 流程引擎
     */
    protected SnakerEngine engine = ServiceContext.getEngine();

    @SuppressWarnings("unchecked")
    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        if (engine == null) throw new JobExecutionException("Snaker流程引擎初始化失败.");
        Map<String, Object> data = context.getJobDetail().getJobDataMap().getWrappedMap();
        if (data == null) throw new JobExecutionException("根据job执行的上下文获取不到snaker相关信息.");
        String key = (String) data.get(IScheduler.KEY);
        String model = (String) data.get(IScheduler.MODEL);
        AssertHelper.notEmpty(key);
        data.remove(IScheduler.KEY);
        data.remove(IScheduler.MODEL);

        String[] ids = key.split("-");
        if (ids.length != 3) {
            log.warn("id值不合法,执行操作被忽略.");
            return;
        }
        String processId = ids[0];
        String orderId = ids[1];
        String taskId = ids[2];

        com.efangtec.workflow.engine.entity.Process process = engine.process().getProcessById(processId);
        ProcessModel processModel = process.getModel();
        com.efangtec.workflow.engine.model.NodeModel nodeModel = null;
        if (processModel != null && StringHelper.isNotEmpty(model)) {
            nodeModel = processModel.getNode(model);
        }
        exec(process, orderId, taskId, nodeModel, data);
    }

    /**
     * 抽象方法，由具体的job进行处理
     *
     * @param process   流程定义对象
     * @param orderId   流程实例id
     * @param taskId    任务id
     * @param nodeModel 节点模型
     * @param data      执行数据
     * @throws JobExecutionException job执行异常
     */
    abstract void exec(com.efangtec.workflow.engine.entity.Process process, String orderId,
                       String taskId, com.efangtec.workflow.engine.model.NodeModel nodeModel, Map<String, Object> data) throws JobExecutionException;

    /**
     * 获取调度器接口
     *
     * @return
     */
    protected IScheduler schedule() {
        if (scheduler == null) {
            scheduler = ServiceContext.getContext().find(IScheduler.class);
        }
        return scheduler;
    }
}
