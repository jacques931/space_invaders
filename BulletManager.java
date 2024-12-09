package SpaceInvader;

import com.jogamp.opengl.GL2;
import java.util.ArrayList;
import java.util.List;

public class BulletManager {
    private final List<Bullet> bullets = new ArrayList<>();

    public void shoot(Position position) {
        bullets.add(new Bullet(position));
    }

    public void draw(GL2 gl) {
        bullets.forEach(bullet -> bullet.draw(gl));
    }

    public void update() {
        List<Bullet> toRemove = new ArrayList<>();
        for (Bullet bullet : bullets) {
            if (bullet.move()) {
                toRemove.add(bullet);
            }
        }
        bullets.removeAll(toRemove);
    }

    public void checkCollisions(EnemyManager enemyManager) {
        List<Bullet> toRemove = new ArrayList<>();
        for (Bullet bullet : bullets) {
            // Supprimer la bullet après une collision
            if (enemyManager.checkCollision(bullet)) {
                toRemove.add(bullet);
            }
        }
        bullets.removeAll(toRemove);
    }
}
