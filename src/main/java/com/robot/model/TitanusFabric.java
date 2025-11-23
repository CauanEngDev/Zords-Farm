package com.robot.model;

import static com.robot.utils.FileFuction.*;
import com.robot.utils.SpriteAnimator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import static com.robot.enums.AnimationState.*;

public class TitanusFabric implements IAnimatable {
    private static final String name = "TitanusZord Fabric";
    private int titanusLevel = 1;
    public int numStegos = 3;

    private ImageView titanusImageView;
    private final SpriteAnimator animator;

    public TitanusFabric() {
        Image titanusIdle = loadingImageSprite("/TitanusFabric/TitanusIdle.png");
        this.titanusImageView = new ImageView(titanusIdle);
        Image titanusCreate = loadingImageSprite("/TitanusFabric/TitanusCreate.png");

        this.titanusImageView.setScaleX(1);
        this.titanusImageView.setScaleY(1);
        this.titanusImageView.setPreserveRatio(true);

        this.titanusImageView.setTranslateY(-30.0);

        this.animator = new SpriteAnimator(titanusImageView);

        this.animator.addAnimation(IDLE, titanusIdle);
        this.animator.addAnimation(WORKING, titanusCreate);

        this.animator.play(IDLE);
    }

    @Override
    public SpriteAnimator getAnimator() {
        return this.animator;
    }

    public void showIdleAnimation() {
        this.animator.play(IDLE);
    }

    public void showCreateAnimation() {
        this.animator.playOneShot(WORKING);
    }

    public ImageView getImageView() {
        return titanusImageView;
    }

    public static String getName() {
        return name;
    }

    public int getLevel() {
        return titanusLevel;
    }

    public void setLevel(int titanusLevel) {
        this.titanusLevel = titanusLevel;
    }

    public void levelUp(int titanusLevel) {
        this.titanusLevel++;
    }
}
