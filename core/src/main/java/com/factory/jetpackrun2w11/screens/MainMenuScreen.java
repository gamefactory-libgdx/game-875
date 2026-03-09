package com.factory.jetpackrun2w11.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
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

public class MainMenuScreen implements Screen {

    private static final Color NEON_BLUE  = new Color(0f,    0.851f, 1f,    1f); // #00D9FF
    private static final Color LIME_GREEN = new Color(0.224f,1f,    0.078f, 1f); // #39FF14

    private final MainGame game;
    private final OrthographicCamera camera;
    private final StretchViewport viewport;
    private final Stage stage;
    private final AssetManager manager;
    private final BitmapFont titleFont;
    private final BitmapFont subtitleFont;
    private final BitmapFont buttonFont;

    public MainMenuScreen(MainGame game) {
        this.game = game;

        manager = new AssetManager();
        manager.load(Constants.BG_MAIN, Texture.class);
        manager.finishLoading();

        camera = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);

        FreeTypeFontGenerator titleGen = new FreeTypeFontGenerator(Gdx.files.internal(Constants.FONT_TITLE));
        FreeTypeFontGenerator.FreeTypeFontParameter tp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        tp.size = Constants.FONT_SIZE_TITLE;
        tp.color = NEON_BLUE;
        titleFont = titleGen.generateFont(tp);
        titleGen.dispose();

        FreeTypeFontGenerator bodyGen = new FreeTypeFontGenerator(Gdx.files.internal(Constants.FONT_MAIN));

        FreeTypeFontGenerator.FreeTypeFontParameter sp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        sp.size = Constants.FONT_SIZE_SUBTITLE;
        sp.color = LIME_GREEN;
        subtitleFont = bodyGen.generateFont(sp);

        FreeTypeFontGenerator.FreeTypeFontParameter bp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        bp.size = Constants.FONT_SIZE_BUTTON;
        bp.color = Color.WHITE;
        buttonFont = bodyGen.generateFont(bp);

        bodyGen.dispose();

        buildUI();

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                // Main menu — back key does nothing (we are the root)
                return keycode == Input.Keys.BACK;
            }
        }));
    }

    private void buildUI() {
        Label.LabelStyle titleStyle    = new Label.LabelStyle(titleFont,    NEON_BLUE);
        Label.LabelStyle subtitleStyle = new Label.LabelStyle(subtitleFont, LIME_GREEN);

        Label titleLabel    = new Label("JETPACK RUN",              titleStyle);
        Label subtitleLabel = new Label("Hold to Fly • Release to Fall", subtitleStyle);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font      = buttonFont;
        btnStyle.fontColor = Color.WHITE;

        TextButton playBtn        = new TextButton("PLAY",        btnStyle);
        TextButton settingsBtn    = new TextButton("SETTINGS",    btnStyle);
        TextButton leaderboardBtn = new TextButton("LEADERBOARD", btnStyle);

        playBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game));
            }
        });
        settingsBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new SettingsScreen(game));
            }
        });
        leaderboardBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new LeaderboardScreen(game));
            }
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(titleLabel)   .padBottom(8f).row();
        table.add(subtitleLabel).padBottom(48f).row();
        table.add(playBtn)       .width(Constants.BUTTON_WIDTH).height(Constants.BUTTON_HEIGHT).padBottom(20f).row();
        table.add(settingsBtn)   .width(Constants.BUTTON_WIDTH).height(Constants.BUTTON_HEIGHT).padBottom(20f).row();
        table.add(leaderboardBtn).width(Constants.BUTTON_WIDTH).height(Constants.BUTTON_HEIGHT).row();

        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.102f, 0.043f, 0.243f, 1f);
        Texture bg = manager.get(Constants.BG_MAIN, Texture.class);
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(bg, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int w, int h) { viewport.update(w, h, true); }
    @Override public void show()   {}
    @Override public void hide()   {}
    @Override public void pause()  {}
    @Override public void resume() {}

    @Override
    public void dispose() {
        stage.dispose();
        manager.dispose();
        titleFont.dispose();
        subtitleFont.dispose();
        buttonFont.dispose();
    }
}
