package com.trufas.platon;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

public class MyCamera extends PerspectiveCamera {
    private float rotX = 0, rotY = 0, rotZ = 0;

    public MyCamera(int i, int width, int height) {
        super(i, width, height);
    }

    public float getRotX() {
        return rotX;
    }

    public float getRotY() {
        return rotY;
    }

    public float getRotZ() {
        return rotZ;
    }

    @Override
    public void rotate(Vector3 axis, float angle) {
        if (axis.epsilonEquals(Vector3.X, 0.1f))
            rotX += angle;
        else if (axis.epsilonEquals(Vector3.Y, 0.1f))
            rotY += angle;
        else if (axis.epsilonEquals(Vector3.Z, 0.1f))
            rotZ += angle;
        super.rotate(axis, angle);
    }
}
