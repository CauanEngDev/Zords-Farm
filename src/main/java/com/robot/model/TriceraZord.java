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

    /**
     * Cria e exibe a caixa de informações específica para o TriceraZord (Minerador).
     */
    @Override
    public VBox showInfoBox(Pane root, double x, double y) {
        VBox newInfoBox = new VBox(5);
        newInfoBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 10; -fx-background-radius: 10;");

        Label lblName = new Label(this.name);
        lblName.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Label lblFunction = new Label(this.function.toString());
        lblFunction.setStyle("-fx-text-fill: yellow;");

        Button minerButton = new Button("Minerar (Em manutenção)");
        minerButton.setDisable(true);
        Tooltip minerTT = new Tooltip("Lógica de mineração não implementada! Esperando implementação da mina.");
        Tooltip.install(minerButton, minerTT);

        newInfoBox.getChildren().addAll(lblName, lblFunction, minerButton);
        // Posicionamento
        newInfoBox.setLayoutX(this.getImageView().getLayoutX() + 20);
        newInfoBox.setLayoutY(this.getImageView().getLayoutY() - 40);
        root.getChildren().add(newInfoBox);
        return newInfoBox;
    }
}