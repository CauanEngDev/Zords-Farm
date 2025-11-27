package com.robot.controller;

import com.robot.enums.ZordFunction;
import com.robot.enums.Zords;
import com.robot.model.RedMagicZord;
import com.robot.model.StegoZord;
import com.robot.model.TriceraZord;
import com.robot.model.Zord;

import static com.robot.Database.ZordsData.*;
import static com.robot.utils.IOFunction.println;

public class ZordCreate {
    public void createZordByTitanus(Zords type) {
        if (type == Zords.STEGOZORD) {
            StegoZord newStego = new StegoZord();
            stegoZords.add(newStego);
        } else if (type == Zords.REDMAGICZORD) {
            RedMagicZord newRedmagic = new RedMagicZord();
            redMagicZords.add(newRedmagic);
        } else if (type == Zords.TRICERAZORD) {
            TriceraZord newTricera = new TriceraZord();
            triceraZords.add(newTricera);
        }
    }

    public Zord createZordByLoad(ZordFunction function) {
        if (function == ZordFunction.BUILDER) {
            StegoZord newStego = new StegoZord();
            stegoZords.add(newStego);
            return newStego;
        } else if (function == ZordFunction.FIGHTER) {
            RedMagicZord newRedmagic = new RedMagicZord();
            redMagicZords.add(newRedmagic);
            return newRedmagic;
        } else if (function == ZordFunction.MINER) {
            TriceraZord newTricera = new TriceraZord();
            triceraZords.add(newTricera);
            return newTricera;
        }
        println("Zord não encontrado!");
        return null;
    }
}
