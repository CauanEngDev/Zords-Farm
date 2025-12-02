package com.robot.model;

import static com.robot.Database.ZordsData.*;
import static com.robot.utils.FileFuction.loadingImageSprite;
import com.robot.Interfaces.IAnimatable;
import com.robot.Interfaces.ISelectable;
import com.robot.controller.SelectionManager;
import com.robot.controller.ZordCreate; // Necessário para a função zordSpawn
import com.robot.enums.Zords;
import com.robot.utils.SpriteAnimator;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import static com.robot.enums.AnimationState.*;

/**
 * Representa o Titanus Zord (Fábrica/Estrutura Principal).
 * Gerencia a produção de unidades, a sua animação e a exibição de suas informações.
 * Implementa IAnimatable e ISelectable.
 */
public class TitanusFabric implements IAnimatable, ISelectable {
    private static final String name = "TitanusZord Fabric";
    private int titanusLevel = 1;
    public int numTriceras = 3;
    private boolean selected;

    // --- DEPENDÊNCIAS DO CONTEXTO DE JOGO ---
    private final Group world; // Camada visual principal (câmera)
    private final SelectionManager selectionManager;
    private final int[][] collisionMap;
    private final int tileGrid;

    // --- ESTADO VISUAL E ANIMAÇÃO ---
    private final ImageView titanusImageView;
    private final SpriteAnimator animator;

    /**
     * Construtor, injetando as dependências do mundo.
     */
    public TitanusFabric(Group world, SelectionManager selectionManager, int[][] collisionMap, int tileGrid) {
        this.world = world;
        this.selectionManager = selectionManager;
        this.collisionMap = collisionMap;
        this.tileGrid = tileGrid;

        Image titanusIdle = loadingImageSprite("/TitanusFabric/TitanusIdle.png");
        this.titanusImageView = new ImageView(titanusIdle);
        Image titanusCreate = loadingImageSprite("/TitanusFabric/TitanusCreate.png");

        // Configuração visual padrão (o scale foi movido para o GameApp)
        this.titanusImageView.setScaleX(1);
        this.titanusImageView.setScaleY(1);
        this.titanusImageView.setPreserveRatio(true);
        this.titanusImageView.setPickOnBounds(false);

        this.animator = new SpriteAnimator(titanusImageView);

        // Adiciona e inicia as animações
        this.animator.addAnimation(IDLE, titanusIdle);
        this.animator.addAnimation(WORKING, titanusCreate);

        this.animator.play(IDLE);
    }

    // --- MÉTODOS DE ANIMAÇÃO E ESTADO ---

    @Override
    public SpriteAnimator getAnimator() {
        return this.animator;
    }

    public void showIdleAnimation() {
        this.animator.play(IDLE);
    }

    public void showCreateAnimation() {
        this.animator.playOneShot(WORKING);
    }

    // --- MÉTODOS ISELECTABLE ---

    @Override
    public void select() {
        if (selected) return;
        selected = true;
        this.titanusImageView.setStyle("-fx-effect: dropshadow(three-pass-box, yellow, 10, 0.5, 0, 0);");
    }

    @Override
    public void deselect() {
        if (!selected) return;
        selected = false;
        this.titanusImageView.setStyle(null);
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public ImageView getImageView() {
        return titanusImageView;
    }

    /**
     * Cria e exibe a caixa de informações do Titanus.
     * @param root O Pane raiz da Scene (para remover a caixa antiga).
     * @param x Coordenada X do clique (para posicionamento).
     * @param y Coordenada Y do clique (para posicionamento).
     * @return O novo VBox criado para ser gerenciado pelo SelectionManager.
     */
    @Override
    public VBox showInfoBox(Pane root, double x, double y) {
        if (root.getChildren().stream().anyMatch(node -> node instanceof VBox)) {
            // Remove a caixa antiga se ela for uma VBox (melhor que passar como parâmetro)
            root.getChildren().removeIf(node -> node instanceof VBox);
        }

        MenuItem stegoItem = new MenuItem("Construtor (Coming Soon)");
        stegoItem.setDisable(true);
        MenuItem redMagicItem = new MenuItem("Combatente (Coming Soon)");
        redMagicItem.setDisable(true);
        MenuItem triceraItem = new MenuItem("Minerador");
        ContextMenu createMenu = new ContextMenu(stegoItem, redMagicItem, triceraItem);

        // Lógica de Limite
        boolean triceraLimit = triceraZords.size() >= numTriceras;
        if (triceraLimit) {
            triceraItem.setDisable(true);
            triceraItem.setText("Minerador (Limite alcançado)");
        }

        VBox newInfoBox = new VBox(5);
        newInfoBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 10; -fx-background-radius: 10;");

        Label lblName = new Label(name);
        lblName.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Label lblLevel = new Label("Nível: " + titanusLevel);
        lblLevel.setStyle("-fx-text-fill: yellow;");

//        Label lblStegos = new Label(stegoZords.size() + "/" + numStegos + " Construtores");
//        lblStegos.setStyle("-fx-text-fill: white;");

        Label lblTricera = new Label(triceraZords.size() + "/" + numTriceras + " Mineradores");
        lblTricera.setStyle("-fx-text-fill: white;");

        boolean maxLevel = titanusLevel >=5;
        Button btnLevelUp = new Button("Upar (+1)");
        if (maxLevel) {
            btnLevelUp.setDisable(true);
            btnLevelUp.setText("MAX LEVEL");
        }
        btnLevelUp.setOnAction(event -> {
            levelUp();
            lblLevel.setText("Nível: " + titanusLevel);
//                numStegos += 1;
            numTriceras += 1;
            showInfoBox(root, x, y);
        });

        Button btnCreateZord = new Button("Criar Zord  >");
        btnCreateZord.setOnAction(event -> {
            createMenu.show(btnCreateZord, Side.BOTTOM, 0, 0);
        });

//        stegoItem.setOnAction(event -> {
//                createController.createZordByTitanus(Zords.STEGOZORD);
//                createMenu.hide();
//        });

        triceraItem.setOnAction(event -> {
            getAnimator().setActionOnFrame(17, () -> {
                zordSpawn(Zords.TRICERAZORD);
            });

            getAnimator().setActionOnFinish(() -> {
                showIdleAnimation();
            });

            showCreateAnimation();
            createMenu.hide();
            selectionManager.deselectCurrent();
        });

        newInfoBox.getChildren().addAll(lblName, lblLevel, lblTricera, btnCreateZord, btnLevelUp);

        // Posicionamento
        newInfoBox.setLayoutX(this.getImageView().getLayoutX() + 40);
        newInfoBox.setLayoutY(this.getImageView().getLayoutY() - 40);

        root.getChildren().add(newInfoBox);
        return newInfoBox;
    }

    /**
     * Lógica que cria a nova unidade, a posiciona e a registra no mundo.
     * É chamada no Frame 17 da animação de criação.
     * @param type O tipo de Zord a ser criado (TRICERA, STEGO, etc.).
     */
    private void zordSpawn(Zords type) {
        // NOTA: O ZordCreate é a dependência que cria a instância.
        Zord newZord = new ZordCreate().createZordByTitanus(type);

        // O Titanus é o ponto de ancoragem para o spawn
        double spawnX = this.getImageView().getLayoutX() - 90; // Posição de Spawn (Ex: na frente do Titanus)
        double spawnY = this.getImageView().getLayoutY() + 70;

        ImageView newZordSprite = newZord.getImageView();

        // 1. POSICIONAMENTO E ADIÇÃO AO WORLD
        newZordSprite.setLayoutX(spawnX);
        newZordSprite.setLayoutY(spawnY);
        newZord.setTarget(spawnX, spawnY);

        // Adiciona ao Grupo (o mundo da câmera)
        this.world.getChildren().add(newZordSprite);

        // 2. ATUALIZAÇÃO DA MATRIZ DE COLISÃO
        int tileX = (int) (spawnX / this.tileGrid);
        int tileY = (int) (spawnY / this.tileGrid);

        if (tileY >= 0 && tileY < this.collisionMap.length && tileX >= 0 && tileX < this.collisionMap[0].length) {
            // Marca a célula como "Ocupada por Unidade" (Colisão dinâmica)
            this.collisionMap[tileY][tileX] = 2;
        }

        // 3. REGISTRO DE CLIQUE (TORNA O NOVO ZORD CLICÁVEL)
        selectionManager.registerUnitClick(newZord);
    }

    // --- GETTERS E SETTERS DE ESTADO ---

    public int getLevel() {
        return titanusLevel;
    }

    public void setLevel(int titanusLevel) {
        this.titanusLevel = titanusLevel;
    }

    public void levelUp() {
        this.titanusLevel++;
    }
}