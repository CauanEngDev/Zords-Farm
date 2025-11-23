package com.robot.controller;

import com.robot.model.RedMagicZord;
import com.robot.model.StegoZord;
import com.robot.model.TriceraZord;

import static com.robot.Database.ZordsData.*;

public class ZordCreate {
    public StegoZord createStego() {
        StegoZord newStego = new StegoZord();
        stegoZords.add(newStego);
        return newStego;
    }

    public void createTricera() {
        TriceraZord newTricera = new TriceraZord();
        triceraZords.add(newTricera);
    }

    public void createRedmagic() {
        RedMagicZord newRedmagic = new RedMagicZord();
        redMagicZords.add(newRedmagic);
    }
}
