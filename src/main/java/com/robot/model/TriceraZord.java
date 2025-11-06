package com.robot.model;

import javafx.scene.image.Image;

public class TriceraZord extends Zord {
    private static final Image TRICERA_IMAGE_IDLE = loadingImageSprite("/TriceraZord (Dino Charge)/TriceraZord.png");
    private Image triceraWalking;

    public TriceraZord() {
        super(TRICERA_IMAGE_IDLE, "Tricera Zord");
    }
}
