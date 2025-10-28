package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Fase 1: player anda pra frente e pra trás, pega embalo e tenta atravessar o loop.
 */
public class GameScreen implements Screen {

    // Mundo "2D lateral":
    public static final float WORLD_WIDTH = 800f;
    public static final float WORLD_HEIGHT = 450f;

    private final MyGame game;
    private final Viewport viewport;
    private final ShapeRenderer shapeRenderer;

    private final Player player;
    private final LoopObstacle loopObstacle;

    // controle simples pra restart se quiser
    private boolean debugResetRequested = false;

    public GameScreen(MyGame game) {
        this.game = game;
        this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
        this.shapeRenderer = new ShapeRenderer();

        // cria o jogador
        this.player = new Player(
            100f,                  // posição inicial X
            80f,                   // posição inicial Y (chão)
            40f, 40f               // tamanho
        );

        // cria um loop a frente
        this.loopObstacle = new LoopObstacle(
            400f,      // centro X do círculo
            100f,      // base Y do círculo (onde o player entra)
            60f,       // raio
            250f       // velocidade mínima necessária pra atravessar
        );
    }

    @Override
    public void show() {
        // nada especial por enquanto
    }

    @Override
    public void render(float delta) {
        // --- INPUT DE DEBUG (ex: R pra resetar fase) ---
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            debugResetRequested = true;
        }
        if (debugResetRequested) {
            resetStage();
            debugResetRequested = false;
        }

        // --- UPDATE LÓGICA ---
        player.update(delta);

        // checar interação player <-> looping
        loopObstacle.checkAndResolve(player);

        // manter player dentro do mundo (por enquanto não tem câmera, então prende)
        player.clampX(0, WORLD_WIDTH);

        // --- DESENHO ---
        Gdx.gl.glClearColor(0f, 0f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // desenha "chão"
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(0, 60, WORLD_WIDTH, 20);

        // desenha o player
        shapeRenderer.setColor(Color.SKY);
        shapeRenderer.rect(player.getX(), player.getY(), player.getWidth(), player.getHeight());

        // desenha o loop (obstáculo)
        loopObstacle.draw(shapeRenderer);

        shapeRenderer.end();

        // desenha overlay debug (velocidade)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.line(player.getX(), player.getY() + player.getHeight() + 10,
                           player.getX() + player.getVelX() * 0.2f,
                           player.getY() + player.getHeight() + 10);
        shapeRenderer.end();
    }

    private void resetStage() {
        player.reset(100f, 80f);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}