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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.factory.jetpackrun2w11.Constants;
import com.factory.jetpackrun2w11.MainGame;

/**
 * Shop screen — buy and equip jetpack skins using collected coins.
 * Skin catalogue: 6 skins using the available player sprites.
 * Coin balance displayed top-right; purchase deducts from balance.
 */
public class ShopScreen implements Screen {

    private static final Color NEON_BLUE  = new Color(0f,    0.851f, 1f,    1f);
    private static final Color LIME_GREEN = new Color(0.224f, 1f,   0.078f, 1f);

    private final MainGame game;
    private final OrthographicCamera camera;
    private final StretchViewport    viewport;
    private final Stage   stage;
    private final AssetManager manager;
    private final ShapeRenderer shapeRenderer;

    private final BitmapFont headerFont;
    private final BitmapFont cardNameFont;
    private final BitmapFont priceFont;
    private final BitmapFont balanceFont;
    private final BitmapFont navFont;
    private final BitmapFont btnFont;

    // Dynamic coin balance label (rebuilt on purchase)
    private Label balanceLabel;

    public ShopScreen(MainGame game) {
        this.game = game;

        manager = new AssetManager();
        manager.load(Constants.BG_MAIN, Texture.class);
        for (String path : Constants.SKIN_SPRITE_PATHS) {
            manager.load(path, Texture.class);
        }
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

        FreeTypeFontGenerator.FreeTypeFontParameter cnp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        cnp.size = Constants.FONT_SIZE_BODY; cnp.color = Color.WHITE;
        cardNameFont = bodyGen.generateFont(cnp);

        FreeTypeFontGenerator.FreeTypeFontParameter pp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        pp.size = Constants.FONT_SIZE_SMALL; pp.color = LIME_GREEN;
        priceFont = bodyGen.generateFont(pp);

        FreeTypeFontGenerator.FreeTypeFontParameter blp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        blp.size = Constants.FONT_SIZE_COIN_HUD; blp.color = LIME_GREEN;
        balanceFont = bodyGen.generateFont(blp);

        FreeTypeFontGenerator.FreeTypeFontParameter np = new FreeTypeFontGenerator.FreeTypeFontParameter();
        np.size = Constants.FONT_SIZE_NAV_BTN; np.color = Color.WHITE;
        navFont = bodyGen.generateFont(np);

        FreeTypeFontGenerator.FreeTypeFontParameter bp = new FreeTypeFontGenerator.FreeTypeFontParameter();
        bp.size = Constants.FONT_SIZE_SMALL + 2; bp.color = Color.WHITE;
        btnFont = bodyGen.generateFont(bp);

        bodyGen.dispose();

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
    // UI
    // ─────────────────────────────────────────────────────────────────────────

    private void buildUI() {
        stage.clear();

        Preferences prefs     = Gdx.app.getPreferences(Constants.PREFS_NAME);
        int         coins     = prefs.getInteger(Constants.KEY_TOTAL_COINS, 0);
        int         equipped  = prefs.getInteger(Constants.KEY_EQUIPPED_SKIN, Constants.SKIN_DEFAULT_INDEX);

        Label.LabelStyle headerStyle  = new Label.LabelStyle(headerFont,   NEON_BLUE);
        Label.LabelStyle cardStyle    = new Label.LabelStyle(cardNameFont, Color.WHITE);
        Label.LabelStyle priceStyle   = new Label.LabelStyle(priceFont,    LIME_GREEN);
        Label.LabelStyle balanceStyle = new Label.LabelStyle(balanceFont,  LIME_GREEN);
        TextButton.TextButtonStyle navStyle = new TextButton.TextButtonStyle();
        navStyle.font = navFont; navStyle.fontColor = Color.WHITE;
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = btnFont; btnStyle.fontColor = Color.WHITE;

        Label header = new Label("SHOP", headerStyle);
        balanceLabel = new Label("Coins: " + coins, balanceStyle);

        // Build skin cards in a 3-column grid
        Table grid = new Table();
        grid.defaults().pad(8f);

        for (int i = 0; i < Constants.SKIN_COUNT; i++) {
            final int skinIdx = i;
            boolean owned    = i == 0 || prefs.getBoolean(Constants.KEY_SKIN_OWNED_PREFIX + i, false);
            boolean isEquip  = (i == equipped);

            Table card = new Table();
            card.pad(8f);

            // Skin preview image
            Texture tex = manager.get(Constants.SKIN_SPRITE_PATHS[i], Texture.class);
            Image preview = new Image(new TextureRegionDrawable(new TextureRegion(tex)));
            card.add(preview).width(80f).height(80f).center().row();

            // Skin name
            card.add(new Label(Constants.SKIN_NAMES[i], cardStyle)).center().row();

            // Price or status
            if (isEquip) {
                card.add(new Label("EQUIPPED", new Label.LabelStyle(priceFont, NEON_BLUE)))
                        .padTop(4f).row();
            } else if (owned) {
                // Equip button
                TextButton equipBtn = new TextButton("EQUIP", btnStyle);
                equipBtn.addListener(new ChangeListener() {
                    @Override public void changed(ChangeEvent event, Actor actor) {
                        equipSkin(skinIdx);
                    }
                });
                card.add(equipBtn).padTop(4f).width(80f).height(28f).row();
            } else {
                // Price label
                card.add(new Label(Constants.SKIN_PRICES[i] + " coins", priceStyle))
                        .padTop(2f).row();
                // Buy button (grey out if can't afford)
                boolean canAfford = (coins >= Constants.SKIN_PRICES[i]);
                Label.LabelStyle buyStyle = new Label.LabelStyle(btnFont,
                        canAfford ? Color.WHITE : Color.GRAY);
                TextButton.TextButtonStyle buyBtnStyle = new TextButton.TextButtonStyle();
                buyBtnStyle.font      = btnFont;
                buyBtnStyle.fontColor = canAfford ? NEON_BLUE : Color.GRAY;
                TextButton buyBtn = new TextButton("BUY", buyBtnStyle);
                if (canAfford) {
                    buyBtn.addListener(new ChangeListener() {
                        @Override public void changed(ChangeEvent event, Actor actor) {
                            buySkin(skinIdx);
                        }
                    });
                }
                card.add(buyBtn).padTop(4f).width(80f).height(28f).row();
            }

            grid.add(card).width(Constants.SHOP_CARD_WIDTH * 0.48f)
                    .height(Constants.SHOP_CARD_HEIGHT * 0.7f);
            if ((i + 1) % 3 == 0) grid.row();
        }

        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFadeScrollBars(false);

        TextButton menuBtn = new TextButton("MAIN MENU", navStyle);
        menuBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        // Top bar: header left, balance right
        Table topBar = new Table();
        topBar.setFillParent(true);
        topBar.top().pad(10f);
        topBar.add(header).left().expandX();
        topBar.add(balanceLabel).right();
        stage.addActor(topBar);

        // Content + nav
        Table root = new Table();
        root.setFillParent(true);
        root.center();
        root.padTop(60f);
        root.add(scrollPane)
                .width(Constants.WORLD_WIDTH - 40f)
                .height(Constants.WORLD_HEIGHT - 140f)
                .padBottom(12f).row();
        root.add(menuBtn).width(Constants.BUTTON_WIDTH).height(Constants.BUTTON_HEIGHT).row();
        stage.addActor(root);
    }

    private void buySkin(int skinIndex) {
        Preferences prefs  = Gdx.app.getPreferences(Constants.PREFS_NAME);
        int         coins  = prefs.getInteger(Constants.KEY_TOTAL_COINS, 0);
        int         price  = Constants.SKIN_PRICES[skinIndex];

        if (coins < price) return;

        prefs.putInteger(Constants.KEY_TOTAL_COINS, coins - price);
        prefs.putBoolean(Constants.KEY_SKIN_OWNED_PREFIX + skinIndex, true);
        prefs.putInteger(Constants.KEY_EQUIPPED_SKIN, skinIndex);
        prefs.flush();

        buildUI(); // Rebuild cards to reflect new state
    }

    private void equipSkin(int skinIndex) {
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        prefs.putInteger(Constants.KEY_EQUIPPED_SKIN, skinIndex);
        prefs.flush();

        buildUI();
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

        drawCardBackgrounds();

        stage.act(delta);
        stage.draw();
    }

    /**
     * Draws rounded-rectangle backgrounds behind each skin card using ShapeRenderer,
     * styled after the design brief (bordered cards with appropriate colors).
     */
    private void drawCardBackgrounds() {
        Preferences prefs    = Gdx.app.getPreferences(Constants.PREFS_NAME);
        int         equipped = prefs.getInteger(Constants.KEY_EQUIPPED_SKIN, Constants.SKIN_DEFAULT_INDEX);

        float cardW   = Constants.SHOP_CARD_WIDTH  * 0.48f + 16f;
        float cardH   = Constants.SHOP_CARD_HEIGHT * 0.7f  + 16f;
        float cols    = 3f;
        float gridW   = Constants.WORLD_WIDTH - 40f;
        float startX  = (Constants.WORLD_WIDTH - gridW) / 2f;
        float startY  = Constants.WORLD_HEIGHT - 140f - 16f; // approx below top bar + padding
        float padX    = (gridW - cols * cardW) / (cols + 1);

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < Constants.SKIN_COUNT; i++) {
            int   col    = i % 3;
            int   row    = i / 3;
            float cx     = startX + padX + col * (cardW + padX);
            float cy     = startY - row * (cardH + 8f);

            // Card background
            shapeRenderer.setColor(0.165f, 0.086f, 0.376f, 0.88f);
            shapeRenderer.rect(cx, cy, cardW, cardH);

            // Border color: NEON_BLUE for equipped, LIME_GREEN for owned, gray for locked
            boolean owned = (i == 0) || prefs.getBoolean(Constants.KEY_SKIN_OWNED_PREFIX + i, false);
            if (i == equipped) {
                shapeRenderer.setColor(NEON_BLUE.r, NEON_BLUE.g, NEON_BLUE.b, 1f);
            } else if (owned) {
                shapeRenderer.setColor(LIME_GREEN.r, LIME_GREEN.g, LIME_GREEN.b, 0.8f);
            } else {
                shapeRenderer.setColor(0.4f, 0.4f, 0.4f, 0.6f);
            }
            // Top border line
            shapeRenderer.rect(cx, cy + cardH - 3f, cardW, 3f);
            // Bottom border line
            shapeRenderer.rect(cx, cy, cardW, 3f);
            // Left border
            shapeRenderer.rect(cx, cy, 3f, cardH);
            // Right border
            shapeRenderer.rect(cx + cardW - 3f, cy, 3f, cardH);
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
        cardNameFont.dispose();
        priceFont.dispose();
        balanceFont.dispose();
        navFont.dispose();
        btnFont.dispose();
    }
}
