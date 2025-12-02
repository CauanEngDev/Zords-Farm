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

/**
 * Controlador estático responsável por inicializar o estado do jogo e carregar
 * os dados estáticos do mundo (mapa, dimensões e colisão).
 */
public class GameInitializer {

    // --- VARIÁVEIS DE DIMENSÃO E MATRIZ ---
    /** Largura total do mapa (em tiles), carregada do arquivo Tiled. */
    private static int MAP_WIDTH;
    /** Altura total do mapa (em tiles), carregada do arquivo Tiled. */
    private static int MAP_HEIGHT;
    /** Tamanho de um tile em pixels. */
    private static final int TILE_GRID = 32;
    /** Matriz de colisão (42x37) carregada da layer 'Colisao' do Tiled. */
    private static int[][] COLLISON_MAP;

    // --- UTILITÁRIOS ---
    private static Gson gson = new Gson();

    /**
     * Carrega o arquivo JSON do mapa Tiled (`/Map/Mapa.tmj`) e preenche a matriz de colisão.
     * Este método usa lazy loading (só carrega na primeira vez que for chamado).
     * @throws RuntimeException Se o arquivo do mapa não for encontrado ou a layer de colisão estiver ausente.
     */
    private static void loadTiledMapData() {
        try {
            // O "Porquê": Usa getClass() para ler o arquivo dentro do JAR/Classpath.
            InputStream in = GameInitializer.class.getResourceAsStream("/Map/Mapa.tmj");
            if (in == null) {
                throw new RuntimeException("Recurso /Map/Mapa.tmj não encontrado no classpath!");
            }

            try (Reader reader = new InputStreamReader(in)) {
                // Mapeia o JSON para a classe TiledMap
                TiledMap mapa = gson.fromJson(reader, TiledMap.class);

                // 1. Define as dimensões do mundo
                MAP_WIDTH = mapa.width;
                MAP_HEIGHT = mapa.height;

                // 2. Encontra a camada de colisão
                TiledLayer layerColisao = mapa.layers.stream()
                        .filter(layer -> "Colisao".equals(layer.name))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Layer 'Colisao' não encontrada no mapa!"));

                // 3. Preenche a matriz de colisão
                COLLISON_MAP = new int[MAP_HEIGHT][MAP_WIDTH];
                for (int y = 0; y < MAP_HEIGHT; y++) {
                    for (int x = 0; x < MAP_WIDTH; x++) {
                        long gidLong = layerColisao.data[y * MAP_WIDTH + x];
                        // O "Porquê": GID 0 (Global Tile ID 0) é livre. Qualquer coisa > 0 é colisão (Valor 9).
                        COLLISON_MAP[y][x] = (gidLong == 0) ? 0 : 9;
                    }
                }

                System.out.println("Mapa carregado: " + MAP_WIDTH + "x" + MAP_HEIGHT);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar mapa Tiled", e);
        }
    }

    /**
     * Retorna a matriz de colisão do mapa (42x37).
     * Se o mapa ainda não foi carregado, ele dispara o processo de carga (lazy loading).
     * @return A matriz de colisão (0=Livre, 9=Obstáculo).
     */
    public static int[][] getCollisonMap() {
        if (COLLISON_MAP == null) loadTiledMapData();
        return COLLISON_MAP;
    }

    /**
     * Retorna o número total de linhas do mapa.
     * @return A altura do mapa (42 linhas).
     */
    public static int getMapRows() {
        if (COLLISON_MAP == null) loadTiledMapData();
        return MAP_HEIGHT;
    }

    /**
     * Retorna o número total de colunas do mapa.
     * @return A largura do mapa (37 colunas).
     */
    public static int getMapColumns() {
        if (COLLISON_MAP == null) loadTiledMapData();
        return MAP_WIDTH;
    }

    /**
     * Configura a posição inicial das unidades quando o jogo é iniciado do zero (sem save).
     * @param triceraZord A instância do Zord inicial.
     * @param titanus A instância do Titanus.
     * @param tileGrid O tamanho do tile em pixels (32).
     */
    public static void initializeNewGame(TriceraZord triceraZord, TitanusFabric titanus, int tileGrid) {
        // Adiciona a unidade inicial à lista estática global
        triceraZords.add(triceraZord);

        int zordCol = 5;
        int zordRow = 5;
        int titaCol = 5;
        int titaRow = 1;

        // Posicionamento visual
        triceraZord.getImageView().setLayoutX(zordCol * tileGrid);
        triceraZord.getImageView().setLayoutY(zordRow * tileGrid);

        titanus.getImageView().setLayoutX(titaCol * tileGrid);
        titanus.getImageView().setLayoutY(titaRow * tileGrid);

        // Define o alvo inicial (o Zord não se move no primeiro frame)
        triceraZord.setTarget(zordCol * tileGrid, zordRow * tileGrid);
    }
}