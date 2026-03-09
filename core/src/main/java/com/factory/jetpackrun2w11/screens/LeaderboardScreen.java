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
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.factory.jetpackrun2w11.Constants;
import com.factory.jetpackrun2w11.MainGame;

public class LeaderboardScreen implements Screen {

    private static final Color NEON_BLUE  = new Color(0f,    0.851f, 1f,    1f); // #00D9FF
    private static final Color LIME_GREEN = new Color(0.224f, 1f,   0.078f, 1f); // #39FF14

    private final MainGame game;
    private final OrthographicCamera camera;
    private final StretchViewport viewport;
    private final Stage stage;
    private final AssetManager manager;
    private final BitmapFont headerFont;
    private final BitmapFont rowFont;
    private final BitmapFont navFont;

    // ── Static helper ────────────────────────────────────────────────────────

    /** Insert score into the top-10 leaderboard stored in SharedPreferences. */
    public static void addScore(int score) {
        if (score <= 0) return;
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        int[] scores = new int[Constants.LEADERBOARD_MAX_ENTRIES];
        for (int i = 0; i < Constants.LEADERBOARD_MAX_ENTRIES; i++) {
            scores[i] = prefs.getInteger(Constants.KEY_LEADERBOARD_PREFIX + i, 0);
        }
        // Sorted insert (descending)
        for (int i = 0; i < Constants.LEADERBOARD_MAX_ENTRIES; i++) {
            if (score > scores[i]) {
                // Shift entries below down by one
                for (int j = Constants.LEADERBOARD_MAX_ENTRIES - 1; j > i; j--) {
                    scores[j] = scores[j - 1];
                }
                scores[i] = score;
                break;
            }
        }
        for (int i = 0; i < Constants.LEADERBOARD_MAX_ENTRIES; i++) {
            prefs.putInteger(Constants.KEY_LEADERBOARD_PREFIX + i, scores[i]);
        }
        prefs.flush();
    }

    // ── Constructor ──────────────────────────────────────────────────────────

    public LeaderboardScreen(MainGame game) {
        this.game = game;

        manager = new AssetManager();
        manager.load(Constants.BG_MAIN, Texture.class);
        manager.finishLoading();

        camera = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);

        FreeTypeFontGenerator titleGen = new FreeTypeFontGenerator(Gdx.files.internal(Constants.FONT_TITLE));
        FreeTypeFontGenerator.FreeTypeFontParameter hp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        hp.size  = Constants.FONT_SIZE_HEADER;
        hp.color = NEON_BLUE;
        headerFont = titleGen.generateFont(hp);
        titleGen.dispose();

        FreeTypeFontGenerator bodyGen = new FreeTypeFontGenerator(Gdx.files.internal(Constants.FONT_MAIN));

        FreeTypeFontGenerator.FreeTypeFontParameter rp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        rp.size  = Constants.FONT_SIZE_BODY;
        rp.color = Color.WHITE;
        rowFont = bodyGen.generateFont(rp);

        FreeTypeFontGenerator.FreeTypeFontParameter np = new FreeTypeFontGenerator.FreeTypeFontParameter();
        np.size  = Constants.FONT_SIZE_NAV_BTN;
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
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        int personalBest = prefs.getInteger(Constants.KEY_HIGH_SCORE, 0);

        Label.LabelStyle headerStyle = new Label.LabelStyle(headerFont, NEON_BLUE);
        Label.LabelStyle rowStyle    = new Label.LabelStyle(rowFont,    Color.WHITE);
        Label.LabelStyle goldStyle   = new Label.LabelStyle(rowFont,    LIME_GREEN);

        Label header    = new Label("LEADERBOARD", headerStyle);
        Label bestLabel = new Label("Your Best: " + personalBest, new Label.LabelStyle(rowFont, NEON_BLUE));

        // Scores table (scrollable)
        Table scoreTable = new Table();
        scoreTable.defaults().height(Constants.LEADERBOARD_ROW_HEIGHT).padBottom(4f);

        // Column headers
        scoreTable.add(new Label("RANK",  rowStyle)).width(80f).left();
        scoreTable.add(new Label("SCORE", rowStyle)).width(Constants.LEADERBOARD_TABLE_WIDTH - 80f).left().row();

        for (int i = 0; i < Constants.LEADERBOARD_MAX_ENTRIES; i++) {
            int s = prefs.getInteger(Constants.KEY_LEADERBOARD_PREFIX + i, 0);
            Label.LabelStyle style = (i < 3 && s > 0) ? goldStyle : rowStyle;
            scoreTable.add(new Label("#" + (i + 1), style)).width(80f).left();
            scoreTable.add(new Label(s > 0 ? String.valueOf(s) : "---", style))
                      .width(Constants.LEADERBOARD_TABLE_WIDTH - 80f).left().row();
        }

        ScrollPane scrollPane = new ScrollPane(scoreTable);
        scrollPane.setFadeScrollBars(false);

        TextButton.TextButtonStyle navStyle = new TextButton.TextButtonStyle();
        navStyle.font      = navFont;
        navStyle.fontColor = Color.WHITE;
        TextButton menuBtn = new TextButton("MAIN MENU", navStyle);
        menuBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.add(header)   .padBottom(8f).row();
        root.add(bestLabel).padBottom(24f).row();
        root.add(scrollPane)
            .width(Constants.LEADERBOARD_TABLE_WIDTH)
            .height(Constants.LEADERBOARD_ROW_HEIGHT * Constants.LEADERBOARD_MAX_ENTRIES + 60f)
            .padBottom(24f).row();
        root.add(menuBtn).width(Constants.BUTTON_WIDTH).height(Constants.BUTTON_HEIGHT).row();

        stage.addActor(root);
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
        headerFont.dispose();
        rowFont.dispose();
        navFont.dispose();
    }
}
