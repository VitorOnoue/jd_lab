package br.mackenzie;

import com.badlogic.gdx.math.Rectangle;

public abstract class GameObject {

    protected float x;
    protected float y;
    protected float width;
    protected float height;

    protected final Rectangle bounds = new Rectangle();

    public GameObject(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        updateBounds();
    }

    protected void updateBounds() {
        bounds.set(x, y, width, height);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public void setPosition(float newX, float newY) {
        this.x = newX;
        this.y = newY;
        updateBounds();
    }

    public void dispose() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'dispose'");
    }
}
