package com.robot.controller;

import com.robot.GameApp;
import com.robot.Interfaces.ISelectable;
import com.robot.model.TitanusFabric;
import com.robot.model.Zord;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.Set;

public class SelectionManager {
    private Object selectedEntity = null;
    private final Pane root;

    public SelectionManager(Pane root, GameApp app) {
        this.root = root;
    }

    public void setupInputHandlers(Set<Zord> allZords, TitanusFabric titanusFabric) {
        root.setOnKeyPressed(event -> {
            if (selectedEntity != null) {
                deselectCurrent();
            }
        });

        registerUnitClick(stego, allZords);
        registerUnitClick(titanusFabric, null);
    }

    public void deselectCurrent() {
        if (selectedEntity instanceof Zord)
            ((Zord) selectedEntity).deselect();
        else if (selectedEntity instanceof TitanusFabric)
            ((TitanusFabric) selectedEntity).deselect();

        selectedEntity = null;
    }

    public void registerUnitClick(ISelectable entity, Set<Zord> allZords) {
        ImageView view = entity.getImageView();

        view.setOnMouseClicked(event -> {
            event.consume();

            if (selectedEntity == entity)
                deselectCurrent();
            else {
                if (selectedEntity != null)
                    deselectCurrent();

                ((Zord) selectedEntity).select();
                selectedEntity = entity;
            }
        });
    }
}
