package com.efangtec.workflow.flow.ext;


import com.efangtec.workflow.engine.model.TaskModel;

/**
 * 自定义任务模型
 * @author yuqs
 * @since 0.1
 */
public class ExtTaskModel extends TaskModel {
    private String assigneeDisplay;
    public String getAssigneeDisplay() {
        return assigneeDisplay;
    }

    public void setAssigneeDisplay(String assigneeDisplay) {
        this.assigneeDisplay = assigneeDisplay;
    }
}
