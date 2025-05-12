package newgame;

import java.awt.*;

public class Stage {
    private int width, height;
    private static final Color OUTER_COLOR = new Color(220, 220, 220); // 外側のライトグレー
    private static final Color CENTER_COLOR = new Color(245, 245, 245); // 中心部分の白に近いグレー
    private static final Color GRID_COLOR = Color.LIGHT_GRAY; // グリッドの色
    private static final int GRID_SIZE = 40; // グリッドのサイズ

    public Stage(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * ステージを描画
     * @param g 描画対象
     * @param offsetX X方向のオフセット
     * @param offsetY Y方向のオフセット
     */
    public void draw(Graphics g, int offsetX, int offsetY) {
        // 背景全体を塗りつぶし
        g.setColor(OUTER_COLOR);
        g.fillRect(0, 0, width, height);

        // 中心部分を塗りつぶし
        int centerX = width / 2;
        int centerY = height / 2;
        int centerSize = Math.min(width, height) / 2;

        g.setColor(CENTER_COLOR);
        g.fillRect(centerX - centerSize / 2, centerY - centerSize / 2, centerSize, centerSize);

        // グリッドを描画
        drawGrid(g, offsetX, offsetY);
    }

    /**
     * グリッドを描画
     * @param g 描画対象
     * @param offsetX X方向のオフセット
     * @param offsetY Y方向のオフセット
     */
    private void drawGrid(Graphics g, int offsetX, int offsetY) {
        g.setColor(GRID_COLOR);

        // 描画範囲の計算
        int startX = Math.max(0, offsetX / GRID_SIZE * GRID_SIZE);
        int startY = Math.max(0, offsetY / GRID_SIZE * GRID_SIZE);
        int endX = Math.min(width, (offsetX + width) / GRID_SIZE * GRID_SIZE + GRID_SIZE);
        int endY = Math.min(height, (offsetY + height) / GRID_SIZE * GRID_SIZE + GRID_SIZE);

        // グリッドを描画
        for (int x = startX; x < endX; x += GRID_SIZE) {
            for (int y = startY; y < endY; y += GRID_SIZE) {
                int screenX = x - offsetX;
                int screenY = y - offsetY;
                g.fillRect(screenX, screenY, GRID_SIZE, GRID_SIZE);
            }
        }
    }

    /**
     * ステージの幅を取得
     * @return ステージの幅
     */
    public int getWidth() {
        return width;
    }

    /**
     * ステージの高さを取得
     * @return ステージの高さ
     */
    public int getHeight() {
        return height;
    }
}