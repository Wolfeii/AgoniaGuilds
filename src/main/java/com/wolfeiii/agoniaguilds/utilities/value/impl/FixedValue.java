package com.wolfeiii.agoniaguilds.utilities.value.impl;

import com.wolfeiii.agoniaguilds.utilities.value.Value;

public class FixedValue<V extends Number> implements Value<V> {

    private final V value;

    public FixedValue(V value) {
        this.value = value;
    }

    @Override
    public V get() {
        return value;
    }

}