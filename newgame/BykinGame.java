package newgame;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage; // BufferedImage をインポート
import java.util.ArrayList;
import java.util.List;
import newgame.SkillProjectile;
import newgame.Projectile;
import newgame.Enemy;
import newgame.GameState; // GameState をインポート
import java.util.Iterator;
import newgame.Bykin; // `Bykin` をインポート
import newgame.SkillType; // `SkillType` をインポート

public class BykinGame extends JPanel implements KeyListener, MouseMotionListener, ActionListener {
    private Bykin bykin;
    private Stage stage;
    private final int charX = 300, charY = 220;
    private int dx = 0, dy = 0;
    private Timer timer;
    private Timer movementTimer;
    private List<Enemy> enemies;
    private boolean showStatus = false;
    private boolean skillOnCooldown = false;
    private int cooldownMax = 3000;
    private long skillUsedTime = 0;
    private boolean isGameOver = false;
    private boolean isGameStarted = false;
    private boolean isPaused = false; // 一時停止状態を管理するフラグ
    private List<Projectile> projectiles = new ArrayList<>();
    private long lastAttackTime = System.currentTimeMillis(); // 初期値を現在の時間
    private Point mousePos = new Point(0, 0);
    private int mouseX = 0;
    private int mouseY = 0;
    private List<DamageDisplay> damageDisplays = new ArrayList<>();
    private List<AOEEffect> effects = new ArrayList<>(); // 範囲攻撃のエフェクトリスト
    private GameRenderer renderer;
    private GameLogic logic;
    private GameInputHandler inputHandler;
    private GameState gameState = GameState.START; // ゲームの状態を管理
    private StatusPanel statusPanel;



    public BykinGame() {       
        initializeGame();

        renderer = new GameRenderer(this);// **GameRenderer を初期化**
        logic = new GameLogic(this); // **GameLogic を初期化**
        statusPanel = new StatusPanel(bykin); // `StatusPanel` をインスタンス化
        inputHandler = new GameInputHandler(this, statusPanel); // `GameInputHandler` に渡す        bykin = new Bykin(100, 200, this);// **Bykin を初期化**
        stage = new Stage(2000, 2000);// **Stage を初期化**
                
        setFocusTraversalKeysEnabled(false);// フォーカス移動キーを無効化
        addMouseMotionListener(this);// **マウスモーションリスナーを追加**
        setFocusable(true);// BykinGameがキーボードやマウスの入力を受け取れる
        addKeyListener(inputHandler);// **GameInputHandler にキーボードリスナーを追加**
        addKeyListener(this);// **BykinGame にキーボードリスナーを追加**

        // 移動を定期的に更新するタイマー
        movementTimer = new Timer(16, e -> {
            if (!isPaused) { // 一時停止中でない場合のみ移動を更新
                inputHandler.updateMovement();
            }
            repaint();
        });
        movementTimer.start();
        timer = new Timer(32, this);//actionPerformedメソッドを呼び出すタイマー
        timer.start();
    

        requestFocusInWindow(); // フォーカスを設定
        setPreferredSize(new Dimension(1280, 720));// ウィンドウサイズを設定
        //setBackground(Color.WHITE);
    
        gameState = GameState.START;
    
    }
    private void initializeGame() {// ゲームの初期化処理
        // プレイヤーキャラクターの生成
        bykin = new Bykin(100, 200, this);
    
        // 敵リストを初期化
        enemies = new ArrayList<>();
        enemies.add(new Enemy(500, 300, "assets/virus01.png", 1, 5, 1, 3, 30));
        enemies.add(new Enemy(700, 400, "assets/virus02.png", 2, 7, 2, 3, 40));
        enemies.add(new Enemy(900, 500, "assets/virus03.png", 3, 10, 3, 3, 60));

        // 状態のリセット
        isGameOver = false;
        skillOnCooldown = false;
        dx = 0;
        dy = 0;
    }    
    
    public void updateGame() {
        if (isPaused) {
            return; // ゲームを一時停止
        }
        logic.updateGame(); // ゲームロジックを更新
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            dx = 0; // キャラクターの動きを止める
            dy = 0;  
        }
        repaint();
    }
    public void useAOEAttack() {
        int centerX = bykin.getX() + bykin.getWidth() / 2;
        int centerY = bykin.getY() + bykin.getHeight() / 2;
        int attackRadius = 200; // 範囲攻撃の半径

        // 範囲攻撃エフェクトを作成して追加
        AOEEffect aoeEffect = new AOEEffect(centerX, centerY, attackRadius, 2000);
        getEffects().add(aoeEffect);

        // 範囲攻撃のダメージ処理をAOEEffectに委譲
        aoeEffect.applyEffect(bykin, getEnemies(), getDamageDisplays());
    }
    
    private void handleEnemyAttacks() {
        for (Enemy enemy : enemies) {
            if (enemy.checkCollision(bykin.getImage(), bykin.getX(), bykin.getY())) {
                bykin.takeDamage(enemy.getAttack());
                System.out.println("プレイヤーがダメージを受けました！");
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
                // プレイヤーのワールド座標から画面座標へのオフセットを計算
        int offsetX = bykin.getX() - getCharX();
        int offsetY = bykin.getY() - getCharY();
        // エフェクトを描画
        for (AOEEffect effect : effects) {
            effect.draw(g, offsetX, offsetY); // 正しい引数を渡す
        } 
        switch (getGameState()) {
                    case START:
                        new StartScreen().draw(g, getWidth(), getHeight());
                        break;
                    case GAME:
                        renderer.render(g);
                        renderer.drawEffects(g2d, offsetX, offsetY);
                        break;
                    case LEVEL_UP_STATS:
                        renderer.drawStatsScreen(g);
                        break;
                    case LEVEL_UP:
                        renderer.drawLevelUpScreen(g);
                        break;
                    case GAME_OVER:
                        new GameOverScreen().draw(g, getWidth(), getHeight());
                        break;
                }
    }
      
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isPaused) { // 一時停止中はゲームロジックを処理しない
            inputHandler.updateMovement();
            logic.updateGame(); // ゲームロジックを更新

            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        inputHandler.handleKeyPress(e); // 入力処理を `GameInputHandler` に委譲
        
    }
    public void returnToStartScreen() {
        System.out.println("スタート画面に戻ります。"); // デバッグ用
        setGameState(GameState.START); // ゲーム状態をスタートに設定
        repaint(); // 再描画してスタート画面を表示
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT -> dx = 0;
            case KeyEvent.VK_UP, KeyEvent.VK_DOWN -> dy = 0;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        mousePos = e.getPoint();
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {}
    
    public void useSkill() {
        if (skillOnCooldown) {
            System.out.println("スキルはクールダウン中です！");
            return;
        }
    
        SkillType selectedSkill = bykin.getSelectedSkill();
        if (selectedSkill == null) {
            System.out.println("スキルが選択されていません！ 発動不可");
            return;
        }
    
        skillOnCooldown = true;
        skillUsedTime = System.currentTimeMillis();
    
        switch (selectedSkill) {
            case AREA_ATTACK -> useAOEAttack();
            case PIERCING_SHOT -> usePiercingShot();
            case RAPID_FIRE -> useRapidFire();
        }
    
        repaint();
    }
        
    private void usePiercingShot() {
        int centerX = bykin.getX() + bykin.getWidth() / 2;
        int centerY = bykin.getY() + bykin.getHeight() / 2;
            // ワールド座標系でのマウス位置を取得
        int offsetX = bykin.getX() - charX;
        int offsetY = bykin.getY() - charY;
        int worldMouseX = mouseX + offsetX;
        int worldMouseY = mouseY + offsetY;
            // 発射角度を計算
        double angle = Math.atan2(worldMouseY - centerY, worldMouseX - centerX);

        // 貫通弾を発射
        projectiles.add(new SkillProjectile(centerX, centerY, angle, "assets/skill_attack.png",bykin));
    }
    
    private void useRapidFire() {
        int centerX = bykin.getX() + bykin.getWidth() / 2;
        int centerY = bykin.getY() + bykin.getHeight() / 2;
    
        int offsetX = bykin.getX() - charX;
        int offsetY = bykin.getY() - charY;
    
        int worldMouseX = mouseX + offsetX;
        int worldMouseY = mouseY + offsetY;
        double angle = Math.atan2(worldMouseY - centerY, worldMouseX - centerX);
    
        // 通常攻撃の弾
        projectiles.add(new Projectile(centerX, centerY, angle, "assets/attack.png",bykin));
    
        // 追加の2発（少し角度を変える）
        double spreadAngle = Math.toRadians(10); // 10度の角度差
        projectiles.add(new Projectile(centerX, centerY, angle + spreadAngle, "assets/attack.png",bykin));
        projectiles.add(new Projectile(centerX, centerY, angle - spreadAngle, "assets/attack.png",bykin));
    }
    
    
    public void useSpecial() {
        System.out.println("必殺技発動！");
    }

    /**
     * ゲームの再スタート処理
     */
    public void restartGame() {
        initializeGame(); // 初期化処理を呼び出す
        gameState = GameState.GAME;
        repaint();
    }
    
    public List<AOEEffect> getEffects() {
        return effects;
    }
        
    public long getLastAttackTime() {
        return lastAttackTime;
    }
        
    public void setLastAttackTime(long time) {
        lastAttackTime = time;
    }
        
    public int getMouseX() {
        return mouseX;
    }
    
    public int getMouseY() {
        return mouseY;
    }

    public void setBykin(Bykin bykin) {
        this.bykin = bykin;
    }
    public Bykin getBykin() {
        if (bykin == null) {
            System.out.println("Warning: Bykin is not initialized!");
        }
        return bykin;
    }


    public Stage getStage() {
        return stage;
    }

    public int getCharX() {
        return charX;
    }

    public int getCharY() {
        return charY;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState state) {
        this.gameState = state;
    }

    public boolean isShowStatus() {
        return showStatus;
    }

    public void setShowStatus(boolean showStatus) {
        this.showStatus = showStatus;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean isGameOver) {
        this.isGameOver = isGameOver;
    }

    public boolean isGameStarted() {
        return isGameStarted;
    }

    public void setGameStarted(boolean isGameStarted) {
        this.isGameStarted = isGameStarted;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    public List<DamageDisplay> getDamageDisplays() {
        return damageDisplays;
    }

    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public int getDy() {
        return dy;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public boolean isSkillOnCooldown() {
        return skillOnCooldown;
    }

    public void setSkillOnCooldown(boolean skillOnCooldown) {
        this.skillOnCooldown = skillOnCooldown;
    }

    public long getSkillUsedTime() {
        return skillUsedTime;
    }

    public int getCooldownMax() {
        return cooldownMax;
    }

}
