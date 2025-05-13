package newgame;

public class DamageDisplay {
    private int damage;
    private int x, y;
    private long timestamp;
    private static final int DURATION = 1500; // 表示時間：1.5秒

    public DamageDisplay(int damage, int x, int y) {
        this.damage = Math.max(1, damage); // ダメージが異常に大きくならないように制限
        this.x = x;  // 敵の位置から渡される x 座標
        this.y = y;  // 敵の位置から渡される y 座標
        this.timestamp = System.currentTimeMillis();
    }

    public int getDamage() {
        return damage;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        long elapsed = System.currentTimeMillis() - timestamp;
        return y - (int)(elapsed / 15); // 浮かび上がる動作
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - timestamp > DURATION;
    }

    public int getAlpha() {
        long elapsed = System.currentTimeMillis() - timestamp;
        int alpha = 255 - (int)(255 * elapsed / DURATION);
        return Math.max(0, alpha); // 最低0
    }
}