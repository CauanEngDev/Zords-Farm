package com.robot.model;

import static com.robot.Database.ZordsData.*;
import static com.robot.utils.FileFuction.loadingImageSprite;
import com.robot.Interfaces.IAnimatable;
import com.robot.Interfaces.ISelectable;
import com.robot.controller.SelectionManager;
import com.robot.controller.ZordCreate; // Necessário para a função zordSpawn
import com.robot.enums.Zords;
import com.robot.utils.SpriteAnimator;
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
public class TitanusFabric extends EdificeZord {
    private static final String name = "TitanusZord Fabric";
    private static final Image TITANUS_IDLE_SPRITE = loadingImageSprite("/TitanusFabric/TitanusIdle.png");
    private static final Image TITANUS_CREATE_SPRITE = loadingImageSprite("/TitanusFabric/TitanusCreate.png");
    private int titanusLevel = 1;
    public int numTriceras = 3;
    public int numStegos = 3;

    // --- DEPENDÊNCIAS DO CONTEXTO DE JOGO ---
    private final int[][] collisionMap;
    private final int tileGrid;

    /**
     * Construtor, injetando as dependências do mundo.
     */
    public TitanusFabric(Group world, SelectionManager selectionManager, int[][] collisionMap, int tileGrid) {
        super(world, selectionManager, name, TITANUS_IDLE_SPRITE, TITANUS_CREATE_SPRITE);
        this.collisionMap = collisionMap;
        this.tileGrid = tileGrid;
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

        MenuItem stegoItem = new MenuItem("Construtor");
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

        boolean stegoLimit = stegoZords.size() >= numStegos;
        if (stegoLimit) {
            stegoItem.setDisable(true);
            stegoItem.setText("Construtor (Limite Alcançado)");
        }

        VBox newInfoBox = new VBox(5);
        newInfoBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 10; -fx-background-radius: 10;");

        Label lblName = new Label(name);
        lblName.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Label lblLevel = new Label("Nível: " + titanusLevel);
        lblLevel.setStyle("-fx-text-fill: yellow;");

        Label lblStegos = new Label(stegoZords.size() + "/" + numStegos + " Construtores");
        lblStegos.setStyle("-fx-text-fill: white;");

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
            numStegos += 1;
            numTriceras += 1;
            showInfoBox(root, x, y);
            selectionManager.deselectCurrent();
        });

        Button btnCreateZord = new Button("Criar Zord  >");
        btnCreateZord.setOnAction(event -> {
            createMenu.show(btnCreateZord, Side.BOTTOM, 0, 0);
        });

        stegoItem.setOnAction(event -> {
            getAnimator().setActionOnFrame(17, () -> {
                zordSpawn(Zords.STEGOZORD);
            });

            getAnimator().setActionOnFinish(() -> {
                showIdleAnimation();
            });

            showWorkAnimation();
            createMenu.hide();
            selectionManager.deselectCurrent();
        });

        triceraItem.setOnAction(event -> {
            getAnimator().setActionOnFrame(17, () -> {
                zordSpawn(Zords.TRICERAZORD);
            });

            getAnimator().setActionOnFinish(this::showIdleAnimation);

            showWorkAnimation();
            createMenu.hide();
            selectionManager.deselectCurrent();
        });

        newInfoBox.getChildren().addAll(lblName, lblLevel, lblStegos, lblTricera, btnCreateZord, btnLevelUp);

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
        WorkZord newWorkZord = new ZordCreate().createZordByTitanus(type);

        // O Titanus é o ponto de ancoragem para o spawn
        double spawnX = this.getImageView().getLayoutX() - 90; // Posição de Spawn (Ex: na frente do Titanus)
        double spawnY = this.getImageView().getLayoutY() + 70;

        ImageView newZordSprite = newWorkZord.getImageView();

        // 1. POSICIONAMENTO E ADIÇÃO AO WORLD
        newZordSprite.setLayoutX(spawnX);
        newZordSprite.setLayoutY(spawnY);
        newWorkZord.setTarget(spawnX, spawnY);

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
        selectionManager.registerUnitClick(newWorkZord);
    }
}