package com.robot.controller;

import com.robot.enums.ZordFunction;
import com.robot.enums.Zords;
import com.robot.model.RedMagicZord;
import com.robot.model.StegoZord;
import com.robot.model.TriceraZord;
import com.robot.model.Zord;

import static com.robot.Database.ZordsData.*;
import static com.robot.utils.GameFunction.println;

public class ZordCreate {
    @SuppressWarnings("unchecked")
    public <T extends Zord> T createZordByTitanus(Zords type) {
        Zord newZord;

        switch (type) {
//            case STEGOZORD:
//                StegoZord newStego = new StegoZord();
//                // Adiciona ao Set
//                stegoZords.add(newStego);
//                newZord = newStego;
//                break;
//
//            case REDMAGICZORD:
//                RedMagicZord newRedmagic = new RedMagicZord();
//                // Adiciona ao Set
//                redMagicZords.add(newRedmagic);
//                newZord = newRedmagic;
//                break;

            case TRICERAZORD:
                newZord = new TriceraZord();
                break;

            default:
                println("Tipo de Zord não reconhecido: " + type);
                return null;
        }
        return (T) newZord;
    }

    public Zord createZordByLoad(ZordFunction function) {
//        if (function == ZordFunction.BUILDER) {
//            StegoZord newStego = new StegoZord();
//            stegoZords.add(newStego);
//            return newStego;
//        }
//        else if (function == ZordFunction.FIGHTER) {
//            RedMagicZord newRedmagic = new RedMagicZord();
//            redMagicZords.add(newRedmagic);
//            return newRedmagic;
//        }
        if (function == ZordFunction.MINER) {
            TriceraZord newTricera = new TriceraZord();
            triceraZords.add(newTricera);
            return newTricera;
        }
        println("Zord não encontrado!");
        return null;
    }
}
