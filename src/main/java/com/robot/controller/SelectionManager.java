package com.robot.controller;

import com.robot.GameApp;
import com.robot.Interfaces.ISelectable;
import com.robot.model.TitanusFabric;
import com.robot.model.WorkZord;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.Set;

public class SelectionManager {
    private ISelectable selectedEntity = null;
    private final Pane root;
    private final GameApp app;
    private VBox infoBox;

    public SelectionManager(Pane root, GameApp app) {
        this.root = root;
        this.app = app;
    }

    public void setupInputHandlers(Set<? extends WorkZord> allZords, TitanusFabric titanusFabric) {
        if (allZords != null && !allZords.isEmpty()) {
            for (WorkZord workZord : allZords)
                registerUnitClick(workZord);
        }

        registerUnitClick(titanusFabric);

        root.setOnMouseClicked(event -> {
            if (selectedEntity != null) {
                double worldTargetX = event.getX() - app.getWorld().getTranslateX();
                double worldTargetY = event.getY() -  app.getWorld().getTranslateY();

                if (selectedEntity instanceof WorkZord) {
                    WorkZord selectedZord = (WorkZord) selectedEntity;

                    double adjustedX = worldTargetX - selectedZord.getImageView().getBoundsInParent().getWidth() / 2;
                    double adjustedY = worldTargetY - selectedZord.getImageView().getBoundsInParent().getHeight() / 2;

                    selectedZord.setTarget(adjustedX, adjustedY);

                    selectedZord.showWalkAnimation();
                } else deselectCurrent();

                if (event.getClickCount() == 2) {
                    deselectCurrent();
                }
            }
        });
    }

    public void deselectCurrent() {
        if (selectedEntity == null) return;

        selectedEntity.deselect();

        if (this.infoBox != null)
            root.getChildren().remove(this.infoBox);
        this.app.hideInfoBox();
        this.infoBox = null;
        selectedEntity = null;
    }

    public void registerUnitClick(ISelectable entity) {
        ImageView view = entity.getImageView();

        view.setOnMouseClicked(event -> {
            event.consume();

            if (selectedEntity == entity) {
                deselectCurrent();
            } else {
                if (selectedEntity != null) {
                    deselectCurrent();
                } else {
                    this.app.hideInfoBox();
                }

                entity.select();
                selectedEntity = entity;
            }

            if (selectedEntity != null) {
                if (this.infoBox != null) root.getChildren().remove(this.infoBox);
                VBox newVBox = selectedEntity.showInfoBox(root, event.getSceneX(), event.getSceneY());
                this.infoBox = newVBox;
            }
        });
    }
}
