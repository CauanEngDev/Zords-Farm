package com.robot.model;

import static com.robot.Database.ZordsData.*;
import static com.robot.utils.FileFuction.*;
import com.robot.Interfaces.IAnimatable;
import com.robot.Interfaces.ISelectable;
import com.robot.controller.ZordCreate;
import com.robot.enums.Zords;
import com.robot.utils.SpriteAnimator;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import static com.robot.enums.AnimationState.*;

public class TitanusFabric implements IAnimatable, ISelectable {
    private static final String name = "TitanusZord Fabric";
    private int titanusLevel = 1;
//    public int numStegos = 3;
    public int numTriceras = 3;
    private boolean selected;

    private final ImageView titanusImageView;
    private final SpriteAnimator animator;

    private final ZordCreate createController = new ZordCreate();

    public TitanusFabric() {
        Image titanusIdle = loadingImageSprite("/TitanusFabric/TitanusIdle.png");
        this.titanusImageView = new ImageView(titanusIdle);
        Image titanusCreate = loadingImageSprite("/TitanusFabric/TitanusCreate.png");

        this.titanusImageView.setScaleX(1);
        this.titanusImageView.setScaleY(1);
        this.titanusImageView.setPreserveRatio(true);
        this.titanusImageView.setPickOnBounds(false);

        this.animator = new SpriteAnimator(titanusImageView);

        this.animator.addAnimation(IDLE, titanusIdle);
        this.animator.addAnimation(WORKING, titanusCreate);

        this.animator.play(IDLE);
    }

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

    @Override
    public void showInfoBox(Pane root, double x, double y, VBox oldInfoBox) {
        if (oldInfoBox != null) root.getChildren().remove(oldInfoBox);

        MenuItem stegoItem = new MenuItem("Construtor (Coming Soon)");
        stegoItem.setDisable(true);
        MenuItem redMagicItem = new MenuItem("Combatente (Coming Soon)");
        redMagicItem.setDisable(true);
        MenuItem triceraItem = new MenuItem("Minerador");
        ContextMenu createMenu = new ContextMenu(stegoItem, redMagicItem, triceraItem);

//        boolean stegoLimit = stegoZords.size() >= numStegos;
        boolean triceraLimit = triceraZords.size() >= numTriceras;

//        if (stegoLimit) {
//            stegoItem.setDisable(true);
//            stegoItem.setText("Construtor (Limite alcançado)");
//        }

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
        btnLevelUp.setOnAction(event -> {
            if (!maxLevel) {
                levelUp();
                lblLevel.setText("Nível: " + titanusLevel);
//                numStegos += 1;
                numTriceras += 1;
                showInfoBox(root, x, y, newInfoBox);
            } else {
                btnLevelUp.setDisable(true);
                btnLevelUp.setText("MAX LEVEL");
            }
        });

        Button btnCreateZord = new Button("Criar Zord");
        btnCreateZord.setOnMouseEntered(event -> {
            createMenu.show(btnCreateZord, Side.BOTTOM, 0, 0);
        });

//        stegoItem.setOnAction(event -> {
//                createController.createZordByTitanus(Zords.STEGOZORD);
//                createMenu.hide();
//        });

        triceraItem.setOnAction(event -> {
                createController.createZordByTitanus(Zords.TRICERAZORD);
                createMenu.hide();
        });

        btnCreateZord.setOnMouseExited(event -> {
            createMenu.hide();
        });

        newInfoBox.getChildren().addAll(lblName,lblLevel, lblTricera, btnCreateZord, btnLevelUp);
        root.getChildren().add(newInfoBox);
    }

//    private void stegoSpawn() {
//        StegoZord newStego = new ZordCreate().createZordByTitanus();
//        ImageView newStegoSprite = newStego.getImageView();
//
//        double stegoSize = newStegoSprite.getBoundsInParent().getHeight();
//        Bounds titanusSize = titanusSprite.getBoundsInParent();
//
//        newStegoSprite.setLayoutX(titanusSprite.getLayoutX() - 70);
//        newStegoSprite.setLayoutY(titanusSprite.getLayoutY() + stegoSize + 40);
//
//        root.getChildren().add(newStegoSprite);
//    }

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
