package com.robot.utils;

import javafx.scene.image.Image;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Objects;

/**
 * Classe de funções relacionadas a arquivos
 */
public class FileFuction {
    /**
     * Método que retorna a imagem que cada zord irá usar verificando nullabilidade
     * @param path caminho até a pasta
     * @return imagem que será usada
     */
    @Contract("_ -> new")
    public static @NotNull Image loadingImageSprite(@NotNull String path) {
        InputStream stream = FileFuction.class.getResourceAsStream(path);

        return new Image(Objects.requireNonNull(stream));
    }
}
