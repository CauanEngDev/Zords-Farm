package com.robot.model;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import static com.robot.enums.ZordFunction.*;
import static com.robot.utils.FileFuction.loadingImageSprite;


public class RedMagicZord extends Zord {
    private static final Image RMZ_IMAGE_IDLE = loadingImageSprite("/RedMagicZord/RedMagicIdle.png");
    private static final Image RMZ_IMAGE_WALKING = loadingImageSprite("/RedMagicZord/RedMagicWalking.png");
    private static final Image RMZ_IMAGE_FIGHTING = loadingImageSprite("/RedMagicZord/RedMagicFighting.png");

    public RedMagicZord() {
        super(RMZ_IMAGE_IDLE, RMZ_IMAGE_WALKING, RMZ_IMAGE_FIGHTING, "Red Magic Zord", FIGHTER);
    }

    @Override
    public VBox showInfoBox(Pane root, double x, double y) {
        VBox newInfoBox = new VBox(5);
        Label lblName = new Label(this.name);
        lblName.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Label lblFunction = new Label(this.function.toString());
        lblFunction.setStyle("-fx-text-fill: yellow;");

        Button fighterButton = new Button("Lutar (Em manutenção)");
        fighterButton.setDisable(true);
        Tooltip minerTT = new Tooltip("Zord não implementado!");
        Tooltip.install(fighterButton, minerTT);

        newInfoBox.getChildren().addAll(lblName, lblFunction, fighterButton);
        root.getChildren().add(newInfoBox);
        return newInfoBox;
    }
}
