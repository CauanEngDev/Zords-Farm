package com.robot.model;

import javafx.scene.image.Image;
import static com.robot.enums.ZordFunction.*;

public class TriceraZord extends Zord {
    private static final Image TRICERA_IMAGE_IDLE = loadingImageSprite("/TriceraZord (Dino Charge)/TriceraZord.png");
    private static final Image TRICERA_IMAGE_WALKING = loadingImageSprite("/TriceraZord (Dino Charge)/TriceraWalking.png");
    private Image triceraWalking;

    public TriceraZord() {
        super(TRICERA_IMAGE_IDLE, TRICERA_IMAGE_WALKING, "Tricera Zord", MINER);
    }
}
