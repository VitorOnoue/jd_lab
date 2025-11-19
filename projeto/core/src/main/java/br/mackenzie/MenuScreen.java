package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class MenuScreen implements Screen {
    private final MyGame game;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture background;

    private Stage stage;
    private Skin skin;

    public MenuScreen(MyGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2f);

        background = new Texture(Gdx.files.internal("background.png"));

        stage = new Stage(new ScreenViewport());
        skin = createBasicSkin();

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        TextButton startBtn = new TextButton("Iniciar Jogo", skin);
        TextButton exitBtn  = new TextButton("Sair", skin);

        table.center();
        table.pad(20);
        table.add(startBtn).width(300).height(70).pad(10);
        table.row();
        table.add(exitBtn).width(300).height(70).pad(10);

        startBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game, 1));
                dispose();
            }
        });

        exitBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // start jogo por teclado
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new GameScreen(game, 1));
            dispose();
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
            return;
        }

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        font.draw(batch, "Pedal Boost", 40, Gdx.graphics.getHeight() - 80);
        font.draw(batch, "Pressione ENTER para Iniciar ou use os botões abaixo", 40, Gdx.graphics.getHeight() - 120);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
        if (background != null) background.dispose();
        if (stage != null) stage.dispose();
        if (skin != null) skin.dispose();
    }

    /** Cria um Skin simples em memória com um botão básico. */
    private Skin createBasicSkin() {
        Skin skin = new Skin();
        BitmapFont f = new BitmapFont();
        f.getData().setScale(1.2f);
        skin.add("default-font", f);

        // cria um drawable branco reutilizável
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
