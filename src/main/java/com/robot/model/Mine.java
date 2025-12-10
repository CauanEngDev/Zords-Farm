package com.robot.model;

import java.util.concurrent.ThreadLocalRandom;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


/**
 * Classe da mina do jogo, onde os recursos necessários pra criação de zords
 * e construções serião obtitds (Não implementado)
 */
public class Mine {
    private ImageView imageView;
    private final double x,y;
    private int min = 5;
    private int max = 35;
    // Variável que armazena o valor base que será minerado por ação
    private final int recursos;

    public Mine(Image spriteMina, double x, double y) {
        this.x = x;
        this.y = y;
        // Inicializa a quantidade base de recursos de forma randômica e final
        this.recursos = ThreadLocalRandom.current().nextInt(min, max + 1);

        imageView = new ImageView(spriteMina);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(96);   // ajusta o tamanho que ficar bom
        imageView.setFitHeight(96);
        imageView.setLayoutX(x);
        imageView.setLayoutY(y);
    }

    public ImageView getImageView() { return imageView; }
    public double getX() { return x; }
    public double getY() { return y; }

    /**
     * Retorna a quantidade de recursos minerados nesta ação.
     * Não precisa de parâmetros, pois a validação do Zord é externa.
     * @return a quantidade de recursos gerada.
     */
    public int minerar() {
        return this.recursos;
    }
}
