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

public class GameOverScreen implements Screen {

    private static final Color NEON_BLUE   = new Color(0f,    0.851f, 1f,    1f); // #00D9FF
    private static final Color LIME_GREEN  = new Color(0.224f, 1f,   0.078f, 1f); // #39FF14
    private static final Color BRIGHT_ORANGE = new Color(1f,  0.420f, 0.208f, 1f); // #FF6B35
    private static final Color HOT_PINK    = new Color(1f,    0.078f, 0.576f, 1f); // #FF1493

    private final MainGame game;
    private final OrthographicCamera camera;
    private final StretchViewport viewport;
    private final Stage stage;
    private final AssetManager manager;
    private final BitmapFont titleFont;
    private final BitmapFont scoreFont;
    private final BitmapFont bodyFont;
    private final BitmapFont buttonFont;
    private final BitmapFont newBestFont;

    /** @param score  final score for this run
     *  @param extra  coins earned during this run */
    public GameOverScreen(MainGame game, int score, int extra) {
        this.game = game;

        // Persist high score and leaderboard entry
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        int prevBest = prefs.getInteger(Constants.KEY_HIGH_SCORE, 0);
        boolean isNewBest = score > prevBest;
        if (isNewBest) {
            prefs.putInteger(Constants.KEY_HIGH_SCORE, score);
        }
        int totalCoins = prefs.getInteger(Constants.KEY_TOTAL_COINS, 0) + extra;
        prefs.putInteger(Constants.KEY_TOTAL_COINS, totalCoins);
        int totalRuns = prefs.getInteger(Constants.KEY_TOTAL_RUNS, 0) + 1;
        prefs.putInteger(Constants.KEY_TOTAL_RUNS, totalRuns);
        prefs.flush();
        LeaderboardScreen.addScore(score);
        int personalBest = isNewBest ? score : prevBest;

        manager = new AssetManager();
        manager.load(Constants.BG_MAIN, Texture.class);
        manager.finishLoading();

        camera = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);

        FreeTypeFontGenerator titleGen = new FreeTypeFontGenerator(Gdx.files.internal(Constants.FONT_TITLE));

        FreeTypeFontGenerator.FreeTypeFontParameter gop = new FreeTypeFontGenerator.FreeTypeFontParameter();
        gop.size  = Constants.FONT_SIZE_GAMEOVER;
        gop.color = BRIGHT_ORANGE;
        titleFont = titleGen.generateFont(gop);

        FreeTypeFontGenerator.FreeTypeFontParameter nbp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        nbp.size  = Constants.FONT_SIZE_SUBTITLE;
        nbp.color = HOT_PINK;
        newBestFont = titleGen.generateFont(nbp);

        titleGen.dispose();

        FreeTypeFontGenerator bodyGen = new FreeTypeFontGenerator(Gdx.files.internal(Constants.FONT_MAIN));

        FreeTypeFontGenerator.FreeTypeFontParameter sp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        sp.size  = Constants.FONT_SIZE_HEADER;
        sp.color = NEON_BLUE;
        scoreFont = bodyGen.generateFont(sp);

        FreeTypeFontGenerator.FreeTypeFontParameter rp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        rp.size  = Constants.FONT_SIZE_REWARD;
        rp.color = LIME_GREEN;
        bodyFont = bodyGen.generateFont(rp);

        FreeTypeFontGenerator.FreeTypeFontParameter bp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        bp.size  = Constants.FONT_SIZE_BUTTON;
        bp.color = Color.WHITE;
        buttonFont = bodyGen.generateFont(bp);

        bodyGen.dispose();

        buildUI(score, extra, personalBest, isNewBest);

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

    private void buildUI(int score, int coinsEarned, int personalBest, boolean isNewBest) {
        Label.LabelStyle titleStyle   = new Label.LabelStyle(titleFont,   BRIGHT_ORANGE);
        Label.LabelStyle scoreStyle   = new Label.LabelStyle(scoreFont,   NEON_BLUE);
        Label.LabelStyle bodyStyle    = new Label.LabelStyle(bodyFont,    LIME_GREEN);
        Label.LabelStyle newBestStyle = new Label.LabelStyle(newBestFont, HOT_PINK);

        Label gameOverLabel  = new Label("GAME OVER",                          titleStyle);
        Label scoreLabel     = new Label("Final Score: " + score,              scoreStyle);
        Label bestLabel      = new Label("Personal Best: " + personalBest,     scoreStyle);
        Label coinsLabel     = new Label("Coins Earned: " + coinsEarned,       bodyStyle);

        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font      = buttonFont;
        btnStyle.fontColor = Color.WHITE;

        TextButton retryBtn = new TextButton("PLAY AGAIN", btnStyle);
        TextButton menuBtn  = new TextButton("MAIN MENU",  btnStyle);

        retryBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game)); // fresh instance every time
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
        root.add(gameOverLabel).padBottom(12f).row();
        if (isNewBest) {
            Label newBestLabel = new Label("NEW BEST!", newBestStyle);
            root.add(newBestLabel).padBottom(8f).row();
        }
        root.add(scoreLabel) .padBottom(6f).row();
        root.add(bestLabel)  .padBottom(6f).row();
        root.add(coinsLabel) .padBottom(32f).row();
        root.add(retryBtn)   .width(Constants.BUTTON_WIDTH).height(Constants.BUTTON_HEIGHT).padBottom(16f).row();
        root.add(menuBtn)    .width(Constants.BUTTON_WIDTH).height(Constants.BUTTON_HEIGHT).row();

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
        titleFont.dispose();
        scoreFont.dispose();
        bodyFont.dispose();
        buttonFont.dispose();
        newBestFont.dispose();
    }
}
