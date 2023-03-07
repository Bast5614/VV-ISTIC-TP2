package terraria.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import terraria.game.TerrariaGame;
import terraria.game.actors.world.ParallaxBackground;

public class MainMenuScreen extends ScreenAdapter {

    private Stage stage;
    private Camera camera;
    private ImageButton playButton;
    private TerrariaGame game;
    private ImageButton exitButton;
    private ParallaxBackground parallaxBackground;
    private Array<Texture> MainScreenParallax;
    private Image logo;

    private Music menuMusic;
    private float rotationTimer = 1;
    private float rotation = 1;

    TextureRegion play;

    public static int TEXTURE_NUMBER_PARALLAX = 2;

    public MainMenuScreen(final TerrariaGame game){
        this.game = game;
        Gdx.graphics.setTitle("Project Terra");

        //Initialize the stage and camera
        stage = new Stage(new ScreenViewport());
        camera = (OrthographicCamera) stage.getViewport().getCamera();

        //Set custom cursor
        Gdx.graphics.setCursor(Gdx.graphics.newCursor(new Pixmap(Gdx.files.internal("cursor32.png")), 0, 0));

        //Initialize menu's buttons
        game.getAssetManager().load("background/play.png", Texture.class);
        game.getAssetManager().load("background/playPressed.png",Texture.class);
        game.getAssetManager().load("background/exit.png",Texture.class);
        game.getAssetManager().load("background/exitPressed.png",Texture.class);

        for(int i = 1; i < TEXTURE_NUMBER_PARALLAX + 1;i++){
            game.getAssetManager().load("background/img"+i+".png",Texture.class);
        }

        game.getAssetManager().finishLoading();



        play = new TextureRegion(game.getAssetManager().get("background/play.png", Texture.class));
        TextureRegion playPressed = new TextureRegion(game.getAssetManager().get("background/playPressed.png", Texture.class));

        playButton = new ImageButton( new TextureRegionDrawable(play), new TextureRegionDrawable(playPressed));
        playButton.setPosition(stage.getViewport().getScreenWidth()/2,stage.getViewport().getScreenHeight()/2, Align.center);
        playButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y)  {
                super.clicked(event, x, y);
                startGame();
                dispose();

            }
        });

        TextureRegion exit = new TextureRegion(game.getAssetManager().get("background/exit.png",Texture.class));
        TextureRegion exitPressed = new TextureRegion(game.getAssetManager().get("background/exitPressed.png",Texture.class));

        exitButton = new ImageButton( new TextureRegionDrawable(exit), new TextureRegionDrawable(exitPressed));
        exitButton.setPosition(stage.getViewport().getScreenWidth()/2,(stage.getViewport().getScreenHeight()/2)-play.getRegionHeight(), Align.center);
        exitButton.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y)  {
                super.clicked(event, x, y);
                dispose();
                game.dispose();
                Gdx.app.exit();
            }
        });

        //Set the menu's background
        MainScreenParallax = new Array<Texture>();
        for(int i = 1; i < 3;i++){
            MainScreenParallax.add(game.getAssetManager().get("background/img"+i+".png", Texture.class));
            MainScreenParallax.get(MainScreenParallax.size-1).setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
        }

        this.parallaxBackground = new ParallaxBackground(MainScreenParallax, true);
        parallaxBackground.setSize(stage.getViewport().getScreenWidth(),stage.getViewport().getScreenHeight());
        parallaxBackground.setSpeed(1);

        logo = new Image(new Texture("logo.png"));
        logo.setSize(logo.getWidth()*4, logo.getHeight()*4);
        logo.setPosition(stage.getViewport().getScreenWidth()/2+16*4,(stage.getViewport().getScreenHeight()/2)+play.getRegionHeight()+logo.getHeight()/2-16, Align.center);
        logo.setOrigin(Align.center);


        //Set the stage
        stage.addActor(parallaxBackground);
        stage.addActor(playButton);
        stage.addActor(exitButton);
        stage.addActor(logo);

        //Set the music loop
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/music/main_menu_song.mp3"));
        menuMusic.setLooping(true);
        menuMusic.play();
    }

    public void startGame(){
        game.setScreen(new LoadingScreen(game));
        menuMusic.stop();
    }

    /**
     * Called when the screen should render itself.
     * @param delta
     */
    @Override
    public void	render(float delta){
        stage.act(delta);
        stage.draw();

        logo.rotateBy(2*delta * rotation);
        rotationTimer += delta;
        if (rotationTimer > 2){
            rotation = -1 * rotation;
            rotationTimer = 0;
        }

        this.parallaxBackground.update((OrthographicCamera) camera, stage);
    }

    /**
     * Called when this screen should release all resources.
     */
    @Override
    public void dispose(){
        stage.dispose();
        menuMusic.dispose();
    }

    /**
     * Called when this screen is no longer the current screen for a Game.
     */
    @Override
    public void hide(){}


    @Override
    public void	pause(){}


    @Override
    public void	resize(int width, int height){
        stage.getViewport().update(width,height,true );
        parallaxBackground.setSize(stage.getViewport().getWorldWidth(),stage.getViewport().getWorldHeight());
    }


    @Override
    public void	resume(){}

    /**
     * Called when this screen becomes the current screen for a Game.
     */
    @Override
    public void	show(){
        Gdx.input.setInputProcessor(stage);
        playButton.setPosition(stage.getViewport().getScreenWidth()/2,stage.getViewport().getScreenHeight()/2, Align.center);
        exitButton.setPosition(stage.getViewport().getScreenWidth()/2,stage.getViewport().getScreenHeight()/2-play.getRegionHeight()-10, Align.center);
    }



}
