package com.xtuml.compiler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ModelClass extends XtUmlModelElement {

    @JsonProperty("class_id")
    private String classId;

    @JsonProperty("class_name")
    private String className;

    @JsonProperty("KL")
    private String keyLetters;

    @JsonProperty("attributes")
    private List<Attribute> attributes;

    @JsonProperty("states")
    private List<State> states;

    @JsonProperty("operations")
    private List<Operation> operations;

    @JsonProperty("supertype")
    private String supertype;

    public String getSupertype() {
        return supertype;
    }

    public void setSupertype(String supertype) {
        this.supertype = supertype;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getKeyLetters() {
        return keyLetters;
    }

    public void setKeyLetters(String keyLetters) {
        this.keyLetters = keyLetters;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public List<State> getStates() {
        return states;
    }

    public void setStates(List<State> states) {
        this.states = states;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public void setOperations(List<Operation> operations) {
        this.operations = operations;
    }
}
