package newgame;

import newgame.GameState; // GameState をインポート
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;

public class GameInputHandler implements KeyListener {
    private BykinGame game;
    private boolean movingUp, movingDown, movingLeft, movingRight;
    private boolean isPaused = false; // Pause state

    private static final Map<Integer, SkillType> skillMap = Map.of(
            KeyEvent.VK_1, SkillType.AREA_ATTACK,
            KeyEvent.VK_2, SkillType.PIERCING_SHOT,
            KeyEvent.VK_3, SkillType.RAPID_FIRE);

    public GameInputHandler(BykinGame game) {
        this.game = game;
    }
    public void resetMovementFlags() {
        movingUp = false;
        movingDown = false;
        movingLeft = false;
        movingRight = false;
    }
    public void updateMovement() {
        if (isPaused || game.getGameState() != GameState.GAME) {
            return; // Do not update movement if paused or not in the GAME state
        }

        int speed = game.getBykin().getStatus().getSpeed();
        int dx = (movingLeft ? -1 : 0) + (movingRight ? 1 : 0);
        int dy = (movingUp ? -1 : 0) + (movingDown ? 1 : 0);

        if (dx != 0 || dy != 0) {
            double magnitude = Math.sqrt(dx * dx + dy * dy);
            dx = (int) (dx / magnitude * speed);
            dy = (int) (dy / magnitude * speed);
            game.getBykin().move(dx, dy);
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_TAB) {
            togglePause();
            return;
        }

        if (isPaused || game.getGameState() != GameState.GAME) {
            return; // Ignore input if paused or not in the GAME state
        }

        handleKeyPress(e);
        updateMovementFlags(e.getKeyCode(), true);
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        if (isPaused || game.getGameState() != GameState.GAME) {
            return; // Ignore input if paused or not in the GAME state
        }

        updateMovementFlags(e.getKeyCode(), false);
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }

    private void updateMovementFlags(int keyCode, boolean isPressed) {
        if (isPaused || game.getGameState() != GameState.GAME) {
            return; // Ignore movement updates if paused or not in the GAME state
        }

        switch (keyCode) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> movingUp = isPressed;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> movingDown = isPressed;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> movingLeft = isPressed;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> movingRight = isPressed;
        }
    }
    private void handleKeyPress(KeyEvent e) {
        if (game.getGameState() == GameState.LEVEL_UP) {
            handleSkillSelection(e.getKeyCode());
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_E -> game.useSkill();
            case KeyEvent.VK_Q -> game.useSpecial();
            case KeyEvent.VK_SPACE -> {
                if (game.isGameOver()) {
                    game.restartGame();
                } else if (!game.isGameStarted()) {
                    game.setGameState(GameState.GAME);
                    game.setGameStarted(true);
                }
                game.repaint();
            }
            case KeyEvent.VK_O -> {
                game.getBykin().getStatus().addExperience(game.getBykin().getStatus().getExperienceToNextLevel());
                game.repaint();
            }
        }
    }
    private void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            game.setGameState(GameState.SHOW_STATS); // Pause the game
        } else {
            game.setGameState(GameState.GAME); // Resume the game
        }
        game.repaint();
    }
    private void handleSkillSelection(int keyCode) {
        SkillType selectedSkill = skillMap.get(keyCode);
        if (selectedSkill != null) {
            game.getBykin().setSelectedSkill(selectedSkill);
            game.setGameState(GameState.GAME);
            game.repaint();
        }
    }
}
