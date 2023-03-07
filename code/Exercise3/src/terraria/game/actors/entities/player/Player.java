package terraria.game.actors.entities.player;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import terraria.game.TerrariaGame;
import terraria.game.actors.Inventory.Inventory;
import terraria.game.actors.entities.*;
import terraria.game.actors.world.CalculatorLight;
import terraria.game.actors.world.GameMap;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import terraria.game.actors.world.TileType;
import terraria.game.screens.GameScreen;
import terraria.game.screens.LoadingScreen;

public class Player extends Entity {

    private static final int WALK_SPEED = 150, RUN_SPEED = 200, JUMP_VELOCITY = 4, RANGE = 3 * TileType.TILE_SIZE, SPAWN_RADIUS = 128;
    private static final double FALLDAMAGE_COEFF = -0.005;

    private static final int textureWidth = 48, lateralOffset = -9;
    private static float itemRotation = 0, itemYOffset = 0;
    private final float ITEMFRAME_TIME = 0.075f;  //1f = 1sec
    private float itemFrameTimer = 0f;

    private static int speed;
    private static float fallDamage;

    public PlayerHealth playerHealth;
    private Inventory inventory;
    private boolean invulnerable = false;
    private final float INVULNERABILITY_TIME = 1f;  //1f = 1sec
    private float invulnerabilityTimer = 0f;
    public static int MAXHEALTH = 10;
    private Vector3 worldCoordinates = new Vector3(0,0,0);
    Boolean tooHigh = false;

    private Sound hurt, footstep_solid, footstep_grass;
    private float stepSoundTimer = 0f;
    private float stepSoundInterval = 0.35f;
    private static boolean movingHorizontally;

    private static int state = 0;
    private static final int IDLE = 0, JUMPING = 1, RUNNING = 2, HIT = 3, WALKING = 4;

    @Override
    public void create(EntitySnapshot snapshot, EntityType type, GameMap gameMap, TerrariaGame game) {
        super.create(snapshot, type, gameMap,game);
        TextureRegion[][] heart =  TextureRegion.split(game.getAssetManager().get("heart.png", Texture.class), 35, 35);
        this.playerHealth = new PlayerHealth(game, heart, snapshot.health);
        this.inventory = new Inventory(game);
        this.inventory.fillInventory(snapshot.inventory);
        init();

    }
    public void create(int posX, int posY, EntityType type, GameMap gameMap, TerrariaGame game) {
        super.create(posX,posY, type, gameMap,game);
        TextureRegion[][] heart =  TextureRegion.split(game.getAssetManager().get("heart.png", Texture.class), 35, 35);
        this.playerHealth = new PlayerHealth(game, heart, MAXHEALTH);
        this.inventory = new Inventory(game);
        init();
    }


    public void init(){
        footstep_grass = game.getAssetManager().get("audio/sound/player_footstep_grass.wav", Sound.class);
        footstep_solid = game.getAssetManager().get("audio/sound/player_footstep.wav", Sound.class);
        hurt = game.getAssetManager().get("audio/sound/player_hurt.wav", Sound.class);

        animations = new Array<>();
        for(int i = 0; i < LoadingScreen.TEXTURE_NUMBER_PLAYER ; i++){
            switch (i){
                case 0: animations.add(new Animation(new TextureRegion(game.getAssetManager().get("playerAnimation/player"+i+".png", Texture.class)),7 , 3F));break;
                case 1: animations.add(new Animation(new TextureRegion(game.getAssetManager().get("playerAnimation/player"+i+".png", Texture.class)),1 , 0.5F));break;
                case 2: animations.add(new Animation(new TextureRegion(game.getAssetManager().get("playerAnimation/player"+i+".png", Texture.class)),6 , 0.5F));break;
                case 3: animations.add(new Animation(new TextureRegion(game.getAssetManager().get("playerAnimation/player"+i+".png", Texture.class)),2 , 0.1F));break;
                case 4: animations.add(new Animation(new TextureRegion(game.getAssetManager().get("playerAnimation/player"+i+".png", Texture.class)),4 , 0.75F));break;
            }
        }


    }


    /**
     * Controle du joueur
     * @param deltaTime
     * @param gravity
     */
    @Override
    public void update(float deltaTime, float gravity, Camera camera, Stage stage) {
        //Handle the camera

        if(TerrariaGame.getState() == GameScreen.GAME_RUNNING) {
            //Quand on pointe a gauche de l'écran
            if (Gdx.input.getX() <= 25) {
                if (pos.x - camera.position.x  < 100) {
                    camera.position.set(camera.position.x - RUN_SPEED * deltaTime, pos.y+100, camera.position.z);
                } else  {
                    camera.position.set(pos.x - 100, pos.y+100, camera.position.z);
                }

            //Quand on pointe a droite de l'écran
            } else if (Gdx.input.getX() > gameMap.ScreenWidth - 25) {
                if (camera.position.x - pos.x  < 100) {
                    camera.position.set(camera.position.x + RUN_SPEED * deltaTime, pos.y+100, camera.position.z);
                } else {
                    camera.position.set(pos.x + 100, pos.y+100, camera.position.z);
                }

            //Quand on pointe en haut de l'écran
            } else if(Gdx.input.getY() <= 25) {
                if(camera.position.y - pos.y < 200) {
                    camera.position.set(pos.x, (camera.position.y + RUN_SPEED * deltaTime), camera.position.z);
                } else {
                    camera.position.set(pos.x, pos.y + 200, camera.position.z);
                }

            //Quand on pointe en bas de l'écran
            } else if(Gdx.input.getY() > gameMap.ScreenHeigth - 25) {
                if(pos.y - camera.position.y < 0) {
                    camera.position.set(pos.x, (camera.position.y - RUN_SPEED * deltaTime), camera.position.z);
                } else {
                    camera.position.set(pos.x, pos.y, camera.position.z);
                }

            } else {

                if (camera.position.x < pos.x - RUN_SPEED * deltaTime)  {
                    camera.position.set(camera.position.x + RUN_SPEED * deltaTime, pos.y+100, camera.position.z);

                } else if (camera.position.x > pos.x + RUN_SPEED * deltaTime){
                    camera.position.set(camera.position.x - RUN_SPEED * deltaTime, pos.y+100, 0);

                } else if (camera.position.y < pos.y +100 - RUN_SPEED * deltaTime) {
                    camera.position.set(pos.x, camera.position.y + RUN_SPEED * deltaTime, camera.position.z);

                } else if (camera.position.y > pos.y +100 + RUN_SPEED * deltaTime) {
                    camera.position.set(pos.x, camera.position.y - RUN_SPEED * deltaTime, camera.position.z);

                //position de base de la cam
                } else {
                    camera.position.set(pos.x , pos.y+100, 0);
                }

                //Si la cam sort du cadre
                if (camera.position.x - pos.x > 201 || pos.x - camera.position.x > 201 || pos.y - camera.position.y > 201 || camera.position.y - pos.y > 201 ){
                    camera.position.set(pos.x , pos.y+200, 0);
                }
            }
            camera.unproject(worldCoordinates);
        }


        //Handle the jump
        if ((Gdx.input.isKeyPressed(Keys.SPACE) || Gdx.input.isKeyPressed(Keys.Z)) && grounded && TerrariaGame.getState() == GameScreen.GAME_RUNNING) {
            this.velocityY += JUMP_VELOCITY * getWeight();
        }
        else if (Gdx.input.isKeyPressed(Keys.SPACE) || Gdx.input.isKeyPressed(Keys.Z) && !grounded && this.velocityY > 0 && TerrariaGame.getState() == GameScreen.GAME_RUNNING) {
            this.velocityY += JUMP_VELOCITY * getWeight() * deltaTime;
        }

        //Check if running
        if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) speed = RUN_SPEED;
        else speed = WALK_SPEED;

        //Check if falling from too high
        fallingCheck();

        super.update(deltaTime, gravity, camera, stage);   //Apply gravity

        //Handle the controls
        if (Gdx.input.isKeyPressed(Keys.Q) && TerrariaGame.getState() == GameScreen.GAME_RUNNING) {
            if (moveX(-speed * deltaTime)) {
                movingHorizontally = true;
                if (grounded && !invulnerable) playSteppingSound(deltaTime);
            } else {
                movingHorizontally = false;
            }
        }
        else if (Gdx.input.isKeyPressed(Keys.D) && TerrariaGame.getState() == GameScreen.GAME_RUNNING) {
            if (moveX(speed * deltaTime)) {
                movingHorizontally = true;
                if (grounded && !invulnerable) playSteppingSound(deltaTime);
            } else {
                movingHorizontally = false;
            }
        }
        else {
            movingHorizontally = false;
        }

        //Check the invulnerability frame
        if (invulnerable) {
            invulnerabilityTimer += deltaTime;
            if (invulnerabilityTimer > INVULNERABILITY_TIME) {
                invulnerabilityTimer = 0f;
                invulnerable = false;
            }
        }

        //Check the state of the character
        if (invulnerable) state = HIT;
        else if (!grounded) state = JUMPING;
        else if (Gdx.input.isKeyPressed(Keys.Q) || Gdx.input.isKeyPressed(Keys.D)) {
            if (speed == RUN_SPEED) state = RUNNING;
            else state = WALKING;
        }
        else state = IDLE;

        //Update health
        playerHealth.update(camera,stage);

        //Update light
        CalculatorLight.calculatorLightSource( (int)((pos.x +getWidth()/2)/ TileType.TILE_SIZE ), (int)(gameMap.getMapHeight() - ((pos.y + getHeight()/2)/ TileType.TILE_SIZE)), gameMap.getMap());

    }

    public void takeAhit(double damage) {
        if (!invulnerable) {
            playerHealth.ApplyDamage(damage);
            hurt.play();
            invulnerable = true;
        }
    }

    public void fallingCheck() {
        if (velocityY < -600) {
            tooHigh = true;
            fallDamage = velocityY;
        }
        if (isGrounded() && tooHigh) {
            takeAhit(fallDamage * FALLDAMAGE_COEFF);
            tooHigh = false;
        }
    }

    public void playSteppingSound(float deltaTime) {
        stepSoundTimer += deltaTime;
        if (stepSoundTimer > stepSoundInterval) {
            stepSoundTimer = 0f;
            TileType floor = gameMap.getTileTypeByLocation(1, pos.x, pos.y - TileType.TILE_SIZE/2);
            if (floor == TileType.GRASS || floor == TileType.DIRT || floor == TileType.MOSSY_STONE)
                footstep_grass.play();
            else
                footstep_solid.play();
        }
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public EntitySnapshot getSaveSnapshot() {
        EntitySnapshot snapshot = super.getSaveSnapshot();
        snapshot.health = playerHealth.health;
        snapshot.inventory = inventory.getItemsList();
        return snapshot;
    }

    @Override
    public void draw(Batch batch, float parentAlpha){
        TextureRegion texture;

        switch (state) {
            case JUMPING:
                texture = animations.get(1).getFrame();
                break;
            case RUNNING:
                texture = animations.get(2).getFrame();
                animations.get(2).update( Gdx.graphics.getDeltaTime());
                break;
            case HIT:
                texture = animations.get(3).getFrame();
                animations.get(3).update( Gdx.graphics.getDeltaTime());
                break;
            case WALKING:
                texture = animations.get(4).getFrame();
                animations.get(4).update( Gdx.graphics.getDeltaTime());
                break;
            default :   //state == IDLE
                texture = animations.get(0).getFrame();
                animations.get(0).update( Gdx.graphics.getDeltaTime());
                break;
        }

        batch.draw(texture, (flipX ? pos.x+textureWidth : pos.x)+lateralOffset, pos.y, flipX ? -textureWidth : textureWidth, getHeight());

        //draw item in hand
        if ( state == IDLE && TileType.getTileTypeById(inventory.getItemsList().get(inventory.getCurrentItems()).getIdTile()).isItem() ) {
            TextureRegion item = inventory.getGraphicItems().get(0).getItemTexture(inventory.getItemsList().get(inventory.getCurrentItems()).getIdTile());
            batch.draw(item, (flipX ? pos.x+30 : pos.x), pos.y+itemYOffset, 0, 0, flipX ? -50 : 50, 50, 1, 1, (flipX ? itemRotation : -itemRotation));
        }

        playerHealth.draw(batch,parentAlpha);
    }

    public void itemAnimationUpdate(float dt) {
        itemFrameTimer += dt;
        if (itemFrameTimer > ITEMFRAME_TIME) {
            itemRotation +=10;
            itemYOffset = 10;
            itemFrameTimer = 0;
        }
        if (itemRotation >= 40) {
            itemRotation = 0;
            itemYOffset = 0;
        }
    }

    public void itemAnimationReset(float dt) {
        if (itemRotation > 0) {
            itemFrameTimer += dt;
            if (itemFrameTimer > ITEMFRAME_TIME) {
                itemRotation +=10;
                itemYOffset = 10;
                itemFrameTimer = 0;
            }
            if (itemRotation >= 40) {
                itemRotation = 0;
                itemYOffset = 0;
            }
        }
    }

    public static int getRange() {
        return RANGE;
    }
    public static int getSpawnRadius() {
        return SPAWN_RADIUS;
    }
    public double getHealth() { return playerHealth.health; }
    public static boolean isMovingHorizontally() {
        return movingHorizontally;
    }
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

}
