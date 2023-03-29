package com.wolfeiii.agoniaguilds.utilities.value.impl;

public class FixedSyncedValue<V extends Number> implements SyncedValue<V> {

    private final V value;

    public FixedSyncedValue(V value) {
        this.value = value;
    }

    @Override
    public V get() {
        return value;
    }

}