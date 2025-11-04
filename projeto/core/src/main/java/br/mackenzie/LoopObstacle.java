package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

public class LoopObstacle {

    // posição e tamanho (baseY = parte de baixo do loop)
    private float centerX;
    private float baseY;
    private float radius;

    // física mínima pra passar
    private float requiredSpeed;

    private Texture texture;

    // zonas de interação
    private final Rectangle entryZone = new Rectangle(); // boca esquerda
    private float exitX;                                  // boca direita (x)
    private float entryCooldown = 0f;                     // evita “multi-trigger” (segundos)

    public LoopObstacle(float centerX, float baseY, float radius, float requiredSpeed) {
        this.centerX = centerX;
        this.baseY = baseY;
        this.radius = radius;
        this.requiredSpeed = requiredSpeed;

        texture = new Texture(Gdx.files.internal("loopObstacle2.png"));
        recomputeZones();
    }

    /** Recalcula a boca de entrada/saída quando mudar posição/raio. */
    private void recomputeZones() {
        // largura/altura da boca de entrada (esquerda, parte de baixo)
        float entryWidth  = 30f;
        float entryHeight = Math.max(40f, radius * 0.65f); // altura proporcional ao loop

        // posiciona a boca encostada na lateral externa do loop
        entryZone.set(centerX - radius - entryWidth, baseY, entryWidth, entryHeight);

        // X da boca direita (um pouco à frente para evitar re-colisão)
        exitX = centerX + radius + 6f;
    }

    /** Chame uma vez por frame. */
    public void checkAndResolve(Player player, float dt) {
        // reseta cooldown com o tempo
        if (entryCooldown > 0f) entryCooldown -= dt;

        // 1) Se não está sobrepondo a boca de entrada, nada a fazer
        if (!player.getBounds().overlaps(entryZone)) return;

        // 2) Evita múltiplos triggers no mesmo contato
        if (entryCooldown > 0f) return;

        // 3) Só vale se estiver vindo DA ESQUERDA para a DIREITA
        if (player.getVelX() <= 0f) return;

        float speed = player.getVelX(); // já sabemos que > 0 aqui

        if (speed >= requiredSpeed) {
            // PASSOU: teleporta pra fora da boca direita e garante que não re-colida
            float newX = exitX + player.getWidth() + 2f; // joga um pouco além
            player.setPosition(newX, player.getY());

            // mantém/garante velocidade mínima de saída (opcional)
            if (player.getVelX() < requiredSpeed * 0.5f) {
                player.setVelX(requiredSpeed * 0.5f);
            }

            entryCooldown = 0.15f; // 150 ms de respiro
        } else {
            // FALHOU: empurra para a esquerda e zera/derruba velocidade
            float pushBack = 40f;
            float newX = entryZone.x - player.getWidth() - 2f;
            player.setPosition(newX, player.getY());
            player.setVelX(0f); // ou *= 0.3f

            entryCooldown = 0.15f;
        }
    }

    public void draw(SpriteBatch batch) {
        float size = radius * 2f;
        batch.draw(texture, centerX - radius, baseY, size, size);
    }

    // --- Debug opcional (mostra boca de entrada) ---
    public void drawDebug(ShapeRenderer renderer) {
        renderer.setColor(Color.RED);
        renderer.rect(entryZone.x, entryZone.y, entryZone.width, entryZone.height);
    }

    public float getRequiredSpeed() { return requiredSpeed; }

    public void setPosition(float centerX, float baseY) {
        this.centerX = centerX;
        this.baseY = baseY;
        recomputeZones();
    }

    public void setRadius(float radius) {
        this.radius = radius;
        recomputeZones();
    }

    public void dispose() {
        if (texture != null) texture.dispose();
    }
}
