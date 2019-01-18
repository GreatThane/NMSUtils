package org.thane.api;

import com.google.gson.TypeAdapterFactory;

public abstract class ItemStackTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    public String toString() {
        return this.getClass().getName();
    }
}
