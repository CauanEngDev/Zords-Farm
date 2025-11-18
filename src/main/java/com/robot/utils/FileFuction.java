package com.robot.utils;

import javafx.scene.image.Image;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Objects;

public class FileFuction {
    @Contract("_ -> new")
    public static @NotNull Image loadingImageSprite(@NotNull String path) {
        InputStream stream = FileFuction.class.getResourceAsStream(path);

        return new Image(Objects.requireNonNull(stream));
    }
}
