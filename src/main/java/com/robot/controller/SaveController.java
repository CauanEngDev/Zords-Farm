package com.robot.controller;

import com.google.gson.Gson;
import com.robot.Database.GameSaveData;
import com.robot.Database.ZordsInfoData;
import com.robot.model.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


import static com.robot.Database.ZordsData.*;
import static com.robot.utils.IOFunction.println;

public class SaveController {
    private static final Logger logger = Logger.getLogger(SaveController.class.getName());
    private final Gson gson = new Gson();
    private final ZordCreate createControl = new ZordCreate();

    private void saveGame(TitanusFabric titanusFabric) {
        println("Salvando progresso...");
        Set<Zord> zords = new HashSet<>();
        zords.addAll(stegoZords);
        zords.addAll(redMagicZords);
        zords.addAll(triceraZords);
        try (FileWriter writer = new FileWriter("saveGames/saveTeste.json")) {
            GameSaveData data = new GameSaveData();

            for (Zord zord : zords) {
                ZordsInfoData zsd = new ZordsInfoData();
                zsd.zordFunction = zord.getFunction();
                zsd.energy = zord.getEnergy();
                zsd.x = zord.getImageView().getLayoutX();
                zsd.y = zord.getImageView().getLayoutY();

                data.allZords.add(zsd);
            }

            data.titanusData.zordType = titanusFabric.getClass().getSimpleName();
            data.titanusData.level = titanusFabric.getLevel();
            data.titanusData.numStegos = titanusFabric.numStegos;
            data.titanusData.x = titanusFabric.getImageView().getLayoutX();
            data.titanusData.y = titanusFabric.getImageView().getLayoutY();

            gson.toJson(data, writer);

            println("Progresso Salvo com sucesso!");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Falha ao salvar o jogo", e);
        }
    }

    private void loadGame(TitanusFabric titanusFabric, Pane root) {
        println("Carregando progresso...");
        try (FileReader reader = new FileReader("saveGames/saveTeste.json")) {
            GameSaveData data = gson.fromJson(reader, GameSaveData.class);

            if (data != null) {
                clearGameWorld(root);

                for (ZordsInfoData zsd : data.allZords) {
                    Zord newZord = createControl.createZordByLoad(zsd.zordFunction);
                    newZord.setEnergy(zsd.energy);
                    newZord.getImageView().setLayoutX(zsd.x);
                    newZord.getImageView().setLayoutY(zsd.y);

                    root.getChildren().add(newZord.getImageView());
                }

                titanusFabric.setLevel(data.titanusData.level);
                titanusFabric.numStegos = data.titanusData.numStegos;
                titanusFabric.getImageView().setLayoutX(data.titanusData.x);
                titanusFabric.getImageView().setLayoutY(data.titanusData.y);

                println("Progresso carregado com sucesso!");
            }
        } catch(Exception e) {
            logger.log(Level.INFO, "Save não encontrado, iniciando novo jogo.");
        }
    }

    private void clearGameWorld(Pane root) {
        root.getChildren().removeIf(node -> node instanceof ImageView);

        stegoZords.clear();
        redMagicZords.clear();
        triceraZords.clear();
    }
}
