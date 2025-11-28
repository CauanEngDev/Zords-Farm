package com.robot.model;

import static com.robot.Database.ZordsData.stegoZords;
import static com.robot.utils.FileFuction.*;
import static com.robot.utils.GameFunction.*;

import com.robot.controller.SaveController;
import com.robot.Interfaces.IAnimatable;
import com.robot.Interfaces.ISelectable;
import com.robot.controller.ZordCreate;
import com.robot.utils.SpriteAnimator;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import static com.robot.enums.AnimationState.*;

public class TitanusFabric implements IAnimatable, ISelectable {
    private static final String name = "TitanusZord Fabric";
    private int titanusLevel = 1;
    public int numStegos = 3;
    private boolean selected;

    private final ImageView titanusImageView;
    private final SpriteAnimator animator;

    private final ZordCreate createController = new ZordCreate()

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
    public void showInfoBox(Pane root, double x, double y, VBox infoBox) {
        if (infoBox != null) root.getChildren().remove(infoBox);

        infoBox = new VBox(5);
        infoBox.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 10; -fx-background-radius: 10;");

        Label lblName = new Label(name);
        lblName.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        Label lblLevel = new Label("Nível: " + titanusLevel);
        lblLevel.setStyle("-fx-text-fill: yellow;");

        Label lblStegos = new Label("Stegos: " + numStegos);
        lblStegos.setStyle("-fx-text-fill: white;");

        Button btnLevelUp = new Button("Upar (+1)");
        btnLevelUp.setOnAction(event -> {
            levelUp();
            lblLevel.setText("Nível: " + titanusLevel);
        });

        Button btnCreateZord = new Button("Criar Zord");
        btnCreateZord.setOnAction(event -> {
            if (stegoZords.size() < numStegos) {
                getAnimator().setActionOnFrame(17, ()-> {
                    stegoSpawn();
                });

                titanus.getAnimator().setActionOnFinish(() ->{
                    titanus.showIdleAnimation();
                });

                titanus.showCreateAnimation();
            } else println("Número máximo de StegoZords no mapa!");
        });
    }

    private void stegoSpawn() {
        StegoZord newStego = new ZordCreate().createStego();
        ImageView newStegoSprite = newStego.getImageView();

        double stegoSize = newStegoSprite.getBoundsInParent().getHeight();
        Bounds titanusSize = titanusSprite.getBoundsInParent();

        newStegoSprite.setLayoutX(titanusSprite.getLayoutX() - 70);
        newStegoSprite.setLayoutY(titanusSprite.getLayoutY() + stegoSize + 40);

        root.getChildren().add(newStegoSprite);
    }

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
