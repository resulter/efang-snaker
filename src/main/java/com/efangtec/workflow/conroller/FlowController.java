package com.efangtec.workflow.conroller;

import com.efangtec.project.entity.Two;
import com.efangtec.workflow.engine.DBAccess;
import com.efangtec.workflow.engine.access.Page;
import com.efangtec.workflow.engine.access.QueryFilter;
import com.efangtec.workflow.engine.entity.HistoryOrder;
import com.efangtec.workflow.engine.entity.HistoryTask;
import com.efangtec.workflow.engine.entity.Process;
import com.efangtec.workflow.engine.model.*;
import com.efangtec.workflow.service.SnakerEngineFacets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author yuqs
 * @since 2.0
 */
@Controller
@RequestMapping(value = "/snaker/flow")
public class FlowController {
    public static final String PARA_PROCESSID = "processId";
    public static final String PARA_ORDERID = "orderId";
    public static final String PARA_TASKID = "taskId";
    public static final String PARA_TASKNAME = "taskName";
    public static final String PARA_METHOD = "method";
    public static final String PARA_NEXTOPERATOR = "nextOperator";
    public static final String PARA_NODENAME = "nodeName";
    public static final String PARA_CCOPERATOR = "ccOperator";
    @Autowired
    private SnakerEngineFacets facets;
    @Autowired
    DBAccess access;
    /**
     * 流程实例查询
     * @param model
     * @param page
     * @return
     */
    @RequestMapping(value = "order", method= RequestMethod.GET)
    public String order(Model model, Page<HistoryOrder> page) {
        facets.getEngine().query().getHistoryOrders(page, new QueryFilter());
        model.addAttribute("page", page);
        return "snaker/order";
    }



    /**
     * 节点信息以json格式返回
     * all页面以节点信息构造tab及加载iframe
     */
    @RequestMapping(value = "node")
    @ResponseBody
    public Object node(String processId,String orderId) {



        Process process = facets.getEngine().process().getProcessById(processId);
        ProcessModel processModel = process.getModel();
        List<TaskModel> models = process.getModel().getModels(TaskModel.class);
        List<TaskModel> viewModels = new ArrayList<TaskModel>();
        for(TaskModel model : models) {
            List<Map<String,String>> operation = new ArrayList<>();
            String expr= "";
            TaskModel viewModel = new TaskModel();
            viewModel.setName(model.getName());
            viewModel.setDisplayName(model.getDisplayName());
            viewModel.setForm(model.getForm());

            NodeModel nodeModel = processModel.getNode(model.getName());
            List<TransitionModel> outputs = nodeModel.getOutputs();
            StringBuffer disName = new StringBuffer();
            if(ObjectUtils.isEmpty(outputs)){
                viewModel.setNodeType(0);
            }else {
                Integer nodeType = outputs.get(0).getNodeType();
                viewModel.setNodeType(nodeType);
                if(nodeType == 2){
                    DecisionModel target = (DecisionModel)outputs.get(0).getTarget();
                    expr = target.getExpr();
                    List<TransitionModel> nextNodes = outputs.get(0).getTarget().getOutputs();
                    for (int i = 0; i < nextNodes.size(); i++) {
                        Map<String,String> s = new HashMap<>();
                        TransitionModel transitionModel = nextNodes.get(i);
                        String displayName = transitionModel.getTarget().getDisplayName();
                        s.put("path",transitionModel.getName());
                        s.put("pathDisplay",transitionModel.getDisplayName());
                        s.put("to",displayName);
                        operation.add(s);
                    }
                    String s = disName.toString();
                }
            }
            viewModel.setExpr(expr);
            viewModel.setOperation(operation);
            Map<String,Object> business = new HashMap<>();
            if(!StringUtils.isEmpty(orderId)) {
                List<HistoryTask> historyTasks = facets.getEngine().query().getHistoryTasks(new QueryFilter().setOrderId(orderId));
                for (int i = 0; i < historyTasks.size(); i++) {
                    if (historyTasks.get(i).getTaskName().equals(model.getName())) {
                        String taskId = historyTasks.get(i).getId();
                        Two two = access.queryObject(Two.class, "select * from ap_two where task_id = ?", new Object[]{taskId});
                        if (ObjectUtils.isEmpty(two)) {

                        } else {
                            business.put("name", two.getName());
                        }
                    }
                }
                viewModel.setBusiness(business);
            }
            viewModels.add(viewModel);
        }
        return viewModels;
    }


}
