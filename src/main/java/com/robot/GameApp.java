package com.robot;

import com.google.gson.Gson;
import com.robot.controller.GameInitializer;
import com.robot.controller.SaveController;
import com.robot.controller.SelectionManager;
import com.robot.controller.ZordCreate;
import com.robot.model.TriceraZord;
import com.robot.model.TitanusFabric;
import com.robot.model.Zord;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Group;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static com.robot.Database.ZordsData.triceraZords;
//import static com.robot.Database.ZordsData.stegoZords;
import static com.robot.utils.GameFunction.clearGameWorld;

/**
 * Classe principal da aplicação JavaFX e o orquestrador do jogo.
 * Gerencia a inicialização, o loop principal e o carregamento/salvamento do estado do mundo.
 */
public class GameApp extends Application {
    private static final int TILE_GRID = 32;
    private final double moveSpeed = 4.0;

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
    private boolean gamePaused = false; // Flag pra pausar o AnimationTimer


    /**
     * Ponto de entrada da aplicação JavaFX. Configura o Stage e a Scene.
     * @param stage O Stage principal da aplicação (a Janela).
     */
    @Override
    public void start(@NotNull Stage stage) {
        // 1. INICIALIZAÇÃO DE UI E HIERARQUIA (ORDEM CRÍTICA)
        world = new Group();
        this.root = new Pane(world); // Inicializa root DEPOIS do world

        // 2. CARREGAMENTO DO MAPA E ZORDS
        // Adquirimos o mapa de colisão
        this.collisionMap = GameInitializer.getCollisonMap(); // Se o GameInitializer for estático
        selectionManager = new SelectionManager(root, this);

        initialTricera = new TriceraZord();
        titanusFabric = new TitanusFabric(this.world, this.selectionManager, this.collisionMap, TILE_GRID); // Passa dependências

        // 3. TENTA CARREGAR O JOGO
        clearGameWorld(this.root); // O root AGORA está inicializado
        triceraZords.add(initialTricera);

        if (!saveController.loadGame(titanusFabric, world)) {
            GameInitializer.initializeNewGame(initialTricera, titanusFabric, TILE_GRID);
            // Adiciona entidades ao mundo (Layer World)
            world.getChildren().addAll(titanusFabric.getImageView(), initialTricera.getImageView());
        }


        int windowWidth = 1280;
        int windowHeight = 768;
        Scene scene = new Scene(root, windowWidth, windowHeight);

        // Desenha o mapa PNG (fundo)
        Image bg = new Image(getClass().getResourceAsStream("/Map/Mapa.png"));
        ImageView bgView = new ImageView(bg);
        bgView.setLayoutX(0);
        bgView.setLayoutY(0);
        world.getChildren().add(0, bgView); // Adiciona na posição 0 para ser o background

        // Inicializa o menu de pausa
        pauseMenu = createPauseMenu();

        // Centraliza o menu (usando Bindings é mais robusto)
        pauseMenu.layoutXProperty().bind(scene.widthProperty().subtract(pauseMenu.widthProperty()).divide(2));
        pauseMenu.layoutYProperty().bind(scene.heightProperty().subtract(pauseMenu.heightProperty()).divide(2));

        // Controle de câmera e input
        Set<KeyCode> pressedKeys = new HashSet<>();
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE)
                togglePause(!gamePaused);
            pressedKeys.add(e.getCode());
        });
        scene.setOnKeyReleased(e -> pressedKeys.remove(e.getCode()));


        // Inicializa o registro de cliques
        selectionManager.setupInputHandlers(triceraZords, titanusFabric);

        // 7. O GAME LOOP (O CORAÇÃO DO JOGO)
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // --- 7.1 LÓGICA DA CÂMERA ---
                double dxCam = 0;
                double dyCam = 0;

                if (gamePaused) return;

                if (pressedKeys.contains(KeyCode.W)) dyCam += cameraSpeed;

                if (pressedKeys.contains(KeyCode.S)) dyCam -= cameraSpeed;

                if (pressedKeys.contains(KeyCode.A)) dxCam += cameraSpeed;

                if (pressedKeys.contains(KeyCode.D)) dxCam -= cameraSpeed;

                // limites: não deixar sair para além do mapa

                double maxOffsetX = 0;
                double minOffsetX = windowWidth - bg.getWidth(); // negativo

                double maxOffsetY = 0;
                double minOffsetY = windowHeight - bg.getHeight(); // negativo

                cameraX = clamp(cameraX + dxCam, minOffsetX, maxOffsetX);
                cameraY = clamp(cameraY + dyCam, minOffsetY, maxOffsetY);

                world.setTranslateX(cameraX);
                world.setTranslateY(cameraY);

                // --- 7.2 LÓGICA DO EXÉRCITO ---
                Set<Zord> allZords = new HashSet<>();
                allZords.addAll(triceraZords);

                for (Zord zord : allZords) {
                    ImageView zordView = zord.getImageView();
                    double currentX = zordView.getLayoutX();
                    double currentY = zordView.getLayoutY();

                    double targetX = zord.getTargetX();
                    double targetY = zord.getTargetY();

                    if (targetX != currentX || targetY != currentY) {
                        double deltax  = targetX - currentX;
                        double deltay = targetY - currentY;
                        double distance = Math.sqrt(Math.pow(deltax, 2) + Math.pow(deltay, 2));

                        if (distance < moveSpeed) {
                            zordView.setLayoutX(targetX);
                            zordView.setLayoutY(targetY);
                            zord.showIdleAnimation();
                        } else {
                            if (targetX < currentX) zordView.setScaleX(-1.0);
                            else if (targetX > currentX) zordView.setScaleX(1.0);

                            double stepX = (deltax/distance) * moveSpeed;
                            double stepY = (deltay/distance) * moveSpeed;

                            //Aqui viria checagem de colisão mas não conseguimos implemetar

                            zordView.setLayoutX(currentX + stepX);
                            zordView.setLayoutY(currentY + stepY);
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


    /**
     * Método auxiliar para calcular limites (clamp) de movimento.
     * @param value O valor atual.
     * @param min O valor mínimo permitido.
     * @param max O valor máximo permitido.
     * @return O valor limitado entre min e max.
     */
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Remove o VBox de informações da tela e zera a referência.
     */
    public void hideInfoBox() {
        if (this.infoBox != null && this.root != null) {
            this.root.getChildren().remove(this.infoBox);
            this.infoBox = null;
        }
    }

    /**
     * Retorna a referência ao Grupo do mundo (camada visual do jogo).
     * @return O objeto Group que contém todas as entidades e o mapa.
     */
    public Group getWorld() {
        return this.world;
    }

    // Em GameApp.java (Adicione este método)

    /**
     * Cria o VBox (o menu) com os botões Salvar e Sair.
     * @return O VBox pronto para ser adicionado à cena.
     */
    private VBox createPauseMenu() {
        // 1. Botão de Salvar
        Button btnSave = new Button("Salvar Jogo");
        btnSave.setOnAction(e -> {
            saveController.saveGame(titanusFabric);
            togglePause(false); // Despausa e volta ao jogo
        });

        // 2. Botão de Sair
        Button btnExit = new Button("Sair do Jogo");
        btnExit.setOnAction(e -> Platform.exit());

        // 3. Layout e Estilo (Centraliza os botões)
        VBox menuBox = new VBox(20, btnSave, btnExit);
        menuBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9); -fx-padding: 30; -fx-background-radius: 10;");
        menuBox.setAlignment(Pos.CENTER);

        return menuBox;
    }

    private void togglePause(boolean pauseState) {
        this.gamePaused = pauseState;

        if (pauseState) {
            // Pausando: Adiciona o menu ao topo do root
            if (!root.getChildren().contains(pauseMenu)) {
                root.getChildren().add(pauseMenu);
            }
        } else {
            // Despausando: Remove o menu
            root.getChildren().remove(pauseMenu);
        }
    }

    /**
     * Ponto de entrada principal do Java (apenas lança o aplicativo).
     * @param args Argumentos de linha de comando.
     */
    public static void main(String[] args) {
        launch(args);
    }
}