package com.xtuml.compiler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class State {

    @JsonProperty("state_id")
    private String stateId;

    @JsonProperty("state_name")
    private String stateName;

    @JsonProperty("state_value")
    private String stateValue;

    @JsonProperty("state_type")
    private String stateType;

    @JsonProperty("state_event")
    private List<String> stateEvents;

    @JsonProperty("action")
    private String action;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getStateValue() {
        return stateValue;
    }

    public void setStateValue(String stateValue) {
        this.stateValue = stateValue;
    }

    public String getStateType() {
        return stateType;
    }

    public void setStateType(String stateType) {
        this.stateType = stateType;
    }

    public List<String> getStateEvents() {
        return stateEvents;
    }

    public void setStateEvents(List<String> stateEvents) {
        this.stateEvents = stateEvents;
    }
}
