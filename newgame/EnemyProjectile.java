package newgame;

import java.awt.*;
import java.awt.image.BufferedImage;

public class EnemyProjectile extends Projectile {
    public EnemyProjectile(int startX, int startY, double angle, String imagePath,Object source) {
        super(startX, startY, angle, imagePath,source);
    }

    @Override
    public void move() {
        // X軸とY軸方向に移動
        x += speed * Math.cos(angle);
        y += speed * Math.sin(angle);
    }

    @Override
    public void draw(Graphics g, int offsetX, int offsetY) {
        if (getImage() != null) {
            g.drawImage(getImage(), getX() - offsetX, getY() - offsetY, null);
        } else {
            g.setColor(Color.RED);
            g.fillOval(getX() - offsetX, getY() - offsetY, 10, 10);
        }
    }
}