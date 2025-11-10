package com.robot.model;

import com.robot.enums.ZordFunction;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public abstract class Zord implements IMovable {
    protected Map<String, ImageView> sprites = new HashMap<>();
    protected String name;
    protected ZordFunction function;
    protected int energy;

    public Zord(Image robotImageIdle, Image robotGifWalking, String name,  ZordFunction function) {
        sprites.put("Idle", new ImageView(robotImageIdle));
        sprites.put("Walk", new ImageView(robotGifWalking));
        this.name = name;
        this.function = function;
        this.energy = 100;
    }

    public ImageView getRobotImageIdle() { return sprites.get("idle"); }

    public ImageView getRobotGifWalking() { return sprites.get("walking"); }

    public String getName() { return name; }

    public int getEnergy() { return energy; }

    public ZordFunction getFunction() { return function; }

    @Override
    public void move(double x, double y) {
        this.sprites.get("idle").setLayoutX(this.sprites.get("idle").getLayoutX() + x);
        this.sprites.get("idle").setLayoutY(this.sprites.get("idle").getLayoutY() + y);
    }

    public static Image loadingImageSprite(String path) {
        InputStream stream = Zord.class.getResourceAsStream(path);

        if  (stream == null) {
            throw new RuntimeException("Não foi possível encontrar a imagem no caminho: " + path);
        }

        return new Image(stream);
    }
}
