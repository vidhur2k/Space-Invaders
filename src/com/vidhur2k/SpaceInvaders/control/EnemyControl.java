package com.vidhur2k.SpaceInvaders.control;

import com.almasb.ents.AbstractControl;
import com.almasb.ents.Entity;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.GameEntity;
import com.almasb.fxgl.entity.component.PositionComponent;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.time.LocalTimer;
import com.vidhur2k.SpaceInvaders.EntityFactory;
import com.vidhur2k.SpaceInvaders.type.EntityType;
import javafx.util.Duration;

import java.util.List;
import java.util.stream.Collectors;

import static com.vidhur2k.SpaceInvaders.Config.*;
import static com.vidhur2k.SpaceInvaders.SpaceInvadersApp.*;

/**
 * Created by vidhur2k on 4/20/17.
 */
public class EnemyControl extends AbstractControl {

    private LocalTimer enemyShootTimer; // Timer to ensure enemy can't shoot continuously.
    private PhysicsComponent physicsComponent;

    @Override
    public void onAdded(Entity entity) {

        // Initializes the timer to make sure the enemies do not shoot continuously.
        enemyShootTimer = FXGL.newLocalTimer();
        enemyShootTimer.capture();

        physicsComponent = Entities.getPhysics(entity);

        // Sets the horizontal velocity for the enemies.
        physicsComponent.setOnPhysicsInitialized(() -> physicsComponent.setLinearVelocity(5 * ENEMY_SPEED, 0));
    }

    @Override
    public void onUpdate(Entity entity, double v) {

        int time = (int) (Math.random() * 4000) + 1000; // Selects a random time between 1 to 4 seconds for the firing.

        // Fires a bullet if the elpased time is within the specified time frame.
        if(enemyShootTimer.elapsed(Duration.millis(time)))
        {
            EntityFactory.makeEnemyBullet((GameEntity) entity);
            enemyShootTimer.capture();
        }

    }
}
