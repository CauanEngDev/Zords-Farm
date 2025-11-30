package com.robot.utils;

import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import static com.robot.Database.ZordsData.*;

public class GameFunction {
    public static void println(String prompt){
        System.out.println(prompt);
    }

    public static void clearGameWorld(Pane root) {
        root.getChildren().removeIf(node -> node instanceof ImageView);

        stegoZords.clear();
        redMagicZords.clear();
        triceraZords.clear();
    }
}
