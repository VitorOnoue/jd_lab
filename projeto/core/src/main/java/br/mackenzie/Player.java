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
    float pedalPower = 150f; // quanto de velocidade ganha por pedalada
    float friction = 200f;   // o quao rapido a bike para se nao pedalar
    float maxSpeed = 600f;   // velocidade maxima permitida

    // usamos SPACE ou UP para simular o pedal
    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
        velX += pedalPower;
    }

    // atrito
    if (velX > 0) {
        velX -= friction * dt;
        if (velX < 0) velX = 0; // impede vel negativa
    }

    // limita velocidade
    if (velX > maxSpeed) {
        velX = maxSpeed;
    }

    // atualiza posiçao
    x += velX * dt;

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
