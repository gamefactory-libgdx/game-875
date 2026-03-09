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

/**
 * Shows all missions with per-mission progress bars.
 * Checks completion on show and marks newly-completed missions.
 * Navigates to MissionCompleteScreen when player claims a reward.
 */
public class MissionsScreen implements Screen {

    private static final Color NEON_BLUE  = new Color(0f,    0.851f, 1f,    1f);
    private static final Color LIME_GREEN = new Color(0.224f, 1f,   0.078f, 1f);

    private final MainGame game;
    private final OrthographicCamera camera;
    private final StretchViewport    viewport;
    private final Stage   stage;
    private final AssetManager manager;
    private final ShapeRenderer shapeRenderer;

    private final BitmapFont headerFont;
    private final BitmapFont bodyFont;
    private final BitmapFont smallFont;
    private final BitmapFont navFont;

    /** Tracks per-mission [progress, target, complete, claimed] after refresh. */
    private int[]     missionProgress;
    private boolean[] missionComplete;
    private boolean[] missionClaimed;

    public MissionsScreen(MainGame game) {
        this.game = game;

        manager = new AssetManager();
        manager.load(Constants.BG_MAIN, Texture.class);
        manager.finishLoading();

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);

        shapeRenderer = new ShapeRenderer();

        FreeTypeFontGenerator titleGen = new FreeTypeFontGenerator(Gdx.files.internal(Constants.FONT_TITLE));
        FreeTypeFontGenerator bodyGen  = new FreeTypeFontGenerator(Gdx.files.internal(Constants.FONT_MAIN));

        FreeTypeFontGenerator.FreeTypeFontParameter hp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        hp.size = Constants.FONT_SIZE_HEADER; hp.color = NEON_BLUE;
        headerFont = titleGen.generateFont(hp);
        titleGen.dispose();

        FreeTypeFontGenerator.FreeTypeFontParameter bp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        bp.size = Constants.FONT_SIZE_BODY; bp.color = Color.WHITE;
        bodyFont = bodyGen.generateFont(bp);

        FreeTypeFontGenerator.FreeTypeFontParameter sp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        sp.size = Constants.FONT_SIZE_SMALL; sp.color = new Color(0.75f, 0.75f, 0.75f, 1f);
        smallFont = bodyGen.generateFont(sp);

        FreeTypeFontGenerator.FreeTypeFontParameter np = new FreeTypeFontGenerator.FreeTypeFontParameter();
        np.size = Constants.FONT_SIZE_NAV_BTN; np.color = Color.WHITE;
        navFont = bodyGen.generateFont(np);

        bodyGen.dispose();

        refreshMissionData();
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

    // ─────────────────────────────────────────────────────────────────────────
    // Data helpers
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Reads SharedPreferences, computes progress for each mission, marks
     * newly-completed missions, and caches results in member arrays.
     */
    private void refreshMissionData() {
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);

        missionProgress = new int[Constants.MISSION_COUNT];
        missionComplete = new boolean[Constants.MISSION_COUNT];
        missionClaimed  = new boolean[Constants.MISSION_COUNT];

        // Compute raw progress from persisted counters
        missionProgress[0] = prefs.getInteger(Constants.KEY_TOTAL_RUNS, 0);
        missionProgress[1] = prefs.getInteger(Constants.KEY_TOTAL_COINS_COLLECTED, 0);
        missionProgress[2] = prefs.getInteger(Constants.KEY_MISSION_PROGRESS_PREFIX + 2, 0);
        missionProgress[3] = prefs.getInteger(Constants.KEY_TOTAL_RUNS, 0);
        missionProgress[4] = (int) prefs.getFloat(Constants.KEY_TOTAL_DISTANCE, 0f);

        boolean dirty = false;
        for (int i = 0; i < Constants.MISSION_COUNT; i++) {
            boolean alreadyComplete = prefs.getBoolean(Constants.KEY_MISSION_COMPLETE_PREFIX + i, false);
            boolean claimed         = prefs.getBoolean(Constants.KEY_MISSION_CLAIMED_PREFIX  + i, false);

            // Detect newly met target
            if (!alreadyComplete && missionProgress[i] >= Constants.MISSION_TARGETS[i]) {
                prefs.putBoolean(Constants.KEY_MISSION_COMPLETE_PREFIX + i, true);
                alreadyComplete = true;
                dirty = true;
            }
            missionComplete[i] = alreadyComplete;
            missionClaimed[i]  = claimed;
        }
        if (dirty) prefs.flush();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UI
    // ─────────────────────────────────────────────────────────────────────────

    private void buildUI() {
        Label.LabelStyle headerStyle = new Label.LabelStyle(headerFont, NEON_BLUE);
        Label.LabelStyle bodyStyle   = new Label.LabelStyle(bodyFont,   Color.WHITE);
        Label.LabelStyle smallStyle  = new Label.LabelStyle(smallFont,  new Color(0.75f, 0.75f, 0.75f, 1f));
        TextButton.TextButtonStyle navStyle = new TextButton.TextButtonStyle();
        navStyle.font = navFont; navStyle.fontColor = Color.WHITE;
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = bodyFont; btnStyle.fontColor = NEON_BLUE;

        Label header = new Label("MISSIONS", headerStyle);

        Table missionList = new Table();
        missionList.defaults().padBottom(16f);

        for (int i = 0; i < Constants.MISSION_COUNT; i++) {
            final int mIdx       = i;
            int   progress       = Math.min(missionProgress[i], Constants.MISSION_TARGETS[i]);
            float fillRatio      = (float) progress / Constants.MISSION_TARGETS[i];
            boolean complete     = missionComplete[i];
            boolean claimed      = missionClaimed[i];

            // Card sub-table
            Table card = new Table();
            card.pad(8f);

            // Title + description
            card.add(new Label(Constants.MISSION_NAMES[i], bodyStyle)).left().expandX().row();
            card.add(new Label(Constants.MISSION_DESCS[i], smallStyle)).left().expandX().row();

            // Progress text
            String progText = complete
                    ? "COMPLETED"
                    : progress + " / " + Constants.MISSION_TARGETS[i];
            card.add(new Label(progText, smallStyle)).left().padTop(4f).expandX().row();

            // Claim / status button
            if (complete && !claimed) {
                TextButton claimBtn = new TextButton("CLAIM +" + Constants.MISSION_REWARD_COINS, btnStyle);
                claimBtn.addListener(new ChangeListener() {
                    @Override public void changed(ChangeEvent event, Actor actor) {
                        claimReward(mIdx);
                    }
                });
                card.add(claimBtn).padTop(6f).row();
            } else if (complete) {
                card.add(new Label("REWARD CLAIMED", smallStyle)).padTop(6f).left().row();
            } else {
                // PLAY button
                TextButton playBtn = new TextButton("PLAY", navStyle);
                playBtn.addListener(new ChangeListener() {
                    @Override public void changed(ChangeEvent event, Actor actor) {
                        game.setScreen(new GameScreen(game));
                    }
                });
                card.add(playBtn).padTop(6f).row();
            }

            // Divider label as spacer (store fill ratio for ShapeRenderer to use)
            // We tag the card with the fill ratio via a custom data approach.
            // Since Scene2D doesn't have a built-in progress bar style here, we render
            // progress bars via ShapeRenderer in the render pass, tracked by index.
            missionList.add(card).width(Constants.MISSION_CARD_WIDTH).left().row();
        }

        ScrollPane scroll = new ScrollPane(missionList);
        scroll.setFadeScrollBars(false);

        TextButton menuBtn = new TextButton("MAIN MENU", navStyle);
        menuBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        TextButton playBtn = new TextButton("PLAY", navStyle);
        playBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game));
            }
        });

        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.add(header).padBottom(24f).row();
        root.add(scroll)
                .width(Constants.MISSION_CARD_WIDTH)
                .height(Constants.WORLD_HEIGHT - 160f)
                .padBottom(16f).row();
        Table navRow = new Table();
        navRow.add(playBtn).width(Constants.BUTTON_WIDTH * 0.55f).height(Constants.BUTTON_HEIGHT).padRight(16f);
        navRow.add(menuBtn).width(Constants.BUTTON_WIDTH).height(Constants.BUTTON_HEIGHT);
        root.add(navRow).row();

        stage.addActor(root);
    }

    private void claimReward(int missionIndex) {
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        prefs.putBoolean(Constants.KEY_MISSION_CLAIMED_PREFIX + missionIndex, true);
        prefs.flush();
        // Navigate to celebration screen
        game.setScreen(new MissionCompleteScreen(game, missionIndex));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Render
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.102f, 0.043f, 0.243f, 1f);
        Texture bg = manager.get(Constants.BG_MAIN, Texture.class);
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(bg, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        drawProgressBars();

        stage.act(delta);
        stage.draw();
    }

    /**
     * Draws progress bars under each mission card via ShapeRenderer.
     * Positions are hardcoded relative to the scrollable list layout.
     */
    private void drawProgressBars() {
        // Estimate Y positions of each card's progress bar.
        // The scroll area starts below the header (~WORLD_HEIGHT - 100) and each card is ~130px.
        float scrollAreaTop = Constants.WORLD_HEIGHT - 96f;
        float cardHeight    = 128f;
        float barY          = scrollAreaTop - 72f; // approx below progress text in first card

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);

        float barX    = (Constants.WORLD_WIDTH - Constants.MISSION_CARD_WIDTH) / 2f + 8f;
        float barW    = Constants.MISSION_CARD_WIDTH - 16f;
        float barH    = Constants.HUD_MISSION_BAR_HEIGHT;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < Constants.MISSION_COUNT; i++) {
            float currentBarY = barY - i * cardHeight;

            // Background
            shapeRenderer.setColor(0.102f, 0.043f, 0.243f, 0.9f);
            shapeRenderer.rect(barX, currentBarY, barW, barH);

            // Fill
            float fill = Math.min((float) missionProgress[i] / Constants.MISSION_TARGETS[i], 1f);
            shapeRenderer.setColor(LIME_GREEN.r, LIME_GREEN.g, LIME_GREEN.b, 1f);
            shapeRenderer.rect(barX, currentBarY, barW * fill, barH);
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
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
        headerFont.dispose();
        bodyFont.dispose();
        smallFont.dispose();
        navFont.dispose();
    }
}
