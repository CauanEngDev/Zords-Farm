package com.robot.model;

import com.robot.controller.SelectionManager;
import static com.robot.enums.AnimationState.*;
import com.robot.utils.SpriteAnimator;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import static com.robot.utils.FileFuction.loadingImageSprite;

public abstract class EdificeZord {
    protected final String name;
    protected int level = 1;
    protected boolean selected;

    protected final Group world;
    protected final SelectionManager selectionManager;

    protected final ImageView edificeImageView;
    protected final SpriteAnimator animator;

    public EdificeZord(Group world, SelectionManager selectionManager, String name) {
        this.name = name;
        this.world = world;
        this.selectionManager = selectionManager;

        Image edificeIdle = loadingImageSprite("/PteraZord/PteraIdle.png");
        Image edificeWork = loadingImageSprite("/PteraZord/PteraEnergy.png");

        this.edificeImageView = new ImageView(edificeIdle);

        this.edificeImageView.setScaleX(1);
        this.edificeImageView.setScaleY(1);
        this.edificeImageView.setPreserveRatio(true);
        this.edificeImageView.setPickOnBounds(false);

        this.animator = new SpriteAnimator(edificeImageView);

        this.animator.addAnimation(IDLE, edificeIdle);
        this.animator.addAnimation(WORKING, edificeWork);

        this.animator.play(IDLE);
    }
}
