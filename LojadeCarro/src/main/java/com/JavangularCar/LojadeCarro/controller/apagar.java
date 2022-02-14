package com.JavangularCar.LojadeCarro.controller;


import static java.lang.Math.floor;
import static java.lang.Math.random;

public class apagar {


    static int funcao(int numero) {
        return numero;

    }

    public static void main(String[] args) {

        int[] vetor = new int[10];
        boolean  temvalores;

        vetor[0] = 1;
        vetor[1] = 2;
        vetor[2] = 3;
        vetor[3] = 4;
        vetor[4] = 5;
        vetor[5] = 6;
        vetor[6] = 8;
        vetor[7] = 8;
        vetor[8] = 9;
        vetor[9] = 10;

        temvalores = false;


        for (int i = 0; i < vetor.length; i++){
            for (int j = vetor.length - 1; j >= 0; j--){
                if (vetor[i] == vetor[j]){
                    temvalores = true;
                }
            }
        }
        System.out.println(temvalores);
    }
}
