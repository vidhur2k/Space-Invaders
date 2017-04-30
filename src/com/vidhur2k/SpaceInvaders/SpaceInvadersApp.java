package com.vidhur2k.SpaceInvaders;

import com.almasb.ents.Entity;
import com.almasb.fxgl.app.ApplicationMode;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.RenderLayer;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.gameplay.Level;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.parser.TextLevelParser;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.time.LocalTimer;
import com.vidhur2k.SpaceInvaders.component.HPComponent;
import com.vidhur2k.SpaceInvaders.control.PlayerControl;
import com.vidhur2k.SpaceInvaders.type.EntityType;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.jbox2d.dynamics.BodyType;


import java.util.List;
import java.util.stream.Collectors;

import static com.vidhur2k.SpaceInvaders.Config.*;
/**
 * Created by vidhur2k on 4/19/17.
 */
public class SpaceInvadersApp extends GameApplication {

    // author: Vidhur Kumar (vidhur2k@gmail.com).

    private PlayerControl playerControl;
    public static IntegerProperty score;    // Player's score.
    public static IntegerProperty lives;    // Player's lives.
    private LocalTimer enemyDirectionTimer; // Timer to switch the enemy ships' direction.



    // Dynamically obtains the player ship.
    public static GameEntity playerShip()
    {
        return (GameEntity) FXGL
                .getApp()
                .getGameWorld()
                .getEntitiesByType(EntityType.PLAYER)
                .get(0);
    }

    // Dynamically obtains the List of enemy ships.
    public static List<GameEntity> enemyShips()
    {
        return FXGL
                .getApp()
                .getGameWorld()
                .getEntitiesByType(EntityType.ENEMY)
                .stream()
                .map(e -> (GameEntity) e)
                .collect(Collectors.toList());
    }

    // Dynamically obtains the List of walls in the game.
    public static List<GameEntity> walls()
    {
        return FXGL
                .getApp()
                .getGameWorld()
                .getEntitiesByType(EntityType.WALL)
                .stream()
                .map(e -> (GameEntity) e)
                .collect(Collectors.toList());
    }


    @Override
    protected void initSettings(GameSettings gameSettings) {

        gameSettings.setApplicationMode(ApplicationMode.DEVELOPER);
        gameSettings.setHeight(BLOCK_SIZE * MAP_SIZE);
        gameSettings.setWidth(BLOCK_SIZE * MAP_SIZE + UI_SIZE);
//        gameSettings.setFullScreen(true);
        gameSettings.setMenuEnabled(false);
        gameSettings.setIntroEnabled(false);
        gameSettings.setProfilingEnabled(false);
        gameSettings.setTitle("Space Invaders");
        gameSettings.setVersion("0.1");

    }

    @Override
    protected void initInput() {

        Input input = getInput();

        // Input to move the player left.
        input.addAction(new UserAction("PLAYER LEFT") {
            @Override
            protected void onActionBegin() {

                playerControl.moveLeft();
            }

            @Override
            protected void onActionEnd() {

                playerControl.stop();
            }
        }, LEFT_KEY);

        // Input to move the player right.
        input.addAction(new UserAction("PLAYER RIGHT") {
            @Override
            protected void onActionBegin() {

                playerControl.moveRight();
            }

            @Override
            protected void onActionEnd() {

                playerControl.stop();
            }
        }, RIGHT_KEY);

        // Input to make the player shoot.
        input.addAction(new UserAction("SHOOT") {
            @Override
            protected void onActionBegin() {

                playerControl.shoot();

            }
        }, FIRE_KEY);
    }

    @Override
    protected void initAssets() {

        getAssetLoader().cache();
    }


    @Override
    protected void initGame() {

        // Initializes the integer properties bound to the game scene.
        score = new SimpleIntegerProperty();
        lives = new SimpleIntegerProperty(3);

        // Initializes a timer to periodically swith the enemy's direction.
        enemyDirectionTimer = FXGL.newLocalTimer();
        enemyDirectionTimer.capture();

        TextLevelParser parser = new TextLevelParser(); // Parse levels through a .txt file.
        parser.setEmptyChar(' ');

        // Assigns each character in the .txt file to a specific GameEntity.
        parser.addEntityProducer('W', EntityFactory::makeWall);
        parser.addEntityProducer('B', EntityFactory::makeBoundingWall);
        parser.addEntityProducer('P', EntityFactory::makePlayerShip);
        parser.addEntityProducer('E', EntityFactory::makeEnemyShip);

        Level level = parser.parse("level0.txt");
        getGameWorld().setLevel(level);
//        getGameWorld().addEntity(Entities.makeScreenBounds(50));

        initBackground();

        // Physics for the bottom bound.
        PhysicsComponent bottomBoundPhysics = new PhysicsComponent();
        bottomBoundPhysics.setBodyType(BodyType.STATIC);

        // Bottom bound to prevent the player ship from being pushed down and handles a bullet collision appropriately.
        Entity bottomBaound = Entities
                .builder()
                .at(0, getHeight())
                .bbox(new HitBox("BODY", BoundingShape.box(getWidth(), 100)))
                .type(EntityType.BOTTOM_BOUND)
                .with(bottomBoundPhysics)
                .with(new CollidableComponent(true))
                .buildAndAttach(getGameWorld());

        // Physics for the top bound.
        PhysicsComponent topBoundPhysics = new PhysicsComponent();
        topBoundPhysics.setBodyType(BodyType.STATIC);

        // Top bound to prevent player bullets that misses enemies from remaining in the game world.
        Entity topBound = Entities
                .builder()
                .at(0, -100)
                .type(EntityType.TOP_BOUND)
                .with(topBoundPhysics)
                .with(new CollidableComponent(true))
                .buildAndAttach(getGameWorld());
    }

    @Override
    protected void initPhysics() {

        getPhysicsWorld().setGravity(0, 0);

        // Handles a collision between the bottom bound and an enemy bullet.
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.ENEMY_BULLET, EntityType.BOTTOM_BOUND) {
            @Override
            protected void onCollisionBegin(Entity enemyBullet, Entity bottomBound) {

                enemyBullet.removeFromWorld();  // Removes the enemy bullet from the game world.

            }
        });

        // Handles a collision between a player bullet and an enemy bullet.
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER_BULLET, EntityType.ENEMY_BULLET) {
            @Override
            protected void onCollisionBegin(Entity playerBullet, Entity enemyBullet) {

                playerBullet.removeFromWorld();
                enemyBullet.removeFromWorld();
            }
        });

        // Handles a collision between an enemy and an player bullet.
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.ENEMY, EntityType.PLAYER_BULLET) {
            @Override
            protected void onCollisionBegin(Entity enemy, Entity bullet) {

                enemy.removeFromWorld();
                bullet.removeFromWorld();

                score.set(score.get() + 10);

            }
        });

        // Handles a collision between a player bullet and an enemy bullet.
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.PLAYER, EntityType.ENEMY_BULLET) {
            @Override
            protected void onCollisionBegin(Entity player, Entity enemyBullet) {

                enemyBullet.removeFromWorld();
                lives.set(lives.get() - 1);

                if(lives.get() == 0)
                    gameOver();
            }
        });

        // Handles a collision between a wall and a player bullet.
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.WALL, EntityType.PLAYER_BULLET) {
            @Override
            protected void onCollisionBegin(Entity wall, Entity playerBullet) {

                playerBullet.removeFromWorld();

                HPComponent wallHP = wall.getComponentUnsafe(HPComponent.class);

                wallHP.setValue(wallHP.getValue() - 1);

                if(wallHP.getValue() == 0) wall.removeFromWorld();

            }
        });

        // Handles a collision between a wall and an enemy bullet.
        getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.WALL, EntityType.ENEMY_BULLET) {
            @Override
            protected void onCollisionBegin(Entity wall, Entity enemyBullet) {

                enemyBullet.removeFromWorld();

                HPComponent wallHP = wall.getComponentUnsafe(HPComponent.class);

                wallHP.setValue(wallHP.getValue() - 1);

                if(wallHP.getValue() == 0) wall.removeFromWorld();

            }
        });

    }

    // Gives the player an option to play a new game or exit out of it.
    private void gameOver() {

        getDisplay().showConfirmationBox("GAME OVER! Play again?", yes -> {

            if(yes)
                startNewGame();

            else
                exit();

        });

    }

    // Initializes the game background.
    public void initBackground()
    {
        GameEntity background = Entities
                .builder()
                .type(EntityType.BACKGROUND)
                .viewFromTexture("background.jpg")
//                .viewFromNode(new Rectangle(getWidth(), getHeight(), Color.BLACK))
                .buildAndAttach(getGameWorld());

        background.setRenderLayer(RenderLayer.BACKGROUND);
    }

    @Override
    protected void initUI() {

        // The first line of the game title.
        Text gametitle1 = new Text("SPACE");

        gametitle1.setTranslateX(BLOCK_SIZE * (MAP_SIZE + 2));
        gametitle1.setTranslateY(24);
        gametitle1.setFont(Font.font(24));
        gametitle1.setFill(Color.LIMEGREEN);

        // The second line of the game title.
        Text gametitle2 = new Text("INVADERS");

        gametitle2.setTranslateX(BLOCK_SIZE * (MAP_SIZE + 1.5));
        gametitle2.setTranslateY(48);
        gametitle2.setFont(Font.font(24));
        gametitle2.setFill(Color.LIMEGREEN);

        // Text display for the score.
        Text scoreText = new Text();

        scoreText.setTranslateX(BLOCK_SIZE * (MAP_SIZE + 1));
        scoreText.setTranslateY(80);
        scoreText.setFont(Font.font(18));
        scoreText.setFill(Color.AQUAMARINE);
        scoreText.textProperty().bind(score.asString("Score: %d")); // Binds the score to the text display.

        // Text display for the number of lives.
        Text livesText = new Text();

        livesText.setTranslateX(BLOCK_SIZE * (MAP_SIZE + 1));
        livesText.setTranslateY(108);
        livesText.setFont(Font.font(18));
        livesText.setFill(Color.AQUAMARINE);
        livesText.textProperty().bind(lives.asString("Lives: %d")); // Binds the # lives to the text display.

        // Adds all the aforementioned nodes to the game scene.
        getGameScene().addUINode(gametitle1);
        getGameScene().addUINode(gametitle2);
        getGameScene().addUINode(scoreText);
        getGameScene().addUINode(livesText);
    }

    /**
     * Switches the direction of the enemy ships periodically.
     */
    private void switchEnemyShipsDirection()
    {
        enemyShips()
                .stream()
                .map(e -> e.getComponentUnsafe(PhysicsComponent.class))
                .forEach(e -> e.setLinearVelocity(-1 * e.getVelocityX(), e.getVelocityY()));
    }


    @Override
    protected void onUpdate(double v) {

        // Initializes playerControl if it has not been.
        if(playerControl == null)
            playerControl = playerShip().getControlUnsafe(PlayerControl.class);

        // Switches the direction of the enemy ships every 1.5 seconds.
        if(enemyDirectionTimer.elapsed(Duration.millis(1000)))
        {
            switchEnemyShipsDirection();
            enemyDirectionTimer.capture();
        }

        if(enemyShips().size() == 0) gameOver();

    }


    public static void main(String[] args) {

        try {
            launch(args);
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
