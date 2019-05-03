package com.workgroup.figureplayground.figure;

public class Vector extends Point {

    private Float z;

    public Vector(Float x, Float y, Float z) {
        super(x, y);
        this.z = z;
    }

    public Float getZ() {
        return z;
    }

    public void setZ(Float z) {
        this.z = z;
    }
}
