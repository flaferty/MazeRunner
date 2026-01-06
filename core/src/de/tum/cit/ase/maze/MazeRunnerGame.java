package de.tum.cit.ase.maze;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;

/**
 * The MazeRunnerGame class represents the core of the Maze Runner game.
 * It manages the screens and global resources like SpriteBatch and Skin.
 */
public class MazeRunnerGame extends Game {
    private MapClass map;

    private NativeFileChooser nativeFileChooser;
    // Screens
    private MenuScreen menuScreen;
    private de.tum.cit.ase.maze.GameScreen gameScreen;

    // Sprite Batch for rendering
    private SpriteBatch spriteBatch;

    private Character character;

    // UI Skin
    private Skin skin;
    private Texture walkSheet;
    private Texture heartSheet;
    private Texture enemySheet;

    // Character animation downwards
    private Animation<TextureRegion> characterDownAnimation;
    private Animation<TextureRegion> characterUpAnimation;
    private Animation<TextureRegion> characterLeftAnimation;
    private Animation<TextureRegion> characterRightAnimation;
    private Animation<TextureRegion> enemyStandingAnimation;
    private Animation<TextureRegion> enemyDownAnimation;
    private Animation<TextureRegion> enemyUpAnimation;
    private Animation<TextureRegion> enemyLeftAnimation;
    private Animation<TextureRegion> enemyRightAnimation;
    private Animation<TextureRegion> characterAttackDownAnimation;
    private Animation<TextureRegion> characterAttackUpAnimation;
    private Animation<TextureRegion> characterAttackLeftAnimation;
    private Animation<TextureRegion> characterAttackRightAnimation;
    private Animation<TextureRegion> heartAnimation;
    private Music backgroundMusic;
    private Music menuMusic;
    private Sound wsound;
    private int currentLevel = 1;
    private Sound levelUp;
    private Texture attackSheet;


    /**
     * Constructor for MazeRunnerGame.
     *
     * @param fileChooser The file chooser for the game, typically used in desktop environment.
     */
    public MazeRunnerGame(NativeFileChooser fileChooser) {
        super();
        this.nativeFileChooser = fileChooser;
        this.map = new MapClass();
        this.character = new Character(64, 128, map);
    }

    /**
     * Called when the game is created. Initializes the SpriteBatch and Skin.
     */
    @Override
    public void create() {
        spriteBatch = new SpriteBatch(); // Create SpriteBatch
        skin = new Skin(Gdx.files.internal("craft/craftacular-ui.json")); // Load UI skin
        walkSheet = new Texture(Gdx.files.internal("character.png"));
        attackSheet = new Texture(Gdx.files.internal("characteratt.png"));
        //   walkSheet = new Texture(Gdx.files.internal("cat1.png"));
        heartSheet = new Texture(Gdx.files.internal("objects.png"));
        enemySheet = new Texture(Gdx.files.internal("mobs.png"));


        this.loadCharacterAnimation(); // Load character animation
        this.loadAttackAnimations();
        this.loadHeartAnimation();
//        map.loadTrapAnimation();


//         Play some background music
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/b.mp3"));
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("music/menu.mp3"));
        wsound = Gdx.audio.newSound(Gdx.files.internal("sounds/win.mp3"));
        levelUp = Gdx.audio.newSound(Gdx.files.internal("sounds/Rise03.mp3"));


        goToMenu(); // Navigate to the menu screen
    }

    @Override
    public void render() {
        super.render();
    }

    /**
     * Switches to the menu screen.
     */
    public void goToMenu() {
        this.setScreen(new MenuScreen(this)); // Set the current screen to MenuScreen
        if (gameScreen != null) {
            gameScreen.dispose(); // Dispose the game screen if it exists
            gameScreen = null;
        }
        backgroundMusic.pause();
        menuMusic.play();
    }

    public void goToNextLevel() {
        if (currentLevel < 5) {
            currentLevel++;
            if (gameScreen != null) {
                gameScreen.dispose();
            }
            gameScreen = new GameScreen(this);
            loadLevel(currentLevel);
            int currentHealth = character.getLives();
            character.setLives(currentHealth);
            setScreen(gameScreen);

//        } else if (gameScreen != null) {
//            gameScreen.dispose();
        } else {
            showVictoryScreen();
        }
    }


    public void loadLevel(int level) {
        currentLevel = level;
        map.resetSinusInput();
        String levelMap = "maps/level-" + level + ".properties";
        map.loadMap(Gdx.files.internal(levelMap));
        character.setToEntryPoint(map);
    }

    /**
     * Switches Game Screen to a Victory Screen if the user has cleared all levels.
     */
    private void showVictoryScreen() {
        VictoryScreen victoryScreen = new VictoryScreen(this);
        setScreen(victoryScreen);
        backgroundMusic.pause();
        wsound.play();
    }


    /**
     * Switches Game Screen to a Game Over Screen if the user has lost all lives.
     */
    public void showGameOverScreen() {
        GameOverScreen gameOverScreen = new GameOverScreen(this);
        setScreen(gameOverScreen);
        backgroundMusic.pause();
    }

    public void updateLevel(int level) {
        currentLevel = level;
    }

    /**
     * Resets the game to the first level.
     */
    public void restartLevel() {
        backgroundMusic.play();
        currentLevel = 1;
        if (gameScreen == null) {
            gameScreen = new GameScreen(this);
        }
        loadLevel(currentLevel);
        setScreen(gameScreen);
    }

    /**
     * Loads the character animation from a png file (attack or walking).
     */
   public void loadAttackAnimations() {
        int frameWidth = 32;
        int frameHeight = 32;
        int animationFrames = 4;
        characterAttackDownAnimation = createAnimation(attackSheet, 0, frameWidth, frameHeight, animationFrames);
        characterAttackUpAnimation = createAnimation(attackSheet, 1, frameWidth, frameHeight, animationFrames);
        characterAttackRightAnimation = createAnimation(attackSheet, 2, frameWidth, frameHeight, animationFrames);
        characterAttackLeftAnimation = createAnimation(attackSheet, 3, frameWidth, frameHeight, animationFrames);
    }

    public void loadCharacterAnimation() {
        int frameWidth = 16;
        int frameHeight = 32;
        int animationFrames = 4;

        // libGDX internal Array instead of ArrayList because of performance
        Array<TextureRegion> walkFrames = new Array<>(TextureRegion.class);

        // Add all frames to the animation
        characterDownAnimation = createAnimation(walkSheet, 0, frameWidth, frameHeight, animationFrames);
        characterLeftAnimation = createAnimation(walkSheet, 3, frameWidth, frameHeight, animationFrames);
        characterRightAnimation = createAnimation(walkSheet, 1, frameWidth, frameHeight, animationFrames);
        characterUpAnimation = createAnimation(walkSheet, 2, frameWidth, frameHeight, animationFrames);

    }


    public Animation<TextureRegion> createAnimation(Texture sheet, int row, int frameWidth, int frameHeight, int frames) {
        Array<TextureRegion> walkFrames = new Array<>(TextureRegion.class);
        for (int col = 0; col < frames; col++) {
            walkFrames.add(new TextureRegion(sheet, col * frameWidth, row * frameHeight, frameWidth, frameHeight));
        }
        return new Animation<>(0.1f, walkFrames);
    }

    public Animation<TextureRegion> loadHeartAnimation() {
        int frameWidth = 16;
        int frameHeight = 16;
        int animationFrames = 4;

        Array<TextureRegion> heartFrames = new Array<>(TextureRegion.class);

        for (int col = 0; col < animationFrames; col++) {
            heartFrames.add(new TextureRegion(heartSheet, col * frameWidth, 48, frameWidth, frameHeight));
        }
        return new Animation<>(0.2f, heartFrames);
    }

    /**
     * Cleans up resources when the game is disposed.
     */
    @Override
    public void dispose() {
        getScreen().hide(); // Hide the current screen
        getScreen().dispose(); // Dispose the current screen
        gameScreen.dispose();
        spriteBatch.dispose(); // Dispose the spriteBatch
        skin.dispose(); // Dispose the skin
        heartSheet.dispose();
        walkSheet.dispose();
        enemySheet.dispose();
        wsound.dispose();
        backgroundMusic.dispose();
        menuMusic.dispose();
        levelUp.dispose();
    }


    // Getter methods
    public Skin getSkin() {
        return skin;
    }


    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }
    public Animation<TextureRegion> getCharacterDownAnimation() {
        return characterDownAnimation;
    }
    public Animation<TextureRegion> getCharacterUpAnimation() {
        return characterUpAnimation;
    }

    public Animation<TextureRegion> getCharacterLeftAnimation() {
        return characterLeftAnimation;
    }

    public Animation<TextureRegion> getCharacterRightAnimation() {
        return characterRightAnimation;
    }

    public Animation<TextureRegion> getCharacterAttackDownAnimation() {
        return characterAttackDownAnimation;
    }

    public Animation<TextureRegion> getCharacterAttackUpAnimation() {
        return characterAttackUpAnimation;
    }

    public Animation<TextureRegion> getCharacterAttackLeftAnimation() {
        return characterAttackLeftAnimation;
    }

    public Animation<TextureRegion> getCharacterAttackRightAnimation() {
        return characterAttackRightAnimation;
    }

    public NativeFileChooser getNativeFileChooser() {
        return nativeFileChooser;
    }

    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public Music getBackgroundMusic() {
        return backgroundMusic;
    }

    public Music getMenuMusic() {
        return menuMusic;
    }

    public Sound getLevelUp() {
        return levelUp;
    }
}

