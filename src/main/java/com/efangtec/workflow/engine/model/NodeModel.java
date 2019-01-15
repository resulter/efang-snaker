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
package com.efangtec.workflow.engine.model;

import com.efangtec.workflow.engine.Action;
import com.efangtec.workflow.engine.SnakerException;
import com.efangtec.workflow.engine.SnakerInterceptor;
import com.efangtec.workflow.engine.core.Execution;
import com.efangtec.workflow.engine.helper.ClassHelper;
import com.efangtec.workflow.engine.helper.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 节点元素（存在输入输出的变迁）
 * @author yuqs
 * @since 1.0
 */
public abstract class NodeModel extends com.efangtec.workflow.engine.model.BaseModel implements Action {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2377317472320109317L;
	private static final Logger log = LoggerFactory.getLogger(NodeModel.class);
	/**
	 * 输入变迁集合
	 */
	private List<com.efangtec.workflow.engine.model.TransitionModel> inputs = new ArrayList<com.efangtec.workflow.engine.model.TransitionModel>();
	/**
	 * 输出变迁集合
	 */
	private List<com.efangtec.workflow.engine.model.TransitionModel> outputs = new ArrayList<com.efangtec.workflow.engine.model.TransitionModel>();
	/**
	 * layout
	 */
	private String layout;
	/**
	 * 局部前置拦截器
	 */
	private String preInterceptors;
	/**
	 * 局部后置拦截器
	 */
	private String postInterceptors;
	/**
	 * 前置局部拦截器实例集合
	 */
	private List<SnakerInterceptor> preInterceptorList = new ArrayList<SnakerInterceptor>();
	/**
	 * 后置局部拦截器实例集合
	 */
	private List<SnakerInterceptor> postInterceptorList = new ArrayList<SnakerInterceptor>();
	
	/**
	 * 具体节点模型需要完成的执行逻辑
	 * @param execution 执行对象
	 */
	protected abstract void exec(Execution execution);
	
	/**
	 * 对执行逻辑增加前置、后置拦截处理
	 * @param execution 执行对象
	 */
	public void execute(Execution execution) {
		intercept(preInterceptorList, execution);
		exec(execution);
		intercept(postInterceptorList, execution);
	}
	
	/**
	 * 运行变迁继续执行
	 * @param execution 执行对象
	 */
	protected void runOutTransition(Execution execution) {

		//System.out.println("getOutputs() = "+getOutputs());

		for (com.efangtec.workflow.engine.model.TransitionModel tm : getOutputs()) {
			tm.setEnabled(true);
			tm.execute(execution);
		}
	}
	
	/**
	 * 拦截方法
	 * @param interceptorList 拦截器列表
	 * @param execution 执行对象
	 */
	private void intercept(List<SnakerInterceptor> interceptorList, Execution execution) {
		try {
			for(SnakerInterceptor interceptor : interceptorList) {
				interceptor.intercept(execution);
			}
		} catch(Exception e) {
			log.error("拦截器执行失败=" + e.getMessage());
            throw new SnakerException(e);
		}
	}
	
	/**
	 * 根据父节点模型、当前节点模型判断是否可退回。可退回条件：
	 * 1、满足中间无fork、join、subprocess模型
	 * 2、满足父节点模型如果为任务模型时，参与类型为any
	 * @param parent 父节点模型
	 * @return 是否可以退回
	 */
	public static boolean canRejected(NodeModel current, NodeModel parent) {
		if(parent instanceof TaskModel && !((TaskModel)parent).isPerformAny()) {
			return false;
		}
        boolean result = false;
		for(com.efangtec.workflow.engine.model.TransitionModel tm : current.getInputs()) {
			NodeModel source = tm.getSource();
			if(source == parent) {
				return true;
			}
			if(source instanceof com.efangtec.workflow.engine.model.ForkModel
					|| source instanceof com.efangtec.workflow.engine.model.JoinModel
					|| source instanceof com.efangtec.workflow.engine.model.SubProcessModel
					|| source instanceof com.efangtec.workflow.engine.model.StartModel) {
				continue;
			}
			result = result || canRejected(source, parent);
		}
		return result;
	}

	/**
	 * * 获取节点的类型
	 * TaskModel == 1;
	 * DecisionModel == 2;
	 * @param current
	 * @return
	 */
	public static Integer getNodeType(NodeModel current){
		if(ObjectUtils.isEmpty(current)) return 0;
		if(current instanceof TaskModel) {
			return  1;
		}else if(current instanceof DecisionModel) {
			return  2;
		}else {
			return -1;
		}
	}

    public <T> List<T> getNextModels(Class<T> clazz) {
        List<T> models = new ArrayList<T>();
        for(com.efangtec.workflow.engine.model.TransitionModel tm : this.getOutputs()) {
            addNextModels(models, tm, clazz);
        }
        return models;
    }

    protected <T> void addNextModels(List<T> models, com.efangtec.workflow.engine.model.TransitionModel tm, Class<T> clazz) {
        if(clazz.isInstance(tm.getTarget())) {
            models.add((T)tm.getTarget());
        } else {
            for(com.efangtec.workflow.engine.model.TransitionModel tm2 : tm.getTarget().getOutputs()) {
                addNextModels(models, tm2, clazz);
            }
        }
    }
	
	public List<com.efangtec.workflow.engine.model.TransitionModel> getInputs() {
		return inputs;
	}
	public void setInputs(List<com.efangtec.workflow.engine.model.TransitionModel> inputs) {
		this.inputs = inputs;
	}
	public List<com.efangtec.workflow.engine.model.TransitionModel> getOutputs() {
		return outputs;
	}
	public void setOutputs(List<com.efangtec.workflow.engine.model.TransitionModel> outputs) {
		this.outputs = outputs;
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	public String getPreInterceptors() {
		return preInterceptors;
	}

	public void setPreInterceptors(String preInterceptors) {
		this.preInterceptors = preInterceptors;
		if(StringHelper.isNotEmpty(preInterceptors)) {
			for(String interceptor : preInterceptors.split(",")) {
				SnakerInterceptor instance = (SnakerInterceptor)ClassHelper.newInstance(interceptor);
				if(instance != null) this.preInterceptorList.add(instance);
			}
		}
	}

	public String getPostInterceptors() {
		return postInterceptors;
	}

	public void setPostInterceptors(String postInterceptors) {
		this.postInterceptors = postInterceptors;
		if(StringHelper.isNotEmpty(postInterceptors)) {
			for(String interceptor : postInterceptors.split(",")) {
				SnakerInterceptor instance = (SnakerInterceptor)ClassHelper.newInstance(interceptor);
				if(instance != null) this.postInterceptorList.add(instance);
			}
		}
	}
}
