package com.vidhur2k.SpaceInvaders.control;

import com.almasb.ents.AbstractControl;
import com.almasb.ents.Entity;
import com.almasb.fxgl.app.FXGL;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.time.LocalTimer;
import com.vidhur2k.SpaceInvaders.EntityFactory;
import javafx.util.Duration;

import static com.vidhur2k.SpaceInvaders.Config.*;

/**
 * Created by vidhur2k on 4/19/17.
 */
public class PlayerControl extends AbstractControl {

    private PhysicsComponent physicsComponent;

    @Override
    public void onAdded(Entity entity) {

        physicsComponent = Entities.getPhysics(entity);
    }

    @Override
    public void onUpdate(Entity entity, double v) {

    }

    public void moveLeft()
    {
        physicsComponent.setLinearVelocity(-5 * PLAYER_SPEED , 0);
    }

    public void moveRight()
    {
        physicsComponent.setLinearVelocity(5 * PLAYER_SPEED, 0);
    }

    public void stop()
    {
        physicsComponent.setLinearVelocity(0, 0);
    }

    private boolean canShoot = true;
    private LocalTimer localTimer = FXGL.newLocalTimer();

    // Executes the firing of a bullet.
    public void shoot() {

        if(canShoot)
        {
            EntityFactory.makePlayerBullet();
            canShoot = false;
            localTimer.capture();
        }

        else if(localTimer.elapsed(Duration.seconds(0.5)))
                canShoot = true;
    }
}
