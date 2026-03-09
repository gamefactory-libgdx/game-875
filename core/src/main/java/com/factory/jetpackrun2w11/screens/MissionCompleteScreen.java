package com.factory.jetpackrun2w11.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
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
 * Celebration screen shown after a player claims a mission reward.
 * Grants the coin reward, animates confetti, and presents two exit options.
 */
public class MissionCompleteScreen implements Screen {

    private static final Color NEON_BLUE   = new Color(0f,    0.851f, 1f,    1f);
    private static final Color LIME_GREEN  = new Color(0.224f, 1f,   0.078f, 1f);
    private static final Color HOT_PINK    = new Color(1f,    0.078f, 0.576f, 1f);

    private static final int   PARTICLE_COUNT = 60;
    private static final float PARTICLE_SPEED = 120f;

    private final MainGame game;
    private final int      missionIndex;

    private final OrthographicCamera camera;
    private final StretchViewport    viewport;
    private final Stage   stage;
    private final AssetManager manager;
    private final ShapeRenderer shapeRenderer;

    private final BitmapFont titleFont;
    private final BitmapFont bodyFont;
    private final BitmapFont buttonFont;

    // Confetti particles
    private final float[] px      = new float[PARTICLE_COUNT];
    private final float[] py      = new float[PARTICLE_COUNT];
    private final float[] pvx     = new float[PARTICLE_COUNT];
    private final float[] pvy     = new float[PARTICLE_COUNT];
    private final Color[] pcolor  = new Color[PARTICLE_COUNT];
    private final float[] psize   = new float[PARTICLE_COUNT];
    private float stateTime = 0f;

    public MissionCompleteScreen(MainGame game, int missionIndex) {
        this.game         = game;
        this.missionIndex = missionIndex;

        // Grant reward coins immediately
        Preferences prefs     = Gdx.app.getPreferences(Constants.PREFS_NAME);
        int currentCoins      = prefs.getInteger(Constants.KEY_TOTAL_COINS, 0);
        prefs.putInteger(Constants.KEY_TOTAL_COINS, currentCoins + Constants.MISSION_REWARD_COINS);
        prefs.flush();

        manager = new AssetManager();
        manager.load(Constants.BG_MAIN, Texture.class);
        manager.finishLoading();

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);

        shapeRenderer = new ShapeRenderer();

        FreeTypeFontGenerator titleGen = new FreeTypeFontGenerator(Gdx.files.internal(Constants.FONT_TITLE));
        FreeTypeFontGenerator bodyGen  = new FreeTypeFontGenerator(Gdx.files.internal(Constants.FONT_MAIN));

        FreeTypeFontGenerator.FreeTypeFontParameter tp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        tp.size = Constants.FONT_SIZE_HEADER; tp.color = LIME_GREEN;
        titleFont = titleGen.generateFont(tp);
        titleGen.dispose();

        FreeTypeFontGenerator.FreeTypeFontParameter bp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        bp.size = Constants.FONT_SIZE_BODY; bp.color = NEON_BLUE;
        bodyFont = bodyGen.generateFont(bp);

        FreeTypeFontGenerator.FreeTypeFontParameter np = new FreeTypeFontGenerator.FreeTypeFontParameter();
        np.size = Constants.FONT_SIZE_NAV_BTN; np.color = Color.WHITE;
        buttonFont = bodyGen.generateFont(np);

        bodyGen.dispose();

        initParticles();
        buildUI();

        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    game.setScreen(new MainMenuScreen(game));
                    return true;
                }
                return false;
            }
        }));
    }

    private void initParticles() {
        Color[] palette = {LIME_GREEN, HOT_PINK, NEON_BLUE, Color.YELLOW, Color.WHITE};
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            px[i]     = MathUtils.random(0f, Constants.WORLD_WIDTH);
            py[i]     = MathUtils.random(Constants.WORLD_HEIGHT * 0.5f, Constants.WORLD_HEIGHT);
            pvx[i]    = MathUtils.random(-40f, 40f);
            pvy[i]    = MathUtils.random(-PARTICLE_SPEED * 0.5f, -PARTICLE_SPEED);
            pcolor[i] = palette[MathUtils.random(palette.length - 1)];
            psize[i]  = MathUtils.random(4f, 9f);
        }
    }

    private void buildUI() {
        Label.LabelStyle titleStyle  = new Label.LabelStyle(titleFont,  LIME_GREEN);
        Label.LabelStyle bodyStyle   = new Label.LabelStyle(bodyFont,   NEON_BLUE);
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = buttonFont; btnStyle.fontColor = Color.WHITE;

        Label missionLabel = new Label("MISSION COMPLETE!", titleStyle);
        Label nameLabel    = new Label(Constants.MISSION_NAMES[missionIndex], bodyStyle);
        Label rewardLabel  = new Label("Reward: +" + Constants.MISSION_REWARD_COINS + " Coins", bodyStyle);

        TextButton playAgainBtn = new TextButton("PLAY AGAIN", btnStyle);
        TextButton menuBtn      = new TextButton("BACK TO MENU", btnStyle);

        playAgainBtn.addListener(new ChangeListener() {
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
        root.add(missionLabel) .padBottom(16f).row();
        root.add(nameLabel)    .padBottom(8f).row();
        root.add(rewardLabel)  .padBottom(40f).row();
        root.add(playAgainBtn) .width(Constants.BUTTON_WIDTH).height(Constants.BUTTON_HEIGHT).padBottom(20f).row();
        root.add(menuBtn)      .width(Constants.BUTTON_WIDTH).height(Constants.BUTTON_HEIGHT).row();
        stage.addActor(root);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.102f, 0.043f, 0.243f, 1f);
        stateTime += delta;

        // Update confetti
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            px[i] += pvx[i] * delta;
            py[i] += pvy[i] * delta;
            if (py[i] < -20f) {
                py[i]  = Constants.WORLD_HEIGHT + 10f;
                px[i]  = MathUtils.random(0f, Constants.WORLD_WIDTH);
                pvy[i] = MathUtils.random(-PARTICLE_SPEED * 0.5f, -PARTICLE_SPEED);
            }
        }

        Texture bg = manager.get(Constants.BG_MAIN, Texture.class);
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(bg, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        // Dark overlay
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.102f, 0.043f, 0.243f, 0.75f);
        shapeRenderer.rect(0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        // Lime green badge circle
        shapeRenderer.setColor(LIME_GREEN.r, LIME_GREEN.g, LIME_GREEN.b, 1f);
        shapeRenderer.circle(Constants.WORLD_WIDTH / 2f, Constants.WORLD_HEIGHT * 0.72f, 60f, 48);

        // Confetti particles
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            shapeRenderer.setColor(pcolor[i].r, pcolor[i].g, pcolor[i].b, 0.85f);
            float s = psize[i];
            if (i % 2 == 0) {
                shapeRenderer.rect(px[i], py[i], s, s);
            } else {
                shapeRenderer.circle(px[i], py[i], s * 0.5f, 6);
            }
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

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
        shapeRenderer.dispose();
        titleFont.dispose();
        bodyFont.dispose();
        buttonFont.dispose();
    }
}
