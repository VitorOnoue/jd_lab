package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.Pixmap;

public class GameScreen implements Screen {

    public static final float WORLD_WIDTH = 1280f;
    public static final float WORLD_HEIGHT = 720f;

    private Texture backgroundTexture;
    private SpriteBatch batch;

    private final MyGame game;
    private final Viewport viewport;
    private final ShapeRenderer shapeRenderer;

    private final Player player;
    private final GapObstacle gapObstacle;

    private BitmapFont font;
    private int currentLevel;

    private boolean paused = false;
    private Stage pauseStage;
    private Skin pauseSkin;

    public GameScreen(MyGame game, int level) {
        this.game = game;
        this.currentLevel = level;
        this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
        this.shapeRenderer = new ShapeRenderer();

        float startX = 100f + level * 100f;
        float startY = 210f;

        // cria o jogador com posição dependente do level
        this.player = new Player(startX, startY, 100f, 100f);

        float difficulty = 250f + ((level - 1) * 100f);

        this.gapObstacle = new GapObstacle(
                700f,
                200f,
                280f,
                difficulty);
    }

    @Override
    public void show() {
        backgroundTexture = new Texture(Gdx.files.internal("background.png"));
        backgroundTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        font = new BitmapFont();
        font.getData().setScale(1.5f);
        font.setColor(Color.YELLOW);

        batch = new SpriteBatch();

        pauseStage = new Stage(new FitViewport(WORLD_WIDTH, WORLD_HEIGHT));
        pauseSkin = createBasicSkin();

        Table table = new Table();
        table.setFillParent(true);
        pauseStage.addActor(table);

        TextButton resumeBtn = new TextButton("Retomar (P)", pauseSkin);
        TextButton restartBtn = new TextButton("Reiniciar (R)", pauseSkin);
        TextButton menuBtn = new TextButton("Menu Principal (M)", pauseSkin);

        table.center();
        table.add(resumeBtn).width(300).height(60).pad(8);
        table.row();
        table.add(restartBtn).width(300).height(60).pad(8);
        table.row();
        table.add(menuBtn).width(300).height(60).pad(8);

        resumeBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setPaused(false);
            }
        });
        menuBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                goToMainMenu();
            }
        });
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            // toggle pause
            setPaused(!paused);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            // volta ao menu
            goToMainMenu();
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            goToMainMenu();
            return;
        }

        if (!paused) {
            player.update(delta);
            gapObstacle.checkAndResolve(player, delta);
            player.clampX(0, WORLD_WIDTH);

            if (player.getY() < 120f - 50f) { // caiu
                // troca para a tela de "falhou"
                game.setScreen(new FailScreen(game, currentLevel));
                dispose();
                return; 
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                resetStage();
            }
        } else {
            pauseStage.act(delta);
        }

        Gdx.gl.glClearColor(0f, 0f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply();

        // SpriteBatch world
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        batch.draw(backgroundTexture, 0f, 0f, viewport.getWorldWidth(), viewport.getWorldHeight());
        gapObstacle.draw(batch);
        player.draw(batch);

        // desenho texto velocidade e fase
        font.draw(batch, "Velocidade Atual: " + player.getVelX(), 20, WORLD_HEIGHT - 20);
        font.draw(batch, "Velocidade Necessária: " + gapObstacle.getRequiredSpeed(), 20, WORLD_HEIGHT - 50);
        font.draw(batch, "Fase: " + currentLevel, 20, WORLD_HEIGHT - 80);

        // desenho texo menu
        font.draw(batch, "Fase: " + currentLevel, 20, WORLD_HEIGHT - 80);

        // desenho texto instruções
        font.draw(batch, "P: Pausar | R: Reiniciar | M ou ESC: Menu Principal", 500, WORLD_HEIGHT - 20);

        // desenho se terminou fase
        if (player.getX() > 1000) {
            font.draw(batch, "FASE " + currentLevel + " COMPLETA!", WORLD_WIDTH / 2 - 150, WORLD_HEIGHT / 2 + 50);
            font.draw(batch, "Pressione ENTER para continuar", WORLD_WIDTH / 2 - 150, WORLD_HEIGHT / 2);

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                if (currentLevel < 3) {
                    game.setScreen(new GameScreen(game, currentLevel + 1));
                    dispose();
                    return;
                } else {
                    game.setScreen(new MenuScreen(game));
                    dispose();
                    return;
                }
            }
        }

        batch.end();

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.line(
                player.getX(), player.getY() + player.getHeight() + 10,
                player.getX() + player.getVelX() * 0.2f,
                player.getY() + player.getHeight() + 10);
        shapeRenderer.end();

        if (paused) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(new Color(0f, 0f, 0f, 0.6f));
            shapeRenderer.rect(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
            shapeRenderer.end();
            Gdx.gl.glDisable(GL20.GL_BLEND);

            pauseStage.getViewport().apply();
            pauseStage.draw();
        }
    }

    private void setPaused(boolean pause) {
        this.paused = pause;
        if (paused) {
            Gdx.input.setInputProcessor(pauseStage);
        } else {
            Gdx.input.setInputProcessor(null);
        }
    }

    private void goToMainMenu() {
        game.setScreen(new MenuScreen(game));
        dispose();
    }

    private void resetStage() {
        float x = 100f + currentLevel * 100f;
        float y = 210f;
        player.reset(x, y);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        if (pauseStage != null)
            pauseStage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        setPaused(true);
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
        if (batch != null)
            batch.dispose();
        if (font != null)
            font.dispose();
        if (backgroundTexture != null)
            backgroundTexture.dispose();
        if (pauseStage != null)
            pauseStage.dispose();
        if (pauseSkin != null)
            pauseSkin.dispose();
    }

    private Skin createBasicSkin() {
        Skin skin = new Skin();
        BitmapFont f = new BitmapFont();
        f.getData().setScale(1.2f);
        skin.add("default-font", f);

        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pix.setColor(1f, 1f, 1f, 1f);
        pix.fill();
        skin.add("white", new com.badlogic.gdx.graphics.Texture(pix));
        pix.dispose();

        com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle tbs = new com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle();
        tbs.up = skin.newDrawable("white", 0.2f, 0.2f, 0.2f, 1f);
        tbs.down = skin.newDrawable("white", 0.1f, 0.1f, 0.1f, 1f);
        tbs.checked = skin.newDrawable("white", 0.25f, 0.25f, 0.25f, 1f);
        tbs.font = skin.getFont("default-font");
        skin.add("default", tbs);

        return skin;
    }
}
