package com.robot;

import com.google.gson.Gson;
import com.robot.Database.GameSaveData;
import com.robot.Database.TitanusSaveData;
import com.robot.controller.ZordCreate;
import com.robot.model.StegoZord;
import com.robot.Database.ZordSaveData;
import static com.robot.Database.ZordsData.*;
import com.robot.model.TitanusFabric;
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

import java.io.FileReader;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.robot.utils.IOFunction.println;


public class GameApp extends Application {
    private static final int TILE_GRID = 64;
    private StegoZord stego;
    private ImageView stegoSprite;
    private TitanusFabric titanus;
    private ImageView titanusSprite;
    private double targetX = 5 * TILE_GRID;
    private double targetY = 5 * TILE_GRID;
    private final double moveSpeed = 4.0;

    private VBox infoBox;

    private final Gson gson = new Gson();

    private static final Logger logger = Logger.getLogger(GameApp.class.getName());

    private static final int MAP_ROWS = 12;
    private static final int MAP_COLUMNS = 20;

    private int[][] collisionMap = new int[MAP_ROWS][MAP_COLUMNS];

    private Pane root;
    @Override
    public void start(@NotNull Stage stage) {
        stego = new StegoZord();
        stegoSprite = stego.getZordImage();

        titanus = new TitanusFabric();
        titanusSprite = titanus.getImageView();

        loadGame();

        initializeCollisionMap();
        this.root = new Pane();
        root.setStyle("-fx-background-color: #3d8c40");
        int windowWidth = 1280;
        int windowHeight = 768;
        Scene scene = new Scene(root,  windowWidth, windowHeight);

        stegoSprite.setPickOnBounds(false);
        titanusSprite.setPickOnBounds(false);

        root.getChildren().addAll(titanusSprite, stegoSprite);

        if (titanusSprite.getLayoutY() == 0 && titanusSprite.getLayoutX() == 0) {
            titanusSprite.setLayoutY(TILE_GRID);
            titanusSprite.setLayoutX(5 * TILE_GRID);
        }

        int stegoCol = 5;
        int stegoRow = 5;

        if (stegoSprite.getLayoutX() == 0 && stegoSprite.getLayoutY() == 0){
            stegoSprite.setLayoutX(stegoCol * TILE_GRID);
            stegoSprite.setLayoutY(stegoRow * TILE_GRID);
        }

        titanusSprite.setOnMouseClicked(event -> {
            event.consume();
            showTitanusInfo(root, event.getSceneX(), event.getSceneY());
        });

        root.setOnMouseClicked(event -> {
            if (infoBox != null) {
                root.getChildren().remove(infoBox);
                infoBox = null;
            }
            double clickX = event.getX();
            double clickY = event.getY();

            double newTargetX = clickX - stegoSprite.getBoundsInParent().getWidth() / 2;
            double newTargetY = clickY - stegoSprite.getBoundsInParent().getHeight() / 2;

            if (isColliding(newTargetX, newTargetY)) {
                println("Movimento Bloqueado! O Titanus está aí.");
                return;
            }

            targetX = newTargetX;
            targetY = newTargetY;

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

    private void showTitanusInfo(Pane root, double x, double y) {
        if (infoBox != null) root.getChildren().remove(infoBox);

        infoBox = new VBox(5);
        infoBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 10; -fx-background-radius: 10;");

        Label lblName =  new Label("Nome: " + TitanusFabric.getName());
        lblName.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Label lblLevel = new Label("Nível: " + titanus.getLevel());
        lblLevel.setStyle("-fx-text-fill: yellow;");

        Label lblStegos = new Label("Stegos: " + titanus.numStegos);
        lblStegos.setStyle("-fx-text-fill: white;");

        Button btnLevelUp = new Button("Upar (+1)");
        btnLevelUp.setOnAction(event -> {
            titanus.levelUp(titanus.getLevel());
            lblLevel.setText("Nível: " + titanus.getLevel());
            saveGame();
        });

        Button btnCreateZord = new Button("Criar Zord");
        btnCreateZord.setOnAction(event -> {
            if (stegoZords.size() < titanus.numStegos) {
                titanus.getAnimator().setActionOnFrame(18, ()-> {
                    stegoSpawn();
                });

                titanus.getAnimator().setActionOnFinish(() ->{
                    titanus.showIdleAnimation();
                });

                titanus.showCreateAnimation();
            } else println("Número máximo de StegoZords no mapa!");
        });

        infoBox.getChildren().addAll(lblName, lblLevel, lblStegos, btnLevelUp, btnCreateZord);

        infoBox.setLayoutX(x + 20);
        infoBox.setLayoutY(y - 50);

        root.getChildren().add(infoBox);
    }

    private void saveGame() {
        println("Salvando progresso...");
        try (FileWriter writer = new FileWriter("saveTeste.json")) {
            GameSaveData data = new GameSaveData();

            data.stegoData = new ZordSaveData();
            data.stegoData.name = stego.getName();
            data.stegoData.zordType = stego.getClass().getSimpleName();
            data.stegoData.x = targetX;
            data.stegoData.y = targetY;
            data.stegoData.energy = stego.getEnergy();

            data.titanusData = new TitanusSaveData();
            data.titanusData.name = TitanusFabric.getName();
            data.titanusData.zordType = titanus.getClass().getSimpleName();
            data.titanusData.x = titanusSprite.getLayoutX();
            data.titanusData.y = titanusSprite.getLayoutY();
            data.titanusData.level = titanus.getLevel();
            data.titanusData.numStegos = titanus.numStegos;

            gson.toJson(data, writer);

            println("Progresso Salvo com sucesso!");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Falha ao salvar o jogo", e);
        }
    }

    private void loadGame() {
        println("Carregando progresso...");
        try (FileReader reader = new FileReader("saveTeste.json")) {
            GameSaveData data = gson.fromJson(reader, GameSaveData.class);

            if (data != null) {
                if (data.stegoData != null) {
                    stegoSprite.setLayoutX(data.stegoData.x);
                    stegoSprite.setLayoutY(data.stegoData.y);

                    targetX = data.stegoData.x;
                    targetY = data.stegoData.y;
                }

                if (data.titanusData != null) {
                    titanusSprite.setLayoutX(data.titanusData.x);
                    titanusSprite.setLayoutY(data.titanusData.y);
                    titanus.setLevel(data.titanusData.level);
                    titanus.numStegos = data.titanusData.numStegos;
                }

                println("Progresso carregado com sucesso!");
            }
        } catch(Exception e) {
            logger.log(Level.INFO, "Save não encontrado, iniciando novo jogo.");
        }
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

    private void stegoSpawn() {
        StegoZord newStego = new ZordCreate().createStego();
        ImageView newStegoSprite = newStego.getZordImage();
        newStegoSprite.setFitWidth(TILE_GRID);
        newStegoSprite.setPreserveRatio(true);
        newStegoSprite.setPickOnBounds(false);

        double stegoSize = newStegoSprite.getBoundsInParent().getHeight();
        Bounds titanusSize = titanusSprite.getBoundsInParent();

        newStegoSprite.setLayoutX(titanusSprite.getLayoutX() - titanusSize.getWidth());
        newStegoSprite.setLayoutY(titanusSprite.getLayoutY() + titanusSize.getHeight() + stegoSize);

        root.getChildren().add(newStegoSprite);
    }

    public static void main(String[] args) {
        launch(args);
    }
}