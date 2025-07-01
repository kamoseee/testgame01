package newgame;

import newgame.GameState;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameInputHandler implements KeyListener {
    private BykinGame game;
    private boolean movingUp, movingDown, movingLeft, movingRight;
    private StatusPanel statusPanel;

    public GameInputHandler(BykinGame game, StatusPanel statusPanel) {
        this.game = game;
        this.statusPanel = statusPanel;
    }


    /**
     * キー入力を処理します。
     */
    public void handleKeyPress(KeyEvent e) {
        // ゲームが一時停止中の場合、特定のキー以外は無視
        if (game.isPaused() && e.getKeyCode() != KeyEvent.VK_TAB) {
            return;
        }

        // ゲーム状態に応じて処理を振り分け
        switch (game.getGameState()) {
            case START -> handleStartScreenInput(e);
            case GAME -> handleGameInput(e);
            case GAME_OVER -> handleGameOverInput(e);
            case LEVEL_UP -> handleSkillSelectionInput(e);
            case LEVEL_UP_STATS -> handleStatsScreenInput(e);
            default -> System.out.println("無効なゲーム状態です。");
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        // 一時停止処理
        

        // ゲームが一時停止中の場合、移動キーを無視
        if (game.isPaused()) {
            return;
        }

        // 移動キーのフラグを設定
        switch (keyCode) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> movingUp = true;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> movingDown = true;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> movingLeft = true;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> movingRight = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // ゲームが一時停止中の場合、キーリリースを無視
        if (game.isPaused()) {
            return;
        }

        // 移動キーのフラグをリセット
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> movingUp = false;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> movingDown = false;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> movingLeft = false;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> movingRight = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    /**
     * スタート画面の入力処理
     */
    private void handleStartScreenInput(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            //System.out.println("スペースキーが押されました。ゲームを開始します。");
            game.restartGame();
        }
    }

    /**
     * ゲーム中の入力処理
     */
    private void handleGameInput(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_E -> game.useSkill();
            case KeyEvent.VK_Q -> game.useSpecial();
            case KeyEvent.VK_G -> {
                //System.out.println("Gキーが押されました。ゲームオーバー状態に遷移します。");
                game.setGameOver(true);
                game.setGameState(GameState.GAME_OVER);
                game.repaint();
            }
            case KeyEvent.VK_TAB -> {
                //System.out.println("TABキーが押されました。ステータス画面に遷移します。");
                game.togglePause();
            }
            case KeyEvent.VK_O -> handleLevelUp(); // レベルアップ処理を追加
            
            default -> {
                if (!isSkillSelectionKey(e)) {
                    //System.out.println("無効なキーが押されました。何も行いません。");
                }
            }
        }
    }
    /**
     * レベルアップ処理
     */
    private void handleLevelUp() {
        //System.out.println("Oキーが押されました。レベルアップ処理を実行します。");

        // Bykin の経験値を次のレベルに必要な経験値に設定
        game.getBykin().getStatus().addExperience(game.getBykin().getStatus().getExperienceToNextLevel());

        // 画面を再描画
        game.repaint();
    }
    /**
     * ゲームオーバー画面の入力処理
     */
    private void handleGameOverInput(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            //System.out.println("スペースキーが押されました。スタート画面に戻ります。");
            game.returnToStartScreen();
        }
    }

    /**
     * スキル選択画面の入力処理
     */
    private void handleSkillSelectionInput(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_1 -> {
                game.getBykin().setSelectedSkill(SkillType.AREA_ATTACK);
                //System.out.println("スキル1が選択されました: AREA_ATTACK");
            }
            case KeyEvent.VK_2 -> {
                game.getBykin().setSelectedSkill(SkillType.PIERCING_SHOT);
                //System.out.println("スキル2が選択されました: PIERCING_SHOT");
            }
            case KeyEvent.VK_3 -> {
                game.getBykin().setSelectedSkill(SkillType.RAPID_FIRE);
                //System.out.println("スキル3が選択されました: RAPID_FIRE");
            }
            default -> {
                // 無効なキーが押された場合、何もしない
                //System.out.println("無効なキーが押されました。スキル選択は変更されません。");
                return;
            }
        }

        // スキル選択後、ゲーム画面に戻る
        game.setGameState(GameState.GAME);
        game.repaint();
    }

    /**
     * ステータス画面の入力処理
     */
    private void handleStatsScreenInput(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            //System.out.println("スペースキーが押されました。スキル選択画面に移行します。");
            game.setGameState(GameState.LEVEL_UP);
            game.repaint();
        }
    }

    /**
     * スキル選択キーの判定
     */
    private boolean isSkillSelectionKey(KeyEvent e) {
        return e.getKeyCode() == KeyEvent.VK_1 ||
               e.getKeyCode() == KeyEvent.VK_2 ||
               e.getKeyCode() == KeyEvent.VK_3;
    }

    /**
     * 移動処理を更新
     */
    public void updateMovement() {
        if (!game.isPaused()) {
            game.getBykin().move(movingUp, movingDown, movingLeft, movingRight);
        }
    }
}