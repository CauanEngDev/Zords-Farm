package com.robot.model;

import com.robot.Interfaces.IAnimatable;
import com.robot.Interfaces.ISelectable;
import com.robot.enums.ZordFunction;
import static com.robot.enums.AnimationState.*;
import com.robot.utils.SpriteAnimator;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * Classe Abstrata base para todos os Zords.
 * Implementa a lógica de animação (Composição) e o contrato de Seleção.
 */
public abstract class Zord implements IAnimatable, ISelectable {
    protected ImageView zordImageView;
    protected final String name;
    protected ZordFunction function;
    protected int energy;
    protected SpriteAnimator animator;
    protected boolean selected = false;
    protected double targetX;
    protected double targetY;


    /**
     * Construtor para inicializar o Zord com seus assets e função.
     */
    public Zord(Image zordImageIdle, Image zordImageWalking, Image zordImageWork, String name,  ZordFunction function) {
        this.zordImageView = new ImageView(zordImageIdle);
        this.name = name;
        this.function = function;
        this.energy = 100;

        // Configuração visual básica (1x escala)
        this.zordImageView.setScaleX(1);
        this.zordImageView.setScaleY(1);
        this.zordImageView.setPreserveRatio(true);
        this.zordImageView.setPickOnBounds(false);

        // Inicialização do motor de animação (Composição)
        this.animator = new SpriteAnimator(this.zordImageView);

        // Adiciona e inicia as animações
        this.animator.addAnimation(IDLE, zordImageIdle);
        this.animator.addAnimation(WALKING, zordImageWalking);
        this.animator.addAnimation(WORKING, zordImageWork);

        this.animator.play(IDLE);
    }

    // --- MÉTODOS IANIMATABLE ---
    @Override
    public SpriteAnimator getAnimator() {
        return this.animator;
    }

    public void showWalkAnimation() {
        this.animator.play(WALKING);
    }

    public void showIdleAnimation() {
        this.animator.play(IDLE);
    }

    // --- MÉTODOS ISELECTABLE ---
    @Override
    public void select() {
        if (selected) return;
        selected = true;
        this.zordImageView.setStyle("-fx-effect: dropshadow(three-pass-box, yellow, 10, 0.5, 0, 0);");
    }

    @Override
    public void deselect() {
        if (!selected) return;
        selected = false;
        this.zordImageView.setStyle(null);
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public ImageView getImageView() { return zordImageView; }

    @Override
    public abstract VBox showInfoBox(Pane root, double x, double y); // Contrato que as filhas devem implementar

    // --- GETTERS E SETTERS DE ESTADO ---
    public int getEnergy() { return energy; }
    public ZordFunction getFunction() { return function; }
    public void setEnergy(int energy) { this.energy = energy; }
    public double getTargetX() { return this.targetX; }
    public double getTargetY() { return this.targetY; }

    public void setTarget(double newX, double newY) {
        this.targetX = newX;
        this.targetY = newY;
    }
}