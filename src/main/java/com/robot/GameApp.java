package com.robot;

import com.robot.model.StegoZord;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import static com.robot.utils.IOFunction.println;


public class GameApp extends Application {
    public static final int TILE_GRID = 64;
    @Override
    public void start(@NotNull Stage stage) throws Exception {
        Pane root = new Pane();
        root.setStyle("-fx-background-color: #3d8c40");
        int windowWidth = 1280;
        int windowHeight = 768;
        Scene scene = new Scene(root,  windowWidth, windowHeight);

        StegoZord stegoZord = new StegoZord();
        ImageView stegoIdle = stegoZord.getRobotImageIdle();

        stegoIdle.setFitWidth(TILE_GRID);
        stegoIdle.setPreserveRatio(true);
        stegoIdle.setPickOnBounds(true);

        root.getChildren().add(stegoIdle);


        int stegoCol = 5;
        int stegoRow = 5;

        stegoIdle.setLayoutX(stegoCol * TILE_GRID);
        stegoIdle.setLayoutY(stegoRow * TILE_GRID);

        stage.setTitle("Meu teste de permanência");
        stage.setScene(scene);

        root.setOnMouseClicked(event -> {
            double targetX = event.getX();
            double targetY = event.getY();

            double spriteWidth = stegoIdle.getBoundsInParent().getWidth();
            double spriteHeight = stegoIdle.getBoundsInParent().getHeight();

            stegoIdle.setLayoutX(targetX - (spriteWidth / 2));
            stegoIdle.setLayoutY(targetY - (spriteHeight / 2));

            int novaCol = (int) (targetX -TILE_GRID);
            int novaRow = (int) (targetY - TILE_GRID);

            println("Stego movido! Posição em pixels: (" + targetX + ", " + targetY + ")");
            println("Célula da matriz: (" + novaCol + ", " + novaRow + ")");
        });

        stegoIdle.setOnMouseClicked(event -> {
            event.consume();
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}