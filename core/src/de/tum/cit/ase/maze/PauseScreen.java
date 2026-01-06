package de.tum.cit.ase.maze;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import games.spooky.gdx.nativefilechooser.NativeFileChooserCallback;
import games.spooky.gdx.nativefilechooser.NativeFileChooserConfiguration;
import org.w3c.dom.Text;

import static com.badlogic.gdx.graphics.g3d.particles.ParticleChannels.TextureRegion;

public class PauseScreen implements Screen {
    private MapClass map;
    private GameScreen gameScreen;
    private Stage stage;
    private final Screen oldScreen;
    private Skin skin;
    private boolean isPaused = false;
    private TextureRegionDrawable pause;
    private TextureRegionDrawable empty;
    private TextureRegionDrawable exit;



    /**
     * Constructor for PauseScreen. Sets up the camera, viewport, stage, and UI elements.
     *
     * @param game The main game class, used to access global resources and methods.
     */

    public PauseScreen(MazeRunnerGame game, Screen oldScreen) {
        gameScreen = new GameScreen(game);
        this.oldScreen = oldScreen;
//        skin = new Skin();
//        skin.add("play", new Texture("buttons/2.png"));
//        pause = new TextureRegionDrawable(new Texture("buttons/2.png"));
//        empty = new TextureRegionDrawable(new Texture("buttons/0.png"));
//        exit = new TextureRegionDrawable(new Texture("buttons/8.png"));

        var camera = new OrthographicCamera();
        camera.zoom = 1.5f; // Set camera zoom for a closer view

        Viewport viewport = new ScreenViewport(camera); // Create a viewport with the camera
        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI elements

        Table table = new Table(); // Create a table for layout
        table.setFillParent(true); // Make the table fill the stage
        stage.addActor(table); // Add the table to the stage
        game.getMenuMusic().play();


        // Add a label as a title
        table.add(new Label("Game paused", game.getSkin(), "title")).padBottom(80).row();
        TextButton resumeButton = new TextButton("Resume", game.getSkin());


//        ImageButton resumeButton = new ImageButton(pause);
//        resumeButton.getImage().setScale(2f);

        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
//                Gdx.app.getApplicationListener().pause();
//                gameScreen.setPaused(true);
                game.setScreen(oldScreen);
                game.getMenuMusic().pause();

            }
        });
        table.add(resumeButton).width(300).row();
//        ImageButton menuButton = new ImageButton(empty);
//        menuButton.getImage().setScale(2f);

        TextButton menuButton = new TextButton("New Map", game.getSkin());
        table.add(menuButton).width(300).row();
        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                NativeFileChooserConfiguration conf = new NativeFileChooserConfiguration();
                conf.directory = Gdx.files.absolute(System.getProperty("user.home")); // sets the initial directory
                conf.mimeFilter = "application/properties"; // sets the file type filter
                conf.title = "Select Map File"; // title of the file chooser window


                game.getNativeFileChooser().chooseFile(conf, new NativeFileChooserCallback() {
                    @Override
                    public void onFileChosen(FileHandle file) {
                        MapClass newMap = new MapClass();
                        Character character = new Character(64, 128, newMap);
                        newMap.loadTextures();
                        newMap.loadMap(file);
                        String filename = file.toString();
                        String levelNumberStr = filename.replaceAll("[^0-9]", ""); // only digits remain
                        game.updateLevel(Integer.parseInt(levelNumberStr));
                        character.setToEntryPoint(newMap);
                        gameScreen.updateMap(newMap);
                        gameScreen.updateCharacter(character);
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
//        ImageButton exitTheGameButton = new ImageButton(exit);
//        exitTheGameButton.getImage().setScale(2f);


        table.add(exitTheGameButton).width(300).row();
        exitTheGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
                Gdx.app.exit();
            }
        });


    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear the screen
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f)); // Update the stage
        stage.draw(); // Draw the stage
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();

    }

    public Screen getOldScreen() {
        return oldScreen;
    }
}
