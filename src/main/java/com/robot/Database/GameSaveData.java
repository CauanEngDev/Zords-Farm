package com.robot.Database;

import java.util.HashSet;
import java.util.Set;

public class GameSaveData {
    public Set<ZordSaveData> allZords = new HashSet<>();
    public TitanusSaveData titanusData = new TitanusSaveData();

    public GameSaveData() {}
}
