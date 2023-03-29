package com.wolfeiii.agoniaguilds.utilities.value;

import com.wolfeiii.agoniaguilds.utilities.value.impl.FixedSyncedValue;
import com.wolfeiii.agoniaguilds.utilities.value.impl.FixedValue;
import com.wolfeiii.agoniaguilds.utilities.value.impl.SuppliedSyncedValue;
import com.wolfeiii.agoniaguilds.utilities.value.impl.SyncedValue;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public interface Value<V extends Number> {

    V get();

    default V getRaw(V syncedValue) {
        return this instanceof SyncedValue ? syncedValue : get();
    }

    static <V extends Number> Value<V> fixed(V value) {
        return new FixedValue<>(value);
    }

    static <V extends Number> Value<V> syncedSupplied(Supplier<V> supplier) {
        return new SuppliedSyncedValue<>(supplier);
    }

    static <V extends Number> Value<V> syncedFixed(V value) {
        return new FixedSyncedValue<>(value);
    }

    static <V extends Number> V getRaw(@Nullable Value<V> value, V syncedValue) {
        return value == null ? syncedValue : value.getRaw(syncedValue);
    }

}