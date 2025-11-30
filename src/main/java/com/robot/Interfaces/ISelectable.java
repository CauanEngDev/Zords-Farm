package com.robot.Interfaces;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public interface ISelectable {
    ImageView getImageView();
    void select();
    void deselect();
    boolean isSelected();
    void showInfoBox(Pane root, double x, double y, VBox oldInfoBox);
}
