package com.robot;

import com.google.gson.Gson;
import com.robot.model.StegoZord;
import com.robot.model.ZordSaveData;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.robot.utils.IOFunction.println;


public class GameApp extends Application {
    private static final int TILE_GRID = 64;
    private StegoZord stego;
    private ImageView stegoSprite;
    private double targetX = 5 * TILE_GRID;
    private double targetY = 5 * TILE_GRID;
    private final double moveSpeed = 4.0;

    private final Gson gson = new Gson();

    private static final Logger logger = Logger.getLogger(GameApp.class.getName());

    @Override
    public void start(@NotNull Stage stage) {
        stego = new StegoZord();
        stegoSprite = stego.getZordImage();

        loadGame();
        
        Pane root = new Pane();
        root.setStyle("-fx-background-color: #3d8c40");
        int windowWidth = 1280;
        int windowHeight = 768;
        Scene scene = new Scene(root,  windowWidth, windowHeight);

        stegoSprite.setFitWidth(TILE_GRID);
        stegoSprite.setPreserveRatio(true);
        stegoSprite.setPickOnBounds(false);

        root.getChildren().add(stegoSprite);


        int stegoCol = 5;
        int stegoRow = 5;

        if (stegoSprite.getLayoutX() == 0 && stegoSprite.getLayoutY() == 0){
            stegoSprite.setLayoutX(stegoCol * TILE_GRID);
            stegoSprite.setLayoutY(stegoRow * TILE_GRID);
        }


        root.setOnMouseClicked(event -> {
            targetX = event.getX();
            targetY = event.getY();

            targetX -= stegoSprite.getBoundsInParent().getWidth() / 2;
            targetY -= stegoSprite.getBoundsInParent().getHeight() / 2;

            println("Novo alvo definido: " + targetX + ", " + targetY);
            saveGame();
        });

            stegoSprite.setOnMouseClicked(MouseEvent::consume);

        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
            double currentX = stegoSprite.getLayoutX();
            double currentY = stegoSprite.getLayoutY();

            double deltaX = targetX - currentX;
            double deltaY = targetY - currentY;
            double distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));

            if (distance < moveSpeed) {
                stegoSprite.setLayoutX(targetX);
                stegoSprite.setLayoutY(targetY);

                stego.showIdleAnimation();
            } else {
                double stepX = (deltaX / distance) * moveSpeed;
                double stepY = (deltaY / distance) * moveSpeed;

                stegoSprite.setLayoutX(currentX + stepX);
                stegoSprite.setLayoutY(currentY + stepY);

                stego.showWalkAnimation();
            }
            }
        };

        gameLoop.start();

        stage.setTitle("Meu teste de animação");
        stage.setScene(scene);
        stage.show();
    }

    private void saveGame() {
        println("Salvando progresso...");
        try (FileWriter writer = new FileWriter("saveTeste.json")) {
            ZordSaveData saveFile = new ZordSaveData();

            saveFile.name = stego.getName();
            saveFile.zordType = stego.getClass().getSimpleName();
            saveFile.energy = stego.getEnergy();

            saveFile.x = targetX;
            saveFile.y = targetY;

            gson.toJson(saveFile, writer);

            println("Progresso Salvo com sucesso!");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Falha ao salvar o jogo", e);
        }
    }

    private void loadGame() {
        println("Carregando progresso...");
        try (FileReader reader = new FileReader("saveTeste.json")) {
            ZordSaveData saveFile = gson.fromJson(reader, ZordSaveData.class);

            if (saveFile != null) {
                stegoSprite.setLayoutX(saveFile.x);
                stegoSprite.setLayoutY(saveFile.y);

                targetX = saveFile.x;
                targetY = saveFile.y;

                println("Progresso carregado com sucesso!");
            }
        } catch(Exception e) {
            saveGame();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}