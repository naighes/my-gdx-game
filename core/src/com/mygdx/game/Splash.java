package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class Splash extends ScreenAdapter {
    private final MyGdxGame game;
    private final Scenario nextScenario;
    private final FadeController controller;

    private Scenario previousScenario;

    Splash(MyGdxGame game,
           Scenario previousScenario,
           Scenario nextScenario) {
        this.game = game;
        this.previousScenario = previousScenario;
        this.nextScenario = nextScenario;
        this.controller = new FadeController(this.game.getAssetManager(), new ShapeRenderer());
    }

    @Override
    public void show() {
        super.show();

        this.nextScenario.loadAssets();
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        int state = this.controller.update(delta);
        switch (state) {
            case 0:
            case 1:
            case 2:
                if (this.previousScenario != null) {
                    this.previousScenario.draw();
                }
                break;
            case 3:
                if (this.previousScenario != null) {
                    this.previousScenario.dispose();
                    this.previousScenario = null;
                }
                if (!this.nextScenario.isInitialized()) {
                    this.nextScenario.init();
                }
                this.nextScenario.render(delta);
                break;
            case 7:
                this.nextScenario.render(delta);
                this.game.setScreen(this.nextScenario);
                break;
        }

        this.controller.render();
    }
}

class FadeController {
    private float alpha;
    private boolean direction = false;
    private boolean completed = false;
    private boolean assetsReady = false;
    private final AssetManager assetManager;
    private final ShapeRenderer renderer;

    FadeController(AssetManager assetManager, ShapeRenderer renderer) {
        this.assetManager = assetManager;
        this.renderer = renderer;
    }

    int update(float delta) {
        if (!this.assetsReady) {
            this.assetsReady = this.assetManager.update();
        }

        int state = 0;

        if (this.alpha >= 1f && !this.direction) {
            this.direction = true;
        }

        if (this.direction) {
            state += 1;
        }

        if (this.assetsReady) {
            state += 2;
        }

        if (this.alpha <= 0f && this.direction) {
            this.completed = true;
        }

        if (this.completed) {
            state += 4;
        }

        this.alpha += (this.direction && this.assetsReady ? -1.5f : 1.5f) * delta;
        this.alpha = MathUtils.clamp(this.alpha, 0f, 1f);
        return state;
    }

    void render() {
        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);
        this.renderer.setColor(0f, 0f, 0f, this.alpha);
        this.renderer.begin(ShapeRenderer.ShapeType.Filled);
        this.renderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.renderer.end();
        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
    }
}