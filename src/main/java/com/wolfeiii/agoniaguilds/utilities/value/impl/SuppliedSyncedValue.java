package com.wolfeiii.agoniaguilds.utilities.value.impl;

import java.util.function.Supplier;

public class SuppliedSyncedValue<V extends Number> implements SyncedValue<V> {

    private final Supplier<V> supplier;

    public SuppliedSyncedValue(Supplier<V> supplier) {
        this.supplier = supplier;
    }

    @Override
    public V get() {
        return supplier.get();
    }

}