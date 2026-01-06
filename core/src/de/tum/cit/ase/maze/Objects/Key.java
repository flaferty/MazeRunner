package de.tum.cit.ase.maze.Objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.ase.maze.Character;
import de.tum.cit.ase.maze.MapClass;

import static de.tum.cit.ase.maze.MapClass.FLOOR;
import static de.tum.cit.ase.maze.MapClass.KEY;
/**
 * The Key class is responsible for drawing its animation.
 */
public class Key extends GameObject {
    private Texture texture;
    private Character character;
    private MapClass map;

    public Key(Texture texture, MapClass map) {
        this.texture = texture;
        this.map = map;
        this.character = new Character(64, 128, map);
    }

    public Key(MapClass map) {
        this.map = map;
    }

    @Override
    public Animation<TextureRegion> loadAnimation() {
        Array<TextureRegion> frames = new Array<>(TextureRegion.class);

        for (int col = 0; col < 11; col++) {
            frames.add(new TextureRegion(texture, col * 32, 0, 32, 32));
        }
        return new Animation<>(0.2f, frames);
    }

    /**
     * Removes the key texture from the map and replaces it with floor texture
     */
    @Override
    public void remove(int x, int y) {
        if (map.getMap()[x][y] == KEY) {
            map.getKeySound().play();
            map.getMap()[x][y] = FLOOR;
        }
    }

}
