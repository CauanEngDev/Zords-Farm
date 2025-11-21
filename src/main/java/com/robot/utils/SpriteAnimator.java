package com.robot.utils;

import com.robot.enums.AnimationState;
import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.HashMap;
import java.util.Map;

public class SpriteAnimator {
    private final ImageView targetImageView;
    private final Map<AnimationState, Image> animations = new HashMap<>();

    private AnimationTimer animationTimer;
    private Object currentState;

    private int cols = 6;
    private int totalFrames = 36;
    private int currentFrame = 0;
    private long lastUpdate = 0;
    private long frameDuration = 83;

    public SpriteAnimator(ImageView target) {
        this.targetImageView = target;
        initializeTimer();
    }

    public void addAnimation(AnimationState stateKey, Image spriteSheet) {
        animations.put(stateKey, spriteSheet);
    }

    public void play(AnimationState stateKey) {
        if (currentState == stateKey) return;

        currentState = stateKey;
        currentFrame = 0;

        Image sheet = animations.get(stateKey);
        if (sheet != null) {
            targetImageView.setImage(sheet);
            animationTimer.start();
        }
    }

    public void stop() {
        animationTimer.stop();
    }

    public void initializeTimer() {
        this.animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (currentState == null || !animations.containsKey(currentState)) return;

                long nowMS = now / 1_000_000;
                if (nowMS - lastUpdate < frameDuration) return;
                lastUpdate = nowMS;

                currentFrame = (currentFrame + 1) % totalFrames;

                Image currentSheet = animations.get(currentState);

                int frameW = (int) (currentSheet.getWidth() / cols);
                int frameH = (int) (currentSheet.getHeight() / (totalFrames / cols));

                int col = currentFrame % cols;
                int row = currentFrame / cols;

                targetImageView.setViewport(new Rectangle2D(col * frameW, row * frameH, frameW, frameH));
            }
        };
    }
}
