package com.factory.jetpackrun2w11.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.factory.jetpackrun2w11.Constants;
import com.factory.jetpackrun2w11.MainGame;

/**
 * Pause overlay screen.
 * Holds a reference to the live GameScreen so it can be resumed.
 * Disposes itself in hide() to avoid leaking fonts/stage.
 */
public class PauseScreen implements Screen {

    private static final Color NEON_BLUE  = new Color(0f,    0.851f, 1f,    1f);

    private final MainGame    game;
    private final GameScreen  gameScreen;
    private final OrthographicCamera camera;
    private final StretchViewport    viewport;
    private final Stage   stage;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont titleFont;
    private final BitmapFont buttonFont;

    public PauseScreen(MainGame game, GameScreen gameScreen) {
        this.game       = game;
        this.gameScreen = gameScreen;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);

        shapeRenderer = new ShapeRenderer();

        FreeTypeFontGenerator titleGen = new FreeTypeFontGenerator(Gdx.files.internal(Constants.FONT_TITLE));
        FreeTypeFontGenerator bodyGen  = new FreeTypeFontGenerator(Gdx.files.internal(Constants.FONT_MAIN));

        FreeTypeFontGenerator.FreeTypeFontParameter tp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        tp.size = Constants.FONT_SIZE_GAMEOVER; tp.color = Color.WHITE;
        titleFont = titleGen.generateFont(tp);
        titleGen.dispose();

        FreeTypeFontGenerator.FreeTypeFontParameter bp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        bp.size = Constants.FONT_SIZE_BUTTON; bp.color = Color.WHITE;
        buttonFont = bodyGen.generateFont(bp);
        bodyGen.dispose();

        buildUI();

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    resumeGame();
                    return true;
                }
                return false;
            }
        }));
    }

    private void buildUI() {
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font      = buttonFont;
        btnStyle.fontColor = Color.WHITE;

        Label pausedLabel = new Label("PAUSED", titleStyle);

        TextButton resumeBtn  = new TextButton("RESUME",    btnStyle);
        TextButton restartBtn = new TextButton("RESTART",   btnStyle);
        TextButton menuBtn    = new TextButton("MAIN MENU", btnStyle);

        resumeBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) { resumeGame(); }
        });
        restartBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game));
            }
        });
        menuBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.add(pausedLabel).padBottom(48f).row();
        root.add(resumeBtn) .width(Constants.BUTTON_WIDTH).height(Constants.BUTTON_HEIGHT).padBottom(20f).row();
        root.add(restartBtn).width(Constants.BUTTON_WIDTH).height(Constants.BUTTON_HEIGHT).padBottom(20f).row();
        root.add(menuBtn)   .width(Constants.BUTTON_WIDTH).height(Constants.BUTTON_HEIGHT).row();
        stage.addActor(root);
    }

    private void resumeGame() {
        game.setScreen(gameScreen);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.102f, 0.043f, 0.243f, 1f);

        camera.update();

        // Dark semi-transparent panel
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.102f, 0.043f, 0.243f, 0.92f);
        shapeRenderer.rect(0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        // Neon blue accent bar at top
        shapeRenderer.setColor(NEON_BLUE.r, NEON_BLUE.g, NEON_BLUE.b, 1f);
        shapeRenderer.rect(0, Constants.WORLD_HEIGHT - 4f, Constants.WORLD_WIDTH, 4f);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int w, int h) { viewport.update(w, h, true); }
    @Override public void show()   {}
    @Override public void hide()   {
        // Post disposal to next frame — avoids disposing stage while act() is still running.
        Gdx.app.postRunnable(new Runnable() {
            @Override public void run() { dispose(); }
        });
    }
    @Override public void pause()  {}
    @Override public void resume() {}

    @Override
    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
        titleFont.dispose();
        buttonFont.dispose();
    }
}
