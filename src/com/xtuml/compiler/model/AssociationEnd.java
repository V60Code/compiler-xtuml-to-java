package com.xtuml.compiler.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AssociationEnd {

    @JsonProperty("class_id")
    private String classId;

    @JsonProperty("class_name")
    private String className;

    @JsonProperty("class_multiplicity")
    private String multiplicity;

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

    public String getMultiplicity() {
        return multiplicity;
    }

    public void setMultiplicity(String multiplicity) {
        this.multiplicity = multiplicity;
    }
}
