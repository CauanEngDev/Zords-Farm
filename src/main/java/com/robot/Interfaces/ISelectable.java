package com.robot.Interfaces;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public interface ISelectable {
    ImageView getImageView();
    void select();
    void deselect();
    boolean isSelected();
    void showInfoBox(Pane root);
}
