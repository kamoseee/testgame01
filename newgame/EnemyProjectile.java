package newgame;

public class EnemyProjectile extends Projectile {
    private boolean canDamagePlayer = true; // プレイヤーにダメージを与えるか

    public EnemyProjectile(int startX, int startY, double angle, String imagePath) {
        super(startX, startY, angle, imagePath);
    }

    @Override
    public boolean canPassThroughEnemies() {
        return true; // 敵の攻撃は貫通する
    }

    public boolean canDamagePlayer() {
        return canDamagePlayer;
    }
}
