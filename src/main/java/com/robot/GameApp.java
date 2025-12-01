package com.robot;
import javafx.scene.paint.Color;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.InputStream;
import javafx.scene.input.KeyCode;
import javafx.scene.Group;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.InputStreamReader;
import java.io.Reader;
import com.google.gson.Gson;
import com.robot.Database.ZordsData;
import com.robot.controller.GameInitializer;
import com.robot.controller.SaveController;
import com.robot.controller.SelectionManager;
import com.robot.controller.ZordCreate;
import com.robot.model.StegoZord;
import model.TiledMap;
import model.TiledLayer;
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
    private static final int TILE_GRID = 32;
    private final double moveSpeed = 4.0;
    private static final String SAVE_FILE_NAME = "saveTesteRefatoração.json";

    private TriceraZord initialTricera;
    private TitanusFabric titanusFabric;
    private Pane root;
    private VBox infoBox;

    // mapa e colisão
    private TiledMap mapa;
    private int[][] colisao;
    private int mapaLargura;
    private int mapaAltura;
    private double cameraX = 0;
    private double cameraY = 0;
    private final double cameraSpeed = 8.0;

    private final Gson gson = new Gson();
    private static final Logger LOGGER = Logger.getLogger(GameApp.class.getName());
    private final ZordCreate zordCreate = new ZordCreate();
    private final SaveController saveController = new SaveController();

    @Override
    public void start(@NotNull Stage stage) {
        carregarMapaTiled(); // monta colisao[][]
        Group world = new Group();
        root = new Pane(world);

        clearGameWorld(this.root);
        // desenha o PNG do mapa completo como fundo
        Image bg = new Image(getClass().getResourceAsStream("/Map/Mapa.png")); // nome do seu PNG
        System.out.println("BG size: " + bg.getWidth() + " x " + bg.getHeight());
        ImageView bgView = new ImageView(bg);
        bgView.setLayoutX(0);
        bgView.setLayoutY(0);
        world.getChildren().add(bgView);

        initialTricera = new TriceraZord();
        titanusFabric = new TitanusFabric();


        if (!saveController.loadGame(titanusFabric, root)) {
            GameInitializer.initializeNewGame(initialTricera, titanusFabric, TILE_GRID);
        }

        // robôs acima do fundo
        world.getChildren().addAll(titanusFabric.getImageView(), initialTricera.getImageView());

        int windowWidth = 1280;   // 1184
        int windowHeight = 768; // 1344
        Scene scene = new Scene(root, windowWidth, windowHeight);
        scene.setFill(Color.web("#09C4C4")); // troca pelo hex do azul do mar
        Set<KeyCode> pressedKeys = new HashSet<>();

        scene.setOnKeyPressed(e -> pressedKeys.add(e.getCode()));
        scene.setOnKeyReleased(e -> pressedKeys.remove(e.getCode()));


        SelectionManager selectionManager = new SelectionManager(root, this);


        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double dxCam = 0;
                double dyCam = 0;

                if (pressedKeys.contains(KeyCode.W)) dyCam += cameraSpeed;
                if (pressedKeys.contains(KeyCode.S)) dyCam -= cameraSpeed;
                if (pressedKeys.contains(KeyCode.A)) dxCam += cameraSpeed;
                if (pressedKeys.contains(KeyCode.D)) dxCam -= cameraSpeed;

// limites: não deixar sair para além do mapa
                double maxOffsetX = 0;
                double minOffsetX = windowWidth - bg.getWidth();   // negativo
                double maxOffsetY = 0;
                double minOffsetY = windowHeight - bg.getHeight(); // negativo

                cameraX = clamp(cameraX + dxCam, minOffsetX, maxOffsetX);
                cameraY = clamp(cameraY + dyCam, minOffsetY, maxOffsetY);

                world.setTranslateX(cameraX);
                world.setTranslateY(cameraY);

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

    private boolean podeMoverPara(double pixelX, double pixelY) {
        int tileX = (int) (pixelX / TILE_GRID);
        int tileY = (int) (pixelY / TILE_GRID);

        if (tileX < 0 || tileY < 0 || tileX >= mapaLargura || tileY >= mapaAltura) {
            return false;
        }
        return colisao[tileY][tileX] == 0;
    }

    private void carregarMapaTiled() {
        try {
            InputStream in = getClass().getResourceAsStream("/Map/Mapa.tmj");
            if (in == null) {
                throw new RuntimeException("Recurso /Map/Mapa.tmj não encontrado no classpath!");
            }

            try (Reader reader = new InputStreamReader(in)) {
                mapa = gson.fromJson(reader, TiledMap.class);

                mapaLargura = mapa.width;
                mapaAltura = mapa.height;

                TiledLayer layerColisao = null;
                for (TiledLayer layer : mapa.layers) {
                    if ("Colisao".equals(layer.name)) {
                        layerColisao = layer;
                        break;
                    }
                }
                if (layerColisao == null) {
                    throw new RuntimeException("Layer 'Colisao' não encontrada no mapa!");
                }

                colisao = new int[mapaAltura][mapaLargura];
                for (int y = 0; y < mapaAltura; y++) {
                    for (int x = 0; x < mapaLargura; x++) {
                        int index = y * mapaLargura + x;
                        long gidLong = layerColisao.data[index];
                        int gid = (int) gidLong;
                        colisao[y][x] = (gid == 0) ? 0 : 1;
                    }
                }

                System.out.println("Mapa carregado: " + mapaLargura + "x" + mapaAltura);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar mapa Tiled", e);
        }
    }
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
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