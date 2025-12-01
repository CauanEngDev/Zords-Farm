package com.robot.model;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import static com.robot.enums.ZordFunction.*;
import static com.robot.utils.FileFuction.loadingImageSprite;

public class TriceraZord extends Zord {
    private static final Image TRICERA_IMAGE_IDLE = loadingImageSprite("/TriceraZord (Dino Charge)/TriceraIdle.png");
    private static final Image TRICERA_IMAGE_WALKING = loadingImageSprite("/TriceraZord (Dino Charge)/TriceraWalking.png");
    private static final Image TRICERA_IMAGE_MINING = loadingImageSprite("/TriceraZord (Dino Charge)/TriceraMining.png");

    public TriceraZord() {
        super(TRICERA_IMAGE_IDLE, TRICERA_IMAGE_WALKING, TRICERA_IMAGE_MINING, "Tricera Zord", MINER);
    }

    @Override
    public void showInfoBox(Pane root, double x, double y, VBox oldInfoBox) {
        if (oldInfoBox != null) root.getChildren().remove(oldInfoBox);

        VBox newInfoBox = new VBox(5);
        Label lblName = new Label(this.name);
        lblName.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Label lblFunction = new Label(this.function.toString());
        lblFunction.setStyle("-fx-text-fill: yellow;");

        Button minerButton = new Button("Minerar (Em manutenção)");
        minerButton.setDisable(true);
        Tooltip minerTT = new Tooltip("Lógica de mineração não implementada! Esperando implementação da mina.");
        Tooltip.install(minerButton, minerTT);

        newInfoBox.getChildren().addAll(lblName, lblFunction, minerButton);
        root.getChildren().add(newInfoBox);
    }
}
