package com.robot.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;

public abstract class Zord {
    protected ImageView robotImageView;
    protected String name;
    protected int energy;

    public Zord(Image robotImage, String name) {
        this.robotImageView = new ImageView(robotImage);
        this.name = name;
        this.energy = 100;
    }

    public ImageView getRobotImage() { return robotImageView; }

    public String getName() { return name; }

    public int getEnergy() { return energy; }

    public void move(double x, double y) {
        this.robotImageView.setLayoutX(this.robotImageView.getLayoutX() + x);
        this.robotImageView.setLayoutY(this.robotImageView.getLayoutY() + y);
    }

    public static Image loadingImageSprite(String path) {
        InputStream stream = Zord.class.getResourceAsStream(path);

        if  (stream == null) {
            throw new RuntimeException("Não foi possível encontrar a imagem no caminho: " + path);
        }

        return new Image(stream);
    }
}
