package com.factory.jetpackrun2w11;

public class Constants {

    // ── World ────────────────────────────────────────────────────────────────
    public static final float WORLD_WIDTH  = 854f;
    public static final float WORLD_HEIGHT = 480f;

    // ── Physics ──────────────────────────────────────────────────────────────
    public static final float GRAVITY         = -900f;
    public static final float JETPACK_THRUST  = 1800f;  // ≥ 2× |GRAVITY| — required
    public static final float MAX_FALL_SPEED  = -700f;
    public static final float PLAYER_START_X  = 160f;
    public static final float PLAYER_START_Y  = 240f;

    // ── Player ───────────────────────────────────────────────────────────────
    public static final float PLAYER_WIDTH    = 64f;
    public static final float PLAYER_HEIGHT   = 64f;

    // ── Scroll & Speed ────────────────────────────────────────────────────────
    public static final float SCROLL_SPEED_INITIAL = 200f;
    public static final float SCROLL_SPEED_MAX     = 500f;
    public static final float SCROLL_SPEED_INCREMENT = 5f;  // per second
    public static final float SPEED_INCREASE_INTERVAL = 5f; // seconds between increases

    // ── Obstacles ────────────────────────────────────────────────────────────
    public static final float LASER_WIDTH        = 16f;
    public static final float LASER_HEIGHT       = 120f;
    public static final float MISSILE_WIDTH      = 64f;
    public static final float MISSILE_HEIGHT     = 32f;
    public static final float ZAPPER_WIDTH       = 48f;
    public static final float ZAPPER_HEIGHT      = 48f;
    public static final float OBSTACLE_SPAWN_MIN = 1.0f;  // seconds
    public static final float OBSTACLE_SPAWN_MAX = 2.5f;

    // ── Coins ─────────────────────────────────────────────────────────────────
    public static final float COIN_SIZE          = 24f;
    public static final int   COIN_SCORE_VALUE   = 10;
    public static final float COIN_SPAWN_MIN     = 0.5f;
    public static final float COIN_SPAWN_MAX     = 1.5f;

    // ── Environments ─────────────────────────────────────────────────────────
    public static final int   ENV_COUNT          = 3;
    public static final float ENV_DURATION       = 30f;   // seconds per environment
    public static final float ENV_BANNER_FADE_IN = 1.0f;
    public static final float ENV_BANNER_DISPLAY = 2.0f;
    public static final float ENV_BANNER_FADE_OUT = 1.0f;

    // ── Score ─────────────────────────────────────────────────────────────────
    public static final int   SCORE_PER_SECOND   = 5;

    // ── Shop / Skins ─────────────────────────────────────────────────────────
    public static final int   SKIN_COUNT          = 6;
    public static final int   SKIN_DEFAULT_INDEX  = 0;
    public static final int[] SKIN_PRICES         = {0, 500, 1000, 1500, 2000, 3000};

    // ── Missions ─────────────────────────────────────────────────────────────
    public static final int   MISSION_COUNT       = 5;
    public static final int   MISSION_REWARD_COINS = 500;

    // ── Difficulty ────────────────────────────────────────────────────────────
    public static final int   DIFFICULTY_EASY     = 0;
    public static final int   DIFFICULTY_NORMAL   = 1;
    public static final int   DIFFICULTY_HARD     = 2;
    public static final float DIFFICULTY_EASY_MULTIPLIER   = 0.75f;
    public static final float DIFFICULTY_NORMAL_MULTIPLIER = 1.0f;
    public static final float DIFFICULTY_HARD_MULTIPLIER   = 1.5f;

    // ── UI / Font Sizes ───────────────────────────────────────────────────────
    public static final int   FONT_SIZE_TITLE     = 72;
    public static final int   FONT_SIZE_SUBTITLE  = 18;
    public static final int   FONT_SIZE_BUTTON    = 48;
    public static final int   FONT_SIZE_NAV_BTN   = 32;
    public static final int   FONT_SIZE_SCORE_HUD = 28;
    public static final int   FONT_SIZE_COIN_HUD  = 20;
    public static final int   FONT_SIZE_HEADER    = 48;
    public static final int   FONT_SIZE_GAMEOVER  = 52;
    public static final int   FONT_SIZE_BODY      = 18;
    public static final int   FONT_SIZE_SMALL     = 14;
    public static final int   FONT_SIZE_REWARD    = 24;

    // ── UI Layout ─────────────────────────────────────────────────────────────
    public static final float BUTTON_WIDTH          = 280f;
    public static final float BUTTON_HEIGHT         = 48f;
    public static final float NAV_BUTTON_WIDTH      = 140f;
    public static final float NAV_BUTTON_HEIGHT     = 32f;
    public static final float HUD_MISSION_BAR_WIDTH = 120f;
    public static final float HUD_MISSION_BAR_HEIGHT = 10f;
    public static final float MISSION_CARD_WIDTH    = 700f;
    public static final float MISSION_CARD_HEIGHT   = 80f;
    public static final float SHOP_CARD_WIDTH       = 240f;
    public static final float SHOP_CARD_HEIGHT      = 200f;
    public static final float LEADERBOARD_ROW_HEIGHT = 50f;
    public static final float LEADERBOARD_TABLE_WIDTH = 700f;
    public static final float PROFILE_ICON_SIZE     = 48f;
    public static final float MISSION_BADGE_SIZE    = 200f;
    public static final float BADGE_STAR_SIZE       = 120f;
    public static final float SETTINGS_PANEL_WIDTH  = 600f;
    public static final float SETTINGS_ROW_HEIGHT   = 60f;

    // ── Leaderboard ───────────────────────────────────────────────────────────
    public static final int   LEADERBOARD_MAX_ENTRIES = 10;

    // ── SharedPreferences Keys ───────────────────────────────────────────────
    public static final String PREFS_NAME           = "JetpackRunPrefs";
    public static final String KEY_HIGH_SCORE       = "highScore";
    public static final String KEY_TOTAL_COINS      = "totalCoins";
    public static final String KEY_EQUIPPED_SKIN    = "equippedSkin";
    public static final String KEY_SOUND_ENABLED    = "soundEnabled";
    public static final String KEY_SFX_ENABLED      = "sfxEnabled";
    public static final String KEY_HAPTICS_ENABLED  = "hapticsEnabled";
    public static final String KEY_DIFFICULTY       = "difficulty";
    public static final String KEY_SKIN_OWNED_PREFIX = "skinOwned_";
    public static final String KEY_LEADERBOARD_PREFIX = "lb_score_";
    public static final String KEY_LEADERBOARD_NAME_PREFIX = "lb_name_";
    public static final String KEY_MISSION_PROGRESS_PREFIX = "mission_progress_";
    public static final String KEY_MISSION_COMPLETE_PREFIX = "mission_complete_";
    public static final String KEY_TOTAL_DISTANCE   = "totalDistance";
    public static final String KEY_TOTAL_COINS_COLLECTED = "totalCoinsCollected";
    public static final String KEY_TOTAL_RUNS       = "totalRuns";

    // ── Asset Paths ───────────────────────────────────────────────────────────
    public static final String BG_MAIN             = "backgrounds/bg_main.png";
    public static final String BG_NEON_LAB         = "backgrounds/bg_neon_lab.png";
    public static final String BG_CRYSTALLINE_VAULT = "backgrounds/bg_crystalline_vault.png";
    public static final String BG_PLASMA_CORE      = "backgrounds/bg_plasma_core.png";
    public static final String SPRITE_PLAYER_IDLE  = "sprites/player_idle.png";
    public static final String SPRITE_PLAYER_WALK1 = "sprites/player_walk1.png";
    public static final String SPRITE_PLAYER_WALK2 = "sprites/player_walk2.png";
    public static final String SPRITE_PLAYER_JUMP  = "sprites/player_jump.png";
    public static final String SPRITE_PLAYER_HURT  = "sprites/player_hurt.png";
    public static final String SPRITE_PLAYER_STAND = "sprites/player_stand.png";
    public static final String SPRITE_COIN1        = "sprites/coin_spin1.png";
    public static final String SPRITE_COIN2        = "sprites/coin_spin2.png";
    public static final String SPRITE_EFFECT_FIRE1 = "sprites/effect_fire1.png";
    public static final String SPRITE_EFFECT_FIRE2 = "sprites/effect_fire2.png";
    public static final String SPRITE_OBSTACLE_SPIKE = "sprites/obstacle_spike.png";
    public static final String SPRITE_ENEMY_SAW    = "sprites/enemy_saw.png";
    public static final String SPRITE_ENEMY_FLY    = "sprites/enemy_fly.png";
    public static final String FONT_MAIN           = "fonts/Roboto-Regular.ttf";
    public static final String FONT_TITLE          = "fonts/Orbitron-Regular.ttf";

    // ── Skin sprite paths (index matches SKIN_PRICES) ─────────────────────────
    public static final String[] SKIN_SPRITE_PATHS = {
        "sprites/player_idle.png",
        "sprites/player_walk1.png",
        "sprites/player_walk2.png",
        "sprites/player_jump.png",
        "sprites/player_stand.png",
        "sprites/player_hurt.png"
    };
    public static final String[] SKIN_NAMES = {
        "DEFAULT", "RUNNER", "SPRINTER", "FLYER", "ELITE", "DAREDEVIL"
    };

    // ── Mission definitions ───────────────────────────────────────────────────
    public static final String KEY_MISSION_CLAIMED_PREFIX = "mission_claimed_";
    public static final String[] MISSION_NAMES = {
        "First Flight", "Coin Hoarder", "High Scorer", "Frequent Flyer", "Jet Setter"
    };
    public static final String[] MISSION_DESCS = {
        "Complete your first run",
        "Collect 100 coins total",
        "Score 1000 in a single run",
        "Complete 10 runs",
        "Travel 5000 total distance"
    };
    public static final int[] MISSION_TARGETS = { 1, 100, 1000, 10, 5000 };
}
