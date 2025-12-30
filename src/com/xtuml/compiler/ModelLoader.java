package com.xtuml.compiler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtuml.compiler.model.MetaModel;
import java.io.File;
import java.io.IOException;

public class ModelLoader {

    private final ObjectMapper mapper;

    public ModelLoader() {
        this.mapper = new ObjectMapper();
    }

    public MetaModel load(String filePath) throws IOException {
        return mapper.readValue(new File(filePath), MetaModel.class);
    }
    
    public MetaModel load(File file) throws IOException {
        return mapper.readValue(file, MetaModel.class);
    }
}
