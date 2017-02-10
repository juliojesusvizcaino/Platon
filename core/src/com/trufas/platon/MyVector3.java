package com.trufas.platon;

import com.badlogic.gdx.math.Vector3;

public class MyVector3 extends Vector3 {
    public final static Vector3 XY = new Vector3(1, 1, 0);
    public final static Vector3 XZ = new Vector3(1, 0, 1);
    public final static Vector3 YZ = new Vector3(0, 1, 1);
    public final static Vector3 XYZ = new Vector3(1, 1, 1);

    public MyVector3() {
        super();
    }

    public float getRadius() {
        return this.dst(Vector3.Zero);
    }

    public float getAzimuthal() {
        return (float) Math.atan(this.y / this.x);
    }

    public float getPolar() {
        return (float) Math.atan(Math.sqrt(this.x * this.x) / this.z);
    }

    public void addRadius(float rad) {
        float newR = rad + getRadius();
        this.x += newR * Math.sin(getPolar()) * Math.cos(getAzimuthal());
        this.y += newR * Math.sin(getPolar()) * Math.sin(getAzimuthal());
        this.z += newR * Math.cos(getPolar());
    }
}
