package com.factory.jetpackrun2w11.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
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

public class SettingsScreen implements Screen {

    private static final Color NEON_BLUE  = new Color(0f,    0.851f, 1f,    1f); // #00D9FF
    private static final Color LIME_GREEN = new Color(0.224f, 1f,   0.078f, 1f); // #39FF14

    private final MainGame game;
    private final OrthographicCamera camera;
    private final StretchViewport viewport;
    private final Stage stage;
    private final AssetManager manager;
    private final BitmapFont headerFont;
    private final BitmapFont bodyFont;
    private final BitmapFont navFont;

    private boolean musicOn;
    private boolean sfxOn;
    private TextButton musicToggleBtn;
    private TextButton sfxToggleBtn;

    public SettingsScreen(MainGame game) {
        this.game = game;

        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        musicOn = prefs.getBoolean(Constants.KEY_SOUND_ENABLED, true);
        sfxOn   = prefs.getBoolean(Constants.KEY_SFX_ENABLED,   true);

        manager = new AssetManager();
        manager.load(Constants.BG_MAIN, Texture.class);
        manager.finishLoading();

        camera = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);

        FreeTypeFontGenerator titleGen = new FreeTypeFontGenerator(Gdx.files.internal(Constants.FONT_TITLE));
        FreeTypeFontGenerator.FreeTypeFontParameter hp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        hp.size = Constants.FONT_SIZE_HEADER;
        hp.color = NEON_BLUE;
        headerFont = titleGen.generateFont(hp);
        titleGen.dispose();

        FreeTypeFontGenerator bodyGen = new FreeTypeFontGenerator(Gdx.files.internal(Constants.FONT_MAIN));

        FreeTypeFontGenerator.FreeTypeFontParameter bp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        bp.size = Constants.FONT_SIZE_BODY;
        bp.color = Color.WHITE;
        bodyFont = bodyGen.generateFont(bp);

        FreeTypeFontGenerator.FreeTypeFontParameter np = new FreeTypeFontGenerator.FreeTypeFontParameter();
        np.size = Constants.FONT_SIZE_NAV_BTN;
        np.color = Color.WHITE;
        navFont = bodyGen.generateFont(np);

        bodyGen.dispose();

        buildUI();

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    game.setScreen(new MainMenuScreen(game));
                    return true;
                }
                return false;
            }
        }));
    }

    private void buildUI() {
        Label.LabelStyle headerStyle = new Label.LabelStyle(headerFont, NEON_BLUE);
        Label.LabelStyle bodyStyle   = new Label.LabelStyle(bodyFont,   Color.WHITE);

        Label header = new Label("SETTINGS", headerStyle);

        TextButton.TextButtonStyle rowStyle = new TextButton.TextButtonStyle();
        rowStyle.font      = navFont;
        rowStyle.fontColor = Color.WHITE;

        musicToggleBtn = new TextButton(musicLabel(), rowStyle);
        sfxToggleBtn   = new TextButton(sfxLabel(),   rowStyle);

        musicToggleBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                musicOn = !musicOn;
                musicToggleBtn.setText(musicLabel());
                Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
                prefs.putBoolean(Constants.KEY_SOUND_ENABLED, musicOn);
                prefs.flush();
            }
        });
        sfxToggleBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                sfxOn = !sfxOn;
                sfxToggleBtn.setText(sfxLabel());
                Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
                prefs.putBoolean(Constants.KEY_SFX_ENABLED, sfxOn);
                prefs.flush();
            }
        });

        TextButton.TextButtonStyle navStyle = new TextButton.TextButtonStyle();
        navStyle.font      = navFont;
        navStyle.fontColor = Color.WHITE;
        TextButton menuBtn = new TextButton("MAIN MENU", navStyle);
        menuBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        Label musicLabel = new Label("MUSIC", bodyStyle);
        Label sfxLabel   = new Label("SFX",   bodyStyle);

        Table settingsPanel = new Table();
        settingsPanel.defaults().height(Constants.SETTINGS_ROW_HEIGHT).padBottom(8f);
        settingsPanel.add(musicLabel)    .left().width(200f);
        settingsPanel.add(musicToggleBtn).left().width(160f).row();
        settingsPanel.add(sfxLabel)      .left().width(200f);
        settingsPanel.add(sfxToggleBtn)  .left().width(160f).row();

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.add(header)       .padBottom(32f).colspan(2).row();
        root.add(settingsPanel).padBottom(40f).colspan(2).row();
        root.add(menuBtn)      .width(Constants.BUTTON_WIDTH).height(Constants.BUTTON_HEIGHT).colspan(2).row();

        stage.addActor(root);
    }

    private String musicLabel() { return "MUSIC: " + (musicOn ? "ON" : "OFF"); }
    private String sfxLabel()   { return "SFX:   " + (sfxOn   ? "ON" : "OFF"); }

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
        headerFont.dispose();
        bodyFont.dispose();
        navFont.dispose();
    }
}
