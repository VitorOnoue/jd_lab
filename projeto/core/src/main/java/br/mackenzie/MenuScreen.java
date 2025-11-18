package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MenuScreen implements Screen {
    private final MyGame game;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture background;

    public MenuScreen(MyGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont(); // Fonte padrão do sistema (Arial simples)
        font.getData().setScale(2); // Aumenta o tamanho do texto
        
        // Pode reusar o background do jogo ou carregar outro
        background = new Texture(Gdx.files.internal("background.png"));
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        // Texto centralizado (simples)
        font.draw(batch, "JOGO DE REABILITACAO - IOT", 300, 400);
        font.draw(batch, "Pressione ESPACO para Iniciar", 300, 300);
        batch.end();

        // Lógica de iniciar o jogo
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            // Inicia a Fase 1
            game.setScreen(new GameScreen(game, 1));
            dispose(); // Limpa o menu da memória
        }
    }

    @Override
    public void resize(int width, int height) {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    
    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        // Não dê dispose no background se ele for compartilhado, senão dê.
    }
}