package com.efangtec.workflow.condition;

import com.efangtec.workflow.engine.DecisionHandler;
import com.efangtec.workflow.engine.SnakerEngine;
import com.efangtec.workflow.engine.core.Execution;
import com.efangtec.workflow.engine.handlers.IHandler;
import lombok.Data;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

/**
 *
 *
 * 自定义决策方式
 * 即-审核不通过
 *   -审核通过走1+1模式
 *   -审核通过走1+2模式
 *   -审核通过走1+3模式
 */
@Data
public class ConditionParam implements DecisionHandler {
    private final  String model_1 = "model_1";
    private final  String model_2 = "model_3";
    private final  String model_3 = "model_3";

    //args.put("content", "toTask3");
    @Override
    public String decide(Execution execution) {
        System.err.println("execution = "+execution);
        return (String)execution.getArgs().get("content");
    }
}
