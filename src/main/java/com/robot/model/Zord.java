package com.robot.model;

import com.robot.enums.ZordFunction;
import static com.robot.enums.AnimationState.*;
import com.robot.utils.SpriteAnimator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class Zord implements IAnimatable{
    protected ImageView zordImageView;
    protected String name;
    protected ZordFunction function;
    protected int energy;
    protected SpriteAnimator animator;

    public Zord(Image zordImageIdle, Image zordImageWalking, Image zordImageWork, String name,  ZordFunction function) {
        this.zordImageView = new ImageView(zordImageIdle);
        this.name = name;
        this.function = function;
        this.energy = 100;

        this.zordImageView.setScaleX(1);
        this.zordImageView.setScaleY(1);
        this.zordImageView.setPreserveRatio(true);
        this.zordImageView.setPickOnBounds(false);

        this.animator = new SpriteAnimator(this.zordImageView);

        this.animator.addAnimation(IDLE, zordImageIdle);
        this.animator.addAnimation(WALKING, zordImageWalking);
        this.animator.addAnimation(WORKING, zordImageWork);

        this.animator.play(IDLE);
    }

    @Override
    public SpriteAnimator getAnimator() {
        return this.animator;
    }

    public void showWalkAnimation() {
        this.animator.play(WALKING);
    }

    public void showIdleAnimation() {
        this.animator.play(IDLE);
    }

    public ImageView getZordImage() { return zordImageView; }

    public String getName() { return name; }

    public int getEnergy() { return energy; }

    public ZordFunction getFunction() { return function; }

    public void setEnergy(int energy) { this.energy = energy; }
}
