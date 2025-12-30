package com.xtuml.compiler.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Attribute {

    @JsonProperty("attribute_type")
    private String attributeType;

    @JsonProperty("attribute_name")
    private String attributeName;

    @JsonProperty("data_type")
    private String dataType;

    @JsonProperty("default_value")
    private String defaultValue;

    public String getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(String attributeType) {
        this.attributeType = attributeType;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
