package newgame;

import newgame.GameState; // GameState をインポート
import java.util.List;

public class GameStateManager {
    private BykinGame game;

    private static final List<Enemy> INITIAL_ENEMIES = List.of(
        new Enemy(500, 300, "assets/virus01.png", 1, 5, 1, 3, 30),
        new Enemy(700, 400, "assets/virus02.png", 2, 7, 2, 3, 40),
        new Enemy(900, 500, "assets/virus03.png", 3, 10, 3, 3, 60),
        new Enemy(500, 900, "assets/virus01.png", 1, 5, 1, 3, 30),
        new Enemy(200, 500, "assets/virus02.png", 2, 7, 2, 3, 40),
        new Enemy(1000, 1000, "assets/virus03.png", 3, 10, 3, 3, 60)
    );

    public GameStateManager(BykinGame game) {
        this.game = game;
    }

    public void restartGame() {
        resetGame();
        setGameState(GameState.GAME);
    }

    public void resetGame() {
        resetEnemies();
        game.setBykin(new Bykin(100, 200, game));
        game.setGameOver(false);
        game.setSkillOnCooldown(false);
        game.setDx(0);
        game.setDy(0);
    }

    private void resetEnemies() {
        game.getEnemies().clear();
        game.getEnemies().addAll(INITIAL_ENEMIES);
    }

    public void setGameState(GameState newState) {
        System.out.println("ゲーム状態を変更: " + game.getGameState() + " -> " + newState);
        game.setGameState(newState);
        game.repaint();
    }

    public void changeState(GameState newState) {
        switch (newState) {
            case GAME_OVER:
                game.setGameOver(true);
                break;
            case LEVEL_UP:
                // 他の状態変更時の追加処理があればここに記述
                break;
            default:
                // 特定の処理が不要な場合は何も行わない
                break;
        }
        setGameState(newState);
    }

    public void handleGameOver() {
        changeState(GameState.GAME_OVER);
    }

    public void handleLevelUp() {
        changeState(GameState.LEVEL_UP);
    }

    public void startGame() {
        game.setGameStarted(true);
        changeState(GameState.GAME);
    }
}