# Jetpack Run — Figma AI Design Brief

---

## 1. Art Style & Color Palette

**Art Style:** Bold, flat-design sci-fi aesthetic with clean geometric shapes and subtle depth via layered shadows and glows. Characters and obstacles are stylized and rounded rather than realistic, appealing to casual audiences aged 8+. All UI elements maintain consistent stroke weight (2–3px) and use icon-driven visual hierarchy.

**Primary Color Palette:**
- **Neon Electric Blue:** #00D9FF (primary interactive elements, jetpack glow, active states)
- **Deep Space Indigo:** #1A0B3E (backgrounds, dark UI panels)
- **Vibrant Lime Green:** #39FF14 (coins, success states, mission progress bars)
- **Bright Orange:** #FF6B35 (hazard warnings, missiles, danger states)

**Accent Colors:**
- **Silver Metallic:** #C0C0C0 (gadget panels, premium currency highlights)
- **Hot Pink:** #FF1493 (mission badges, special events)

**Typography Mood:** Modern, tech-forward sans-serif (e.g., Roboto Bold, Montserrat ExtraBold). Heavy weights (700–900) for titles and CTAs; regular weight (400) for body copy. All text uses anti-aliasing for clarity on mobile screens.

---

## 2. App Icon — icon_512.png (512×512px)

**Canvas Background:** Radial gradient from Deep Space Indigo (#1A0B3E) at edges to Neon Electric Blue (#00D9FF) at center, creating depth and suggesting a lab portal aesthetic.

**Central Symbol:** A stylized jetpack-equipped character in mid-flight silhouette, rendered in clean white with a bold, confident upward pose. The jetpack exhaust plume trails beneath in Vibrant Lime Green (#39FF14) and Bright Orange (#FF6B35), blending smoothly to suggest motion and energy. The character's body occupies the safe zone (centered within 400×400px).

**Glow & Depth Effects:** Outer glow around the jetpack exhaust in Neon Electric Blue with a 12px blur radius; subtle inner shadow on the character silhouette to separate it from the background. A thin silver highlight (2px, #C0C0C0) traces the top edge of the jetpack pack itself.

**Overall Mood:** Energetic, playful, immediately recognizable as a flight/jetpack game. The gradient background suggests infinite sky and sci-fi labs; the character's upward motion communicates the core mechanic instantly.

---

## 3. Backgrounds (854x480 landscape)

**Identified Environments (from GDD: "Three lab environments cycle"):**
1. Neon Lab (electric/tech-focused)
2. Crystalline Vault (ice/mineral-focused)
3. Plasma Core (molten/energy-focused)

**Background Files:**

**backgrounds/bg_main.png (854×480)**
Main menu title screen backdrop. A seamless tiled grid pattern in Deep Space Indigo (#1A0B3E) with thin Neon Electric Blue (#00D9FF) grid lines (1px spaced 40px apart), suggesting a digital interface. Subtle animated scanlines (horizontal stripes, 2% opacity) hint at future animation. Center foreground features abstract floating geometric shapes (triangles, circles) in varying opacities of Neon Electric Blue and Vibrant Lime Green, creating depth layers and framing the play button area. No photorealism; maintain flat geometric aesthetic throughout.

**backgrounds/bg_neon_lab.png (854×480)**
Neon Lab environment. Dominant color: Deep Space Indigo (#1A0B3E) with bright Neon Electric Blue (#00D9FF) vertical light strips resembling lab walls/panels. Scattered holographic circuit board patterns (thin lines forming hex and square shapes) float mid-ground in semi-transparent Neon Electric Blue. Foreground includes oversized lab equipment silhouettes (beakers, test tubes) in dark indigo with glowing Neon Electric Blue outlines. Mood: high-tech, energetic, clean and clinical. No hazard elements in the background itself.

**backgrounds/bg_crystalline_vault.png (854×480)**
Crystalline Vault environment. Dominant color: cool cyan-blue gradient (#1A5F7A to #00D9FF), suggesting an icy underground cavern. Large crystalline geometric formations (isometric cubes and shards) jut from top and bottom edges in translucent turquoise (#40E0D0) with darker crystal core shadows. Scattered smaller crystal particles float at various depths, catching light with subtle white highlights. Foreground floor and ceiling suggest jagged ice/mineral surfaces. Mood: cold, otherworldly, visually distinct from the neon lab. Hazards will be overlaid during gameplay.

**backgrounds/bg_plasma_core.png (854×480)**
Plasma Core environment. Dominant color: warm gradient from deep orange (#8B4513) to bright Bright Orange (#FF6B35) and hot yellow (#FFD700), suggesting molten energy. Swirling, organic plasma patterns (flowing wavy lines, 2–3px stroke) in Vibrant Lime Green (#39FF14) and yellow move through the space, hinting at energy currents. Distant background suggests a massive circular reactor or sun in the center horizon (large gradient orb, #FFD700 core fading to orange). Foreground features heated rocky terrain silhouettes in dark brown with orange glow edges. Mood: intense, dangerous, visually warm and dynamic. Communicates high-difficulty environment.

---

## 4. UI Screens (854x480 landscape)

### **main_menu.png (854×480)**
Uses **backgrounds/bg_main.png** as backdrop. Title "JETPACK RUN" rendered in bold Montserrat ExtraBold, size 72px, color Neon Electric Blue (#00D9FF), horizontally centered, positioned in upper-third of screen. Subtitle text "Hold to Fly • Release to Fall" in regular weight, 18px, Vibrant Lime Green (#39FF14), centered below title. Large primary CTA button labeled "PLAY" (48px bold, white text) centered horizontally, positioned in lower-middle area; button background is solid Neon Electric Blue (#00D9FF) with 8px rounded corners and a 3px darker blue outline. Four smaller secondary buttons (32px height, 3px white stroke, transparent fill, white text) arranged horizontally at bottom: "SHOP," "GADGETS," "MISSIONS," "SETTINGS" (left to right). Top-right corner displays a small profile card: player's best score in 20px bold white text ("Best: 4,250"), profile icon (48×48px circle, #C0C0C0 background). Leaderboard icon (star symbol) positioned top-right corner, clickable area.

### **game_neon_lab.png (854×480)**
Uses **backgrounds/bg_neon_lab.png** as gameplay backdrop. Top-left HUD displays: current score in 28px bold Neon Electric Blue (#00D9FF) text ("Score: 1,240"), coin counter immediately below in 20px bold Vibrant Lime Green (#39FF14) with coin icon. Top-right corner shows mission progress bar (thin horizontal bar, 120px width, background #1A0B3E, fill Vibrant Lime Green (#39FF14), no text). Center screen: player character (jetpack sprite) positioned center-bottom, with animated jetpack flame particles (orange/yellow) trailing upward when holding input. Obstacles (lasers, missiles, zappers) spawn and scroll downward across screen. Coins scatter across playfield as yellow/lime geometric shapes (12×12px). No pause menu visible in this state; UI is minimal and does not obscure gameplay. Mood: fast-paced, immediately playable.

### **game_crystalline_vault.png (854×480)**
Uses **backgrounds/bg_crystalline_vault.png** as gameplay backdrop. Identical HUD layout to game_neon_lab.png: score top-left (28px bold, Neon Electric Blue #00D9FF), coin counter below (20px Vibrant Lime Green #39FF14), mission progress bar top-right. Player character and input mechanics are identical; hazards (lasers, missiles, zappers) maintain consistent design but may visually blend differently against the cool cyan background, so obstacle glow/outline color shifts slightly to maintain contrast (e.g., laser color shifts to hot pink #FF1493 for visibility). Coins and particles are unchanged. Mood: same fast-paced gameplay, but cooler aesthetic environment.

### **game_plasma_core.png (854×480)**
Uses **backgrounds/bg_plasma_core.png** as gameplay backdrop. HUD identical to previous game screens. Hazards (lasers, missiles) may have warmer glow colors (orange/yellow tones) to harmonize with environment, but maintain gameplay clarity. Player character sprite and all interactive elements remain consistent. Coins and particles unchanged. Mood: intense, high-energy environment variant with identical gameplay mechanics.

### **environment_banner.png (854×480)**
Full-screen transition banner. Solid Deep Space Indigo (#1A0B3E) background with a large centered text label (48px bold Montserrat, Neon Electric Blue #00D9FF) announcing the new environment: "NEON LAB" or "CRYSTALLINE VAULT" or "PLASMA CORE." Below the label, a small environment-specific icon (64×64px) styled consistently with app icon. Thin horizontal accent bars (2px, Vibrant Lime Green #39FF14) extend from icon left and right edges. Fades in over 1 second, displays for 2 seconds, fades out. No interactive elements; purely informational transition. Mood: celebratory, brief, professional.

### **mission_screen.png (854×480)**
Uses **backgrounds/bg_main.png** as backdrop (lightly darkened via 20% black overlay for focus). Header "MISSIONS" in 48px bold Neon Electric Blue (#00D9FF), positioned top-center. Content area displays a scrollable list (3–4 visible mission cards). Each mission card (width: 700px, height: 80px): light background (#2A1560, semi-transparent), 4px border Vibrant Lime Green (#39FF14). Card shows mission title (18px bold, white), short description (14px regular, light gray), progress bar (width: 500px, background #1A0B3E, fill #39FF14), and a right-aligned CTA button labeled "PLAY" (36px, Neon Electric Blue background, white text, 6px rounded corners). Bottom-center: "BACK" button (40px height, white stroke, transparent background, white text). Mood: clear, organized, motivating.

### **mission_complete_screen.png (854×480)**
Uses **backgrounds/bg_main.png** with a darker overlay (40% black opacity). Center screen: large celebration graphic (circular badge, 200×200px background Vibrant Lime Green #39FF14, white border 4px). Inside badge: gold star icon (120px) and bold text "MISSION" stacked above "COMPLETE!" (both 28px bold, Deep Space Indigo #1A0B3E). Below badge: reward text (20px bold, Neon Electric Blue #00D9FF): "Reward: +500 Coins." Bottom area: two buttons side by side (180px width each, 48px height): left button "CLAIM & PLAY AGAIN" (Neon Electric Blue background, white text, 6px rounded), right button "BACK TO MENU" (white stroke, transparent background, white text). Confetti particle animation (small triangles, circles in Vibrant Lime Green and Hot Pink #FF1493) falls continuously across screen. Mood: celebratory, rewarding, joyful.

### **game_over_screen.png (854×480)**
Uses **backgrounds/bg_main.png** with 50% black overlay for darkness. Center: "GAME OVER" title (52px bold Bright Orange #FF6B35), followed by final score display (36px bold Neon Electric Blue #00D9FF): "Final Score: 8,450." Below: coin earned (24px regular, Vibrant Lime Green #39FF14): "Coins Earned: 280." Bottom area contains two large buttons stacked vertically (280px width, 48px height): top "PLAY AGAIN" (Neon Electric Blue background, white bold text), bottom "MAIN MENU" (white stroke, transparent background, white text). Top-right corner displays rank/position badge if player achieved high score (e.g., "NEW BEST!" in Hot Pink #FF1493, 18px bold). Mood: respectful, encouraging restart.

### **shop_screen.png (854×480)**
Uses **backgrounds/bg_main.png** as backdrop. Header "SHOP" (48px bold Neon Electric Blue #00D9FF), top-center. Player's coin balance displayed top-right (20px, Vibrant Lime Green #39FF14 with coin icon): "Coins: 1,200." Content area displays a grid of jetpack skin cards (3 columns, 2 rows visible, scrollable). Each skin card (240×200px): rounded corners 8px, background #2A1560, 2px border alternating Neon Electric Blue (#00D9FF) or Vibrant Lime Green (#39FF14) based on owned/locked status. Card shows skin preview (120×80px centered image of jetpack variant), name (16px bold, white), price in coins (14px, Vibrant Lime Green #39FF14) with coin icon, and CTA button: "BUY" or "OWNED" or "EQUIPPED" (32px height, appropriate color-coded). Locked skins show a padlock icon (32×32px, gray). Bottom-center: "BACK" button (40px height, white stroke). Mood: enticing, organized, clear purchase path.

### **leaderboard_screen.png (854×480)**
Uses **backgrounds/bg_main.png** as backdrop. Header "LEADERBOARD" (48px bold Neon Electric Blue #00D9FF), top-center. Player's current rank and score displayed in a prominent card below header (width: 700px, background silver #C0C0C0, 2px border Neon Electric Blue #00D9FF): "Your Rank: #7 | Your Best: 12,340." Leaderboard table below (700px width, scrollable): 10 rows, each 50px height. Column headers (14px bold, white on dark background #1A0B3E): "Rank," "Player," "Score." Rows alternate background colors (#2A1560 and #1A0B3E, 1px separator lines). Rank 1–3 rows highlight: background tint Vibrant Lime Green (#39FF14) with 10% opacity. Each row displays rank number (18px bold), player name (16px regular), and score (16px bold, Neon Electric Blue #00D9FF). Bottom-center: "BACK" button. Mood: competitive, clear, aspirational.

### **settings_screen.png (854×480)**
Uses **backgrounds/bg_main.png** as backdrop. Header "SETTINGS" (48px bold Neon Electric Blue #00D9FF), top-center. Settings panel (600px width, vertically scrollable): list of toggle switches and selectors. Each setting row (height 60px): label text (18px regular, white) on left, interactive control on right. Settings include: "SOUND ON/OFF" (toggle switch, Neon Electric Blue #00D9FF when active), "DIFFICULTY: EASY / NORMAL / HARD" (dropdown or segmented buttons, white text on dark background), "HAPTICS ON/OFF" (toggle switch). Below toggles, a warning section: "RESET GAME DATA" button (180px width, 40px height, Bright Orange #FF6B35 background, white bold text, 4px rounded corners) with confirmation modal trigger. Bottom-center: "BACK" button (40px height, white stroke). Mood: clear, accessible, cautious (for destructive actions like reset).

---

## 5. Export Checklist

- icon_512.png (512×512)
- backgrounds/bg_main.png (854×480)
- backgrounds/bg_neon_lab.png (854×480)
- backgrounds/bg_crystalline_vault.png (854×480)
- backgrounds/bg_plasma_core.png (854×480)
- main_menu.png (854×480)
- game_neon_lab.png (854×480)
- game_crystalline_vault.png (854×480)
- game_plasma_core.png (854×480)
- environment_banner.png (854×480)
- mission_screen.png (854×480)
- mission_complete_screen.png (854×480)
- game_over_screen.png (854×480)
- shop_screen.png (854×480)
- leaderboard_screen.png (854×480)
- settings_screen.png (854×480)

---

**Total Files: 16**  
**All assets are production-ready for Android (libGDX) integration at 854×480 landscape resolution.**
