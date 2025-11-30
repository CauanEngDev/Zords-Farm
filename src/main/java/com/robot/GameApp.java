package com.robot;

import com.google.gson.Gson;
import com.robot.Database.ZordsData;
import com.robot.controller.SaveController;
import com.robot.controller.SelectionManager;
import com.robot.controller.ZordCreate;
import com.robot.model.StegoZord;

import static com.robot.Database.ZordsData.*;
import com.robot.model.TitanusFabric;
import com.robot.model.TriceraZord;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

import static com.robot.utils.GameFunction.clearGameWorld;
import static com.robot.utils.GameFunction.println;


public class GameApp extends Application {
    private static final int TILE_GRID = 64;
    private static final int MAP_ROWS = 12;
    private static final int MAP_COLUMNS = 20;
    private final double moveSpeed = 4.0;
    private static final String SAVE_FILE_NAME = "saveTesteRefatoração.json";

    private TriceraZord initialTricera;
    private TitanusFabric titanusFabric;
    private Pane root;
    private VBox infoBox;
    private final int[][] collisionMap = new int[MAP_ROWS][MAP_COLUMNS];

    private final Gson gson = new Gson();
    private static final Logger LOGGER = Logger.getLogger(GameApp.class.getName());
    private final ZordCreate zordCreate = new ZordCreate();
    private final SaveController saveController = new SaveController();

    @Override
    public void start(@NotNull Stage stage) {
        initialTricera = new TriceraZord();
        titanusFabric = new TitanusFabric();

        clearGameWorld(this.root);
        if (!saveController.loadGame(titanusFabric, this.root))
            GameInitializer.initializeNewGame(initialTricera, titanusFabric, TILE_GRID);

        root.getChildren().addAll(titanusFabric.getImageView(), initialTricera.getImageView());

        this.root = new Pane();
        root.setStyle("-fx-background-color: #3d8c40");
        int windowWidth = 1280;
        int windowHeight = 768;
        Scene scene = new Scene(root,  windowWidth, windowHeight);


        SelectionManager selectionManager = new SelectionManager(root, this, initialTricera);
        selectionManager.setupInputHandlers(triceraZords, titanusFabric);

        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double currentX = initialTricera.getImageView().getLayoutX();
                double currentY = initialTricera.getImageView().getLayoutY();

                double deltaX = targetX - currentX;
                double deltaY = targetY - currentY;
                double distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));

                if (distance < moveSpeed) {
                    initialTricera.getImageView().setLayoutX(targetX);
                    initialTricera.getImageView().setLayoutY(targetY);

                    stego.showIdleAnimation();
                } else {
                    if (targetX < currentX) stegoSprite.setScaleX(-1.0);
                    else if (targetX > currentX) stegoSprite.setScaleX(1.0);
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

    private boolean isColliding(double futureX, double futureY) {
        Bounds titanusBounds = titanusSprite.getBoundsInParent();
        Bounds stegoFutureBounds = new BoundingBox(
                futureX,
                futureY,
                stegoSprite.getBoundsInParent().getWidth(),
                stegoSprite.getBoundsInParent().getHeight()
        );

        double shirk = 20.0;

        if (stegoFutureBounds.intersects(titanusBounds.getMinX() + shirk,
                                        titanusBounds.getMinY() + shirk,
                                        titanusBounds.getWidth() - (2 * shirk),
                                        titanusBounds.getHeight() - (2 * shirk))) {
            return true;
        }
        return false;
    }

    private void initializeCollisionMap() {
        for (int i = 0; i < MAP_ROWS; i++) {
            for (int j = 0; j < MAP_COLUMNS; j++) {
                collisionMap[i][j] = 0;
            }
        }

        int titanusCol = 5;
        int titanusRow = 1;

        collisionMap[titanusRow][titanusCol] = 9;
        collisionMap[titanusRow][titanusCol + 1] = 9;
        collisionMap[titanusRow + 1][titanusCol] = 9;
        collisionMap[titanusRow + 1][titanusCol + 1] = 9;
    }



    public static void main(String[] args) {
        launch(args);
    }
}