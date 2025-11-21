package com.robot.model;

import javafx.scene.image.Image;
import static com.robot.enums.ZordFunction.*;
import static com.robot.utils.FileFuction.loadingImageSprite;


public class RedMagicZord extends Zord {
    private static final Image RMZ_IMAGE_IDLE = loadingImageSprite("/RedMagicZord/RedMagicIdle.png");
    private static final Image RMZ_IMAGE_WALKING = loadingImageSprite("/RedMagicZord/RedMagicWalking.png");
    private static final Image RMZ_IMAGE_FIGHTING = loadingImageSprite("/RedMagicZord/RedMagicFighting.png");

    public RedMagicZord() {
        super(RMZ_IMAGE_IDLE, RMZ_IMAGE_WALKING, RMZ_IMAGE_FIGHTING, "Red Magic Zord", FIGHTER);
    }
}
