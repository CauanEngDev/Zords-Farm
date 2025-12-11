package com.robot.model;

import com.robot.Interfaces.IAnimatable;
import com.robot.Interfaces.ISelectable;
import com.robot.controller.SelectionManager;
import static com.robot.enums.AnimationState.*;
import com.robot.utils.SpriteAnimator;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public abstract class EdificeZord implements IAnimatable, ISelectable {
    protected final String name;
    protected int level = 1;
    protected boolean selected;

    protected final Group world;
    protected final SelectionManager selectionManager;

    protected final ImageView edificeImageView;
    protected final SpriteAnimator animator;

    public EdificeZord(Group world, SelectionManager selectionManager, String name, Image idleSprite, Image workingSprite) {
        this.name = name;
        this.world = world;
        this.selectionManager = selectionManager;

        this.edificeImageView = new ImageView(idleSprite);

        this.edificeImageView.setScaleX(1);
        this.edificeImageView.setScaleY(1);
        this.edificeImageView.setPreserveRatio(true);
        this.edificeImageView.setPickOnBounds(false);

        this.animator = new SpriteAnimator(edificeImageView);

        this.animator.addAnimation(IDLE, idleSprite);
        this.animator.addAnimation(WORKING, workingSprite);

        this.animator.play(IDLE);
    }

    @Override
    public SpriteAnimator getAnimator() { return this.animator; }

    @Override
    public void select() {
        if (selected) return;
        selected = true;
        this.edificeImageView.setStyle("-fx-effect: dropshadow(three-pass-box, yellow, 10, 0.5, 0, 0);");
    }

    @Override
    public void deselect() {
        if (!selected) return;
        selected = false;
        this.edificeImageView.setStyle(null);
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public ImageView getImageView() {
        return edificeImageView;
    }

    @Override
    public abstract VBox showInfoBox(Pane root, double x, double y);

    public void showIdleAnimation() {
        this.animator.play(IDLE);
    }

    public void showWorkAnimation() {
        this.animator.playOneShot(WORKING);
    }

    public int getLevel() { return level; }

    public void setLevel(int level) { this.level = level; }

    public void levelUp() { this.level++; }

}
