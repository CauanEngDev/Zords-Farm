package com.robot.model;

import javafx.scene.image.Image;
import static com.robot.enums.ZordFunction.*;

public class StegoZord extends Zord {
    private static final Image STEGO_IMAGE_IDLE = loadingImageSprite("/StegoZord/StegoZord.png");
    private static final Image STEGO_IMAGE_WALKING = loadingImageSprite("/StegoZord/StegoWalking.png");
    private Image stegoImageWalking;

    public StegoZord() {
        super(STEGO_IMAGE_IDLE, STEGO_IMAGE_WALKING, "Stego Zord", BUILDER);
    }
}
