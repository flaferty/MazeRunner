package de.tum.cit.ase.maze.Objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * The GameObject class creates some methods that are common among its inheritors.
 */
public abstract class GameObject {
    private Texture texture;

    public GameObject() {
        this.texture = texture;
    }

    public abstract Animation<TextureRegion> loadAnimation();

    public abstract void remove(int x, int y);

}
