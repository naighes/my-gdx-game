package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.descriptors.ScenarioDescriptor;
import com.mygdx.game.descriptors.ScenariosDescriptor;
import com.mygdx.game.junctions.Connections;

public class MyGdxGame extends Game {
    private final Connections connections;
    private final AssetManager assetManager;

    private SpriteBatch batch;
    private Scenario currentScenario;
    private ScenariosDescriptor scenarios;

    public MyGdxGame() {
        super();

        this.assetManager = new AssetManager();
        this.connections = new Connections();
    }

    // TODO: do on per scenario basis
    public Connections getConnections() {
        return this.connections;
    }

    public void setCurrentScenario(String scenarioName,
                                   Vector2 playerPosition,
                                   Direction playerDirection) {
        Scenario previousScenario = this.currentScenario;
        ScenarioDescriptor descriptor = this.scenarios.descriptors.get(scenarioName);
        this.currentScenario = new Scenario(
                this,
                descriptor.name,
                0f,
                0f,
                playerPosition,
                playerDirection,
                descriptor
        );

        this.setScreen(new ScenarioTransitionScreen(this,
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
        Json json = new Json();
        this.scenarios = json.fromJson(ScenariosDescriptor.class,
                Gdx.files.internal("scenarios.json"));
        ScenarioDescriptor descriptor = this.scenarios.descriptors.get("forest_1");
        this.connections.addRange(this.scenarios.connections);
        this.setCurrentScenario("forest_1",
                descriptor.playerInitialPosition,
                Direction.UP);
    }

    @Override
    public void render() {
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
