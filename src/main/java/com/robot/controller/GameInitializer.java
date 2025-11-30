package com.robot.controller;

import com.robot.model.TitanusFabric;
import com.robot.model.TriceraZord;

public class GameInitializer {
    private static final int MAP_ROWS = 35;
    private static final int MAP_COLUMNS = 40;
    private final int[][] collisionMap = new int[MAP_ROWS][MAP_COLUMNS];


    public static void initializeNewGame(TriceraZord triceraZord, TitanusFabric titanus, int tileGrid) {
        int zordCol = 5;
        int zordRow = 5;
        int titaCol = 5;
        int titaRow = 1;

        triceraZord.getImageView().setLayoutX(zordCol * tileGrid);
        triceraZord.getImageView().setLayoutY(zordRow * tileGrid);

        titanus.getImageView().setLayoutX(titaCol * tileGrid);
        titanus.getImageView().setLayoutY(titaRow * tileGrid);

        triceraZord.setTarget(zordCol * tileGrid, zordRow * tileGrid);
    }

    private void initializeCollisionMap() {
        for (int i = 0; i < MAP_ROWS; i++) {
            for (int j = 0; j < MAP_COLUMNS; j++) {
                collisionMap[i][j] = 0;
            }
        }

        int titanusCol = 5;
        int titanusRow = 1;

        collisionMap[titanusRow][titanusCol] = 9;
        collisionMap[titanusRow][titanusCol + 1] = 9;
        collisionMap[titanusRow + 1][titanusCol] = 9;
        collisionMap[titanusRow + 1][titanusCol + 1] = 9;
    }
}
