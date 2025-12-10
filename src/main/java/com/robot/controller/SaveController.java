package com.robot.controller;

import com.google.gson.Gson;
import com.robot.Database.GameSaveData;
import com.robot.Database.ZordsInfoData;
import com.robot.model.*;
import javafx.scene.Group;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


import static com.robot.Database.ZordsData.*;
import static com.robot.utils.GameFunction.println;

/**
 * Controlador responsável pela persistência do estado do jogo.
 * Lida com a serialização (salvar) e desserialização (carregar)
 * de todas as unidades (Zords) e estruturas (TitanusFabric)
 * usando o formato JSON (Gson).
 */
public class SaveController {
    private static final Logger logger = Logger.getLogger(SaveController.class.getName());
    private final Gson gson = new Gson();
    /** Controlador responsável por instanciar os Zords após o carregamento. */
    private final ZordCreate createControl = new ZordCreate();

    // --- VARIÁVEIS DE CAMINHO ---
    private static final String SAVE_DIR = "saveGames";
    private static final String SAVE_PATH = SAVE_DIR + "/saveTeste.json";

    /**
     * Serializa o estado atual de todas as unidades e do Titanus para um arquivo JSON.
     * @param titanusFabric A instância viva do TitanusFabric (para pegar o estado atual).
     */
    public void saveGame(TitanusFabric titanusFabric) {
        println("Salvando progresso...");
        // 1. Consolida todos os Zords ativos em um único Set.
        Set<WorkZord> workZords = new HashSet<>();
        workZords.addAll(stegoZords);
        workZords.addAll(redMagicZords);
        workZords.addAll(triceraZords);

        try {
            File directory = new File(SAVE_DIR);
            if (!directory.exists()) directory.mkdir();

            try (FileWriter writer = new FileWriter(SAVE_PATH)) {
                GameSaveData data = new GameSaveData(); // Contêiner raiz para o save

                // 2. Itera sobre a coleção e converte Zord vivo para objeto de dados (DTO).
                for (WorkZord workZord : workZords) {
                    ZordsInfoData zsd = new ZordsInfoData();
                    zsd.zordFunction = workZord.getFunction();
                    zsd.energy = workZord.getEnergy();
                    zsd.x = workZord.getImageView().getLayoutX(); // Salva a posição visual
                    zsd.y = workZord.getImageView().getLayoutY();

                    data.allZords.add(zsd);
                }

                // 3. Salva os dados do Titanus (requer inicialização no GameSaveData)
                data.titanusData.zordType = titanusFabric.getClass().getSimpleName();
                data.titanusData.level = titanusFabric.getLevel();
                data.titanusData.numTricera = titanusFabric.numTriceras;
                data.titanusData.numStego = titanusFabric.numStegos;
                data.titanusData.x = titanusFabric.getImageView().getLayoutX();
                data.titanusData.y = titanusFabric.getImageView().getLayoutY();

                gson.toJson(data, writer);

                println("Progresso Salvo com sucesso!");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Falha ao salvar o jogo", e);
        }
    }

    /**
     * Desserializa o estado do jogo do arquivo JSON, reconstruindo o Titanus e os Zords.
     * @param titanusFabric A instância viva do TitanusFabric para aplicar os dados carregados.
     * @param world o grupo (necessário para adicionar os Zords recriados à tela).
     * @return true se o carregamento for bem-sucedido, false caso contrário.
     */
    public boolean loadGame(TitanusFabric titanusFabric, Group world) {
        println("Carregando progresso...");
        try (FileReader reader = new FileReader(SAVE_PATH)) {
            GameSaveData data = gson.fromJson(reader, GameSaveData.class);

            if (data != null) {
                // Limpeza Crítica: Assume que o GameApp chamou clearGameWorld antes,
                // mas é necessário limpar as listas estáticas aqui.
                stegoZords.clear();
                redMagicZords.clear();
                triceraZords.clear();

                // 1. Carrega os Zords
                for (ZordsInfoData zsd : data.allZords) {
                    // Recria a instância do Zord usando a Factory (ZordCreate)
                    WorkZord newWorkZord = createControl.createZordByLoad(zsd.zordFunction);

                    // Aplica os dados carregados
                    newWorkZord.setEnergy(zsd.energy);
                    newWorkZord.getImageView().setLayoutX(zsd.x);
                    newWorkZord.getImageView().setLayoutY(zsd.y);

                    newWorkZord.setTarget(zsd.x,  zsd.y);

                    // Adiciona a View do Zord recém-criado à cena
                    world.getChildren().add(newWorkZord.getImageView());
                }

                // 2. Carrega o Titanus
                titanusFabric.setLevel(data.titanusData.level);
                titanusFabric.numTriceras = data.titanusData.numTricera;
                titanusFabric.numStegos = data.titanusData.numStego;
                titanusFabric.getImageView().setLayoutX(data.titanusData.x); // Aplica a posição salva
                titanusFabric.getImageView().setLayoutY(data.titanusData.y);

                world.getChildren().add(titanusFabric.getImageView());

                println("Progresso carregado com sucesso!");
                return true;
            }
            return false;
        } catch(Exception e) {
            logger.log(Level.INFO, "Save não encontrado, iniciando novo jogo.");
            return false;
        }
    }
}