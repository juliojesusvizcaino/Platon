package com.trufas.platon;

import com.badlogic.gdx.math.MathUtils;
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
        return MathUtils.atan2(-z, x);
    }

    public float getPolar() {
        return (float) Math.acos(y / getRadius());
    }

    public Vector3 addRadius(float rad) {
        float newR = rad + getRadius();
        return this.set(newR * MathUtils.sin(getPolar()) * MathUtils.cos(getAzimuthal()),
                newR * MathUtils.cos(getPolar()),
                -newR * MathUtils.sin(getPolar()) * MathUtils.sin(getAzimuthal()));
    }
}
