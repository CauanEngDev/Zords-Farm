package com.robot.controller;

import com.robot.enums.ZordFunction;
import com.robot.enums.Zords;
import com.robot.model.RedMagicZord;
import com.robot.model.StegoZord;
import com.robot.model.TriceraZord;
import com.robot.model.Zord;

import static com.robot.Database.ZordsData.*; // Acessa os Sets estáticos
import static com.robot.utils.GameFunction.println;

/**
 * Classe Factory (Fábrica) responsável por instanciar e gerenciar
 * a criação de novos Zords no jogo,
 * adicionando-os às listas estáticas (ZordsData).
 */
public class ZordCreate {

    /**
     * Cria um novo Zord baseado no Enum Zords (para criação em tempo de jogo).
     * O método usa Generics para retornar o tipo de Zord específico sem necessidade de cast.
     * @param type O Enum Zords (STEGOZORD, TRICERAZORD, etc.) que será criado.
     * @param <T> O tipo de Zord que se espera receber (ex: StegoZord)
     * @return A nova instância do Zord.
     */
    @SuppressWarnings("unchecked")
    public <T extends Zord> T createZordByTitanus(Zords type) {
        Zord newZord = null; // Inicializa a variável para garantir que todos os caminhos tenham um retorno

        switch (type) {
            case STEGOZORD:
                StegoZord newStego = new StegoZord();
                stegoZords.add(newStego); // CRITICAL: Adiciona ao Set estático
                newZord = newStego;
                break;

            case REDMAGICZORD:
                RedMagicZord newRedmagic = new RedMagicZord();
                redMagicZords.add(newRedmagic); // CRITICAL: Adiciona ao Set estático
                newZord = newRedmagic;
                break;

            case TRICERAZORD:
                TriceraZord newTricera = new TriceraZord();
                triceraZords.add(newTricera); // CRITICAL: Adiciona ao Set estático
                newZord = newTricera;
                break;

            default:
                println("Tipo de Zord não reconhecido: " + type);
                return null;
        }
        return (T) newZord;
    }

    /**
     * Cria um Zord baseado na Função (para carregamento do jogo salvo).
     * @param function A Função (BUILDER, FIGHTER, MINER) salva no arquivo.
     * @return A nova instância do Zord reconstruído.
     */
    public Zord createZordByLoad(ZordFunction function) {
        // O código de Load é simplificado porque a função é o principal diferenciador.
        // Assumimos que cada função só tem um Zord correspondente.

        if (function == ZordFunction.BUILDER) {
            StegoZord newStego = new StegoZord();
            stegoZords.add(newStego); // Adiciona ao Set estático
            return newStego;
        } else if (function == ZordFunction.FIGHTER) {
            RedMagicZord newRedmagic = new RedMagicZord();
            redMagicZords.add(newRedmagic); // Adiciona ao Set estático
            return newRedmagic;
        } else if (function == ZordFunction.MINER) {
            TriceraZord newTricera = new TriceraZord();
            triceraZords.add(newTricera); // Adiciona ao Set estático
            return newTricera;
        }

        println("Zord não encontrado para a função: " + function);
        return null;
    }
}