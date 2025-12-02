package com.robot.utils;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import static com.robot.Database.ZordsData.*;

/**
 * Classe para guardar funções do jogo
 */
public class GameFunction {
    public static void println(String prompt){
        System.out.println(prompt);
    }

    /**
     * método que limpa os itens no root e os data bases
     * @param root root vindo do GameApp
     */
    public static void clearGameWorld(Pane root) {
        root.getChildren().removeIf(node -> node instanceof ImageView);

        stegoZords.clear();
        redMagicZords.clear();
        triceraZords.clear();
    }

//    public static boolean isColliding(double futureX, double futureY) {
//
//    }
}
