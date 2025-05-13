package newgame;
import newgame.GameState; // GameState をインポート

import java.util.ArrayList;

public class GameStateManager {
    private BykinGame game;

    public GameStateManager(BykinGame game) {
        this.game = game;
    }

    /**
     * ゲームの再スタート処理
     */
    public void restartGame() {
        initializeGame(); // 初期化処理
        game.setGameState(GameState.GAME); // ゲーム状態を設定
        game.repaint(); // 再描画
    }

    /**
     * ゲームを初期化する処理
     */
    private void initializeGame() {
        game.setBykin(new Bykin(100, 200, game)); // プレイヤーキャラクターを初期化

        // 敵リストを初期化
        initializeEnemies();

        // 状態のリセット
        game.setGameOver(false);
        game.setSkillOnCooldown(false);
        game.setDx(0);
        game.setDy(0);
    }

    /**
     * 敵を初期化する処理
     */
    private void initializeEnemies() {
        game.getEnemies().clear();
        game.getEnemies().add(new Enemy(500, 300, "assets/virus01.png", 1, 5, 1, 3, 30));
        game.getEnemies().add(new Enemy(700, 400, "assets/virus02.png", 2, 7, 2, 3, 40));
        game.getEnemies().add(new Enemy(900, 500, "assets/virus03.png", 3, 10, 3, 3, 60));
    }

    /**
     * ゲームオーバー時の処理
     */
    public void handleGameOver() {
        game.setGameOver(true);
        setGameState(GameState.GAME_OVER);
    }

    /**
     * レベルアップ時の処理
     */
    public void handleLevelUp() {
        setGameState(GameState.LEVEL_UP);
    }

    /**
     * ゲームの状態を設定
     */
    public void setGameState(GameState newState) {
        game.setGameState(newState);
        game.repaint();
    }

    /**
     * ゲームを開始する処理
     */
    public void startGame() {
        game.setGameStarted(true);
        restartGame(); // ゲームを初期化して開始
    }
}