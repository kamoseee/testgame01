package newgame;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Iterator;

public class GameRenderer {
    private BykinGame game;

    public GameRenderer(BykinGame game) {
        this.game = game;
    }

    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;// GraphicsをGraphics2Dにキャスト
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);// アンチエイリアスを有効にする

        int offsetX = game.getBykin().getX() - game.getCharX();// キャラクターのX座標を取得
        int offsetY = game.getBykin().getY() - game.getCharY();// キャラクターのY座標を取得

        switch (game.getGameState()) {
            case START:
                new StartScreen().draw(g2d, game.getWidth(), game.getHeight());
                break;
            case GAME:
                game.getStage().draw(g2d, offsetX, offsetY);
                game.getBykin().draw(g2d, game.getCharX(), game.getCharY());
                drawEnemies(g2d, offsetX, offsetY);
                drawProjectiles(g2d, offsetX, offsetY);
                drawDamageDisplays(g2d, offsetX, offsetY);
                drawHealthBar(g2d, 10, 10);
                drawCoordinates(g2d);
                drawSkillIcons(g2d);
                if (game.isShowStatus()) {
                    drawStatusPanel(g2d);
                }
                break;
            case LEVEL_UP:
                drawLevelUpScreen(g2d);
                break;
            case GAME_OVER:
                new GameOverScreen().draw(g2d, game.getWidth(), game.getHeight());
                break;
        }
    }
    public void drawEffects(Graphics2D g2d, int offsetX, int offsetY) {
        for (Iterator<AOEEffect> it = game.getEffects().iterator(); it.hasNext();) {
            AOEEffect effect = it.next();
            effect.draw(g2d, offsetX, offsetY);
            if (effect.isExpired()) {
                it.remove();
            }
        }
    }

    private void drawEnemies(Graphics g, int offsetX, int offsetY) {
        for (Enemy enemy : game.getEnemies()) {
            enemy.draw(g, offsetX, offsetY);
            drawEnemyHealthBar(g, enemy, offsetX, offsetY);
        }
    }

    private void drawProjectiles(Graphics g, int offsetX, int offsetY) {
        for (Projectile projectile : game.getProjectiles()) {
            projectile.move();
            projectile.draw(g, offsetX, offsetY);
        }
    }

    private void drawDamageDisplays(Graphics g, int offsetX, int offsetY) {
        Graphics2D g2d = (Graphics2D) g;

        for (Iterator<DamageDisplay> it = game.getDamageDisplays().iterator(); it.hasNext();) {
            DamageDisplay display = it.next();

            // 時間切れの場合はリストから削除
            if (display.isExpired()) {
                it.remove();
                continue;
            }

            // 描画位置を計算
            int drawX = display.getX() - offsetX;
            int drawY = display.getY() - offsetY;

            // アルファ値（透明度）を設定
            int alpha = display.getAlpha();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha / 255f));

            // ダメージ数値を描画
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD,32));
            g2d.drawString(String.valueOf(display.getDamage()), drawX, drawY);

            // アルファ値を元に戻す
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
    }

    private void drawHealthBar(Graphics g, int x, int y) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("HP:", x, y + 15);

        g.setColor(Color.BLACK);
        g.fillRect(x + 50, y, 200, 20);

        int currentHp = game.getBykin().getStatus().getCurrentHp();
        int maxHp = game.getBykin().getStatus().getMaxHp();
        int barWidth = (int) (200 * ((double) currentHp / maxHp));
        g.setColor(Color.GREEN);
        g.fillRect(x + 50, y, barWidth, 20);

        g.setColor(Color.BLACK);
        g.drawString(currentHp + "/" + maxHp, x + 260, y + 15);
    }

    private void drawEnemyHealthBar(Graphics g, Enemy enemy, int offsetX, int offsetY) {
        int x = enemy.getX() - offsetX;
        int y = enemy.getY() - offsetY;

        int barWidth = 150;
        int barHeight = 18;
        int barX = x + enemy.getWidth() / 2 - barWidth / 2;
        int barY = y - 10;

        int currentHp = enemy.getCurrentHp();
        int maxHp = enemy.getMaxHp();
        int filledWidth = (int) (barWidth * ((double) currentHp / maxHp));

        g.setColor(Color.BLACK);
        g.fillRect(barX, barY, barWidth, barHeight);

        Color hpColor = Color.GREEN;
        if (currentHp <= maxHp * 0.5) hpColor = Color.YELLOW;
        if (currentHp <= maxHp * 0.25) hpColor = Color.RED;

        g.setColor(hpColor);
        g.fillRect(barX, barY, filledWidth, barHeight);

        g.setColor(Color.WHITE);
        g.drawRect(barX, barY, barWidth, barHeight);

        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(Color.BLACK);
        g.drawString("Lv." + enemy.getLevel(), x + 220, y + 15);
    }

    public void drawStatusPanel(Graphics g) {
        int panelX = game.getWidth() - 220;
        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        int panelY = 10;
        int panelWidth = 200;
        int panelHeight = 140;

        g.setColor(Color.BLACK);
        g.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 15, 15);

        g.setColor(Color.WHITE);
        Status s = game.getBykin().getStatus();

        g.drawString("レベル: " + s.getLevel(), panelX + 10, panelY + 30);
        g.drawString("攻撃: " + s.getAttack(), panelX + 10, panelY + 50);
        g.drawString("防御: " + s.getDefense(), panelX + 10, panelY + 70);
        g.drawString("速度: " + s.getSpeed(), panelX + 10, panelY + 90);
        g.drawString("HP: " + s.getCurrentHp() + "/" + s.getMaxHp(), panelX + 10, panelY + 110);
        g.drawString("経験値: " + s.getExperience() + "/" + s.getExperienceToNextLevel(), panelX + 10, panelY + 130);
    }

    private void drawSkillIcons(Graphics g) {
        int padding = 10;
        int iconSize = 64;
        int screenHeight = game.getHeight();

        Image skillImage = game.getBykin().getSkillImage();
        Image specialImage = game.getBykin().getSpecialImage();

        int skillX = padding;
        int skillY = screenHeight - iconSize - padding - 30;

        int specialX = skillX + iconSize + padding;
        int specialY = skillY;

        Graphics2D g2 = (Graphics2D) g.create();

        if (skillImage != null) {
            g2.setClip(new Ellipse2D.Float(skillX, skillY, iconSize, iconSize));
            g2.drawImage(skillImage, skillX, skillY, iconSize, iconSize, game);
            g2.setClip(null);
    
            if (game.isSkillOnCooldown()) {
                long elapsed = System.currentTimeMillis() - game.getSkillUsedTime();
                if (elapsed >= game.getCooldownMax()) {
                    game.setSkillOnCooldown(false);
                } else {
                    double cooldownRatio = (double) elapsed / game.getCooldownMax();
                    int angle = (int) (360 * (1 - cooldownRatio));
    
                    g2.setColor(new Color(0, 0, 0, 150));
                    g2.fillArc(skillX, skillY, iconSize, iconSize, 90, angle);
                }
            }
        }

        if (specialImage != null) {
            g2.setClip(new Ellipse2D.Float(specialX, specialY, iconSize, iconSize));
            g2.drawImage(specialImage, specialX, specialY, iconSize, iconSize, game);
            g2.setClip(null);
        }

        g2.dispose();

    }

    private void drawCoordinates(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("MS Gothic", Font.PLAIN, 16));
        int x = game.getBykin().getX();
        int y = game.getBykin().getY();
        String coordText = "座標: (" + x + ", " + y + ")";
        g.drawString(coordText, 10, game.getHeight() - 10);
    }
    public void drawLevelUpScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, game.getWidth(), game.getHeight());
    
        g.setColor(Color.YELLOW);
        g.setFont(new Font("SansSerif", Font.BOLD, 48));
        g.drawString("スキルを選択してください", game.getWidth() / 2 - 150, game.getHeight() / 2 - 100);
    
        g.setFont(new Font("SansSerif", Font.PLAIN, 24));
        g.setColor(Color.WHITE);
        g.drawString("1: 範囲攻撃", game.getWidth() / 2 - 100, game.getHeight() / 2);
        g.drawString("2: 貫通弾", game.getWidth() / 2 - 100, game.getHeight() / 2 + 30);
        g.drawString("3: 連続攻撃", game.getWidth() / 2 - 100, game.getHeight() / 2 + 60);
    }
    public void drawStatsScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, game.getWidth(), game.getHeight());
    
        g.setColor(Color.YELLOW);
        g.setFont(new Font("SansSerif", Font.BOLD, 48));
        g.drawString("レベルアップ！", game.getWidth() / 2 - 150, game.getHeight() / 2 - 100);
    
        g.setFont(new Font("SansSerif", Font.PLAIN, 24));
        g.setColor(Color.WHITE);
        Status s = game.getBykin().getStatus();
        g.drawString("新しいレベル: " + s.getLevel(), game.getWidth() / 2 - 100, game.getHeight() / 2 - 50);
        g.drawString("攻撃力: " + s.getAttack(), game.getWidth() / 2 - 100, game.getHeight() / 2 - 20);
        g.drawString("防御力: " + s.getDefense(), game.getWidth() / 2 - 100, game.getHeight() / 2 + 10);
        g.drawString("速度: " + s.getSpeed(), game.getWidth() / 2 - 100, game.getHeight() / 2 + 40);
        g.drawString("最大HP: " + s.getMaxHp(), game.getWidth() / 2 - 100, game.getHeight() / 2 + 70);
        g.drawString("スペースキーで続行", game.getWidth() / 2 - 120, game.getHeight() / 2 + 120);
    }
    
}    
