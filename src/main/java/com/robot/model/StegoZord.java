package com.robot.model;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import static com.robot.enums.ZordFunction.*;
import static com.robot.utils.FileFuction.*;

public class StegoZord extends WorkZord {
    // Carregamento de Assets
    private static final Image STEGO_IMAGE_IDLE = loadingImageSprite("/StegoZord/StegoIdle.png");
    private static final Image STEGO_IMAGE_WALKING = loadingImageSprite("/StegoZord/StegoWalking.png");
    private static final Image STEGO_IMAGE_WORKING = loadingImageSprite("/StegoZord/StegoBuilding.png");

    public StegoZord() {
        super(STEGO_IMAGE_IDLE, STEGO_IMAGE_WALKING, STEGO_IMAGE_WORKING, "Stego Zord", BUILDER);
    }

    /**
     * Cria e exibe a caixa de informações específica para o StegoZord (Construtor).
     */
    @Override
    public VBox showInfoBox(Pane root, double x, double y) {
        VBox newInfoBox = new VBox(5);
        newInfoBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 10; -fx-background-radius: 10;");

        Label lblName = new Label(this.name);
        lblName.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Label lblFunction = new Label(this.function.toString());
        lblFunction.setStyle("-fx-text-fill: yellow;");

        Button builderButton = new Button("Construir (Em manutenção)");
        builderButton.setDisable(true);
        Tooltip builderTT = new Tooltip("Lógica de construção não implementada!");
        Tooltip.install(builderButton, builderTT);

        newInfoBox.getChildren().addAll(lblName, lblFunction, builderButton);
        // Posicionamento
        newInfoBox.setLayoutX(this.getImageView().getLayoutX() + 20);
        newInfoBox.setLayoutY(this.getImageView().getLayoutY() - 40);
        // O "Porquê": Adicionamos o novo infoBox ao root e o retornamos para o SelectionManager
        root.getChildren().add(newInfoBox);
        return newInfoBox;
    }
}