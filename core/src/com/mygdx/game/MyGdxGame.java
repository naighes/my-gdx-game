package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MyGdxGame extends ApplicationAdapter {
    private SpriteBatch batch;
    private OrthographicCamera camera;

    private Bucket bucket;
    private RainDrops rainDrops;
    private Player player;

    @Override
    public void create() {
        batch = new SpriteBatch();

        bucket = Bucket.New(Gdx.graphics);
        bucket.create(Gdx.files, Gdx.graphics);

        rainDrops = RainDrops.New(Gdx.graphics);
        rainDrops.create(Gdx.files, Gdx.graphics);

        player = Player.New(Gdx.graphics);
        player.create(Gdx.files, Gdx.graphics);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        this.player.render(Gdx.input, Gdx.graphics, camera, batch);
        this.bucket.render(Gdx.input, Gdx.graphics, camera, batch);
        this.rainDrops.render(Gdx.input, Gdx.graphics, camera, batch);

        batch.end();
    }

    @Override
    public void dispose() {
        this.batch.dispose();
        this.rainDrops.dispose();
    }
}
