package com.example;

import java.io.File;
import java.io.IOException;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BpmnModelElementInstance;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.json.JSONObject;

public class App {

    public static void main(String[] args) {
        JSONObject process_values = new JSONObject(
                "{" +
                        "process_id: process-with-one-task" +
                        "}");
        JSONObject start_event_values = new JSONObject(
                "{" +
                        "start_id: Start-Task" +
                        "}");
        JSONObject user_event_values = new JSONObject(
                "{" +
                        "user_event_id: User-Task" +
                        "}");

        // create an empty model
        BpmnModelInstance modelInstance = Bpmn.createEmptyModel();
        Definitions definitions = modelInstance.newInstance(Definitions.class);
        definitions.setTargetNamespace("http://camunda.org/examples");
        modelInstance.setDefinitions(definitions);

        // create the process
        Process process = createElement(definitions, process_values.getString("process_id"), Process.class);
        process.setExecutable(true);

        // create start event, user task and end event
        StartEvent startEvent = createElement(process, start_event_values.getString("start_id"), StartEvent.class);
        UserTask task1 = createElement(process, user_event_values.getString("user_event_id"), UserTask.class);
        task1.setName("User Task");
        EndEvent endEvent = createElement(process, "end", EndEvent.class);

        // create the connections between the elements
        createSequenceFlow(process, startEvent, task1);
        createSequenceFlow(process, task1, endEvent);

        // validate and write model to file
        Bpmn.validateModel(modelInstance);
        File myObj = new File("filename.bpmn");
        try {
            myObj.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Bpmn.writeModelToFile(myObj, modelInstance);

    }

    protected static <T extends BpmnModelElementInstance> T createElement(BpmnModelElementInstance parentElement,
            String id, Class<T> elementClass) {
        T element = parentElement.getModelInstance().newInstance(elementClass);
        element.setAttributeValue("id", id, true);
        parentElement.addChildElement(element);
        return element;
    }

    public static SequenceFlow createSequenceFlow(Process process, FlowNode from, FlowNode to) {
        String identifier = from.getId() + "-" + to.getId();
        SequenceFlow sequenceFlow = createElement(process, identifier, SequenceFlow.class);
        process.addChildElement(sequenceFlow);
        sequenceFlow.setSource(from);
        from.getOutgoing().add(sequenceFlow);
        sequenceFlow.setTarget(to);
        to.getIncoming().add(sequenceFlow);
        return sequenceFlow;
    }

}
