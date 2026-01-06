package de.tum.cit.ase.maze.Objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
/**
 * The Trap class is responsible for drawing its animation.
 */
public class Trap extends GameObject{

    private Texture trapSheet;

    public Trap(Texture trapSheet) {
        this.trapSheet = trapSheet;
    }

    /**
     * Loads the sheet and turns it into a trap animation
     * @return
     */

    @Override
    public Animation<TextureRegion> loadAnimation() {
        Array<TextureRegion> frames = new Array<>(TextureRegion.class);

        for (int col = 0; col < 3; col++) {
            frames.add(new TextureRegion(trapSheet, col * 16, 80, 16, 16));
        }
        return new Animation<>(0.5f, frames);
    }

    @Override
    public void remove(int x, int y) {
    }


    /**
     * Loads the sheet and turns it into a trap animation
     * @return
     */
    public Animation<TextureRegion> loadSecondTrapAnimation() {
        Array<TextureRegion> frames = new Array<>(TextureRegion.class);

        for (int row = 0; row < 7; row++) {
            frames.add(new TextureRegion(trapSheet, row * 32, 0, 32, 25));
        }
        return new Animation<>(0.2f, frames);
    }
}
