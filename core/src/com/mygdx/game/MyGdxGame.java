package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyGdxGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private OrthographicCamera camera;

    private Player player;
    private Background background;

    @Override
    public void create() {
        batch = new SpriteBatch();

        player = Player.New(Gdx.graphics);
        player.create(Gdx.files, Gdx.graphics);

        background = Background.New(Gdx.graphics);
        background.create(Gdx.files, Gdx.graphics);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0x64 / 255f, 0x95 / 255f, 0xed / 255f, 0xff / 255f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        this.background.render(Gdx.input, Gdx.graphics, camera, batch);
        this.player.render(Gdx.input, Gdx.graphics, camera, batch);

        batch.end();
    }

    @Override
    public void dispose() {
        this.batch.dispose();
    }
}
