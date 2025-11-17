package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Fase 1: player anda pra frente e pra trás, pega embalo e tenta atravessar o
 * loop.
 */
public class GameScreen implements Screen {

    // Mundo "2D lateral":
    public static final float WORLD_WIDTH = 1280f;
    public static final float WORLD_HEIGHT = 720f;

    private Texture backgroundTexture;
    private SpriteBatch batch;

    private final MyGame game;
    private final Viewport viewport;
    private final ShapeRenderer shapeRenderer;

    private final Player player;
    private final LoopObstacle loopObstacle;

    private BitmapFont font;

    // controle simples pra restart se quiser
    private boolean debugResetRequested = false;

    public GameScreen(MyGame game) {
        this.game = game;
        this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
        this.shapeRenderer = new ShapeRenderer();

        // cria o jogador
        this.player = new Player(
                150f, // posição inicial X
                150f, // posição inicial Y (chão)
                100f, 100f // tamanho
        );

        // cria um loop a frente
        this.loopObstacle = new LoopObstacle(
                700f, // centro X do círculo
                120f, // base Y do círculo (onde o player entra)
                280f, // raio
                250f // velocidade mínima necessária pra atravessar
        );
    }

    @Override
    public void show() {
        // nada especial por enquanto
        backgroundTexture = new Texture(Gdx.files.internal("background.png"));
        backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        font = new BitmapFont();
        font.getData().setScale(1.5f);
        font.setColor(Color.YELLOW);

        batch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        // --- INPUT DE DEBUG ---
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            debugResetRequested = true;
        }
        if (debugResetRequested) {
            resetStage();
            debugResetRequested = false;
        }

        // --- UPDATE ---
        player.update(delta);
        loopObstacle.checkAndResolve(player, delta);
        player.clampX(0, WORLD_WIDTH);

        // --- DESENHO ---
        Gdx.gl.glClearColor(0f, 0f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();

        // 1) BACKGROUND (SpriteBatch) — ocupa o mundo lógico inteiro
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(
                backgroundTexture,
                0f, 0f,
                viewport.getWorldWidth(), // ou WORLD_WIDTH
                viewport.getWorldHeight() // ou WORLD_HEIGHT
        );

        loopObstacle.draw(batch);
        player.draw(batch);

        int speedDisplay = (int) Math.abs(player.getVelX() / 10);

        font.draw(batch, "VELOCIDADE: " + speedDisplay + " km/h", 20, WORLD_HEIGHT - 20);
        font.draw(batch, "Fase: 1", 20, WORLD_HEIGHT - 50);
        
        if (player.getX() > 1000) { // Exemplo de fim de fase
        font.draw(batch, "Fase Completa!", WORLD_WIDTH/2 - 100, WORLD_HEIGHT/2);
        }
        
        batch.end();

        // 2) GAMEPLAY (ShapeRenderer) por cima
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);


        shapeRenderer.end();

        // overlay debug (velocidade)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.line(
                player.getX(), player.getY() + player.getHeight() + 10,
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
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
    }
}