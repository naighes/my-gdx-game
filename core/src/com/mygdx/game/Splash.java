package com.mygdx.game;

import com.badlogic.gdx.ScreenAdapter;

public class Splash extends ScreenAdapter {
    private final MyGdxGame game;
    private final Scenario nextScenario;

    public Splash(MyGdxGame game,
                  Scenario nextScenario) {
        this.game = game;
        this.nextScenario = nextScenario;
    }

    @Override
    public void show() {
        super.show();

        this.nextScenario.loadAssets();
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (this.game.getAssetManager().update()) {
            this.game.setScreen(this.nextScenario);
        }
    }
}
