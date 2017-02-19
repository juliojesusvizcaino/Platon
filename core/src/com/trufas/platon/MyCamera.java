package com.trufas.platon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class MyCamera extends PerspectiveCamera {
    private float rotX = 0, rotY = 0, rotZ = 0;
    private boolean compassAvail;

    public MyCamera(int i, int width, int height) {
        super(i, width, height);
        compassAvail = Gdx.input.isPeripheralAvailable(Input.Peripheral.Compass);
    }

    private Vector3 lpFilter(Vector3 x, Vector3 s, float delta) {
        return new Vector3(lpFilter(x.x, s.x, delta), lpFilter(x.y, s.y, delta), lpFilter(x.z, s.z, delta));
    }

    private float lpFilter(float x, float s, float delta) {
        return lpFilter(x, s, delta, 0.1f);
    }

    private float lpFilter(float x, float s, float delta, float smoothing) {
        return s + delta * (x - s) / smoothing;
    }

    @Override
    public void update() {
        if (compassAvail) {
            Matrix4 t = new Matrix4();
            Gdx.input.getRotationMatrix(t.val);

            float delta = Gdx.graphics.getDeltaTime();
            Vector3 newUp = lpFilter(new Vector3(t.val[Matrix4.M01], t.val[Matrix4.M02], t.val[Matrix4.M00]),
                    up, delta);
            Vector3 newDirection = lpFilter(new Vector3(-t.val[Matrix4.M21], -t.val[Matrix4.M22], -t.val[Matrix4.M20]),
                    direction, delta);

            direction.set(newDirection);
            up.set(newUp);
        }
        super.update();
    }
}
