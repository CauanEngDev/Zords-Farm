package com.robot;
import javafx.scene.paint.Color;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
//import javafx.scene.input.KeyEvent;
//import javafx.scene.control.CheckBox;
//import javafx.scene.media.Media;
//import javafx.scene.media.MediaPlayer;
//import java.net.URL;
//import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.InputStream;
import javafx.scene.input.KeyCode;
import javafx.scene.Group;
//import java.io.InputStreamReader;
//import java.io.Reader;
//import java.io.InputStreamReader;
//import java.io.Reader;
import com.google.gson.Gson;
import com.robot.controller.GameInitializer;
import com.robot.controller.SaveController;
import com.robot.controller.SelectionManager;

import static com.robot.Database.ZordsData.*;
import com.robot.model.TitanusFabric;
import com.robot.model.TriceraZord;
import com.robot.model.Zord;
import javafx.animation.AnimationTimer;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static com.robot.utils.GameFunction.clearGameWorld;

public class GameApp extends Application {
    private static final int TILE_GRID = 32;
    private final double moveSpeed = 4.0;
    private static final String SAVE_FILE_NAME = "saveTesteRefatoração.json";

    private TriceraZord initialTricera;
    private TitanusFabric titanusFabric;
    private Pane root;
    private Group world;
    private VBox infoBox;
    private SelectionManager selectionManager;
    private int[][] collisionMap;

//    private Stage stage;
//    private Scene cenaJogo;
//    private Scene cenaMenu;
//    private MediaPlayer mediaPlayer;
//    private boolean podetocar = true;

    private double cameraX = 0;
    private double cameraY = 0;
    private final double cameraSpeed = 8.0;

    private final Gson gson = new Gson();
    private static final Logger LOGGER = Logger.getLogger(GameApp.class.getName());
    private final SaveController saveController = new SaveController();

    @Override
    public void start(@NotNull Stage stage) {
//        this.stage = primaryStage;
        // Cria o menu inicial
//        criarMenu();

        world = new Group();
        root = new Pane(world);
        clearGameWorld(this.root);
        // desenha o PNG do mapa completo como fundo
        Image bg = new Image(getClass().getResourceAsStream("/Map/Mapa.png")); // nome do seu PNG
        System.out.println("BG size: " + bg.getWidth() + " x " + bg.getHeight());
        ImageView bgView = new ImageView(bg);
        bgView.setLayoutX(0);
        bgView.setLayoutY(0);
        world.getChildren().add(bgView);
        collisionMap = GameInitializer.getCollisonMap();
        initialTricera = new TriceraZord();
        titanusFabric = new TitanusFabric(this.world, this.selectionManager, this.collisionMap, TILE_GRID);


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


        selectionManager = new SelectionManager(root, this);
        selectionManager.setupInputHandlers(triceraZords, titanusFabric);
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
//    private void iniciarjogo(){
//        world = new Group();
//        root = new Pane(world);
//        clearGameWorld(this.root);
//        // desenha o PNG do mapa completo como fundo
//        Image bg = new Image(getClass().getResourceAsStream("/Map/Mapa.png")); // nome do seu PNG
//        System.out.println("BG size: " + bg.getWidth() + " x " + bg.getHeight());
//        ImageView bgView = new ImageView(bg);
//        bgView.setLayoutX(0);
//        bgView.setLayoutY(0);
//        world.getChildren().add(bgView);
//
//        initialTricera = new TriceraZord();
//        titanusFabric = new TitanusFabric();
//
//        int[][] collisionMap = GameInitializer.getCollisonMap();
//
//        if (!saveController.loadGame(titanusFabric, root)) {
//            GameInitializer.initializeNewGame(initialTricera, titanusFabric, TILE_GRID);
//        }
//
//        // robôs acima do fundo
//        world.getChildren().addAll(titanusFabric.getImageView(), initialTricera.getImageView());
//
//        int windowWidth = 1280;   // 1184
//        int windowHeight = 768; // 1344
//        Scene scene = new Scene(root, windowWidth, windowHeight);
//        scene.setFill(Color.web("#09C4C4")); // troca pelo hex do azul do mar
//        Set<KeyCode> pressedKeys = new HashSet<>();
//
//        scene.setOnKeyPressed(e -> pressedKeys.add(e.getCode()));
//        scene.setOnKeyReleased(e -> pressedKeys.remove(e.getCode()));
//
//
//        selectionManager = new SelectionManager(root, this);
//        AnimationTimer gameLoop = new AnimationTimer() {
//            @Override
//            public void handle(long now) {
//                double dxCam = 0;
//                double dyCam = 0;
//
//                if (pressedKeys.contains(KeyCode.W)) dyCam += cameraSpeed;
//                if (pressedKeys.contains(KeyCode.S)) dyCam -= cameraSpeed;
//                if (pressedKeys.contains(KeyCode.A)) dxCam += cameraSpeed;
//                if (pressedKeys.contains(KeyCode.D)) dxCam -= cameraSpeed;
//
//// limites: não deixar sair para além do mapa
//                double maxOffsetX = 0;
//                double minOffsetX = windowWidth - bg.getWidth();   // negativo
//                double maxOffsetY = 0;
//                double minOffsetY = windowHeight - bg.getHeight(); // negativo
//
//                cameraX = clamp(cameraX + dxCam, minOffsetX, maxOffsetX);
//                cameraY = clamp(cameraY + dyCam, minOffsetY, maxOffsetY);
//
//                world.setTranslateX(cameraX);
//                world.setTranslateY(cameraY);
//
//                Set<Zord> allZords = new HashSet<>();
//                allZords.addAll(stegoZords);
//                allZords.addAll(triceraZords);
//
//                for (Zord zord : allZords) {
//                    double currentX = zord.getImageView().getLayoutX();
//                    double currentY = zord.getImageView().getLayoutY();
//
//                    double targetX = zord.getTargetX();
//                    double targetY = zord.getTargetY();
//
//                    if (targetX != currentX || targetY != currentY) {
//                        double deltax  = targetX - currentX;
//                        double deltay = targetY - currentY;
//                        double distance = Math.sqrt(Math.pow(deltax, 2) + Math.pow(deltay, 2));
//
//                        if (distance < moveSpeed) {
//                            zord.getImageView().setLayoutX(targetX);
//                            zord.getImageView().setLayoutY(targetY);
//                            zord.showIdleAnimation();
//                        } else {
//                            if (targetX < currentX) zord.getImageView().setScaleX(-1.0);
//                            else if (targetX > currentX) zord.getImageView().setScaleX(1.0);
//
//                            double stepX = (deltax/distance) * moveSpeed;
//                            double stepY = (deltay/distance) * moveSpeed;
//
//                            zord.getImageView().setLayoutX(currentX + stepX);
//                            zord.getImageView().setLayoutY(currentY + stepY);
//                            zord.showWalkAnimation();
//                        }
//                    }
//                }
//            }
//        };
//
//        gameLoop.start();
//
//        stage.setTitle("Meu teste de refatoração");
//        stage.setScene(scene);
//        stage.show();
////        if (mediaPlayer == null) {
////            URL url = getClass().getResource("/musica.mp3");
////            System.out.println("URL musica = " + url);
////
////            if (url == null) {
////                throw new RuntimeException("musica.mp3 NÃO encontrado no classpath!");
////            }
////
////            Media media = new Media(url.toExternalForm());
////            mediaPlayer = new MediaPlayer(media);
////            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // loop infinito
////        }
////        if (podetocar == true){
////            mediaPlayer.play();
////        }
////    }
////    }
////    private void criarMenu() {
////        Button btnJogar = new Button("Jogar");
////        Button btnConfig = new Button("Configurações");
////        Button btnSair = new Button("Sair");
////
////        btnJogar.setOnAction(e -> iniciarJogo());
////        btnConfig.setOnAction(e -> abrirConfiguracoes());
////        btnSair.setOnAction(e -> stage.close());
////
////        VBox vbox = new VBox(20, btnJogar, btnConfig, btnSair);
////        vbox.setAlignment(Pos.CENTER);
////        vbox.setId("root");
////
////        cenaMenu = new Scene(vbox, 800, 600);
////        cenaMenu.getStylesheets().add(getClass().getResource("menu.css").toExternalForm());
////
////        stage.setScene(cenaMenu);
////        stage.setTitle("Meu Jogo - Menu");
////        stage.show();
////    }
////
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
    public void hideInfoBox() {
        if (this.infoBox != null && this.root != null) {
            this.root.getChildren().remove(this.infoBox);
            this.infoBox = null;
        }
    }
////
    public Group getWorld() {
        return this.world;
    }
////
////    private void abrirConfiguracoes() {
////        VBox vboxConfig = new VBox(20);
////        vboxConfig.setAlignment(Pos.CENTER);
////        vboxConfig.setId("root");
////
////        // Checkbox para música
////        CheckBox chkMusica = new CheckBox("Habilitar Música");
////        chkMusica.setSelected(true); // Padrão: música ligada
////        chkMusica.setId("chk-musica");
////
////        Button btnVoltar = new Button("Voltar");
////        btnVoltar.setId("btn-voltar");
////
////        // Salva estado da música (você pode usar Preferences ou arquivo)
////        chkMusica.setOnAction(e -> {
////            boolean musicaLigada = chkMusica.isSelected();
////            if (musicaLigada== true){
////                podetocar = true;
////            }else{
////                podetocar = false;
////            }
////            System.out.println("Música: " + (musicaLigada ? "LIGADA" : "DESLIGADA"));
////            // Aqui você salva a preferência e controla áudio do jogo
////        });
////
////        btnVoltar.setOnAction(e -> stage.setScene(cenaMenu));
////
////        vboxConfig.getChildren().addAll(chkMusica, btnVoltar);
////
////        Scene cenaConfig = new Scene(vboxConfig, 800, 600);
////        cenaConfig.getStylesheets().add(getClass().getResource("menu.css").toExternalForm());
////
////        stage.setScene(cenaConfig);
////        stage.setTitle("Meu Jogo - Configurações");
//        }

    public static void main(String[] args) {
        launch(args);
    }
}