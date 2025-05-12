package newgame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class AOEEffect {
    private int centerX, centerY, radius;
    private long startTime;
    private long duration;

    public AOEEffect(int centerX, int centerY, int radius, long duration) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
        this.duration = duration;
        this.startTime = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - startTime > duration;
    }

    public void draw(Graphics g, int offsetX, int offsetY) {
        int drawX = centerX - offsetX;
        int drawY = centerY - offsetY;

        g.setColor(new Color(255, 0, 0, 100)); // 半透明の赤色
        g.fillOval(drawX - radius, drawY - radius, radius * 2, radius * 2); // 中心座標を考慮
    }

    public void applyEffect(Bykin player, List<Enemy> enemies, List<DamageDisplay> damageDisplays) {
        int attackRadiusSquared = radius * radius; // 範囲の二乗を計算（高速化）

        for (Enemy enemy : enemies) {
            BufferedImage enemyImage = enemy.getImage(); // 敵の画像を取得
            int enemyWidth = enemyImage.getWidth();
            int enemyHeight = enemyImage.getHeight();
            boolean damageApplied = false; // ダメージ適用済みかどうかのフラグ

            for (int x = 0; x < enemyWidth; x++) {
                for (int y = 0; y < enemyHeight; y++) {
                    int pixel = enemyImage.getRGB(x, y);

                    // 透明ピクセルは無視
                    if ((pixel >> 24) == 0) {
                        continue;
                    }

                    int worldX = enemy.getX() + x;
                    int worldY = enemy.getY() + y;

                    int distanceSquared = (worldX - centerX) * (worldX - centerX) +
                                          (worldY - centerY) * (worldY - centerY);

                    if (distanceSquared <= attackRadiusSquared) { // 範囲内ならダメージ適用
                        if (!damageApplied) { // まだダメージを適用していない場合のみ
                            int actualDamage = enemy.takeDamage(player.getStatus().getAttack() * 2);
                            damageDisplays.add(new DamageDisplay(actualDamage, worldX, worldY)); // ダメージ表示

                            if (enemy.getCurrentHp() <= 0) {
                                player.getStatus().addExperience(enemy.getLevel() * 20);
                                enemy.startDying();
                            }

                            damageApplied = true; // ダメージ適用済みにする
                        }
                        break; // 1つでも当たり判定があればダメージ適用
                    }
                }
                if (damageApplied) break; // すでにダメージを適用したらループを抜ける
            }
        }
    }
}