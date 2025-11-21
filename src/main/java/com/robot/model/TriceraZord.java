package com.robot.model;

import javafx.scene.image.Image;
import static com.robot.enums.ZordFunction.*;
import static com.robot.utils.FileFuction.loadingImageSprite;

public class TriceraZord extends Zord {
    private static final Image TRICERA_IMAGE_IDLE = loadingImageSprite("/TriceraZord (Dino Charge)/TriceraIdle.png");
    private static final Image TRICERA_IMAGE_WALKING = loadingImageSprite("/TriceraZord (Dino Charge)/TriceraWalking.png");
    private static final Image TRICERA_IMAGE_MINING = loadingImageSprite("/TriceraZord (Dino Charge)/TriceraMining.png");

    public TriceraZord() {
        super(TRICERA_IMAGE_IDLE, TRICERA_IMAGE_WALKING, TRICERA_IMAGE_MINING, "Tricera Zord", MINER);
    }
}
