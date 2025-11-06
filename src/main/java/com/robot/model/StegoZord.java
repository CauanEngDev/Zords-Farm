package com.robot.model;

import javafx.scene.image.Image;

public class StegoZord extends Zord {
    private static final Image STEGO_IMAGE_IDLE = loadingImageSprite("/StegoZord/StegoZord.png");
    private Image stegoImageWalking;

    public StegoZord() {
        super(STEGO_IMAGE_IDLE, "Stego Zord");
    }
}
