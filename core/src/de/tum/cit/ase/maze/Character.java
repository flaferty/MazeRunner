package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;

import java.awt.*;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.addAction;

/**
 * The Character class is responsible for the movement of the character.
 * It creates boundaries, such that the character can't collide with certain objects (like WALL)
 * or loses a life when collision is detected (like ENEMY or TRAP).
 */
public class Character {
    private MazeRunnerGame game;
    private float xPosition;
    private float yPosition;
    private int width;
    private int height;
    private MapClass map;
    private boolean hasKey = false;
    private int lives = 5;
    private boolean isDamaged;

    private float trapCooldownTimer = 0f; // Timer to manage the cooldown period
    private float enemyCooldownTimer = 0f;
    private final float trapCooldown = 1f;
    private final float enemyCooldown = 1f;
    private Animation<TextureRegion> attackAnimationDown;
    private Animation<TextureRegion> attackAnimationUp;
    private Animation<TextureRegion> attackAnimationLeft;
    private Animation<TextureRegion> attackAnimationRight;
    private Sound hitSound;


    public Character(int width, int height, MapClass map) {
        this.map = map;
        this.width = width;
        this.height = height;
    }

    /**
     * Character moves up/down/left/right if the character won't collide with a wall.
     * Sets map boundaries, such that the character is unable to walk off the map.
     *
     * @param amount movement amount for the character per game frame
     */
    public void moveLeft(float amount) {
        if (!map.isWall(this.xPosition - amount, this.yPosition)
                && map.isWithinBounds((int) ((this.xPosition - 60) / 64), (int) ((this.yPosition) / 64))) {
            this.xPosition -= amount;
        }
    }

    public void moveRight(float amount) {
        if (!map.isWall(this.xPosition + amount, this.yPosition)
                && map.isWithinBounds((int) ((this.xPosition + 40) / 64), (int) ((this.yPosition) / 64))) {
            this.xPosition += amount;
        }
    }

    public void moveUp(float amount) {
        if (!map.isWall(this.xPosition, this.yPosition + amount)
                && map.isWithinBounds((int) ((this.xPosition) / 64), (int) ((this.yPosition) / 64))) {
            this.yPosition += amount;
        }
    }

    public void moveDown(float amount) {
        if (!map.isWall(this.xPosition, this.yPosition - amount)
                && map.isWithinBounds((int) ((this.xPosition) / 64), (int) ((this.yPosition - 40) / 64))) {
            this.yPosition -= amount;
        }
    }

    /**
     * Updates game state by decreasing cooldown timers for traps and enemies.
     *
     * @param deltaTime time elapsed since the last update call, used to
     *                  decrement the cooldown timers for traps and enemies
     */
    public void update(float deltaTime) {
        if (trapCooldownTimer > 0) {
            trapCooldownTimer -= deltaTime;
        }
        if (enemyCooldownTimer > 0) {
            enemyCooldownTimer -= deltaTime;
        }
        checkForTrap();
        checkForEnemy();
    }

    /**
     * Uses findEntryPoint method to retrieve x and y coordinates of entry,
     * scales and sets the spawn of the map to those coordinates.
     *
     * @param map
     */
    public void setToEntryPoint(MapClass map) {
        Point entryPoint = map.findEntryPoint();
        xPosition = entryPoint.x * 64;
        yPosition = entryPoint.y * 64;
    }

    /**
     * Checks if the position of the character is a trap / enemy.
     * If it is, the character loses one heart, and the cooldown is reset
     */
    public void checkForTrap() {
        if (map.isTrap(this.xPosition + 40, this.yPosition + 40)) {
            if (trapCooldownTimer <= 0) {
                decreaseHealth(1);
                hitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/laser.mp3"));
                hitSound.play();
                isDamaged = true;
                trapCooldownTimer = trapCooldown; // Reset the cooldown timer
            }
        }
    }

    private void checkForEnemy() {
        if (map.isEnemy(this.xPosition + 20, this.yPosition + 20)) {
            if (enemyCooldownTimer <= 0) {
                decreaseHealth(1);
                setDamaged(true);
                hitSound = Gdx.audio.newSound(Gdx.files.internal("sounds/laser.mp3"));
                hitSound.play();
                enemyCooldownTimer = enemyCooldown; // Reset the cooldown timer
            }
        } else {
            enemyCooldownTimer = 0;
        }
    }

    /**
     * Character lives decrease by one or show that the game is over.
     *
     * @param amount
     */
    public void decreaseHealth(int amount) {
        if (lives > 0) {
            lives -= amount;
        } else {
            game.showGameOverScreen();
        }
    }

    public void increaseHealth(int amount) {
        lives += amount;
    }


    public float getxPosition() {
        return xPosition;
    }

    public float getyPosition() {
        return yPosition;
    }

    public void setxPosition(float xPosition) {
        this.xPosition = xPosition;
    }

    public void setyPosition(float yPosition) {
        this.yPosition = yPosition;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public boolean isHasKey() {
        return hasKey;
    }

    public void setHasKey(boolean hasKey) {
        this.hasKey = hasKey;
    }

    public MapClass getMap() {
        return map;
    }

    public boolean isDamaged() {
        return isDamaged;
    }

    public void setDamaged(boolean damaged) {
        isDamaged = damaged;
    }

}
