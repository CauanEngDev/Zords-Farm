//package com.robot.controller;
//
//import com.robot.enums.Actions;
//import com.robot.model.Zord;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//
//
//public class ControllerMain {
//    private List<String> Acoes = new ArrayList<>(Arrays.asList("Minerar", "Construir", "?"));
//    private int[] Custo = {30, 50, 0};
//
//
//    public void Gastoenergia(String acao, Zord zord){
//        if (zord == null || !Acoes.contains(acao)){
//            return;
//        }
//        for (int i = 0; i < Custo.length; i++) {
//            if (acao == Acoes.get(i)){
//                zord.setEnergy(zord.getEnergy() - Custo[i]);
//                return;
//            }
//        }
//    }
//
//}
