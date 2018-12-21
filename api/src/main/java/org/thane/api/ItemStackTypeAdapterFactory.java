package org.thane.api;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

public abstract class ItemStackTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    public String toString() {
        return this.getClass().getName();
    }
}
