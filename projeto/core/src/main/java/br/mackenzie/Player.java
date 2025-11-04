package br.mackenzie;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class Player extends GameObject {

    // física horizontal
    private float velX = 0f;
    private float accel = 600f; // quão rápido acelera segurando a tecla
    private float maxSpeed = 400f; // velocidade máxima que pode atingir
    private float friction = 500f; // atrito quando não aperta nada
    private Texture texture;

    // "chão" fixo
    private final float groundY = 210f;

    public Player(float x, float y, float width, float height) {
        super(x, y, width, height);
        texture = new Texture("Player.png");
    }

    public void update(float dt) {
        // input esquerda/direita
        boolean left = Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A);
        boolean right = Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D);

        if (left && !right) {
            velX -= accel * dt;
        } else if (right && !left) {
            velX += accel * dt;
        } else {
            // sem input -> atrito freia
            if (velX > 0) {
                velX -= friction * dt;
                if (velX < 0)
                    velX = 0;
            } else if (velX < 0) {
                velX += friction * dt;
                if (velX > 0)
                    velX = 0;
            }
        }

        // limita velocidade máxima
        velX = MathUtils.clamp(velX, -maxSpeed, maxSpeed);

        // aplica deslocamento
        x += velX * dt;

        // mantém no chão (por enquanto não tem gravidade/pulo)
        y = Math.max(y, groundY);

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
