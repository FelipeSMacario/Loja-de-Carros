package com.JavangularCar.LojadeCarro.controller;

import java.util.*;

public class apagar {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (sc.hasNext()){

            String valor = sc.nextLine();
            int cont1 = 0, cont2 = 0;

            for (int c = 0; c < valor.length(); c ++){
                char texto = valor.charAt(c);

                if (texto == '('){
                    cont1 ++;
                } else if (texto == ')') {
                    cont2 ++;
                }

            }
            if (cont1 == cont2 && valor.charAt(0) != ')') {
                System.out.println("correct");
            }
            else System.out.println("incorrect");
        }

    }
}
