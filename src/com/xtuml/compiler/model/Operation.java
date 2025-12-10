package com.xtuml.compiler.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Operation {
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("return_type")
    private String returnType;
    
    @JsonProperty("action")
    private String action;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getReturnType() { return returnType; }
    public void setReturnType(String returnType) { this.returnType = returnType; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}
