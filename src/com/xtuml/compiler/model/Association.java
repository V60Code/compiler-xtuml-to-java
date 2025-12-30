package com.xtuml.compiler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Association extends XtUmlModelElement {

    @JsonProperty("name")
    private String name;

    @JsonProperty("class")
    private List<AssociationEnd> classes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AssociationEnd> getClasses() {
        return classes;
    }

    public void setClasses(List<AssociationEnd> classes) {
        this.classes = classes;
    }
}
