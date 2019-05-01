package com.workgroup.figureplayground.figure;

public class Vector extends Point {

    private Double z;

    public Vector(Double x, Double y, Double z) {
        super(x, y);
        this.z = z;
    }

    public Double getZ() {
        return z;
    }

    public void setZ(Double z) {
        this.z = z;
    }
}
