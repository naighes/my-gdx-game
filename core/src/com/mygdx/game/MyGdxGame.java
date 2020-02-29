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
        this.batch = new SpriteBatch();

        this.player = Player.New(Gdx.graphics);
        this.player.create(Gdx.files, Gdx.graphics);

        this.background = Background.New(Gdx.graphics);
        this.background.create(Gdx.files, Gdx.graphics);

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

        float bx1 = this.background.getWidth() - Gdx.graphics.getWidth() / 2f;

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

        float by1 = this.background.getHeight() - Gdx.graphics.getHeight() / 2f;

        if (this.camera.position.y >= by1) {
            this.camera.position.y = by1;
        }

        float by2 = Gdx.graphics.getHeight() / 2f;

        if (this.camera.position.y <= by2) {
            this.camera.position.y = by2;
        }

        this.camera.update();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0x64 / 255f, 0x95 / 255f, 0xed / 255f, 0xff / 255f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.updateCamera();
        this.batch.setProjectionMatrix(this.camera.combined);

        this.batch.begin();

        this.background.render(Gdx.input, Gdx.graphics, this.camera, this.batch);
        this.player.render(Gdx.input, Gdx.graphics, this.camera, this.batch);

        this.batch.end();
    }

    @Override
    public void dispose() {
        this.batch.dispose();
    }
}
