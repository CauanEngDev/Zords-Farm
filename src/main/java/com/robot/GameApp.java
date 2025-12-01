package com.robot;

import com.google.gson.Gson;
import com.robot.Database.ZordsData;
import com.robot.controller.GameInitializer;
import com.robot.controller.SaveController;
import com.robot.controller.SelectionManager;
import com.robot.controller.ZordCreate;
import com.robot.model.StegoZord;

import static com.robot.Database.ZordsData.*;
import com.robot.model.TitanusFabric;
import com.robot.model.TriceraZord;
import com.robot.model.Zord;
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

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static com.robot.utils.GameFunction.clearGameWorld;
import static com.robot.utils.GameFunction.println;


public class GameApp extends Application {
    private static final int TILE_GRID = 64;
    private final double moveSpeed = 4.0;
    private static final String SAVE_FILE_NAME = "saveTesteRefatoração.json";

    private TriceraZord initialTricera;
    private TitanusFabric titanusFabric;
    private Pane root;
    private VBox infoBox;

    private final Gson gson = new Gson();
    private static final Logger LOGGER = Logger.getLogger(GameApp.class.getName());
    private final ZordCreate zordCreate = new ZordCreate();
    private final SaveController saveController = new SaveController();

    @Override
    public void start(@NotNull Stage stage) {
        root = new Pane();
        initialTricera = new TriceraZord();
        titanusFabric = new TitanusFabric();

        clearGameWorld(this.root);
        if (!saveController.loadGame(titanusFabric, root))
            GameInitializer.initializeNewGame(initialTricera, titanusFabric, TILE_GRID);

        root.getChildren().addAll(titanusFabric.getImageView(), initialTricera.getImageView());

        root.setStyle("-fx-background-color: #3d8c40");
        int windowWidth = 1280;
        int windowHeight = 768;
        Scene scene = new Scene(root,  windowWidth, windowHeight);

        SelectionManager selectionManager = new SelectionManager(root, this);

        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Set<Zord> allZords = new HashSet<>();
                allZords.addAll(stegoZords);
                allZords.addAll(triceraZords);
                selectionManager.setupInputHandlers(allZords, titanusFabric);

                for (Zord zord : allZords) {
                    double currentX = zord.getImageView().getLayoutX();
                    double currentY = zord.getImageView().getLayoutY();

                    double targetX = zord.getTargetX();
                    double targetY = zord.getTargetY();

                    if (targetX != currentX || targetY != currentY) {
                        double deltax  = targetX - currentX;
                        double deltay = targetY - currentY;
                        double distance = Math.sqrt(Math.pow(deltax, 2) + Math.pow(deltay, 2));

                        if (distance < moveSpeed) {
                            zord.getImageView().setLayoutX(targetX);
                            zord.getImageView().setLayoutY(targetY);
                            zord.showIdleAnimation();
                        } else {
                            if (targetX < currentX) zord.getImageView().setScaleX(-1.0);
                            else if (targetX > currentX) zord.getImageView().setScaleX(1.0);

                            double stepX = (deltax/distance) * moveSpeed;
                            double stepY = (deltay/distance) * moveSpeed;

                            zord.getImageView().setLayoutX(currentX + stepX);
                            zord.getImageView().setLayoutY(currentY + stepY);
                            zord.showWalkAnimation();
                        }
                    }
                }
            }
        };

        gameLoop.start();

        stage.setTitle("Meu teste de refatoração");
        stage.setScene(scene);
        stage.show();
    }

    public void hideInfoBox() {
        if (this.infoBox != null && this.root != null) {
            this.root.getChildren().remove(this.infoBox);
            this.infoBox = null;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}