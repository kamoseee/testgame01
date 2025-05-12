package newgame;
import newgame.GameState; // GameState をインポート
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameInputHandler implements KeyListener {
    private BykinGame game;
    private boolean movingUp, movingDown, movingLeft, movingRight;

    public GameInputHandler(BykinGame game) {
        this.game = game;
    }

    public void handleKeyPress(KeyEvent e) {
        if (game.getGameState() == GameState.LEVEL_UP) {
            handleSkillSelection(e.getKeyCode()); // スキル選択処理を追加
            return;
        }

        if (game.getGameState() == GameState.SHOW_STATS && e.getKeyCode() == KeyEvent.VK_SPACE) {
            game.setGameState(GameState.LEVEL_UP); // スペースキーでスキル選択画面へ移行
            game.repaint();
            return;
        }

        if (game.isShowStatus()) {
            if (e.getKeyCode() == KeyEvent.VK_TAB) {
                game.setShowStatus(false);
                game.repaint();
            }
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_E -> game.useSkill();
            case KeyEvent.VK_Q -> game.useSpecial();
            case KeyEvent.VK_TAB -> {
                game.setShowStatus(true);
                game.repaint();
            }
            case KeyEvent.VK_SPACE -> {
                if (game.isGameOver()) {
                    game.restartGame();
                    game.setGameState(GameState.START);
                } else if (!game.isGameStarted()) {
                    game.setGameState(GameState.GAME);
                    game.setGameStarted(true);
                    game.repaint();
                }
            }
            case KeyEvent.VK_O -> {
                game.getBykin().getStatus().addExperience(game.getBykin().getStatus().getExperienceToNextLevel());
                game.repaint();
            }
        }
    }
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_TAB) { // Tabキーが押されたときの処理
            game.togglePause(); // ゲームの一時停止状態を切り替える
            return;
        }
        if (game.isPaused()) { // 一時停止中は移動キーのフラグを無視
            return;
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> movingUp = true;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> movingDown = true;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> movingLeft = true;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> movingRight = true;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        if (game.isPaused()) { // 一時停止中はキーリリース処理を無視
            return;
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> movingUp = false;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> movingDown = false;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> movingLeft = false;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> movingRight = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public void updateMovement() {
        if (game.isPaused()) { // 一時停止中は移動処理をスキップ
            return;
        }
        game.getBykin().move(movingUp, movingDown, movingLeft, movingRight);

        }
    private void handleSkillSelection(int keyCode) {
        switch (keyCode) {
            case KeyEvent.VK_1 -> game.getBykin().setSelectedSkill(SkillType.AREA_ATTACK);
            case KeyEvent.VK_2 -> game.getBykin().setSelectedSkill(SkillType.PIERCING_SHOT);
            case KeyEvent.VK_3 -> game.getBykin().setSelectedSkill(SkillType.RAPID_FIRE);
        }

        game.setGameState(GameState.GAME);
        game.repaint();
    }
}
