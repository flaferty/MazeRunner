package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ScreenUtils;
import de.tum.cit.ase.maze.Objects.Key;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.addAction;


/**
 * The GameScreen class is responsible for rendering the gameplay screen.
 * It handles the game logic and rendering of the game elements.
 */
public class GameScreen implements Screen {

    private Character character;
    private final MazeRunnerGame game;
    private final OrthographicCamera camera;
    private MapClass map;
    private final BitmapFont font;
    private float sinusInput = 0f;
    private float characterSpeed = 375f;
    private Animation<TextureRegion> currentAnimation;
    private boolean isMoving = false;
    private float exitTimeCounter = 0f;
    private boolean isExiting = false;
    private long startTime = System.currentTimeMillis();
    private Texture keysTexture;
    private int numOfKeysCollected = 0;
    private boolean isFlickering = false;
    private float flickerTime = 0;
    private final float duration = 0.1f; // Duration for each flicker
    private final float totalFlickerTime = 1f;
    private boolean isAttacking;
    private Sound swoosh;
    private boolean playedSound;


    /**
     * Constructor for GameScreen. Sets up the camera and font.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public GameScreen(MazeRunnerGame game) {
        this.game = game;
        keysTexture = new Texture(Gdx.files.internal("keys.png"));
        swoosh = Gdx.audio.newSound(Gdx.files.internal("sounds/swosh-05.mp3"));
        if (this.map == null) {
            this.map = new MapClass();
        } else {
            updateMap(map);
        }

        if (this.character == null) {
            this.character = new Character(64, 128, map);
        } else {
            updateCharacter(character);
        }


        // Create and configure the camera for the game view
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        camera.zoom = 0.75f;

        // Get the font from the game's skin
        font = game.getSkin().getFont("font");

        //loading textures
        map.loadTextures();
        map.loadMap(Gdx.files.internal("maps/level-" + game.getCurrentLevel() + ".properties"));
        character.setToEntryPoint(map);
        map.placeLemonRandomly();
        map.placeHeartRandomly();

    }

    // Screen interface methods with necessary functionality
    @Override
    public void render(float delta) {

        // Check for escape key press to go back to the menu
        pauseGame();
        buttonInput();

        ScreenUtils.clear(0, 0, 0, 1); // Clear the screen
        camera.update(); // Update the camera

        if (character.getLives() < 1) {
            game.showGameOverScreen();
        }

        sinusInput += delta;
//        System.out.println("sinusInput " + sinusInput);
//        System.out.println("x:" + character.getxPosition() + " y:" + character.getyPosition());


        adjustCameraPosition();

        // Set up and begin drawing with the sprite batch
        game.getSpriteBatch().setProjectionMatrix(camera.combined);
        game.getSpriteBatch().begin(); // Important to call this before drawing anything

        map.render(game.getSpriteBatch());

//is inside of method because of the delta thing
        if (character.isDamaged() && !isFlickering) {
            isFlickering = true;
            flickerTime = 0;
        }

        if (isFlickering) {
            flickerTime += delta; // deltaTime is the time since the last frame
            if (flickerTime > totalFlickerTime) {
                isFlickering = false;
                character.setDamaged(false);
            }
        }
        drawCharacter();

        game.getSpriteBatch().setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));


        for (int i = 0; i < 70 * character.getLives(); i += 70) {
            game.getSpriteBatch().draw(game.loadHeartAnimation().getKeyFrame(sinusInput, true), i, Gdx.graphics.getHeight() - 70, 70, 70);
        }

        tabMenu();
        drawKeys();
        checkForKeyCollision();
        checkForHeartCollision();
        checkForLemonCollision();
        checkForEnemyCollision();

        character.update(delta);
        checkForExitCollision();

        game.getSpriteBatch().end(); // Important to call this after drawing everything
    }

    /**
     * Based on user key input, moves the character and gets correct animation.
     */
    public void buttonInput() {
        float moveAmount = Gdx.graphics.getDeltaTime() * characterSpeed;
        currentAnimation = game.getCharacterDownAnimation();

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            currentAnimation = game.getCharacterLeftAnimation();
            character.moveLeft(moveAmount);
            isMoving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            currentAnimation = game.getCharacterRightAnimation();
            character.moveRight(moveAmount);
            isMoving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            currentAnimation = game.getCharacterUpAnimation();
            character.moveUp(moveAmount);
            isMoving = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            currentAnimation = game.getCharacterDownAnimation();
            character.moveDown(moveAmount);
            isMoving = true;
        } else {
            isMoving = false;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Z) && currentAnimation == game.getCharacterRightAnimation()) {
            currentAnimation = game.getCharacterAttackRightAnimation();
            isAttacking = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Z) && currentAnimation == game.getCharacterUpAnimation()) {
            currentAnimation = game.getCharacterAttackUpAnimation();
            isAttacking = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Z) && currentAnimation == game.getCharacterLeftAnimation()) {
            currentAnimation = game.getCharacterAttackLeftAnimation();
            isAttacking = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Z) && currentAnimation == game.getCharacterDownAnimation()) {
            currentAnimation = game.getCharacterAttackDownAnimation();
            isAttacking = true;
        } else {
            isAttacking = false;
        }

    }

    private void checkForEnemyCollision() {
        int characterTileX = (int) ((character.getxPosition() + 12) / 64);
        int characterTileY = (int) ((character.getyPosition() + 12) / 64);

        if (isAttacking) {
            if (!playedSound) {
                swoosh.play();
                playedSound = true;
            }

            for (int x = characterTileX - 1; x <= characterTileX + 1; x++) {
                for (int y = characterTileY - 1; y <= characterTileY + 1; y++) {
                    if (map.isWithinBounds(x, y) && map.getMap()[x][y] == MapClass.ENEMY) {
                        map.removeEnemy(x, y);
                    }
                }
            }
        } else {
            playedSound = false;
        }
    }

    /**
     * Checks if the position of the character corresponds to the tile
     * of a key and adds one key to the character's inventory.
     */
    public void checkForKeyCollision() {
        int characterTileX = (int) ((character.getxPosition() + 12) / 64);
        int characterTileY = (int) ((character.getyPosition() + 12) / 64);

        for (int x = characterTileX; x <= characterTileX; x++) {
            for (int y = characterTileY; y <= characterTileY; y++) {
                if (map.getTile(x, y) == MapClass.KEY) {
                    character.setHasKey(true);
                    map.removeKey(x, y);
                    numOfKeysCollected++;
                }
            }
        }
    }

    /**
     * Checks if the position of the character corresponds to the tile
     * of a heart and adds one live to the character's health.
     */
    public void checkForHeartCollision() {
        int characterTileX = (int) ((character.getxPosition() + 12) / 64);
        int characterTileY = (int) ((character.getyPosition() + 12) / 64);

        for (int x = characterTileX; x <= characterTileX; x++) {
            for (int y = characterTileY; y <= characterTileY; y++) {
                if (map.getTile(x, y) == MapClass.HEART) {
                    if (character.getLives() <= 5) {
                        character.increaseHealth(1); // Assuming this method exists in Character
                    }
                    map.removeHeart(x, y);
                }
            }

        }
    }

    /**
     * Checks if the position of the character corresponds to the tile
     * of a lemon and adds the ability to walk faster.
     */
    public void checkForLemonCollision() {
        int characterTileX = (int) ((character.getxPosition() + 12) / 64);
        int characterTileY = (int) ((character.getyPosition() + 12) / 64);

        for (int x = characterTileX; x <= characterTileX; x++) {
            for (int y = characterTileY; y <= characterTileY; y++) {
                if (map.getTile(x, y) == MapClass.LEMON) {
                    characterSpeed *= 1.5f;
                    map.removeLemon(x, y);
                }
            }

        }
    }

    /**
     * Checks if the position of the character corresponds to the tile
     * of an exit, adds a timer between this and next level and
     * goes to next level.
     */
    public void checkForExitCollision() {
        float x = character.getxPosition();
        float y = character.getyPosition();
        float[][] adjustmentAreas = {

                // array with coordinates around exit areas on right side of map
                {2400, 2500, 550, 600, 64, 0},
                {0, 100, 1250, 1300, 0, 64},
                {1150, 1200, 800, 850, 64, 0}
        };

        // check if character is within any of the defined areas and adjust
        for (float[] area : adjustmentAreas) {
            if (x >= area[0] && x <= area[1] && y >= area[2] && y <= area[3]) {
                x += area[4]; // adjust X coordinate
                y += area[5]; // adjust Y coordinate
                break; // break after finding the first matching area
            }
        }
        int characterTileX = (int) (x / 64);
        int characterTileY = (int) (y / 64);
//        System.out.println(characterTileX + " " + characterTileY);

        if (map.getTile(characterTileX, characterTileY) == MapClass.EXIT && character.isHasKey()) {
            numOfKeysCollected = 0;
            isExiting = true;
            map.removeExit(characterTileX, characterTileY);
        }

        if (isExiting) {
            sinusInput += Gdx.graphics.getDeltaTime();
            exitTimeCounter += Gdx.graphics.getDeltaTime();

        }
        if (exitTimeCounter >= 1f) {
            game.goToNextLevel();
            isExiting = false;
            exitTimeCounter = 0;
            if (game.getCurrentLevel() <= 5) {
                game.getLevelUp().play();
            }
        }
    }

    /**
     * Camera follows the character
     */

    public void adjustCameraPosition() {
        camera.position.set(character.getxPosition(), character.getyPosition(), 0);
        camera.update();
    }

    /**
     * When TAB is pressed, the time elapsed in the current level is displayed on the top left side of the screen,
     * as well as the number of the level.
     */
    private void tabMenu() {
        if (Gdx.input.isKeyPressed(Input.Keys.TAB)) {
            font.draw(game.getSpriteBatch(), "time elapsed: " + ((System.currentTimeMillis() - startTime) / 1000f),
                    Gdx.graphics.getWidth() * 0.015f, Gdx.graphics.getHeight() * 0.888f);
            font.draw(game.getSpriteBatch(), "current level: " + game.getCurrentLevel(),
                    Gdx.graphics.getWidth() * 0.015f, Gdx.graphics.getHeight() * 0.83f);

        }
    }

    /**
     * Draws the character based on the animation (walking or attacking).
     * If the character is damaged, it will flicker between a visible and an invisible state.
     */
    public void drawCharacter() {
        if (!character.isDamaged() || (isFlickering && (int) (flickerTime / duration) % 2 == 0)) { //flickertime / duration -> interval for flicker,
            if (currentAnimation == game.getCharacterAttackRightAnimation() || currentAnimation == game.getCharacterAttackLeftAnimation()
                    || currentAnimation == game.getCharacterAttackUpAnimation() || currentAnimation == game.getCharacterAttackDownAnimation()) {
                game.getSpriteBatch().draw(
                        isMoving ? currentAnimation.getKeyFrame(sinusInput, true) : currentAnimation.getKeyFrames()[0],
                        character.getxPosition() - 32,
                        character.getyPosition(),
                        128,
                        128
                );
            } else {
                game.getSpriteBatch().draw(
                        isMoving ? currentAnimation.getKeyFrame(sinusInput, true) : currentAnimation.getKeyFrames()[0],
                        character.getxPosition(),
                        character.getyPosition(),
                        64,
                        128
                );
            }
        }
    }

    /**
     * Keys are drawn in the top right corner of the screen.
     */
    private void drawKeys() {
        game.getSpriteBatch().draw(keysTexture, Gdx.graphics.getWidth() - 224, Gdx.graphics.getHeight() - 62, 64, 64);
        font.draw(game.getSpriteBatch(), "Keys: " + numOfKeysCollected, Gdx.graphics.getWidth() - 148, Gdx.graphics.getHeight() - 20);
    }

    /**
     * Updates the map whenever a new map object is created.
     *
     * @param newMap
     */
    public void updateMap(MapClass newMap) {
        this.map = newMap;
    }

    /**
     * Updates the character whenever a new map object is created.
     *
     * @param characterNew
     */
    public void updateCharacter(Character characterNew) {
        this.character = characterNew;
    }

    /**
     * By pressing escape, the player goes to the pause menu
     * and the menu background music plays.
     */
    public void pauseGame() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new PauseScreen(game, this));
            game.getBackgroundMusic().pause();
        } else {
            game.getBackgroundMusic().play();
            game.getBackgroundMusic().setLooping(true);
        }
    }


    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void show() {

    }


    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        font.dispose();
        map.dispose();
        keysTexture.dispose();
    }

    public boolean isExiting() {
        return isExiting;
    }

    public void setExiting(boolean exiting) {
        isExiting = exiting;
    }

    public int getNumOfKeysCollected() {
        return numOfKeysCollected;
    }

    public void setNumOfKeysCollected(int numOfKeysCollected) {
        this.numOfKeysCollected = numOfKeysCollected;
    }

    public float getSinusInput() {
        return sinusInput;
    }

    public void setSinusInput(float sinusInput) {
        this.sinusInput = sinusInput;
    }


}
