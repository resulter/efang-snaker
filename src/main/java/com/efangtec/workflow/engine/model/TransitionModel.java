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
import com.efangtec.workflow.engine.core.Execution;
import com.efangtec.workflow.engine.handlers.impl.CreateTaskHandler;
import com.efangtec.workflow.engine.handlers.impl.StartSubProcessHandler;

/**
 * 变迁定义transition元素
 * @author yuqs
 * @since 1.0
 */
public class TransitionModel extends BaseModel implements Action {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3688123410411321158L;
	/**
	 * 变迁的源节点引用
	 */
	private NodeModel source;
	/**
	 * 变迁的目标节点引用
	 */
	private NodeModel target;
	/**
	 * 变迁的目标节点name名称
	 */
	private String to;
	/**
	 * 变迁的条件表达式，用于decision
	 */
	private String expr;
	/**
	 * 转折点图形数据
	 */
	private String g;
	/**
	 * 描述便宜位置
	 */
	private String offset;
	/**
	 * 当前变迁路径是否可用
	 */
	private boolean enabled = false;
	
	public void execute(Execution execution) {
		if(!enabled) return;
		if(target instanceof TaskModel) {
			//如果目标节点模型为TaskModel，则创建task
			fire(new CreateTaskHandler((TaskModel)target), execution);
		} else if(target instanceof SubProcessModel) {
			//如果目标节点模型为SubProcessModel，则启动子流程
			fire(new StartSubProcessHandler((SubProcessModel)target), execution);
		} else {
			//如果目标节点模型为其它控制类型，则继续由目标节点执行
			target.execute(execution);
		}
	}

	/**
	 * 获取下一个节点的类型
	 * @return
	 */
	public Integer getNodeType(){
//		if(!enabled) return 0;
		if(target instanceof TaskModel) {
			//如果目标节点模型为TaskModel，则创建task
			return  1;
		}else if(target instanceof DecisionModel) {
			//如果目标节点模型为TaskModel，则创建task
			return  2;
		}else if(target instanceof EndModel) {
			//如果目标节点模型为TaskModel，则创建task
			return  3;
		}else if(target instanceof StartModel) {
			//如果目标节点模型为TaskModel，则创建task
			return  4;
		}else {
			return -1;
		}
	}
	
	public NodeModel getSource() {
		return source;
	}
	public void setSource(NodeModel source) {
		this.source = source;
	}
	public NodeModel getTarget() {
		return target;
	}
	public void setTarget(NodeModel target) {
		this.target = target;
	}
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getExpr() {
		return expr;
	}

	public void setExpr(String expr) {
		this.expr = expr;
	}

	public String getG() {
		return g;
	}

	public void setG(String g) {
		this.g = g;
	}

	public String getOffset() {
		return offset;
	}

	public void setOffset(String offset) {
		this.offset = offset;
	}
}
