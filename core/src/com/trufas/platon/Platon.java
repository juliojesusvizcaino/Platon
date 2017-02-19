package com.trufas.platon;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

public class Platon extends ApplicationAdapter implements InputProcessor {
    private ModelBatch modelBatch;
    private Environment environment;
    private MyCamera cam;
    private AssetManager assets;
    private boolean loading;
    private Array<MyModelInstance> instances = new Array<MyModelInstance>();
    private CameraInputController camController;
    private DirectionalLight light;
    private Model testModel;
    private float elapsed = 0;
    protected Stage stage;
    protected Label label;
    protected BitmapFont font;
    protected StringBuilder stringBuilder;
    int destroyNum = 0;

    @Override
    public void create() {
        modelBatch = new ModelBatch();

        cam = new MyCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(0, 0, 0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();

        Gdx.input.setInputProcessor(this);

        light = new DirectionalLight().set(0.8f, 0.8f, 0.8f, cam.direction);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(light);

        assets = new AssetManager();
        assets.load("platon.g3db", Model.class);
        loading = true;

        stage = new Stage();
        font = new BitmapFont();
        label = new Label(" ", new Label.LabelStyle(font, Color.BLACK));
        label.setFontScale(2f);
        stage.addActor(label);
        stringBuilder = new StringBuilder();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    private void doneLoading() {
        testModel = assets.get("platon.g3db", Model.class);

        loading = false;
    }

    private void addTarget() {
        MyModelInstance testInstance = new MyModelInstance(testModel, "test");
        float posMax = 30, posMin = 5;
        MyVector3 pos = (MyVector3) new MyVector3().setToRandomDirection().scl(MathUtils.random()).scl(posMax - posMin);
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
        Gdx.gl.glClearColor(240 / 255f, 240 / 255f, 240 / 255f, 0.5f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        cam.update();
        light.setDirection(cam.direction);

        elapsed += Gdx.graphics.getDeltaTime();

        if (elapsed > 1.0f) {
            elapsed -= 1.0f;
            addTarget();
        }

        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        modelBatch.end();

        stringBuilder.setLength(0);
        stringBuilder.append(" Destroyed: ").append(destroyNum);
        stringBuilder.append(" Remain: ").append(instances.size);
        label.setText(stringBuilder);
        stage.draw();
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

    public int getObject(int screenX, int screenY) {
        Ray ray = cam.getPickRay(screenX, screenY);
        int result = -1;
        Vector3 position = new Vector3();
        float distance = -1;

        for (int i = 0; i < instances.size; i++) {
            final MyModelInstance instance = instances.get(i);

            instance.transform.getTranslation(position);
            position.add(instance.center);

            float dist2 = ray.origin.dst2(position);
            if (distance >= 0f && dist2 > distance)
                continue;

            if (Intersector.intersectRaySphere(ray, position, instance.radius, null)) {
                result = i;
                distance = dist2;
            }
        }

        return result;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        int object = getObject(screenX, screenY);
        if (object != -1) {
            instances.removeIndex(getObject(screenX, screenY));
            destroyNum++;
        }

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
