package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class MyGdxGame extends Game {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Player player;
    private ObjectMap<String, Scenario> scenarios;
    private Scenario currentScenario;
    private Connections connections;

    public MyGdxGame() {
        super();
        this.connections = new Connections();

        this.connections = new Connections();
        this.connections.add(
                new Connection(
                        new Endpoint(
                                "forest_1",
                                new Rectangle(877f, 910f, 76f, 64f),
                                new Vector2(880f, 50f)
                        ),
                        new Endpoint(
                                "inner_castle_1",
                                new Rectangle(1262, 378f, 142f, 64f),
                                new Vector2(1310f, 200f)
                        )
                )
        );
        this.scenarios = new ObjectMap<>();
    }

    Scenario getCurrentScenario() {
        return this.currentScenario;
    }

    Array<Junction> getConnections(String scenarioName) {
        return this.connections.get(scenarioName);
    }

    void setCurrentScenario(Scenario currentScenario) {
        this.currentScenario = currentScenario;
        this.setScreen(this.currentScenario);
    }

    Player getPlayer() {
        return this.player;
    }

    SpriteBatch getBatch() {
        return this.batch;
    }

    OrthographicCamera getCamera() {
        return this.camera;
    }

    Scenario getScenario(String name) {
        return this.scenarios.get(name);
    }

    @Override
    public void create() {
        this.batch = new SpriteBatch();

        this.scenarios.put("forest_1",
                new Scenario(
                        this,
                        "forest_1",
                        0f,
                        0f,
                        new Vector2(880f, 50f),
                        "background_1.jpg",
                        "background_1_collision.gif",
                        "background_1_overlay.png"
                ));

        this.scenarios.put("inner_castle_1",
                new Scenario(
                        this,
                        "inner_castle_1",
                        0f,
                        0f,
                        new Vector2(1310f, 410f),
                        "inner_castle_1.png",
                        "inner_castle_1_collision.gif",
                        null
                ));

        this.player = Player.New(this);
        this.setCurrentScenario(this.scenarios.get("forest_1"));
        player.setX(this.currentScenario.getPlayerInitialPosition().x);
        player.setY(this.currentScenario.getPlayerInitialPosition().y);

        this.player.create(Gdx.files);

        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.camera.position.x = this.player.getX();
        this.camera.position.y = this.player.getY();
    }

    private void updateCamera() {
        float dx = this.player.getX() - this.camera.position.x;
        float lx = Gdx.graphics.getWidth() / 5f;

        if (dx > lx) {
            this.camera.position.x = this.player.getX() - lx;
        }

        if (dx < -1f * lx) {
            this.camera.position.x = this.player.getX() + lx;
        }

        float bx1 = this.getCurrentScenario().getArea().getWidth() - Gdx.graphics.getWidth() / 2f;

        if (this.camera.position.x >= bx1) {
            this.camera.position.x = bx1;
        }

        float bx2 = Gdx.graphics.getWidth() / 2f;

        if (this.camera.position.x <= bx2) {
            this.camera.position.x = bx2;
        }

        float dy = this.player.getY() - this.camera.position.y;
        float ly = Gdx.graphics.getHeight() / 5f;

        if (dy > ly) {
            this.camera.position.y = this.player.getY() - ly;
        }

        if (dy < -1f * ly) {
            this.camera.position.y = this.player.getY() + ly;
        }

        float by1 = this.getCurrentScenario().getArea().getHeight() - Gdx.graphics.getHeight() / 2f;

        if (this.camera.position.y >= by1) {
            this.camera.position.y = by1;
        }

        float by2 = Gdx.graphics.getHeight() / 2f;

        if (this.camera.position.y <= by2) {
            this.camera.position.y = by2;
        }

        this.camera.update();
    }

    private void update() {
        this.player.update(Gdx.input, Gdx.graphics, this.camera);
    }

    @Override
    public void render() {
        update();

        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.updateCamera();
        this.batch.setProjectionMatrix(this.camera.combined);

        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();

        this.batch.dispose();
    }
}
