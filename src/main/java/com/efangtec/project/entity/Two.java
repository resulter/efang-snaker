package com.efangtec.project.entity;

import lombok.Data;

@Data
public class Two {
    int id;
    String name;
    String orderId;
    String taskId;
    String msg;

    public Two() {
    }

    public Two(int id, String name, String orderId, String taskId, String msg) {
        this.id = id;
        this.name = name;
        this.orderId = orderId;
        this.taskId = taskId;
        this.msg = msg;
    }

    public Two(String name, String orderId, String taskId) {
        this.name = name;
        this.orderId = orderId;
        this.taskId = taskId;
    }
}
