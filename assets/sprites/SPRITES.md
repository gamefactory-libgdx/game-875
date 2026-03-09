# SPRITES.md — Available Sprite Assets

All sprites are from the **Kenney.nl** free CC0 game asset library.
Load them via `AssetManager` using paths like `"sprites/player_idle.png"`.

## RUNNER / PLATFORMER / JETPACK sprites
Use for: side-scrollers, endless runners, jetpack games, flappy-style games.

| File | Description |
|------|-------------|
| `sprites/player_idle.png` | Player character facing forward (idle/standing) |
| `sprites/player_stand.png` | Player standing (same as idle, alternate) |
| `sprites/player_walk1.png` | Player walk frame 1 — animate with walk2 for walk cycle |
| `sprites/player_walk2.png` | Player walk frame 2 |
| `sprites/player_jump.png` | Player jumping |
| `sprites/player_hurt.png` | Player hit / hurt |
| `sprites/enemy_slime.png` | Green slime enemy |
| `sprites/enemy_fly.png` | Fly / winged enemy |
| `sprites/enemy_bee.png` | Bee enemy |
| `sprites/enemy_saw.png` | Spinning saw blade obstacle |
| `sprites/obstacle_spike.png` | Spikes (floor/ceiling hazard) |
| `sprites/obstacle_rock.png` | Rock obstacle |
| `sprites/obstacle_lava.png` | Lava tile |
| `sprites/tile_ground_mid.png` | Grass ground — middle tile |
| `sprites/tile_ground_left.png` | Grass ground — left edge |
| `sprites/tile_ground_right.png` | Grass ground — right edge |
| `sprites/tile_dirt_mid.png` | Dirt ground — middle tile |
| `sprites/tile_box.png` | Wooden crate tile |
| `sprites/tile_brick.png` | Brown brick tile |
| `sprites/particle_fireball.png` | Fireball particle effect |

## SPACE SHOOTER sprites
Use for: space shooters, asteroid dodge, bullet-hell games.

| File | Description |
|------|-------------|
| `sprites/player_ship.png` | Player spaceship (blue) |
| `sprites/player_ship_alt.png` | Player spaceship alternate (green) |
| `sprites/enemy_ship1.png` | Red enemy ship |
| `sprites/enemy_ship2.png` | Blue enemy ship |
| `sprites/enemy_ship3.png` | Green enemy ship |
| `sprites/enemy_ship4.png` | Black enemy ship |
| `sprites/laser_player.png` | Blue laser (player bullet) |
| `sprites/laser_enemy.png` | Red laser (enemy bullet) |
| `sprites/asteroid_big.png` | Large brown asteroid |
| `sprites/asteroid_med.png` | Medium asteroid |
| `sprites/asteroid_small.png` | Small asteroid |
| `sprites/effect_fire1.png` | Fire/explosion frame 1 |
| `sprites/effect_fire2.png` | Fire/explosion frame 2 |

## RACING sprites
Use for: top-down car racing, lane-dodge, traffic games.

| File | Description |
|------|-------------|
| `sprites/car_player.png` | Player car (yellow) — top-down view |
| `sprites/car_red.png` | Red traffic car |
| `sprites/car_blue.png` | Blue traffic car |
| `sprites/car_green.png` | Green traffic car |
| `sprites/car_black.png` | Black traffic car |

## PUZZLE / BRICK-BREAKER sprites
Use for: brick breaker, Arkanoid, ball bounce, Breakout games.

| File | Description |
|------|-------------|
| `sprites/ball_blue.png` | Blue ball (the main ball) |
| `sprites/ball_yellow.png` | Yellow ball (alternate color) |
| `sprites/ball_grey.png` | Grey ball |
| `sprites/paddle.png` | Standard paddle (white/grey) |
| `sprites/paddle_blue.png` | Blue paddle |
| `sprites/coin_spin1.png` | Coin spin frame 1 |
| `sprites/coin_spin2.png` | Coin spin frame 2 |
| `sprites/back_tile.png` | Background tile for puzzle board |
| `sprites/back_tile2.png` | Background tile variant 2 |

## GENERIC COLLECTIBLES
Use in any game type for coins, gems, stars.

| File | Description |
|------|-------------|
| `sprites/coin_gold.png` | Gold coin |
| `sprites/coin_silver.png` | Silver coin |
| `sprites/coin_bronze.png` | Bronze coin |
| `sprites/star.png` | Star collectible |
| `sprites/gem_blue.png` | Blue gem / crystal |
| `sprites/gem_red.png` | Red gem / ruby |
| `sprites/gem_green.png` | Green gem / emerald |
| `sprites/gem_yellow.png` | Yellow gem / topaz |

## Usage example

```java
// Loading (in your LoadingScreen or MainGame):
manager.load("sprites/player_idle.png", Texture.class);
manager.load("sprites/enemy_slime.png", Texture.class);
manager.load("sprites/coin_gold.png", Texture.class);
manager.finishLoading();

// Rendering (in your GameScreen):
Texture playerTex = manager.get("sprites/player_idle.png", Texture.class);
batch.draw(playerTex, x, y, 64, 64);  // 64x64 world units

// Walk animation:
TextureRegion[] frames = {
    new TextureRegion(manager.get("sprites/player_walk1.png", Texture.class)),
    new TextureRegion(manager.get("sprites/player_walk2.png", Texture.class))
};
Animation<TextureRegion> walkAnim = new Animation<>(0.15f, frames);
```

**Source:** Kenney.nl — CC0 license — free to use in any project.
