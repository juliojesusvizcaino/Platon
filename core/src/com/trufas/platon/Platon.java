package com.trufas.platon;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class Platon extends ApplicationAdapter {
    private ModelBatch modelBatch;
    private Environment environment;
    private PerspectiveCamera cam;
    private AssetManager assets;
    private boolean loading;
    private Array<ModelInstance> instances = new Array<ModelInstance>();
    private CameraInputController camController;
    private DirectionalLight light;
    private Model testModel;

    @Override
    public void create() {
        modelBatch = new ModelBatch();

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0, 0, 0);
        cam.lookAt(10f, 0, 0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();

        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(camController);

        light = new DirectionalLight().set(0.8f, 0.8f, 0.8f, cam.direction);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(light);

        assets = new AssetManager();
        assets.load("platon.g3db", Model.class);
        loading = true;
    }

    @Override
    public void resize(int width, int height) {

    }

    private void doneLoading() {
        testModel = assets.get("platon.g3db", Model.class);

        loading = false;
    }

    private void addTarget() {
        ModelInstance testInstance = new ModelInstance(testModel, "test");
        float posMax = 50, posMin = 10;
        MyVector3 pos = (MyVector3) new MyVector3().setToRandomDirection().scl(posMax - posMin);
        pos.addRadius(posMin);

        testInstance.transform.setToTranslation(pos).
                rotateRad(Vector3.Y, (float) (Math.PI - Math.atan2(pos.z, pos.x))).
                rotateRad(Vector3.Z, (float) -Math.atan2(pos.y, Math.sqrt(pos.x * pos.x + pos.z * pos.z)));
        instances.add(testInstance);
    }

	@Override
	public void render () {
        if (loading && assets.update())
            doneLoading();
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        camController.update();
        light.setDirection(cam.direction);

        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        modelBatch.end();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        instances.clear();
        assets.dispose();
    }
}
