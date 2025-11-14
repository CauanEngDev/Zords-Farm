package com.robot.model;

import javafx.scene.image.Image;
import static com.robot.enums.ZordFunction.*;

public class StegoZord extends Zord {
    private static final Image STEGO_IMAGE_IDLE = loadingImageSprite("/StegoZord/StegoIdle.png");
    private static final Image STEGO_IMAGE_WALKING = loadingImageSprite("/StegoZord/StegoWalking.png/");
    private static final Image STEGO_IMAGE_WORKING = loadingImageSprite("/StegoZord/StegoBuilding.png/");

    public StegoZord() {
        super(STEGO_IMAGE_IDLE, STEGO_IMAGE_WALKING, STEGO_IMAGE_WORKING, "Stego Zord", BUILDER);
    }
}
