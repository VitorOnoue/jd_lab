package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class GapObstacle {

    private float x;           
    private float width;       
    private float groundY;     
    private float requiredSpeed;

    private Texture texture;   

    
    private final Rectangle entryZone = new Rectangle();
    private float exitX; 

    private float entryCooldown = 0f;

    /**
     * @param x
     * @param groundY
     * @param width
     * @param requiredSpeed
     */
    public GapObstacle(float x, float groundY, float width, float requiredSpeed) {
        this.x = x;
        this.groundY = groundY;
        this.width = width;
        this.requiredSpeed = requiredSpeed;

        try {
            texture = new Texture(Gdx.files.internal("gap2.png"));
        } catch (Exception e) {
            texture = null;
        }

        recomputeZones();
    }

    private void recomputeZones() {
        float triggerWidth = 60f;
        float triggerHeight = 160f; 
        entryZone.set(x - triggerWidth, groundY, triggerWidth, triggerHeight);

        exitX = x + width + 8f;
    }

    public void checkAndResolve(Player player, float dt) {
        if (entryCooldown > 0f) entryCooldown -= dt;

        if (!player.getBounds().overlaps(entryZone)) return;

        if (entryCooldown > 0f) return;

        if (player.getVelX() <= 0f) return;

        float speed = player.getVelX();

        if (speed >= requiredSpeed) {
            // passou: teleporta pra margem direita (outro lado do gap)
            float newX = exitX + 2f;
            player.setPosition(newX, player.getY());
            if (player.getVelX() < requiredSpeed * 0.5f) {
                player.setVelX(requiredSpeed * 0.5f);
            }
        } else {
            // falhou: jogador cai no buraco -> colocamos ele para baixo do chão
            float fallY = groundY - (player.getHeight() + 120f); // afunda o jogador
            player.setPosition(player.getX(), fallY);
            player.setVelX(0f);
        }

        entryCooldown = 0.15f;
    }

    public void draw(SpriteBatch batch) {
        if (texture != null) {
            // desenha a textura cobrindo o gap
            batch.draw(texture, x, groundY - 16f, width, 64f); // ajuste vertical se necessário
        } // se não houver textura, você pode desenhar debug com ShapeRenderer no GameScreen
    }

    public void dispose() {
        if (texture != null) texture.dispose();
    }

    public float getRequiredSpeed() { return requiredSpeed; }
    public float getX() { return x; }
    public float getWidth() { return width; }
}
