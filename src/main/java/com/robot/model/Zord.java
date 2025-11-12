package com.robot.model;

import com.robot.enums.ZordFunction;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class Zord implements IMovable {
    protected static final Map<String, Image> sprites = new HashMap<>();
    protected ImageView zordImageView;
    protected static final String name;
    protected static final ZordFunction function;
    protected int energy;

    public Zord(Image zordImageIdle, Image zordImageWalking, String name,  ZordFunction function) {
        this.sprites.put("Idle", zordImageIdle);
        this.sprites.put("Walk", zordImageWalking);
        this.zordImageView = new ImageView(zordImageIdle);
        this.name = name;
        this.function = function;
        this.energy = 100;
    }

    public ImageView getZordImage() { return zordImageView; }

    public String getName() { return name; }

    public int getEnergy() { return energy; }

    public ZordFunction getFunction() { return function; }

    @Override
    public void move(double x, double y) {
        this.zordImageView.setLayoutX(this.zordImageView.getLayoutX() + x);
        this.zordImageView.setLayoutY(this.zordImageView.getLayoutY() + y);
    }

    public void showWalkAnimation() {
        this.zordImageView.setImage(sprites.get("Walk"));
    }

    public void showIdleAnimation() {
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
