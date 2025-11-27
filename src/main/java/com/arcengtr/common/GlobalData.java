package com.arcengtr.common;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class GlobalData {
    private double simulationTime;
    private double simulationStepTime;
    private double conductivity;
    private double alfa;
    private double tot;
    private double initialTemp;
    private double density;
    private double specificHeat;
    private int nN; // liczba węzłów
    private int nE; // liczba elementów
    private int npc; // punkty Gaussa
    private List<Integer> bcNodes; // punkty graniczne
}
