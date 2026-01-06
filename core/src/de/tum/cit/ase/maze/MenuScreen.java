package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import games.spooky.gdx.nativefilechooser.NativeFileChooserCallback;
import games.spooky.gdx.nativefilechooser.NativeFileChooserConfiguration;

import org.w3c.dom.Text;

/**
 * The MenuScreen class is responsible for displaying the main menu of the game.
 * It extends the LibGDX Screen class and sets up the UI components for the menu.
 */
public class MenuScreen implements Screen {
    private Character character;
    private GameScreen gameScreen;

    private MazeRunnerGame game;

    private MapClass map;
    private final Stage stage;
    private float delta;
    private Texture image;
    private OrthographicCamera camera;

    /**
     * Constructor for MenuScreen. Sets up the camera, viewport, stage, and UI elements.
     *
     * @param game The main game class, used to access global resources and methods.
     */

    public MenuScreen(MazeRunnerGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        image = new Texture("char.png");
        gameScreen = new GameScreen(game);
        map = new MapClass();
        character = new Character(64,128, map);


        var camera = new OrthographicCamera();
        camera.zoom = 1.5f; // Set camera zoom for a closer view

        Viewport viewport = new ScreenViewport(camera); // Create a viewport with the camera
        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI elements

        Table table = new Table(); // Create a table for layout
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage
        game.getBackgroundMusic().pause();
        game.getMenuMusic().play();

        // Add a label as a title
        table.add(new Label("Maze Game", game.getSkin(), "title")).padBottom(80).row();

        // Create and add a button to go to the game screen
        TextButton goToGameButton = new TextButton("New Game", game.getSkin());
        table.add(goToGameButton).width(300).row();
        goToGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.restartLevel(); // Change to the game screen when button is pressed
                game.getMenuMusic().pause();

            }
        });

        TextButton startNewGameButton = new TextButton("Load a map", game.getSkin());
        table.add(startNewGameButton).width(300).row();
        startNewGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                NativeFileChooserConfiguration conf = new NativeFileChooserConfiguration();
                conf.directory = Gdx.files.absolute(System.getProperty("user.home")); // sets the initial directory
//                conf.mimeFilter = "application/properties"; // sets the file type filter
                conf.title = "Select Map File"; // title of the file chooser window

                game.getNativeFileChooser().chooseFile(conf, new NativeFileChooserCallback() {
                    @Override
                    public void onFileChosen(FileHandle file) {
                        MapClass newMap = new MapClass();
                        Character character1 = new Character(64,128, newMap);
                        newMap.loadTextures();
                        newMap.loadMap(file);
                        String filename = file.toString();
                        String levelNumberStr = filename.replaceAll("[^0-9]", ""); // only digits remain
                        game.updateLevel(Integer.parseInt(levelNumberStr));
                        character1.setToEntryPoint(newMap);
                        gameScreen.updateMap(newMap);
                        gameScreen.updateCharacter(character1);
                        game.setScreen(gameScreen);
                        game.getMenuMusic().pause();

                    }

                    @Override
                    public void onCancellation() {

                    }

                    @Override
                    public void onError(Exception exception) {

                    }
                });
            }
        });

        TextButton exitTheGameButton = new TextButton("Exit", game.getSkin());
        table.add(exitTheGameButton).width(300).row();
        exitTheGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                Gdx.app.exit();

            }
        });


    }

    @Override
    public void render(float delta) {
        camera.update();
        game.getSpriteBatch().setProjectionMatrix(camera.combined);
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f)); // Update the stage
        game.getSpriteBatch().begin();
        game.getSpriteBatch().draw(image, 200, 128, 300, 300);
        game.getSpriteBatch().end();
        stage.draw(); // Draw the stage


    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true); // Update the stage viewport on resize
    }

    @Override
    public void dispose() {
        // Dispose of the stage when screen is disposed
        stage.dispose();
        map.dispose();
        gameScreen.dispose();
    }

    @Override
    public void show() {
        // Set the input processor so the stage can receive input events
        Gdx.input.setInputProcessor(stage);
    }


    // Th       e following methods are part of the Screen interface but are not used in this screen.
    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }
}
