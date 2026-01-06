package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import de.tum.cit.ase.maze.Objects.Enemy;
import de.tum.cit.ase.maze.Objects.Key;
import de.tum.cit.ase.maze.Objects.Trap;

import java.awt.*;
import java.io.FileReader;
import java.util.Properties;

import static com.badlogic.gdx.math.MathUtils.random;

/**
 * The MapClass class is responsible for loading and drawing the map and the textures inside of it.
 */
public class MapClass {
    private MazeRunnerGame game;
    public static final int FLOOR = -1;
    public static final int WALL = 0;
    public static final int ENTRY = 1;
    public static final int EXIT = 2;
    public static final int TRAP = 3;
    public static final int ENEMY = 4;
    public static final int KEY = 5;
    public static final int HEART = 6;
    public static final int LEMON = 7;


    private TextureRegion wallTexture;
    private TextureRegion wallTextureAbove;
    private TextureRegion wallTextureBelow;
    private TextureRegion wallTextureBoth;


    private TextureRegion entryTexture;
    private TextureRegion trapTexture;
    private TextureRegion exitTexture;
    private TextureRegion enemyTexture;
    private TextureRegion keyTexture;
    private TextureRegion floorTexture;
    private TextureRegion floorTexture2;

    private TextureRegion heartTexture;
    private TextureRegion lemonTexture;
    private Texture tiles;
    private Texture key;
    private Texture mobs;
    private Texture floor;
    private Texture heart;
    private Texture lemon;

    private boolean heartPlaced = false;
    private boolean lemonPlaced = false;

    private int[][] map;
    private Character character;
    float sinusInput;
    private Texture trapSheet2 = null;
    private Texture walls = null;

    private Texture trapSheet = null;
    private Texture enemySheet = null;
    private Animation<TextureRegion> enemyAnimationUp;
    private Animation<TextureRegion> enemyAnimationDown;
    private Animation<TextureRegion> enemyAnimationLeft;
    private Animation<TextureRegion> enemyAnimationRight;
    private Animation<TextureRegion> currentEnemyAnimation;
    private long lastMoveTime = 0;
    private long moveInterval = 250;
    private Sound heartPickup;
    private Sound keySound;
    private Sound explosion;
    private Sound lemonSound;
    private Trap trap;
    private Trap trap2;
    private Enemy enemy;
    private Key keys;


    public MapClass() {

    }

    /**
     * Initializes and loads textures
     */

    public void loadTextures() {
        tiles = new Texture(Gdx.files.internal("basictiles.png"));
        key = new Texture(Gdx.files.internal("key-white.png"));
        mobs = new Texture(Gdx.files.internal("mobs.png"));
        floor = new Texture(Gdx.files.internal("floor.png"));
        walls = new Texture(Gdx.files.internal("basictiles2.png"));

        this.keys = new Key(key, this);

        heartPickup = Gdx.audio.newSound(Gdx.files.internal("sounds/Rise06.mp3"));
        keySound = Gdx.audio.newSound(Gdx.files.internal("sounds/coin01.mp3"));
        explosion = Gdx.audio.newSound(Gdx.files.internal("sounds/Explosion11.mp3"));
        lemonSound = Gdx.audio.newSound(Gdx.files.internal("sounds/pick.mp3"));

        trapSheet2 = new Texture(Gdx.files.internal("slime-Sheet.png"));
        trapSheet = new Texture(Gdx.files.internal("things.png"));
        enemySheet = new Texture(Gdx.files.internal("mobs.png"));
        lemon = new Texture(Gdx.files.internal("lem.png"));
        enemySheet = new Texture(Gdx.files.internal("mobs.png"));
        heart = new Texture(Gdx.files.internal("heart1.png"));

        trap = new Trap(trapSheet);
        trap2 = new Trap(trapSheet2);
        enemy = new Enemy(enemySheet);

        TextureRegion[][] splitTiles = TextureRegion.split(tiles, 16, 16);
        TextureRegion[][] splitTiles2 = TextureRegion.split(walls, 16, 16);
        wallTexture = splitTiles2[0][2];
        wallTextureAbove = splitTiles2[0][3];
        wallTextureBelow = splitTiles2[0][1];
        wallTextureBoth = splitTiles2[0][0];
        exitTexture = splitTiles[6][1];
        entryTexture = splitTiles[7][1];
        floorTexture = new TextureRegion(floor, 16, 16);
        trapTexture = splitTiles[7][4];
//        enemyTexture = new TextureRegion(mobs, 0, 0, 16, 16); // not animated enemy
        keyTexture = new TextureRegion(key, 16, 16);
        heartTexture = new TextureRegion(heart, 18, 18);
        lemonTexture = new TextureRegion(lemon, 32, 32);
//        loadEnemyAnimation();
//        loadSecondTrapAnimation();
//        loadTrapAnimation();
    }

    /**
     * Draws the map
     *
     * @param batch
     */
    public void render(SpriteBatch batch) {
        updateSinusInput(Gdx.graphics.getDeltaTime());

        moveEnemies();
//        placeLemonRandomly();
//        placeHeartRandomly();
        currentEnemyAnimation = enemyAnimationDown;

        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                TextureRegion textureRegion = getTextureRegionForTile(map[x][y]);

                if (map[x][y] != KEY && map[x][y] != TRAP && map[x][y] != ENEMY
                        && map[x][y] != HEART && map[x][y] != LEMON) {
                    textureRegion = getTextureRegionForTile(map[x][y]);
                } else {
                    textureRegion = floorTexture;
                }
                batch.draw(textureRegion, x * 64, y * 64, 64, 64);

                if (map[x][y] == WALL) {
                    if (isWallAbove(x, y) && isWallBelow(x, y)) {
                        textureRegion = wallTextureBelow;
                    }
                    if ((!isWallAbove(x, y) && isWallBelow(x, y))) {
                        textureRegion = wallTexture;
                    }
                    if ((!isWallAbove(x, y) && !isWallBelow(x, y))){
                        textureRegion = wallTextureBoth;
                    }
                    if ((isWallAbove(x, y) && !isWallBelow(x, y))) {
                        textureRegion = wallTextureAbove;
                    }
                    batch.draw(textureRegion, x * 64, y * 64, 64, 64);
                }

                if (map[x][y] == KEY) {
                    TextureRegion frame = keys.loadAnimation().getKeyFrame(sinusInput, true);
                    batch.draw(frame, x * 64 + 12, y * 64 + 15, 40, 40);
                }

                if (map[x][y] == TRAP) {
                    TextureRegion currentFrame = trap.loadAnimation().getKeyFrame(sinusInput, true);
                    if (useSecondTrapTexture(x, y)) {
                        currentFrame = trap2.loadSecondTrapAnimation().getKeyFrame(sinusInput, true);
                    } else {
                        currentFrame = trap.loadAnimation().getKeyFrame(sinusInput, true);
                    }
                    batch.draw(currentFrame, x * 64, y * 64, 64, 64);
                }

                if (map[x][y] == ENEMY) {
                    TextureRegion enemyFrame = enemy.loadAnimation().getKeyFrame(sinusInput, true);
                    batch.draw(enemyFrame, x * 64, y * 64 + 10, 64, 64);
                }

                if (map[x][y] == HEART) {
                    batch.draw(heartTexture, x * 64 + 10, y * 64 + 10, 45, 45);
                }

                if (map[x][y] == LEMON) {
                    batch.draw(lemonTexture, x * 64 + 10, y * 64 + 10, 45, 45);
                }
            }


        }
    }

    /**
     * Depending on the type of coordinate it returns the specific texture needed
     *
     * @param tileType
     * @return
     */

    private TextureRegion getTextureRegionForTile(int tileType) {
        return switch (tileType) {
            case WALL -> wallTexture;
            case ENTRY -> entryTexture;
            case EXIT -> exitTexture;
            case TRAP -> trapTexture;
            case ENEMY -> enemyTexture;
            case KEY -> keyTexture;
            case FLOOR -> floorTexture;
            default -> null;
        };
    }

    /**
     * Checks if there is a wall one coordinate up
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isWallAbove(int x, int y) {
        return y > 0 && map[x][y - 1] == WALL; // y > 0 no exception
    }

    /**
     * Checks if there is a wall one coordinate below
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isWallBelow(int x, int y) {
        return y < map[x].length - 1 && map[x][y + 1] == WALL; // y < map[x].length - 1 no exception
    }

    /**
     * Reads and splits the coordinates in the .properties file
     *
     * @param file (.properties file loaded)
     */

    public void loadMap(FileHandle file) {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader(file.file()));

            // searches for highest number in file, so that it can set correct boundaries
            int maxx = 0;
            int Maxy = 0;
            for (String key : properties.stringPropertyNames()) {
                String[] parts = key.split(",");

                int x = Integer.parseInt(parts[0]); // x coordinate
                int y = Integer.parseInt(parts[1]); // y coordinate

                if (x > maxx) {
                    maxx = x;
                }
                if (y > Maxy) {
                    Maxy = y;
                }
            }

            map = new int[maxx + 1][Maxy + 1]; // +1 because arrays are zero

            // initialize map with default floor texture
            for (int i = 0; i < map.length; i++) {
                for (int j = 0; j < map[i].length; j++) {
                    map[i][j] = FLOOR;
                }
            }

            // coordinates equal to object
            for (String key : properties.stringPropertyNames()) {
                String[] parts = key.split(",");

                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                int value = Integer.parseInt(properties.getProperty(key));

                map[x][y] = value;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Searches for and returns the coordinates of the entry
     *
     * @return
     */

    public Point findEntryPoint() {
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                if (map[x][y] == ENTRY) {
                    return new Point(x, y);

                }
            }
        }
        return null;
    }

    /**
     * Sets a 50% probability to add a new heart coordinate
     * on the map and places it there
     */
    public void placeHeartRandomly() {
        if (!heartPlaced && MathUtils.randomBoolean(0.5f)) { // 50% probability
            int x, y;
            do {
                x = random(map.length - 1);
                y = random(map[0].length - 1);

            } while (map[x][y] != FLOOR); // Ensure it's placed on a floor tile

            map[x][y] = HEART;
            heartPlaced = true;
        }
    }

    /**
     * Sets a 15% probability to add a new lemon coordinate
     * on the map and places it there
     */
    public void placeLemonRandomly() {
        if (!lemonPlaced && MathUtils.randomBoolean(0.10f)) { // 15% probability
            int x, y;
            do {
                x = random(map.length - 1);
                y = random(map[0].length - 1);

            } while (map[x][y] != FLOOR); // Ensure it's placed on a floor tile

            map[x][y] = LEMON;
            lemonPlaced = true;
//            System.out.println("lemon placed");
        }
    }


    /**
     * Retrieves the tile based on the position of the character
     *
     * @param x x position of character
     * @param y y position of character
     * @return
     */

    public int getTile(int x, int y) {
        if (x >= 0 && x < map.length && y >= 0 && y < map[x].length) {
            return map[x][y];
        }
        return -1;
    }

    /**
     * Removes the key texture from the map and replaces it with floor texture
     */
    public void removeKey(int x, int y) {
        if (map[x][y] == KEY) {
            keySound.play();
            map[x][y] = FLOOR;
        }
    }

    /**
     * Removes the heart texture from the map and replaces it with floor texture
     */
    public void removeHeart(int x, int y) {
        if (map[x][y] == HEART) {
            heartPickup.play();
            map[x][y] = FLOOR;
        }
    }
    public void removeEnemy(int x, int y){
        setTile(x, y, MapClass.FLOOR);
    }

    public void setTile(int x, int y, int tileType) {
            map[x][y] = tileType;
    }

    /**
     * Removes the exit texture from the map and replaces it with floor texture
     */
    public void removeExit(int x, int y) {
        if (map[x][y] == MapClass.EXIT) {
            explosion.play();
            map[x][y] = MapClass.FLOOR;
        }
    }

    /**
     * Removes the lemon texture from the map and replaces it with floor texture
     */
    public void removeLemon(int x, int y) {
        if (map[x][y] == MapClass.LEMON) {
            lemonSound.play();
            map[x][y] = MapClass.FLOOR;
        }
    }

    /**
     * Moves the enemies in the map based on their current position.
     */
    public void moveEnemies() {

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMoveTime < moveInterval) {
            return; // not enough time has passed since the last move
        }
        lastMoveTime = currentTime;

        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                if (map[x][y] == ENEMY) {
                    // Randomly choose a direction to move: 0-up, 1-down, 2-left, 3-right

                    int direction = random(0, 3);

                    int newX = x;
                    int newY = y;

                    switch (direction) {
                        case 0:
                            newY++;
                            break; // up
                        case 1:
                            newY--;
                            break; // down
                        case 2:
                            newX--;
                            break; // left
                        case 3:
                            newX++;
                            break; // right
                    }

                    // Check if the new position is within bounds and not a wall or another enemy
                    if (isWithinBounds(newX, newY) && map[newX][newY] == FLOOR) {
                        map[x][y] = FLOOR;
                        map[newX][newY] = ENEMY;
                        currentEnemyAnimation = getEnemyAnimationByDirection(direction);

                    }
                }
            }
        }
    }


    private Animation<TextureRegion> getEnemyAnimationByDirection(int direction) {
        return switch (direction) {
            case 0 -> enemyAnimationUp;
            case 1 -> enemyAnimationDown;
            case 2 -> enemyAnimationLeft;
            case 3 -> enemyAnimationRight;
            default -> enemyAnimationDown;
        };
    }

    /**
     * Calculates the coordinates based off the position of the character
     *
     * @param newX used for x character position
     * @param newY used for y character position
     * @return whether the coordinate is a wall or an exit
     */

    public boolean isWall(float newX, float newY) {

        int leftX = (int) ((newX + 6) / 64);
        int rightX = (int) ((newX + 50) / 64);
        int topY = (int) ((newY + 20) / 64);
        int bottomY = (int) ((newY + 40) / 64);

        // check all corners of the character for collision
        return getTile(leftX, topY) == WALL ||
                getTile(rightX, topY) == WALL ||
                getTile(leftX, bottomY) == WALL ||
                getTile(rightX, bottomY) == WALL || getTile(leftX, topY) == EXIT ||
                getTile(rightX, topY) == EXIT ||
                getTile(leftX, bottomY) == EXIT ||
                getTile(rightX, bottomY) == EXIT;

    }

    /**
     * Checks if the given coordinates are within the bounds of the map.
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < map.length && y >= 0 && y < map[x].length;
    }


    /**
     * Calculates coordinates based on character position
     * and returns true if the coordinates correspond to a trap
     *
     * @param x
     * @param y
     * @return
     */

    public boolean isTrap(float x, float y) {
        int tileX = (int) (x / 64);
        int tileY = (int) (y / 64);
        return getTile(tileX, tileY) == TRAP;
    }

    /**
     * Calculates coordinates based on character position
     * and returns true if the coordinates correspond to an enemy
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isEnemy(float x, float y) {
        int tileX = (int) ((x ) / 64);
        int tileY = (int) ((y ) / 64);
        return getTile(tileX, tileY) == ENEMY;
    }

    //    public void loadEnemyAnimation() {
//        int frameWidth = 16;
//        int frameHeight = 16;
//        int animationFrames = 3;
//
//        // libGDX internal Array instead of ArrayList because of performance
//        Array<TextureRegion> walkFrames = new Array<>(TextureRegion.class);
//
//        // Add all frames to the animation
//        enemyAnimationDown = createEnemyAnimation(enemySheet, 4, frameWidth, frameHeight, animationFrames);
//        enemyAnimationLeft = createEnemyAnimation(enemySheet, 5, frameWidth, frameHeight, animationFrames);
//        enemyAnimationRight = createEnemyAnimation(enemySheet, 6, frameWidth, frameHeight, animationFrames);
//        enemyAnimationUp = createEnemyAnimation(enemySheet, 7, frameWidth, frameHeight, animationFrames);
//
//    }
//    public Animation<TextureRegion> createEnemyAnimation(Texture sheet, int row, int frameWidth, int frameHeight, int frames) {
//
//        //   Texture enemySheet = new Texture(Gdx.files.internal("mobs.png"));
//        Array<TextureRegion> walkFrames = new Array<>(TextureRegion.class);
//        for (int col = 0; col < frames; col++) {
//            walkFrames.add(new TextureRegion(enemySheet, col * frameWidth, row * frameHeight, frameWidth, frameHeight));
//        }
//        return new Animation<>(3f, walkFrames);
//    }


    public void dispose() {
        if (tiles != null) {
            tiles.dispose();
        }
        if (key != null) {
            key.dispose();
        }
        if (mobs != null) {
            mobs.dispose();
        }
        if (floor != null) {
            floor.dispose();
        }
        if (trapSheet != null) {
            trapSheet.dispose();
        }
        if (keySound != null) {
            keySound.dispose();
        }
        if (heartPickup != null) {
            heartPickup.dispose();
        }
        if (enemySheet != null) {
            enemySheet.dispose();
        }
    }

    /**
     * Resets to initial state
     */

    public void resetSinusInput() {
        sinusInput = 0f;
    }

    /**
     * @param deltaTime time elapsed since last frame rendered
     */
    public void updateSinusInput(float deltaTime) {
        sinusInput += deltaTime;
    }

    /**
     * Loads a different trap texture on odd rows
     *
     * @param x
     * @param y
     * @return
     */
    public boolean useSecondTrapTexture(int x, int y) {
        return y % 2 == 1;
    }

    public int[][] getMap() {
        return map;
    }

    public Sound getKeySound() {
        return keySound;
    }
}




