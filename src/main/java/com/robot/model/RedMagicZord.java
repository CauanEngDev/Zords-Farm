package com.robot.model;

import javafx.scene.image.Image;
import static com.robot.enums.ZordFunction.*;

public class RedMagicZord extends Zord {
    private static final Image RMZ_IMAGE_IDLE = loadingImageSprite("/RedMagicZord/RedMagicZord.png");
    private static final Image RMZ_IMAGE_wALKING = loadingImageSprite("/RedMagicZord/RMZWalking.png");
    private Image rmzImageWalking;

    public RedMagicZord() {
        super(RMZ_IMAGE_IDLE, RMZ_IMAGE_wALKING, "Red Magic Zord", FIGHTER);
    }
}
