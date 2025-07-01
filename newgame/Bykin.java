package newgame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Bykin {
    private BufferedImage image;
    private int x, y, width, height;
    private double baseSpeed; // 最低速度を表すフィールドを追加
    private Status status;
    private Image skillImage;
    private boolean invincible = false;
    private long lastDamageTime = 0;
    private static final int INVINCIBLE_TIME = 1000; // 1秒無敵
    private Image specialImage;
    private BykinGame game; // `game` 変数を追加
    private SkillType selectedSkill = null; // 初期値を null に設定

    public Bykin(int startX, int startY, BykinGame game) {

        this.x = startX;
        this.y = startY;
        this.width = 50;  // 例として幅を指定
        this.height = 50; // 例として高さを指定
        this.game = game; // `game` をセット
        this.status = new Status(1, 10, 5, 3, 100, game); // `Status` を適切に初期化
        this.baseSpeed = 0.5; // 初期値を設定（例：1.0）

        try {
            image = ImageIO.read(new File("assets/bykin.png"));
            skillImage = ImageIO.read(new File("assets/E.png"));
            specialImage = ImageIO.read(new File("assets/Q.png"));
        } catch (IOException e) {
            System.err.println("画像の読み込みに失敗しました: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public SkillType getSelectedSkill() {
        return selectedSkill;
    }

    public void setSelectedSkill(SkillType skill) {
        this.selectedSkill = skill;
    }

    // 画像の幅を取得
    public int getWidth() {
        return (image != null) ? image.getWidth() : 0;
    }

    public int getHeight() {
        return (image != null) ? image.getHeight() : 0;
    }

    public boolean checkCollision(Rectangle hitbox) {
        Rectangle playerBounds = new Rectangle(x, y, width, height);
        return playerBounds.intersects(hitbox);
    }

    public void move(boolean movingUp, boolean movingDown, boolean movingLeft, boolean movingRight) {
        int dx = 0, dy = 0;

        if (movingUp)
            dy -= status.getSpeed();
        if (movingDown)
            dy += status.getSpeed();
        if (movingLeft)
            dx -= status.getSpeed();
        if (movingRight)
            dx += status.getSpeed();

        // 実際の移動処理（範囲チェックを含む）
        move(dx, dy);
    }

    public void move(int dx, int dy) {
        if (dx == 0 && dy == 0) {
            return; // 移動がない場合は処理をスキップ
        }
        // 移動距離の計算を正規化
        double distance = Math.max(1.0, Math.sqrt(dx * dx + dy * dy)); // 最小距離を1とする
        double speed = Math.max(baseSpeed, status.getSpeed()); // speedにbaseSpeedを適用

        // 移動量をスケールに基づいて計算
        x += dx * (speed / distance);
        y += dy * (speed / distance);

        // ステージの範囲内に座標を制限
        x = Math.max(0, Math.min(x, game.getStage().getWidth() - getWidth()));
        y = Math.max(0, Math.min(y, game.getStage().getHeight() - getHeight()));

        game.repaint(); // 画面を更新して移動を反映
    }

    public boolean isInvincible() {
        long now = System.currentTimeMillis();
        if (invincible && now - lastDamageTime >= INVINCIBLE_TIME) {
            invincible = false;
        }
        return invincible;
    }

    public void setInvincible(boolean b) {
        invincible = b;
        lastDamageTime = System.currentTimeMillis();
    }

    public void takeDamage(int damage) {
        if (isInvincible()) {
            // System.out.println("無敵状態のためダメージなし！");
            return;
        }

        int reduced = Math.max(1, damage - status.getDefense()); // 最低1ダメージ
        status.setCurrentHp(status.getCurrentHp() - reduced);
        System.out.println("ダメージを受けた！ 残HP: " + status.getCurrentHp());
    }

    public void heal(int amount) {
        status.heal(amount);
    }

    public void draw(Graphics g, int screenX, int screenY) {
        if (image != null) {
            g.drawImage(image, screenX, screenY, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect(screenX, screenY, 50, 50); // 赤い四角を描画（エラー表示）
            g.setColor(Color.WHITE);
            g.drawString("画像なし", screenX + 5, screenY + 25);
        }
    }

    public Image getSkillImage() {
        return skillImage;
    }

    public Image getSpecialImage() {
        return specialImage;
    }

    public BufferedImage getMaskImage() {
        return image;
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Status getStatus() {
        return status;
    }
}
