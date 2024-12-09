package SpaceInvader;

import com.jogamp.opengl.GL2;
import java.util.ArrayList;
import java.util.List;

public class EnemyManager {
    private final List<Enemy> enemies = new ArrayList<>();
    private final int rows;
    private final int cols;
    private int direction = 1; // 1 pour droite, -1 pour gauche

    public EnemyManager(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        initEnemies();
    }

    public boolean areAllEnemiesDead() {
        return enemies.isEmpty();
    }

    private void initEnemies() {
        int spacing = Setting.enemySpacing;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Position position = new Position(col * spacing + 50, Setting.screenHeight - row * spacing - 50);
                enemies.add(new Enemy(position));
            }
        }
    }

    public void draw(GL2 gl) {
        for (Enemy enemy : enemies) {
            enemy.draw(gl);
        }
    }

    public void update() {
        boolean hitEdge = false;

        // Vérifiez si un ennemi touche le bord
        for (Enemy enemy : enemies) {
            enemy.move(direction * Setting.enemySpeed);
            // Si un ennemi touche le bord
            if (enemy.getPosition().getX() >= Setting.screenWidth - enemy.getWidth() / 2 || enemy.getPosition().getX() <= enemy.getWidth() / 2) {
                hitEdge = true;
            }
        }

        // Si un ennemi touche le bord, descendez tous les ennemis d'une ligne et changez de direction
        if (hitEdge) {
            for (Enemy enemy : enemies) {
                enemy.descend();
            }
            direction = -direction;
        }
    }

    public boolean checkCollision(Bullet bullet) {
        for (Enemy enemy : enemies) {
            if (enemy.isHit(bullet)) {
                enemies.remove(enemy);
                return true;
            }
        }
        return false;
    }

    public boolean isGameOver() {
        return enemies.stream().anyMatch(enemy -> enemy.getPosition().getY() <= Setting.endPosition);
    }
}
