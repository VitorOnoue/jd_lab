package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Player extends GameObject {

    // física horizontal
    private float velX = 0f;
    private Texture texture;

    public Player(float x, float y, float width, float height) {
        super(x, y, width, height);
        texture = new Texture("Player.png");
    }

    public void update(float dt) {
    // CONFIGURAÇÃO DA BIKE
    float pedalPower = 150f; // Quanto de velocidade ganha por "pedalada" (toque)
    float friction = 200f;   // O quão rápido a bike para se não pedalar
    float maxSpeed = 600f;   // Velocidade máxima permitida

    // 1. DETECTAR PEDALADA (Trocar "isKeyPressed" por "isKeyJustPressed")
    // Usamos SPACE ou SETA PRA CIMA para simular o pedal
    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
        velX += pedalPower;
    }

    // 2. APLICAR ATRITO CONSTANTE (A bike sempre quer parar)
    if (velX > 0) {
        velX -= friction * dt;
        if (velX < 0) velX = 0; // Não deixa ir para trás (negativo) sozinho
    }

    // 3. LIMITAR VELOCIDADE MÁXIMA
    if (velX > maxSpeed) {
        velX = maxSpeed;
    }

    // 4. APLICAR MOVIMENTO
    x += velX * dt;

    // 5. MANTER NO CHÃO
    y = Math.max(y, 210f); // groundY fixo conforme seu código original

    updateBounds();
    }

    public void clampX(float min, float max) {
        if (x < min) {
            x = min;
            velX = 0;
        }
        if (x + width > max) {
            x = max - width;
            velX = 0;
        }
        updateBounds();
    }

    public void reset(float startX, float startY) {
        this.x = startX;
        this.y = startY;
        this.velX = 0;
        updateBounds();
    }

    public float getVelX() {
        return velX;
    }

    public void setVelX(float velX) {
        this.velX = velX;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        updateBounds();
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
        updateBounds();
    }

    @Override
    public void dispose() {
    }
}
