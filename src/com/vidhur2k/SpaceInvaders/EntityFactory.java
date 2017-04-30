package com.vidhur2k.SpaceInvaders;

import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.vidhur2k.SpaceInvaders.component.HPComponent;
import com.vidhur2k.SpaceInvaders.component.OwnerComponent;
import com.vidhur2k.SpaceInvaders.control.Enemy_BulletControl;
import com.vidhur2k.SpaceInvaders.control.Player_BulletControl;
import com.vidhur2k.SpaceInvaders.control.EnemyControl;
import com.vidhur2k.SpaceInvaders.control.PlayerControl;
import com.vidhur2k.SpaceInvaders.type.EntityType;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import org.jbox2d.dynamics.BodyType;

import static com.vidhur2k.SpaceInvaders.Config.*;

/**
 * Created by vidhur2k on 4/19/17.
 */
public class EntityFactory {

    /**
     * Constructs a Player ship at the specified coordinates (TILE LOCATION).
     * @param x
     * @param y
     * @return
     */
    public static GameEntity makePlayerShip(double x, double y)
    {
        PhysicsComponent physicsComponent = new PhysicsComponent();
        physicsComponent.setBodyType(BodyType.DYNAMIC);

        return Entities
                .builder()
                .at(x * BLOCK_SIZE, y * BLOCK_SIZE)
                .bbox(new HitBox("BODY", BoundingShape.circle(BLOCK_SIZE / 2)))
                .type(EntityType.PLAYER)
                .viewFromTexture(PLAYER_TEXTURE)
//                .viewFromNode(new Circle(BLOCK_SIZE / 2, Color.AQUA))
                .with(physicsComponent)
                .with(new HPComponent(3))
                .with(new CollidableComponent(true))
                .with(new PlayerControl())
                .build();
    }

    /**
     * Constructs an Enemy ship at the specified coordinates (TILE LOCATION).
     * @param x
     * @param y
     * @return
     */
    public static GameEntity makeEnemyShip(double x, double y)
    {
        PhysicsComponent physicsComponent = new PhysicsComponent();
        physicsComponent.setBodyType(BodyType.DYNAMIC);

        return Entities
                .builder()
                .at(x * BLOCK_SIZE, y * BLOCK_SIZE)
                .bbox(new HitBox("BODY", BoundingShape.circle(BLOCK_SIZE / 2)))
                .type(EntityType.ENEMY)
                .viewFromTexture(ENEMY_TEXTURES[(int) (Math.random() * ENEMY_TEXTURES.length)])
//                .viewFromNode(new Circle(BLOCK_SIZE / 2, Color.WHITE))
                .with(new CollidableComponent(true))
                .with(physicsComponent)
                .with(new EnemyControl())
                .build();
    }

    /**
     * Makes a boundary wall at the specified coordinates (TILE LOCATION).
     * @param x
     * @param y
     * @return
     */
    public static GameEntity makeBoundingWall(double x, double y)
    {
        PhysicsComponent physicsComponent = new PhysicsComponent();
        physicsComponent.setBodyType(BodyType.STATIC);

        return Entities
                .builder()
                .at(x * BLOCK_SIZE, y * BLOCK_SIZE)
                .bbox(new HitBox("BODY", BoundingShape.box(BLOCK_SIZE, BLOCK_SIZE)))
                .type(EntityType.WALL)
                .viewFromNode(new Rectangle(BLOCK_SIZE, BLOCK_SIZE, Color.BROWN))
                .with(new CollidableComponent(true))
                .with(physicsComponent)
                .with(new HPComponent(Integer.MAX_VALUE))
                .build();
    }

    /**
     * Makes a wall at the specified coordinates (TILE LOCATION).
     * @param x
     * @param y
     * @return
     */
    public static GameEntity makeWall(double x, double y)
    {
        PhysicsComponent physicsComponent = new PhysicsComponent();
        physicsComponent.setBodyType(BodyType.STATIC);

        return Entities
                .builder()
                .at(x * BLOCK_SIZE, y * BLOCK_SIZE)
                .bbox(new HitBox("BODY", BoundingShape.box(BLOCK_SIZE, BLOCK_SIZE)))
                .type(EntityType.WALL)
                .viewFromNode(new Rectangle(BLOCK_SIZE, BLOCK_SIZE, Color.LIMEGREEN))
                .with(new CollidableComponent(true))
                .with(physicsComponent)
                .with(new HPComponent(25))
                .build();
    }

    // Dynamically obtains the player ship from the list of entities in the game world.
    private static GameEntity playerShip()
    {
        return (GameEntity) FXGL
                .getApp()
                .getGameWorld()
                .getEntitiesByType(EntityType.PLAYER)
                .get(0);
    }

    /**
     * Makes a player bullet at the specified coordinates.
     * The coordinates always match that of the top of the player ship.
     * @return
     */
    public static GameEntity makePlayerBullet()
    {
        Point2D position = playerShip().getCenter();

        PhysicsComponent physicsComponent = new PhysicsComponent();
        physicsComponent.setBodyType(BodyType.DYNAMIC);

        return Entities
                .builder()
                .at(position.getX() - BULLET_SIZE, position.getY() - BLOCK_SIZE / 2)
                .bbox(new HitBox("BODY", BoundingShape.circle(BULLET_SIZE)))
                .type(EntityType.PLAYER_BULLET)
                .viewFromNode(new Circle(BULLET_SIZE, Color.RED))
                .with(new CollidableComponent(true))
                .with(physicsComponent)
                .with(new Player_BulletControl())
                .with(new OwnerComponent(EntityType.PLAYER))
                .buildAndAttach(FXGL.getApp().getGameWorld());
    }

    /**
     * Makes an enemy bullet at the specified coordinates.
     * The coordinates always match that of the bottom of the enemy ship.
     * @param enemy
     * @return
     */
    public static GameEntity makeEnemyBullet(GameEntity enemy)
    {
        Point2D position = enemy.getCenter();

        PhysicsComponent physicsComponent = new PhysicsComponent();
        physicsComponent.setBodyType(BodyType.DYNAMIC);

        return Entities
                .builder()
                .at(position.getX() + BLOCK_SIZE / 2 - BULLET_SIZE, position.getY() + BLOCK_SIZE)
                .bbox(new HitBox("BODY", BoundingShape.circle(BULLET_SIZE)))
                .type(EntityType.ENEMY_BULLET)
                .viewFromNode(new Circle(BULLET_SIZE, Color.WHITE))
                .with(new CollidableComponent(true))
                .with(physicsComponent)
                .with(new Enemy_BulletControl())
                .with(new OwnerComponent(EntityType.ENEMY))
                .buildAndAttach(FXGL.getApp().getGameWorld());
    }

}
