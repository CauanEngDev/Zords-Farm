package com.robot.Interfaces;

import javafx.scene.image.ImageView;

public interface ISelectable {
    ImageView getImageView();
    void select();
    void deselect();
    boolean isSelected();
}
