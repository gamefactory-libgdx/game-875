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
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.factory.jetpackrun2w11.Constants;
import com.factory.jetpackrun2w11.MainGame;

public class GameScreen implements Screen {

    // ── Colors ────────────────────────────────────────────────────────────────
    private static final Color NEON_BLUE     = new Color(0f,    0.851f, 1f,    1f);
    private static final Color LIME_GREEN    = new Color(0.224f, 1f,   0.078f, 1f);

    // ── Obstacle types ────────────────────────────────────────────────────────
    private static final int OBS_LASER   = 0;
    private static final int OBS_MISSILE = 1;
    private static final int OBS_ZAPPER  = 2;

    // ── Banner states ─────────────────────────────────────────────────────────
    private static final int BANNER_NONE     = 0;
    private static final int BANNER_FADE_IN  = 1;
    private static final int BANNER_DISPLAY  = 2;
    private static final int BANNER_FADE_OUT = 3;

    private static final float DEATH_DELAY = 1.5f;

    // ── Core ──────────────────────────────────────────────────────────────────
    private final MainGame game;
    private final OrthographicCamera camera;
    private final StretchViewport viewport;
    private final Stage hudStage;
    private final AssetManager manager;
    private final ShapeRenderer shapeRenderer;

    // ── Fonts ─────────────────────────────────────────────────────────────────
    private final BitmapFont hudScoreFont;
    private final BitmapFont hudCoinFont;
    private final BitmapFont bannerFont;
    private final BitmapFont pauseBtnFont;
    private final GlyphLayout glyphLayout = new GlyphLayout();

    // ── Textures / animations ─────────────────────────────────────────────────
    private final Texture bgNeonLab;
    private final Texture bgCrystallineVault;
    private final Texture bgPlasmaCore;
    private final Animation<TextureRegion> coinAnim;
    private final Animation<TextureRegion> fireAnim;
    private final TextureRegion[] skinRegions;   // all 6 skin textures
    private final TextureRegion playerHurt;
    private final TextureRegion missileTex;
    private final TextureRegion zapperTex;

    // ── State ─────────────────────────────────────────────────────────────────
    private float stateTime       = 0f;
    private boolean paused        = false;
    private boolean dead          = false;
    private float   deathTimer    = 0f;

    // ── Player ────────────────────────────────────────────────────────────────
    private float playerX;
    private float playerY;
    private float playerVelY      = 0f;
    private int   equippedSkin;

    // ── Score & coins ─────────────────────────────────────────────────────────
    private int   score           = 0;
    private int   coinsCollected  = 0;
    private float scoreAccum      = 0f;

    // ── Speed ─────────────────────────────────────────────────────────────────
    private float scrollSpeed;
    private float speedTimer      = 0f;
    private float diffMultiplier;

    // ── Obstacle spawning ─────────────────────────────────────────────────────
    private float obstacleTimer   = 0f;
    private float obstacleInterval;
    private final Array<ObstacleData> obstacles = new Array<>();

    // ── Coin spawning ─────────────────────────────────────────────────────────
    private float coinTimer       = 0f;
    private float coinInterval;
    private final Array<CoinData> coins = new Array<>();

    // ── Background ────────────────────────────────────────────────────────────
    private float bgOffsetX       = 0f;

    // ── Environment ───────────────────────────────────────────────────────────
    private int   currentEnv      = 0;
    private float envTimer        = 0f;

    // ── Banner ────────────────────────────────────────────────────────────────
    private int   bannerState     = BANNER_NONE;
    private float bannerTimer     = 0f;
    private float bannerAlpha     = 0f;
    private String bannerText     = "";

    // ── Distance tracking (per run) ───────────────────────────────────────────
    private float distanceThisRun = 0f;

    // ── Mission 2 (score 1000 in single run) local flag ──────────────────────
    private boolean mission2Pending;

    // ── HUD labels ────────────────────────────────────────────────────────────
    private Label scoreLabel;
    private Label coinLabel;

    // ─────────────────────────────────────────────────────────────────────────
    // Constructor
    // ─────────────────────────────────────────────────────────────────────────

    public GameScreen(MainGame game) {
        this.game = game;

        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        int diff = prefs.getInteger(Constants.KEY_DIFFICULTY, Constants.DIFFICULTY_NORMAL);
        switch (diff) {
            case Constants.DIFFICULTY_EASY: diffMultiplier = Constants.DIFFICULTY_EASY_MULTIPLIER; break;
            case Constants.DIFFICULTY_HARD: diffMultiplier = Constants.DIFFICULTY_HARD_MULTIPLIER; break;
            default:                         diffMultiplier = Constants.DIFFICULTY_NORMAL_MULTIPLIER;
        }
        equippedSkin       = prefs.getInteger(Constants.KEY_EQUIPPED_SKIN, Constants.SKIN_DEFAULT_INDEX);
        mission2Pending    = !prefs.getBoolean(Constants.KEY_MISSION_COMPLETE_PREFIX + 2, false);

        scrollSpeed        = Constants.SCROLL_SPEED_INITIAL * diffMultiplier;
        obstacleInterval   = MathUtils.random(Constants.OBSTACLE_SPAWN_MIN, Constants.OBSTACLE_SPAWN_MAX)
                             / diffMultiplier;
        coinInterval       = MathUtils.random(Constants.COIN_SPAWN_MIN, Constants.COIN_SPAWN_MAX);

        // Load assets
        manager = new AssetManager();
        manager.load(Constants.BG_NEON_LAB,           Texture.class);
        manager.load(Constants.BG_CRYSTALLINE_VAULT,  Texture.class);
        manager.load(Constants.BG_PLASMA_CORE,        Texture.class);
        manager.load(Constants.SPRITE_PLAYER_HURT,    Texture.class);
        manager.load(Constants.SPRITE_EFFECT_FIRE1,   Texture.class);
        manager.load(Constants.SPRITE_EFFECT_FIRE2,   Texture.class);
        manager.load(Constants.SPRITE_COIN1,          Texture.class);
        manager.load(Constants.SPRITE_COIN2,          Texture.class);
        manager.load(Constants.SPRITE_ENEMY_FLY,      Texture.class);
        manager.load(Constants.SPRITE_ENEMY_SAW,      Texture.class);
        for (String path : Constants.SKIN_SPRITE_PATHS) {
            manager.load(path, Texture.class);
        }
        manager.finishLoading();

        bgNeonLab          = manager.get(Constants.BG_NEON_LAB,          Texture.class);
        bgCrystallineVault = manager.get(Constants.BG_CRYSTALLINE_VAULT, Texture.class);
        bgPlasmaCore       = manager.get(Constants.BG_PLASMA_CORE,       Texture.class);
        playerHurt         = new TextureRegion(manager.get(Constants.SPRITE_PLAYER_HURT, Texture.class));
        missileTex         = new TextureRegion(manager.get(Constants.SPRITE_ENEMY_FLY,  Texture.class));
        zapperTex          = new TextureRegion(manager.get(Constants.SPRITE_ENEMY_SAW,  Texture.class));

        skinRegions = new TextureRegion[Constants.SKIN_SPRITE_PATHS.length];
        for (int i = 0; i < Constants.SKIN_SPRITE_PATHS.length; i++) {
            skinRegions[i] = new TextureRegion(
                    manager.get(Constants.SKIN_SPRITE_PATHS[i], Texture.class));
        }

        coinAnim = new Animation<>(0.15f,
                new TextureRegion(manager.get(Constants.SPRITE_COIN1, Texture.class)),
                new TextureRegion(manager.get(Constants.SPRITE_COIN2, Texture.class)));
        coinAnim.setPlayMode(Animation.PlayMode.LOOP);

        fireAnim = new Animation<>(0.1f,
                new TextureRegion(manager.get(Constants.SPRITE_EFFECT_FIRE1, Texture.class)),
                new TextureRegion(manager.get(Constants.SPRITE_EFFECT_FIRE2, Texture.class)));
        fireAnim.setPlayMode(Animation.PlayMode.LOOP);

        playerX = Constants.PLAYER_START_X;
        playerY = Constants.PLAYER_START_Y;

        // Camera / viewport / stage
        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        hudStage = new Stage(viewport, game.batch);

        shapeRenderer = new ShapeRenderer();

        // Fonts
        FreeTypeFontGenerator titleGen = new FreeTypeFontGenerator(Gdx.files.internal(Constants.FONT_TITLE));
        FreeTypeFontGenerator bodyGen  = new FreeTypeFontGenerator(Gdx.files.internal(Constants.FONT_MAIN));

        FreeTypeFontGenerator.FreeTypeFontParameter sp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        sp.size = Constants.FONT_SIZE_SCORE_HUD; sp.color = NEON_BLUE;
        hudScoreFont = titleGen.generateFont(sp);

        FreeTypeFontGenerator.FreeTypeFontParameter cp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        cp.size = Constants.FONT_SIZE_COIN_HUD; cp.color = LIME_GREEN;
        hudCoinFont = bodyGen.generateFont(cp);

        FreeTypeFontGenerator.FreeTypeFontParameter bp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        bp.size = Constants.FONT_SIZE_HEADER; bp.color = NEON_BLUE;
        bannerFont = titleGen.generateFont(bp);

        FreeTypeFontGenerator.FreeTypeFontParameter pp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        pp.size = Constants.FONT_SIZE_NAV_BTN; pp.color = Color.WHITE;
        pauseBtnFont = bodyGen.generateFont(pp);

        titleGen.dispose();
        bodyGen.dispose();

        buildHUD();
        resetInputProcessor();

        triggerBanner(0);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HUD
    // ─────────────────────────────────────────────────────────────────────────

    private void buildHUD() {
        Label.LabelStyle scoreStyle = new Label.LabelStyle(hudScoreFont, NEON_BLUE);
        Label.LabelStyle coinStyle  = new Label.LabelStyle(hudCoinFont,  LIME_GREEN);
        TextButton.TextButtonStyle pauseStyle = new TextButton.TextButtonStyle();
        pauseStyle.font      = pauseBtnFont;
        pauseStyle.fontColor = Color.WHITE;

        scoreLabel = new Label("Score: 0",  scoreStyle);
        coinLabel  = new Label("Coins: 0",  coinStyle);

        TextButton pauseBtn = new TextButton("II", pauseStyle);
        pauseBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                if (!dead) {
                    paused = true;
                    game.setScreen(new PauseScreen(game, GameScreen.this));
                }
            }
        });

        Table hud = new Table();
        hud.setFillParent(true);
        hud.top().pad(8f);
        hud.add(scoreLabel).left().expandX();
        hud.add(pauseBtn).right().width(48f).height(40f);
        hud.row();
        hud.add(coinLabel).left().colspan(2);
        hudStage.addActor(hud);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Screen lifecycle
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.102f, 0.043f, 0.243f, 1f);

        if (dead) {
            stateTime  += delta;
            deathTimer += delta;
            if (deathTimer >= DEATH_DELAY) {
                persistRunStats();
                game.setScreen(new GameOverScreen(game, score, coinsCollected));
                return;
            }
        } else if (!paused) {
            update(delta);
        }

        camera.update();
        draw();
        hudStage.act(delta);
        hudStage.draw();
    }

    @Override
    public void show() {
        // Called when resuming from pause
        paused = false;
        resetInputProcessor();
    }

    @Override public void resize(int w, int h) {
        viewport.update(w, h, true);
    }
    @Override public void hide()   {}
    @Override public void pause()  { paused = true; }
    @Override public void resume() {}

    @Override
    public void dispose() {
        hudStage.dispose();
        manager.dispose();
        shapeRenderer.dispose();
        hudScoreFont.dispose();
        hudCoinFont.dispose();
        bannerFont.dispose();
        pauseBtnFont.dispose();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Update
    // ─────────────────────────────────────────────────────────────────────────

    private void update(float delta) {
        stateTime += delta;

        // Speed ramp
        speedTimer += delta;
        if (speedTimer >= Constants.SPEED_INCREASE_INTERVAL) {
            speedTimer -= Constants.SPEED_INCREASE_INTERVAL;
            scrollSpeed = Math.min(
                    scrollSpeed + Constants.SCROLL_SPEED_INCREMENT * diffMultiplier,
                    Constants.SCROLL_SPEED_MAX * diffMultiplier);
        }

        // Score per second
        scoreAccum += delta;
        if (scoreAccum >= 1f) {
            score      += Constants.SCORE_PER_SECOND;
            scoreAccum -= 1f;
            scoreLabel.setText("Score: " + score);
        }

        // Distance
        distanceThisRun += scrollSpeed * delta;

        // Mission 2: score 1000 single run
        if (mission2Pending && score >= 1000) {
            mission2Pending = false;
            Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
            prefs.putInteger(Constants.KEY_MISSION_PROGRESS_PREFIX + 2, 1000);
            prefs.putBoolean(Constants.KEY_MISSION_COMPLETE_PREFIX + 2, true);
            prefs.flush();
        }

        // Player physics — poll touch directly (not stage events)
        boolean thrusting = Gdx.input.isTouched();
        float netForce = Constants.GRAVITY + (thrusting ? Constants.JETPACK_THRUST : 0f);
        playerVelY += netForce * delta;
        playerVelY  = Math.max(playerVelY, Constants.MAX_FALL_SPEED);
        playerY    += playerVelY * delta;

        // Clamp to world bounds
        if (playerY < 0) {
            playerY    = 0;
            playerVelY = 0;
        }
        if (playerY > Constants.WORLD_HEIGHT - Constants.PLAYER_HEIGHT) {
            playerY    = Constants.WORLD_HEIGHT - Constants.PLAYER_HEIGHT;
            playerVelY = 0;
        }

        // Background scroll
        bgOffsetX += scrollSpeed * delta;
        if (bgOffsetX >= Constants.WORLD_WIDTH) bgOffsetX -= Constants.WORLD_WIDTH;

        // Environment timer
        envTimer += delta;
        if (envTimer >= Constants.ENV_DURATION) {
            envTimer  -= Constants.ENV_DURATION;
            currentEnv = (currentEnv + 1) % Constants.ENV_COUNT;
            triggerBanner(currentEnv);
        }
        updateBanner(delta);

        // Spawn obstacles
        obstacleTimer += delta;
        if (obstacleTimer >= obstacleInterval) {
            obstacleTimer    = 0;
            obstacleInterval = MathUtils.random(Constants.OBSTACLE_SPAWN_MIN,
                    Constants.OBSTACLE_SPAWN_MAX) / diffMultiplier;
            spawnObstacle();
        }

        // Spawn coins
        coinTimer += delta;
        if (coinTimer >= coinInterval) {
            coinTimer    = 0;
            coinInterval = MathUtils.random(Constants.COIN_SPAWN_MIN, Constants.COIN_SPAWN_MAX);
            spawnCoin();
        }

        // Shrunk player hitbox for fair feel
        Rectangle playerRect = new Rectangle(
                playerX + 10, playerY + 10,
                Constants.PLAYER_WIDTH - 20, Constants.PLAYER_HEIGHT - 20);

        // Update obstacles
        for (int i = obstacles.size - 1; i >= 0; i--) {
            ObstacleData obs = obstacles.get(i);
            obs.x -= scrollSpeed * delta;

            if (obs.type == OBS_ZAPPER) {
                obs.velY  += Constants.GRAVITY * 0.4f * delta;
                obs.y     += obs.velY * delta;
                obs.rotation += 150f * delta;
                if (obs.y < 0) {
                    obs.y    = 0;
                    obs.velY = Math.abs(obs.velY) * 0.8f;
                }
                if (obs.y > Constants.WORLD_HEIGHT - Constants.ZAPPER_HEIGHT) {
                    obs.y    = Constants.WORLD_HEIGHT - Constants.ZAPPER_HEIGHT;
                    obs.velY = -Math.abs(obs.velY) * 0.8f;
                }
            }

            obs.rect.set(obs.x, obs.y, obs.width, obs.height);

            if (!dead && obs.rect.overlaps(playerRect)) {
                onPlayerHit();
            }

            if (obs.x + obs.width < 0) {
                obstacles.removeIndex(i);
            }
        }

        // Update coins
        for (int i = coins.size - 1; i >= 0; i--) {
            CoinData coin = coins.get(i);
            coin.x -= scrollSpeed * delta;
            coin.rect.set(coin.x, coin.y, Constants.COIN_SIZE, Constants.COIN_SIZE);

            if (!coin.collected && coin.rect.overlaps(playerRect)) {
                coin.collected = true;
                coinsCollected++;
                score += Constants.COIN_SCORE_VALUE;
                coinLabel.setText("Coins: " + coinsCollected);
                scoreLabel.setText("Score: " + score);
            }

            if (coin.x + Constants.COIN_SIZE < 0 || coin.collected) {
                coins.removeIndex(i);
            }
        }
    }

    private void onPlayerHit() {
        dead       = true;
        deathTimer = 0f;
        playerVelY = 0f;
    }

    private void spawnObstacle() {
        int type = MathUtils.random(2);
        ObstacleData obs = new ObstacleData();
        obs.type = type;

        if (type == OBS_LASER) {
            obs.width  = Constants.LASER_WIDTH;
            obs.height = Constants.LASER_HEIGHT;
            obs.y      = MathUtils.random(0, (int)(Constants.WORLD_HEIGHT - Constants.LASER_HEIGHT));
        } else if (type == OBS_MISSILE) {
            obs.width  = Constants.MISSILE_WIDTH;
            obs.height = Constants.MISSILE_HEIGHT;
            obs.y      = MathUtils.random(20, (int)(Constants.WORLD_HEIGHT - Constants.MISSILE_HEIGHT - 20));
        } else {
            obs.width  = Constants.ZAPPER_WIDTH;
            obs.height = Constants.ZAPPER_HEIGHT;
            obs.y      = MathUtils.random(
                    (int)(Constants.WORLD_HEIGHT * 0.2f),
                    (int)(Constants.WORLD_HEIGHT * 0.75f));
            obs.velY = MathUtils.randomSign() * MathUtils.random(80f, 180f);
        }

        obs.x    = Constants.WORLD_WIDTH + 10;
        obs.rect = new Rectangle(obs.x, obs.y, obs.width, obs.height);
        obstacles.add(obs);
    }

    private void spawnCoin() {
        int   count  = MathUtils.random(3, 6);
        float baseY  = MathUtils.random(60f, Constants.WORLD_HEIGHT - 60f);
        float startX = Constants.WORLD_WIDTH + 10;

        for (int i = 0; i < count; i++) {
            CoinData coin = new CoinData();
            coin.x    = startX + i * (Constants.COIN_SIZE + 10);
            coin.y    = baseY + MathUtils.random(-24f, 24f);
            coin.rect = new Rectangle(coin.x, coin.y, Constants.COIN_SIZE, Constants.COIN_SIZE);
            coins.add(coin);
        }
    }

    private void triggerBanner(int envIndex) {
        String[] names = {"NEON LAB", "CRYSTALLINE VAULT", "PLASMA CORE"};
        bannerText  = names[envIndex];
        bannerState = BANNER_FADE_IN;
        bannerTimer = 0f;
        bannerAlpha = 0f;
    }

    private void updateBanner(float delta) {
        if (bannerState == BANNER_NONE) return;
        bannerTimer += delta;
        if (bannerState == BANNER_FADE_IN) {
            bannerAlpha = bannerTimer / Constants.ENV_BANNER_FADE_IN;
            if (bannerTimer >= Constants.ENV_BANNER_FADE_IN) {
                bannerAlpha = 1f; bannerState = BANNER_DISPLAY; bannerTimer = 0f;
            }
        } else if (bannerState == BANNER_DISPLAY) {
            if (bannerTimer >= Constants.ENV_BANNER_DISPLAY) {
                bannerState = BANNER_FADE_OUT; bannerTimer = 0f;
            }
        } else if (bannerState == BANNER_FADE_OUT) {
            bannerAlpha = 1f - bannerTimer / Constants.ENV_BANNER_FADE_OUT;
            if (bannerTimer >= Constants.ENV_BANNER_FADE_OUT) {
                bannerAlpha = 0f; bannerState = BANNER_NONE;
            }
        }
    }

    private void persistRunStats() {
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        int prevCoins = prefs.getInteger(Constants.KEY_TOTAL_COINS_COLLECTED, 0);
        prefs.putInteger(Constants.KEY_TOTAL_COINS_COLLECTED, prevCoins + coinsCollected);
        float prevDist = prefs.getFloat(Constants.KEY_TOTAL_DISTANCE, 0f);
        prefs.putFloat(Constants.KEY_TOTAL_DISTANCE, prevDist + distanceThisRun);
        prefs.flush();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Draw
    // ─────────────────────────────────────────────────────────────────────────

    private void draw() {
        shapeRenderer.setProjectionMatrix(camera.combined);

        // ── Batch pass: backgrounds, sprites ─────────────────────────────────
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // Tiled scrolling background
        Texture bg  = currentBackground();
        float   bgX = -(bgOffsetX % Constants.WORLD_WIDTH);
        game.batch.draw(bg, bgX,                    0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.draw(bg, bgX + Constants.WORLD_WIDTH, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        // Coins
        TextureRegion coinFrame = coinAnim.getKeyFrame(stateTime, true);
        for (CoinData coin : coins) {
            game.batch.draw(coinFrame, coin.x, coin.y, Constants.COIN_SIZE, Constants.COIN_SIZE);
        }

        // Missiles and zappers
        for (ObstacleData obs : obstacles) {
            if (obs.type == OBS_MISSILE) {
                game.batch.draw(missileTex, obs.x, obs.y, obs.width, obs.height);
            } else if (obs.type == OBS_ZAPPER) {
                game.batch.draw(zapperTex,
                        obs.x + obs.width * 0.5f, obs.y + obs.height * 0.5f,
                        obs.width * 0.5f, obs.height * 0.5f,
                        obs.width, obs.height, 1f, 1f, obs.rotation, false);
            }
        }

        // Player
        TextureRegion playerFrame = dead ? playerHurt : skinRegions[equippedSkin];
        game.batch.draw(playerFrame, playerX, playerY, Constants.PLAYER_WIDTH, Constants.PLAYER_HEIGHT);

        // Jetpack fire when thrusting
        if (!dead && Gdx.input.isTouched()) {
            TextureRegion fire = fireAnim.getKeyFrame(stateTime, true);
            game.batch.draw(fire, playerX + 8f, playerY - 20f,
                    Constants.PLAYER_WIDTH * 0.5f, 20f);
        }

        game.batch.end();

        // ── Shape pass: lasers + banner overlay ───────────────────────────────
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        // Laser obstacles
        for (ObstacleData obs : obstacles) {
            if (obs.type == OBS_LASER) {
                shapeRenderer.setColor(NEON_BLUE.r, NEON_BLUE.g, NEON_BLUE.b, 0.9f);
                shapeRenderer.rect(obs.x, obs.y, obs.width, obs.height);
            }
        }
        // Banner dark overlay
        if (bannerState != BANNER_NONE && bannerAlpha > 0f) {
            shapeRenderer.setColor(0.102f, 0.043f, 0.243f, bannerAlpha * 0.85f);
            shapeRenderer.rect(0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // ── Batch pass 2: banner text ─────────────────────────────────────────
        if (bannerState != BANNER_NONE && bannerAlpha > 0f) {
            game.batch.setProjectionMatrix(camera.combined);
            game.batch.begin();
            bannerFont.setColor(NEON_BLUE.r, NEON_BLUE.g, NEON_BLUE.b, bannerAlpha);
            glyphLayout.setText(bannerFont, bannerText);
            float bx = (Constants.WORLD_WIDTH  - glyphLayout.width)  / 2f;
            float by = (Constants.WORLD_HEIGHT + glyphLayout.height) / 2f;
            bannerFont.draw(game.batch, bannerText, bx, by);
            bannerFont.setColor(Color.WHITE);
            game.batch.end();
        }
    }

    private Texture currentBackground() {
        switch (currentEnv) {
            case 1: return bgCrystallineVault;
            case 2: return bgPlasmaCore;
            default: return bgNeonLab;
        }
    }

    private void resetInputProcessor() {
        Gdx.input.setInputProcessor(new InputMultiplexer(hudStage, new InputAdapter() {
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
    // Inner data classes
    // ─────────────────────────────────────────────────────────────────────────

    private static class ObstacleData {
        int   type;
        float x, y, width, height;
        float velY     = 0f;
        float rotation = 0f;
        Rectangle rect = new Rectangle();
    }

    private static class CoinData {
        float   x, y;
        boolean collected = false;
        Rectangle rect    = new Rectangle();
    }
}
