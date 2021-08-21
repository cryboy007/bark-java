package com.tao.common.retry;

import com.fasterxml.jackson.core.type.TypeReference;

import java.lang.reflect.Type;
import java.util.List;

public class ParameterizedTypeReference extends TypeReference<List<Object>> {
    private Type type;

    public ParameterizedTypeReference(Type type) {
        this.type = type;
    }

    @Override
    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

}
