package com.trufas.platon;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class Platon extends ApplicationAdapter implements InputProcessor {
    private ModelBatch modelBatch;
    private Environment environment;
    private MyCamera cam;
    private AssetManager assets;
    private boolean loading;
    private Array<MyModelInstance> instances = new Array<MyModelInstance>();
    private DirectionalLight light;
    private Model testModel;
    private float elapsed = 0;
    private Stage stage;
    private Stage endStage;
    private StringBuilder stringBuilder;
    private int destroyNum = 0;
    private float time = 30;
    private Label remainLabel, timeLabel, destroyLabel, endLabel;
    private Skin skin;
    private boolean screenLoaded = false;
    private float limit = 1.0f;

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

        stage = new Stage(new ExtendViewport(640, 480, 800, 480));
        endStage = new Stage(new ExtendViewport(640, 480, 800, 480));
        BitmapFont font = new BitmapFont();
        remainLabel = new Label(" ", new Label.LabelStyle(font, Color.BLACK));
        timeLabel = new Label(" ", new Label.LabelStyle(font, Color.BLACK));
        destroyLabel = new Label(" ", new Label.LabelStyle(font, Color.BLACK));
        endLabel = new Label(" ", new Label.LabelStyle(font, Color.BLACK));
        stringBuilder = new StringBuilder();

        Table table = new Table();
        Table timeTable = new Table();
        Table destroyTable = new Table();
        Table remainTable = new Table();
        Table endTable = new Table();

        table.setFillParent(true);
        table.top();
        table.add(destroyTable).expandX().fill();
        table.add(timeTable).expandX().fill();
        table.add(remainTable).expandX().fill();
        destroyTable.add(destroyLabel).expand().left();
        timeTable.add(timeLabel).expand().center();
        remainTable.add(remainLabel).expand().right();


        skin = new Skin();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("white", new Texture(pixmap));

        skin.add("default", font);

        // Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't overwrite the font.
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
        textButtonStyle.font = skin.getFont("default");
        skin.add("default", textButtonStyle);

        endTable.setFillParent(true);
        stage.addActor(table);
        endStage.addActor(endTable);


        TextButton resetButton = new TextButton("RESET", skin);

        endTable.center();
        endTable.add(endLabel).center();
        endTable.row();
        endTable.add(resetButton);

        resetButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                resetGame();
                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }

    private void resetGame() {
        destroyNum = 0;
        instances.clear();
        time = 30;
        screenLoaded = false;
        limit = 1.0f;
        elapsed = 0f;
        Gdx.input.setInputProcessor(this);
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

        if (time > 0) {
            cam.update();
            light.setDirection(cam.direction);
            time -= Gdx.graphics.getDeltaTime();

        } else {
            endGame();
            endStage.act(Gdx.graphics.getDeltaTime());
            endStage.draw();
        }

        elapsed += Gdx.graphics.getDeltaTime();

        if (elapsed > limit) {
            elapsed -= limit;
            limit *= 0.95f;
            addTarget();
        }

        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        modelBatch.end();

        stringBuilder.setLength(0);
        stringBuilder.append(destroyNum);
        destroyLabel.setText(stringBuilder);
        stringBuilder.setLength(0);
        stringBuilder.append(instances.size);
        remainLabel.setText(stringBuilder);
        stringBuilder.setLength(0);
        stringBuilder.append(((int) time));
        timeLabel.setText(stringBuilder);
        stage.draw();
    }

    private void endGame() {
        if (!screenLoaded) {
            Gdx.input.setInputProcessor(endStage);
            stringBuilder.setLength(0);
            stringBuilder.append(destroyNum);
            endLabel.setText(stringBuilder);
            screenLoaded = true;
        }
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
        stage.dispose();
        endStage.dispose();
        skin.dispose();
    }

    private int getObject(int screenX, int screenY) {
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
