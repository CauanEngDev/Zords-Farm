package com.robot.controller;

import com.google.gson.Gson;
import com.robot.model.TiledLayer;
import com.robot.model.TiledMap;
import com.robot.model.TitanusFabric;
import com.robot.model.TriceraZord;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import static com.robot.Database.ZordsData.triceraZords;

public class GameInitializer {
    private static int MAP_WIDTH;
    private static int MAP_HEIGHT;
    private static final int TILE_GRID = 32;
    private static int[][] COLLISON_MAP;
    private static Gson gson = new Gson();

    private static void loadTiledMapData() {
        try {
            InputStream in = GameInitializer.class.getResourceAsStream("/Map/Mapa.tmj");
            if (in == null) {
                throw new RuntimeException("Recurso /Map/Mapa.tmj não encontrado no classpath!");
            }

            try (Reader reader = new InputStreamReader(in)) {
                TiledMap mapa = gson.fromJson(reader, TiledMap.class);

                MAP_WIDTH = mapa.width;
                MAP_HEIGHT = mapa.height;

                TiledLayer layerColisao = mapa.layers.stream()
                        .filter(layer -> "Colisao".equals(layer.name))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Layer 'Colisao' não encotrada no mapa!"));

                COLLISON_MAP = new int[MAP_HEIGHT][MAP_WIDTH];
                for (int y = 0; y < MAP_HEIGHT; y++) {
                    for (int x = 0; x < MAP_WIDTH; x++) {
                        long gidLong = layerColisao.data[y * MAP_WIDTH + x];
                        COLLISON_MAP[y][x] = (gidLong == 0) ? 0 : 9;
                    }
                }

                System.out.println("Mapa carregado: " + MAP_WIDTH + "x" + MAP_HEIGHT);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar mapa Tiled", e);
        }
    }

    public static int[][] getCollisonMap() {
        if (COLLISON_MAP == null) loadTiledMapData();
        return COLLISON_MAP;
    }

    public static int getMapRows() {
        if (COLLISON_MAP == null) loadTiledMapData();
        return MAP_HEIGHT;
    }

    public static int getMapColumns() {
        if (COLLISON_MAP == null) loadTiledMapData();
        return MAP_WIDTH;
    }

    public static void initializeNewGame(TriceraZord triceraZord, TitanusFabric titanus, int tileGrid) {
        triceraZords.add(triceraZord);
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
}
