package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Obstáculo tipo "gap" (buraco) — o player precisa ter velocidade suficiente
 * ao cruzar a zona de entrada para saltar/atravessar. Caso contrário, cai.
 */
public class GapObstacle {

    private float x;           // posição X do início do buraco
    private float width;       // largura do buraco (gap)
    private float groundY;     // altura do "chão" (base do gap)
    private float requiredSpeed;

    private Texture texture;   // opcional: textura do buraco (pode ser null)

    // zonas de trigger
    private final Rectangle entryZone = new Rectangle(); // área antes do buraco onde checamos velocidade
    private float exitX; // X onde o jogador aparecerá ao passar

    // evita multi-trigger
    private float entryCooldown = 0f;

    /**
     * @param x posição X do início do gap (lado esquerdo)
     * @param groundY Y do solo (onde o player caminha)
     * @param width largura do gap em unidades do mundo
     * @param requiredSpeed velocidade mínima necessária (mesma unidade de player.getVelX())
     */
    public GapObstacle(float x, float groundY, float width, float requiredSpeed) {
        this.x = x;
        this.groundY = groundY;
        this.width = width;
        this.requiredSpeed = requiredSpeed;

        // tenta carregar uma textura chamada "gap.png" se existir (opcional)
        try {
            texture = new Texture(Gdx.files.internal("gap2.png"));
        } catch (Exception e) {
            texture = null; // sem textura, usaremos apenas debug-draw ou transparente
        }

        recomputeZones();
    }

    private void recomputeZones() {
        // largura da zona de trigger antes do gap (onde medimos a velocidade)
        float triggerWidth = 60f; // ajuste conforme necessário
        float triggerHeight = 160f; // altura tolerada (ajuste ao tamanho do player)
        entryZone.set(x - triggerWidth, groundY, triggerWidth, triggerHeight);

        // posição X de onde o jogador deve reaparecer na outra margem
        exitX = x + width + 8f;
    }

    /**
     * Checa e resolve a interação com o jogador. Chamado uma vez por frame.
     */
    public void checkAndResolve(Player player, float dt) {
        if (entryCooldown > 0f) entryCooldown -= dt;

        // se não estiver sobrepondo a boca de entrada, nada a fazer
        if (!player.getBounds().overlaps(entryZone)) return;

        // evita multiples triggers
        if (entryCooldown > 0f) return;

        // queremos que o jogador esteja se movendo da esquerda pra direita
        if (player.getVelX() <= 0f) return;

        float speed = player.getVelX();

        if (speed >= requiredSpeed) {
            // PASSOU: teleporta pra margem direita (outro lado do gap)
            float newX = exitX + 2f; // posiciona um pouco à frente da saída
            player.setPosition(newX, player.getY());
            // garante velocidade de saída mínima (opcional)
            if (player.getVelX() < requiredSpeed * 0.5f) {
                player.setVelX(requiredSpeed * 0.5f);
            }
        } else {
            // FALHOU: jogador cai no buraco -> colocamos ele para baixo do chão
            // (o GameScreen deverá detectar y abaixo do groundY e reiniciar)
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

    // Getters úteis
    public float getRequiredSpeed() { return requiredSpeed; }
    public float getX() { return x; }
    public float getWidth() { return width; }

    // Debug draw usando ShapeRenderer (opcional)
    public void drawDebug(com.badlogic.gdx.graphics.glutils.ShapeRenderer renderer) {
        renderer.setColor(1f, 0f, 0f, 1f);
        // marca entrada
        renderer.rect(entryZone.x, entryZone.y, entryZone.width, entryZone.height);
        // marca o gap como retângulo
        renderer.setColor(0f, 0f, 0f, 1f);
        renderer.rect(x, groundY - 6f, width, 6f);
    }
}
