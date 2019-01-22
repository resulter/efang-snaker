package com.efangtec.project.entity;

import lombok.Data;

@Data
public class BsActorTask {
    int id;
    int actorId;
    String taskId;
    String orderId;

    public BsActorTask() {
    }

    public BsActorTask(int actorId, String taskId, String orderId) {
        this.actorId = actorId;
        this.taskId = taskId;
        this.orderId = orderId;
    }
}
