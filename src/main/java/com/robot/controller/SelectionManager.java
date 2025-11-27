package com.robot.controller;

import com.robot.GameApp;
import com.robot.Interfaces.ISelectable;
import com.robot.model.TitanusFabric;
import com.robot.model.Zord;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.Set;

public class SelectionManager {
    private ISelectable selectedEntity = null;
    private final Pane root;

    public SelectionManager(Pane root, GameApp app) {
        this.root = root;
    }

    public void setupInputHandlers(Set<Zord> allZords, TitanusFabric titanusFabric) {
        for (Zord zord : allZords)
            registerUnitClick(zord);

        registerUnitClick(titanusFabric);

        root.setOnMouseClicked(event -> {
            if (selectedEntity != null) {
                double targetX = event.getX();
                double targetY = event.getY();

                if (event.getClickCount() == 2) {
                    deselectCurrent();
                }
            }
        });
    }

    public void deselectCurrent() {
        selectedEntity.deselect();
        selectedEntity = null;
    }

    public void registerUnitClick(ISelectable entity) {
        ImageView view = entity.getImageView();

        view.setOnMouseClicked(event -> {
            event.consume();

            if (selectedEntity == entity)
                deselectCurrent();
            else {
                if (selectedEntity != null)
                    deselectCurrent();

                entity.select();
                selectedEntity = entity;
            }
        });
    }
}
