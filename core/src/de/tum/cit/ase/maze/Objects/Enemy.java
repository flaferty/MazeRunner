package de.tum.cit.ase.maze.Objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * The Enemy class is responsible for drawing its animation.
 */
public class Enemy extends GameObject {
    private Texture enemySheet;

    public Enemy(Texture enemySheet) {
        this.enemySheet = enemySheet;
    }

    @Override
    public Animation<TextureRegion> loadAnimation() {
        Array<TextureRegion> frames = new Array<>(TextureRegion.class);
        for (int col = 0; col < 3; col++) {
            frames.add(new TextureRegion(enemySheet, col * 16, 64, 16, 16));
        }
        return new Animation<>(0.35f, frames);
    }

    @Override
    public void remove(int x, int y) {
    }

}
