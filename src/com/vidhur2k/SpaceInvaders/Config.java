package com.vidhur2k.SpaceInvaders;

import javafx.scene.input.KeyCode;

/**
 * Created by vidhur2k on 4/19/17.
 */
public class Config {

    // UI elements.
    public static final int BLOCK_SIZE = 40;
    public static final int MAP_SIZE = 15;
    public static final int UI_SIZE = 200;

    // Input elements.
    public static final KeyCode LEFT_KEY = KeyCode.A;
    public static final KeyCode RIGHT_KEY = KeyCode.D;
    public static final KeyCode FIRE_KEY = KeyCode.SPACE;

    // Player ship elements.
    public static final int PLAYER_SPEED = 30;
    public static final String PLAYER_TEXTURE = "player.png";

    // Enemy ship elements.
    public static final int ENEMY_SPEED = 20;
    public static final String[] ENEMY_TEXTURES = {"enemy.png", "enemy2.png"};

    // Bullet elements.
    public static final int BULLET_SIZE = BLOCK_SIZE / 10 + 1;
    public static final int BULLET_SPEED = 50;
}
