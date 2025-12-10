package com.robot.model;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import static com.robot.enums.ZordFunction.*;
import static com.robot.utils.FileFuction.loadingImageSprite;


public class RedMagicZord extends WorkZord {
    private static final Image RMZ_IMAGE_IDLE = loadingImageSprite("/RedMagicZord/RedMagicIdle.png");
    private static final Image RMZ_IMAGE_WALKING = loadingImageSprite("/RedMagicZord/RedMagicWalking.png");
    private static final Image RMZ_IMAGE_FIGHTING = loadingImageSprite("/RedMagicZord/RedMagicFighting.png");

    public RedMagicZord() {
        super(RMZ_IMAGE_IDLE, RMZ_IMAGE_WALKING, RMZ_IMAGE_FIGHTING, "Red Magic Zord", FIGHTER);
    }

    /**
     * Cria e exibe a caixa de informações específica para o RedMagicZord (Combatente).
     */
    @Override
    public VBox showInfoBox(Pane root, double x, double y) {
        VBox newInfoBox = new VBox(5);
        newInfoBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 10; -fx-background-radius: 10;");

        Label lblName = new Label(this.name);
        lblName.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Label lblFunction = new Label(this.function.toString());
        lblFunction.setStyle("-fx-text-fill: yellow;");

        Button fighterButton = new Button("Lutar (Em manutenção)");
        fighterButton.setDisable(true);
        Tooltip minerTT = new Tooltip("Lógica de luta não implementada!");
        Tooltip.install(fighterButton, minerTT);

        newInfoBox.getChildren().addAll(lblName, lblFunction, fighterButton);
        // Posicionamento
        newInfoBox.setLayoutX(this.getImageView().getLayoutX() + 20);
        newInfoBox.setLayoutY(this.getImageView().getLayoutY() - 40);
        root.getChildren().add(newInfoBox);
        return newInfoBox;
    }
}