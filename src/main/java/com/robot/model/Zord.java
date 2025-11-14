package com.robot.model;

import com.robot.enums.ZordFunction;
import com.robot.enums.ZordState;
import javafx.animation.AnimationTimer;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class Zord {
    protected Map<String, Image> sprites = new HashMap<>();
    protected ImageView zordImageView;
    protected String name;
    protected ZordFunction function;
    protected int energy;

    protected AnimationTimer animationLoop;
    private ZordState currentState = ZordState.IDLE;

    protected static final int SPRITE_COLUMNS = 6;
    protected static final int SPRITE_FRAMES_TOTAL = 36;
    protected final int frameWidth;
    protected final int frameHeight;

    protected int currentIdleFrame = 0;
    protected int currentWalkFrame = 0;
    private long lastUptade = 0;

    public Zord(Image zordImageIdle, Image zordImageWalking, Image zordImageWork, String name,  ZordFunction function) {
        this.sprites.put("Idle", zordImageIdle);
        this.sprites.put("Walk", zordImageWalking);
        this.sprites.put("Work", zordImageWork);

        this.zordImageView = new ImageView(zordImageIdle);
        this.name = name;
        this.function = function;
        this.energy = 100;

        Image idleSheet = sprites.get("Idle");
        this.frameWidth = (int) (idleSheet.getWidth() / SPRITE_COLUMNS);
        this.frameHeight = (int) (idleSheet.getHeight() / SPRITE_COLUMNS);

        initializeAnimationLoop();

        this.animationLoop.start();
    }

    protected void initializeAnimationLoop() {
        this.animationLoop = new AnimationTimer() {
            private static final long FRAME_DURATION_MS = 83;

            @Override
            public void handle(long now) {
                long nowMs = now / 1_000_000;

                if (nowMs - lastUptade < FRAME_DURATION_MS) return;
                lastUptade = nowMs;

                int col, row, frame;

                if (currentState == ZordState.WALKING) {
                    currentWalkFrame++;
                    if (currentWalkFrame >= SPRITE_FRAMES_TOTAL)
                        currentWalkFrame = 0;
                    frame = currentWalkFrame;
                } else {
                    currentIdleFrame++;
                    if (currentIdleFrame >= SPRITE_FRAMES_TOTAL)
                        currentIdleFrame = 0;
                    frame = currentIdleFrame;
                }

                col = frame % SPRITE_COLUMNS;
                row = frame / SPRITE_COLUMNS;

                int offsetX = col * frameWidth;
                int offsetY = row * frameHeight;

                zordImageView.setViewport(new Rectangle2D(offsetX, offsetY, frameWidth, frameHeight));
            }
        };
    }

    public ImageView getZordImage() { return zordImageView; }

    public String getName() { return name; }

    public int getEnergy() { return energy; }

    public ZordFunction getFunction() { return function; }

    public void showWalkAnimation() {
        if (currentState == ZordState.WALKING) return;
        currentState = ZordState.WALKING;
        this.zordImageView.setImage(sprites.get("Walk"));
    }

    public void showIdleAnimation() {
        if (currentState == ZordState.IDLE) return;
        currentState = ZordState.IDLE;
        this.zordImageView.setImage(sprites.get("Idle"));
    }

    public static Image loadingImageSprite(String path) {
        InputStream stream = Zord.class.getResourceAsStream(path);

        if  (stream == null) {
            throw new RuntimeException("Não foi possível encontrar a imagem no caminho: " + path);
        }

        return new Image(stream);
    }
}
