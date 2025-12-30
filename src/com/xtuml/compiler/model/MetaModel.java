package com.xtuml.compiler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class MetaModel {

    @JsonProperty("type")
    private String type;

    @JsonProperty("sub_id")
    private String subId;

    @JsonProperty("sub_name")
    private String subName;

    @JsonProperty("model")
    private List<XtUmlModelElement> model;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubId() {
        return subId;
    }

    public void setSubId(String subId) {
        this.subId = subId;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public List<XtUmlModelElement> getModel() {
        return model;
    }

    public void setModel(List<XtUmlModelElement> model) {
        this.model = model;
    }
}
