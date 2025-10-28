package br.mackenzie;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class LoopObstacle {

    // posição do loop
    private float centerX;
    private float baseY;
    private float radius;

    // física mínima pra passar
    private float requiredSpeed;

    // região "de entrada" por onde o player tenta entrar no loop
    private Rectangle entryZone = new Rectangle();
    // ponto de saída (lado direito do loop)
    private float exitX;

    public LoopObstacle(float centerX, float baseY, float radius, float requiredSpeed) {
        this.centerX = centerX;
        this.baseY = baseY;
        this.radius = radius;
        this.requiredSpeed = requiredSpeed;

        // zona de entrada: um retângulo pequeno na esquerda inferior do loop
        float entryWidth = 20f;
        float entryHeight = 40f;
        entryZone.set(centerX - radius - entryWidth, baseY, entryWidth, entryHeight);

        // saída: lado direito inferior do loop
        this.exitX = centerX + radius + 5f;
    }

    /**
     * Checa se o player encostou na entrada do loop.
     * - Se sim e velocidade suficiente -> passa pro outro lado.
     * - Se não -> 'rebate' o player pra trás.
     */
    public void checkAndResolve(Player player) {
        if (player.getBounds().overlaps(entryZone)) {

            float speed = Math.abs(player.getVelX());

            if (speed >= requiredSpeed) {
                // PASSOU
                // Coloca o player do outro lado do loop
                float newX = exitX;
                player.setPosition(newX, player.getY());

                // mantém velocidade (ele sai "voando" pra direita)
                // se quiser dar um boost extra, pode adicionar algo aqui.
            } else {
                // FALHOU
                // empurra o player de volta um pouco pra esquerda e tira a velocidade
                float pushBack = 40f;
                float newX = player.getX() - pushBack;
                player.setPosition(newX, player.getY());

                // desacelera forte
                player.setVelX(player.getVelX() * 0.3f);
            }
        }
    }

    public void draw(ShapeRenderer renderer) {
        // corpo do loop (um círculo)
        renderer.setColor(Color.GOLD);
        renderer.circle(centerX, baseY + radius, radius);

        // zona de entrada (debug visual)
        renderer.setColor(Color.RED);
        renderer.rect(entryZone.x, entryZone.y, entryZone.width, entryZone.height);

        // zona de saída (debug)
        renderer.setColor(Color.GREEN);
        renderer.rect(exitX, baseY, 10f, 40f);
    }

    public float getRequiredSpeed() {
        return requiredSpeed;
    }
}
