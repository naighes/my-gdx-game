package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class MyGdxGame extends Game {
    private final Connections connections;
    private final ObjectMap<String, ScenarioDescriptor> descriptors;
    private final AssetManager assetManager;

    private SpriteBatch batch;
    private Scenario currentScenario;
    private boolean changingScenario = false;

    public MyGdxGame() {
        super();

        this.assetManager = new AssetManager();
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
        this.descriptors = new ObjectMap<>();
    }

    boolean isChangingScenario() {
        return this.changingScenario;
    }

    void scenarioChanged() {
        this.changingScenario = false;
    }

    Array<Junction> getConnections(String scenarioName) {
        return this.connections.get(scenarioName);
    }

    void setCurrentScenario(String scenarioName,
                            Vector2 playerPosition,
                            Direction playerDirection) {
        Scenario previousScenario = this.currentScenario;
        this.changingScenario = true;
        ScenarioDescriptor descriptor = this.descriptors.get(scenarioName);
        this.currentScenario = new Scenario(
                this,
                descriptor.name,
                0f,
                0f,
                playerPosition,
                playerDirection,
                descriptor.assetPath,
                descriptor.collisionAssetPath,
                descriptor.overlayAssetPath,
                descriptor.playerAssetPath
        );

        this.setScreen(new Splash(this,
                previousScenario,
                this.currentScenario));
    }

    AssetManager getAssetManager() {
        return this.assetManager;
    }

    SpriteBatch getBatch() {
        return this.batch;
    }

    @Override
    public void create() {
        this.batch = new SpriteBatch();

        this.descriptors.put("forest_1",
                new ScenarioDescriptor(
                        "forest_1",
                        new Vector2(880f, 50f),
                        "background_1.jpg",
                        "background_1_collision.gif",
                        "background_1_overlay.png",
                        "player_1.png"
                ));

        this.descriptors.put("inner_castle_1",
                new ScenarioDescriptor(
                        "inner_castle_1",
                        new Vector2(1310f, 410f),
                        "inner_castle_1.png",
                        "inner_castle_1_collision.gif",
                        null,
                        "player_1.png"
                ));

        ScenarioDescriptor descriptor = this.descriptors.get("forest_1");
        this.setCurrentScenario("forest_1",
                descriptor.playerPosition,
                Direction.NONE);
    }

    @Override
    public void render() {
        Color c = Color.BLACK;
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();

        this.batch.dispose();
    }
}
