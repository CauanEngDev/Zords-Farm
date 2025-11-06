package com.robot.model;

import javafx.scene.image.Image;

public class RedMagicZord extends Zord {
    private static final Image RMZ_IMAGE_IDLE = loadingImageSprite("/RedMagicZord/RedMagicZord.png");
    private Image rmzImageWalking;

    public RedMagicZord() {
        super(RMZ_IMAGE_IDLE, "Red Magic Zord");
    }
}
