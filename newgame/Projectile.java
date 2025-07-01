package newgame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Projectile {
    protected double x, y; // `protected` に変更
    protected int speed = 10;
    protected double angle;
    private int damage = 10; // デフォルトのダメージ値を設定
    private BufferedImage image;
    private BufferedImage maskImage; // ピクセル単位の判定用マスク画像
    private String imagePath;
    private Object source; // 発射元（Enemy または Player）


    public Projectile(int startX, int startY, double angle, String imagePath, Object source) {
        this.x = startX;
        this.y = startY;
        this.angle = angle;
        this.imagePath = imagePath;
        this.source = source;
        try {
            image = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("画像の読み込みに失敗しました: " + imagePath);
        }
        loadImages();
    }

    public boolean canPassThroughEnemies() {
        return false; // 通常の弾は貫通しない
    }

    protected void loadImages() {
        try {
            File file = new File(imagePath);
            if (file.exists()) {
                image = ImageIO.read(file);
                maskImage = ImageIO.read(file); // マスク画像も同じものを使用
            } else {
                System.err.println("画像が見つかりません: " + imagePath);
            }
        } catch (IOException e) {
            System.err.println("画像の読み込みに失敗しました: " + imagePath);
            e.printStackTrace();
        }
    }
    public Object getSource() {
        return source;
    }

    public BufferedImage getImage() {
        return image;
    }

    public BufferedImage getMaskImage() {
        return maskImage;
    }

    public void move() {
        x += speed * Math.cos(angle);
        y += speed * Math.sin(angle);
    }

    public void draw(Graphics g, int offsetX, int offsetY) {
        if (image != null) {
            g.drawImage(image, getX() - offsetX, getY() - offsetY, null);
        } else {
            g.setColor(Color.RED);
            g.fillOval(getX() - offsetX, getY() - offsetY, 10, 10);
        }
    }

    public Rectangle getBounds() {
        if (image != null) {
            return new Rectangle(getX(), getY(), image.getWidth(), image.getHeight());
        } else {
            return new Rectangle(getX(), getY(), 10, 10);
        }
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    // 新しく追加されたメソッド: getDamage()
    public int getDamage() {
        return damage;
    }

    // ダメージを設定するメソッドを追加（必要に応じて）
    public void setDamage(int damage) {
        this.damage = damage;
    }
}