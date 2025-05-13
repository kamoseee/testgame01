package newgame; // すべてのクラスに追加
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Random;
import java.awt.image.BufferedImage;  // これを追加


public class Enemy {
    private int x, y;
    private BufferedImage image; 
    // 敵のステータス
    // 宣言だけ残す（初期化は
    private int level;
    private int attack;
    private int defense;  
    private int speed;
    private int maxHp;
    private int currentHp;
    private boolean dying = false; 
    private Random rand = new Random(); 
    private float alpha = 1.0f; // 1.0 
    // 敵が最後に移動した時間
    private long lastMoveTime;
    // 移動間隔（ミリ秒）
    private static final long MOVE_INTERVAL = 500; // 0.5秒
    
    public Enemy(int x, int y, String imagePath, int level, int attack, int defense, int speed, int maxHp) {
        this.x = x;
        this.y = y;
        this.level = level;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.lastMoveTime = System.currentTimeMillis();

        try {
            image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("画像の読み込みに失敗しました: " + imagePath);
        }
    }

    // 敵のステータスに関連するゲッター
    public int getLevel() {
        return level;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getSpeed() {
        return speed;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getCurrentHp() {
        return currentHp;
    }
    public BufferedImage getImage() {
        return image;
    }

    // 敵のHPを減らす処理
    // 攻撃力と防御力を考慮したバージョン
    public int takeDamage(int attackerAttack) {
        int reduced = Math.max(1, attackerAttack - this.defense); // 最低1ダメージ
        this.currentHp -= reduced;
        if (this.currentHp < 0) {
            this.currentHp = 0;
        }
        //System.out.println("敵に与えたダメージ: " + reduced); // デバッグ用
        return reduced; // 実際に与えたダメージを返す
    }
    public boolean checkCollision(BufferedImage otherImage, int otherX, int otherY) {
        // 体力が0の場合、常にfalseを返す
        if (currentHp <= 0 || dying) {
            return false;
        }

        int top = Math.max(y, otherY);
        int bottom = Math.min(y + image.getHeight(), otherY + otherImage.getHeight());
        int left = Math.max(x, otherX);
        int right = Math.min(x + image.getWidth(), otherX + otherImage.getWidth());

        for (int py = top; py < bottom; py++) {
            for (int px = left; px < right; px++) {
                // 自分の画像のピクセル
                int myPixel = image.getRGB(px - x, py - y);
                // 相手の画像のピクセル
                int otherPixel = otherImage.getRGB(px - otherX, py - otherY);

                // どちらも不透明（アルファ値が0より大きい）なら衝突
                if (((myPixel >> 24) & 0xFF) > 0 && ((otherPixel >> 24) & 0xFF) > 0) {
                    return true;
                }
            }
        }
        return false;
    }
    

        //ランダムに移動するメソッド（間隔を調整）
    public void move(int screenWidth, int screenHeight) {
        long currentTime = System.currentTimeMillis();
        
        // 移動間隔が経過した場合のみ移動
        if (currentTime - lastMoveTime >= MOVE_INTERVAL) {
            int[] dx = {-speed, speed, 0, 0}; // 左, 右, 上, 下
            int[] dy = {0, 0, -speed, speed};
            int direction = rand.nextInt(4);

            x = Math.max(0, Math.min(x + dx[direction], screenWidth - getWidth()));
            y = Math.max(0, Math.min(y + dy[direction], screenHeight - getHeight()));

            lastMoveTime = currentTime;
        }
    }

    public void draw(Graphics g, int offsetX, int offsetY) {
        Graphics2D g2d = (Graphics2D) g;
        Composite original = g2d.getComposite();

        if (dying) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        }

        g2d.drawImage(image, x - offsetX, y - offsetY, null);

        if (dying) {
            g2d.setComposite(original);
        }
    }
    public void startDying() {
        this.dying = true;
    }
    public boolean isDying() {
        return dying;
    }
    
    public boolean updateDying() {
        if (dying) {
            alpha -= 0.05f; // 徐々に透明に
            if (alpha <= 0) {
                return true; // 消滅完了のサイン
            }
        }
        return false;
    }
    // Enemyクラスに追加
    public boolean isDead() {
        return currentHp <= 0 && alpha <= 0; // HPが0で透明化終了
    }



    // 位置を取得するゲッター
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public BufferedImage getMaskImage() {
        return (BufferedImage) image;
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, image.getWidth(null), image.getHeight(null));
    }
    
    public int getWidth() {
        return image.getWidth();
    }
    
    public int getHeight() {
        return image.getHeight();
    }
    
    
}
