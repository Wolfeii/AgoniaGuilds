package com.wolfeiii.agoniaguilds.utilities.objects;

public class Mutable<E> {

    private E value;

    public Mutable(E value) {
        this.value = value;
    }

    public E getValue() {
        return value;
    }

    public void setValue(E value) {
        this.value = value;
    }

}