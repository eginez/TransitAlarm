package com.eginez.transitalarm.model.remote.deserializers

import com.google.gson.JsonDeserializer

import java.lang.reflect.Type

public interface Deserializable<T> extends JsonDeserializer<T> {
    public Type getType()
}