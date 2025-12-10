package com.robot;

// IMPORTANTE!!!!! COLOQUE ISTO NAS CONFIGURAÇÕES DE RUN -> javafx:run

import com.google.gson.Gson;
import com.robot.controller.GameInitializer;
import com.robot.controller.SaveController;
import com.robot.controller.SelectionManager;
import com.robot.controller.ZordCreate;
import com.robot.model.TriceraZord;
import com.robot.model.TitanusFabric;
import com.robot.model.WorkZord;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.scene.Group;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static com.robot.Database.ZordsData.stegoZords;
import static com.robot.Database.ZordsData.triceraZords;
import static com.robot.utils.GameFunction.clearGameWorld;

public class GameApp extends Application {
    private static final int TILE_GRID = 32;
    private final double moveSpeed = 4.0;

    // --- VARIÁVEIS DO MENU ---
    private Stage stage;
    private Scene cenaMenu;
    private Scene cenaJogo;
    private MediaPlayer mediaPlayer;
    private boolean podetocar = true;

    // --- VARIÁVEIS DO JOGO (MODELO) ---
    private TriceraZord initialTricera;
    private TitanusFabric titanusFabric;

    // --- VARIÁVEIS DE UI/RENDERIZAÇÃO ---
    private Pane root;
    private Group world;
    private VBox infoBox;
    private SelectionManager selectionManager;

    // --- VARIÁVEIS DE MAPA/CÂMERA ---
    private int[][] collisionMap;
    private double cameraX = 0;
    private double cameraY = 0;
    private final double cameraSpeed = 8.0;

    // --- CONTROLADORES/UTILITÁRIOS ---
    private final Gson gson = new Gson();
    private static final Logger LOGGER = Logger.getLogger(GameApp.class.getName());
    private final SaveController saveController = new SaveController();
    private final ZordCreate zordCreate = new ZordCreate();
    private final GameInitializer gameInitializer = new GameInitializer();
    private VBox pauseMenu;
    private boolean gamePaused = false;
    private AnimationTimer gameLoop; // Para poder parar ao voltar pro menu

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        criarMenu(); // Começa com o MENU
    }

    private void criarMenu() {
        Button btnJogar = new Button("Jogar");
        Button btnConfig = new Button("Configurações");
        Button btnSair = new Button("Sair");

        btnJogar.setOnAction(e -> iniciarJogo());
        btnConfig.setOnAction(e -> abrirConfiguracoes());
        btnSair.setOnAction(e -> stage.close());

        VBox vbox = new VBox(20, btnJogar, btnConfig, btnSair);
        vbox.setAlignment(Pos.CENTER);
        vbox.setId("root");

        cenaMenu = new Scene(vbox, 800, 600);
        try {
            cenaMenu.getStylesheets().add(getClass().getResource("/menu.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("CSS não encontrado, continuando sem estilo...");
        }

        stage.setScene(cenaMenu);
        stage.setTitle("Meu Jogo - Menu");
        stage.show();
    }

    private void iniciarJogo() {
        // === TODO O TEU CÓDIGO ATUAL DO JOGO AQUI (SEM MUDANÇA NENHUMA) ===

        // 1. INICIALIZAÇÃO DE UI E HIERARQUIA (ORDEM CRÍTICA)
        world = new Group();
        root = new Pane(world);

        // 2. CARREGAMENTO DO MAPA E ZORDS
        collisionMap = GameInitializer.getCollisonMap();
        selectionManager = new SelectionManager(root, this);

        initialTricera = new TriceraZord();
        titanusFabric = new TitanusFabric(world, selectionManager, collisionMap, TILE_GRID);

        clearGameWorld(root);
        triceraZords.add(initialTricera);

        if (!saveController.loadGame(titanusFabric, world)) {
            GameInitializer.initializeNewGame(initialTricera, titanusFabric, TILE_GRID);
            world.getChildren().addAll(titanusFabric.getImageView(), initialTricera.getImageView());
        }

        int windowWidth = 1280;
        int windowHeight = 768;
        cenaJogo = new Scene(root, windowWidth, windowHeight);

        // Desenha o mapa PNG (fundo)
        Image bg = new Image(getClass().getResourceAsStream("/Map/Mapa.png"));
        ImageView bgView = new ImageView(bg);
        bgView.setLayoutX(0);
        bgView.setLayoutY(0);
        world.getChildren().add(0, bgView);

        // Inicializa o menu de pausa
        pauseMenu = createPauseMenu();
        pauseMenu.layoutXProperty().bind(cenaJogo.widthProperty().subtract(pauseMenu.widthProperty()).divide(2));
        pauseMenu.layoutYProperty().bind(cenaJogo.heightProperty().subtract(pauseMenu.heightProperty()).divide(2));

        // === BOTÃO VOLTAR AO MENU (NOVO) ===
        Button btnVoltarMenu = new Button("← Voltar ao Menu");
        btnVoltarMenu.setStyle("-fx-background-color: rgba(255,0,0,0.8); -fx-text-fill: white; -fx-font-size: 16px;");
        btnVoltarMenu.setLayoutX(10);
        btnVoltarMenu.setLayoutY(10);
        btnVoltarMenu.setOnAction(e -> {
            if (gameLoop != null) gameLoop.stop();
            stage.setScene(cenaMenu);
            stage.setTitle("Meu Jogo - Menu");
        });
        root.getChildren().add(btnVoltarMenu);

        // Controle de câmera e input
        Set<KeyCode> pressedKeys = new HashSet<>();
        cenaJogo.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) togglePause(!gamePaused);
            pressedKeys.add(e.getCode());
        });
        cenaJogo.setOnKeyReleased(e -> pressedKeys.remove(e.getCode()));

        selectionManager.setupInputHandlers(triceraZords, titanusFabric);
        selectionManager.setupInputHandlers(stegoZords, titanusFabric);

        // 7. O GAME LOOP (O CORAÇÃO DO JOGO)
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double dxCam = 0;
                double dyCam = 0;

                if (gamePaused) return;

                if (pressedKeys.contains(KeyCode.W)) dyCam += cameraSpeed;
                if (pressedKeys.contains(KeyCode.S)) dyCam -= cameraSpeed;
                if (pressedKeys.contains(KeyCode.A)) dxCam += cameraSpeed;
                if (pressedKeys.contains(KeyCode.D)) dxCam -= cameraSpeed;

                double maxOffsetX = 0;
                double minOffsetX = windowWidth - bg.getWidth();
                double maxOffsetY = 0;
                double minOffsetY = windowHeight - bg.getHeight();

                cameraX = clamp(cameraX + dxCam, minOffsetX, maxOffsetX);
                cameraY = clamp(cameraY + dyCam, minOffsetY, maxOffsetY);

                world.setTranslateX(cameraX);
                world.setTranslateY(cameraY);

                Set<WorkZord> allWorkZords = new HashSet<>();
                allWorkZords.addAll(triceraZords);
                allWorkZords.addAll(stegoZords);

                for (WorkZord workZord : allWorkZords) {
                    ImageView zordView = workZord.getImageView();
                    double currentX = zordView.getLayoutX();
                    double currentY = zordView.getLayoutY();

                    double targetX = workZord.getTargetX();
                    double targetY = workZord.getTargetY();

                    if (targetX != currentX || targetY != currentY) {
                        double deltax = targetX - currentX;
                        double deltay = targetY - currentY;
                        double distance = Math.sqrt(Math.pow(deltax, 2) + Math.pow(deltay, 2));

                        if (distance < moveSpeed) {
                            zordView.setLayoutX(targetX);
                            zordView.setLayoutY(targetY);
                            workZord.showIdleAnimation();
                        } else {
                            if (targetX < currentX) zordView.setScaleX(-1.0);
                            else if (targetX > currentX) zordView.setScaleX(1.0);

                            double stepX = (deltax / distance) * moveSpeed;
                            double stepY = (deltay / distance) * moveSpeed;

                            zordView.setLayoutX(currentX + stepX);
                            zordView.setLayoutY(currentY + stepY);
                            workZord.showWalkAnimation();
                        }
                    }
                }
            }
        };

        gameLoop.start();

        stage.setScene(cenaJogo);
        stage.setTitle("Meu teste de refatoração");
        root.requestFocus();

        // === MÚSICA DO MENU ===
        if (mediaPlayer == null) {
            URL url = getClass().getResource("/musica.mp3");
            System.out.println("URL musica = " + url);
            if (url == null) {
                System.out.println("musica.mp3 NÃO encontrado no classpath!");
            } else {
                Media media = new Media(url.toExternalForm());
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            }
        }
        if (podetocar) {
            mediaPlayer.play();
        }
    }

    private void abrirConfiguracoes() {
        VBox vboxConfig = new VBox(20);
        vboxConfig.setAlignment(Pos.CENTER);
        vboxConfig.setId("root");

        CheckBox chkMusica = new CheckBox("Habilitar Música");
        chkMusica.setSelected(true);
        chkMusica.setId("chk-musica");

        Button btnVoltar = new Button("Voltar");
        btnVoltar.setId("btn-voltar");

        chkMusica.setOnAction(e -> {
            podetocar = chkMusica.isSelected();
            System.out.println("Música: " + (podetocar ? "LIGADA" : "DESLIGADA"));
        });

        btnVoltar.setOnAction(e -> stage.setScene(cenaMenu));

        vboxConfig.getChildren().addAll(chkMusica, btnVoltar);

        Scene cenaConfig = new Scene(vboxConfig, 800, 600);
        try {
            cenaConfig.getStylesheets().add(getClass().getResource("/menu.css").toExternalForm());
        } catch (Exception e) {
            System.out.println("CSS não encontrado...");
        }

        stage.setScene(cenaConfig);
        stage.setTitle("Meu Jogo - Configurações");
    }

    // === MÉTODOS DO JOGO (SEM MUDANÇA) ===
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public void hideInfoBox() {
        if (this.infoBox != null && this.root != null) {
            this.root.getChildren().remove(this.infoBox);
            this.infoBox = null;
        }
    }

    public Group getWorld() {
        return this.world;
    }

    private VBox createPauseMenu() {
        Button btnSave = new Button("Salvar Jogo");
        btnSave.setOnAction(e -> {
            saveController.saveGame(titanusFabric);
            togglePause(false);
        });

        Button btnExit = new Button("Sair do Jogo");
        btnExit.setOnAction(e -> Platform.exit());

        VBox menuBox = new VBox(20, btnSave, btnExit);
        menuBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9); -fx-padding: 30; -fx-background-radius: 10;");
        menuBox.setAlignment(Pos.CENTER);

        return menuBox;
    }

    private void togglePause(boolean pauseState) {
        this.gamePaused = pauseState;
        if (pauseState) {
            if (!root.getChildren().contains(pauseMenu)) {
                root.getChildren().add(pauseMenu);
            }
        } else {
            root.getChildren().remove(pauseMenu);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
