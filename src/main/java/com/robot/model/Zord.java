package com.robot.model;

import com.robot.Interfaces.IAnimatable;
import com.robot.Interfaces.ISelectable;
import com.robot.enums.ZordFunction;
import static com.robot.enums.AnimationState.*;
import com.robot.utils.SpriteAnimator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public abstract class Zord implements IAnimatable, ISelectable {
    protected ImageView zordImageView;
    protected String name;
    protected ZordFunction function;
    protected int energy;
    protected SpriteAnimator animator;
    protected boolean selected = false;

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

    @Override
    public void select() {
        if (selected) return;
        selected = true;
        this.zordImageView.setStyle("-fx-effect: dropshadow(three-pass-box, yellow, 10, 0.5, 0, 0);");
    }

    @Override
    public void deselect() {
        if (!selected) return;
        selected = false;
        this.zordImageView.setStyle(null);
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public ImageView getImageView() { return zordImageView; }

    public String getName() { return name; }

    public int getEnergy() { return energy; }

    public ZordFunction getFunction() { return function; }

    public void setEnergy(int energy) { this.energy = energy; }
}
