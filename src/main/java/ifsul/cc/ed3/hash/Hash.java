package ifsul.cc.ed3.hash;

import java.text.DecimalFormat;
import java.util.ArrayList; //importa array list
import java.util.List;// importa listas
import java.util.Random; //importa numero aleatorios

import static java.lang.Math.*; //biblioteca math 

public class Hash {

    public static void main(String[] args) {
        Random rand = new Random();
        //gera números aleatórios

        int difficulty = 3;
        List<Integer> valueList = generateList(difficulty);

        //calcula o tempo 
        Float timeHash = 0F, timeCalculation = 0F, timePrime = 0F, timeSearch = 0F;

        //mede o tamanho da hash = primeiro número primo maior que qtd de valores
        Long startPrime = System.nanoTime();
        Integer L = nearestPrimeAbove(valueList.size());
        Long finishPrime = System.nanoTime();
        timePrime += finishPrime - startPrime;
        timePrime /= 1000000;

        //Cria e inicia a hash
        ArrayList<Integer>[] hash = new ArrayList[L];
        for (int i = 0; i < L; i++) {
            hash[i] = new ArrayList<>();
        }

        //faz o hash e inicia a medição do tempo
        Long startHash = System.nanoTime();
        for (Integer i : valueList) {
            Long startCalculation = System.nanoTime();
            hash[i % L].add(i);
            Long finishCalculation = System.nanoTime();
            timeCalculation += finishCalculation - startCalculation;
        }
        Long finishHash = System.nanoTime();

        timeCalculation /= valueList.size();
        timeCalculation /= 1000000;

        timeHash += finishHash - startHash;
        timeHash /= 1000000;

        //mostra os restultados na tela
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        System.out.println("\nTESTE: = " + valueList.size());
        System.out.println("Tempo médio para calcular posição: " + timeCalculation + " milisegundos");
        System.out.println("Tempo para hash do lista: " + timeHash + " milisegundos");
        System.out.println("Tempo para calcular tamanho do hash: " + timePrime + " milisegundos");
        System.out.println("Total: " + (timePrime + timeHash) + " milisegundos");

        timeHash = 0F;
        timeCalculation = 0F;
        timePrime = 0F;
        timeSearch = 0F;
        //Tamanho do hash = pequena fração da qtd de valores
        startPrime = System.nanoTime();
        L = nearestPrimeAbove((int) ceil(
                valueList.size() * (0.1 / pow(10, nearestTenPotency(valueList.size()))))
        );
        finishPrime = System.nanoTime();
        timePrime += finishPrime - startPrime;
        timePrime /= 1000000;

        //Define a linha de colisões
        Integer[] collisions = new Integer[L];

        //Realiza hash e medições de tempo
        startHash = System.nanoTime();
        //Conta o máximo de colisões
        for (Integer i : valueList) {
            if (collisions[i % L] == null) {
                collisions[i % L] = 1;
            } else {
                collisions[i % L]++;
            }
        }
        int x = 0;
        for (int i = 0; i < L; i++) {
            if (collisions[i] != null && collisions[i] > x) {
                x = collisions[i];
            }
        }
        //Cria hash L * x
        Integer[][] hashBig = new Integer[x][];
        for (int i = 0; i < x; i++) {
            hashBig[i] = new Integer[L];
        }
        //Aloca valores ao hash
        for (Integer i : valueList) {
            Long startCalculation = System.nanoTime();
            for (int j = 0; j < x; j++) {
                if (hashBig[j][i % L] == null) {
                    hashBig[j][i % L] = i;
                    break;
                }
            }
            Long finishCalculation = System.nanoTime();
            timeCalculation += finishCalculation - startCalculation;
        }
        finishHash = System.nanoTime();

        timeCalculation /= valueList.size();
        timeCalculation /= 1000000;

        timeHash += finishHash - startHash;
        timeHash /= 1000000;

        //TESTA TEMPO DE PESQUISA
        int nroTestesPesquisa = 100;
        for (int i = 0; i < nroTestesPesquisa; i++) {
            int buscar = valueList.get(rand.nextInt(valueList.size()));

            Long startSearch = System.nanoTime();
            int coluna = buscar % L;
            for (int j = 0; j < x; j++) {
                if (hashBig[j][coluna] == buscar) {
                    break;
                }
            }
            Long finishSearch = System.nanoTime();
            timeSearch += finishSearch - startSearch;
        }
        timeSearch /= nroTestesPesquisa;
        timeSearch /= 1000000;

        //Imprime resultados
        int utilizados = 0;
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < L; j++) {
                if (hashBig[i][j] != null) {
                    utilizados++;
                }
            }
        }

        System.out.println("Tamanho lista: L = " + L + "; x = " + x);
        System.out.println("Aproveitamento da lista: " + ((utilizados * 100) / (L * x)) + "%");
        System.out.println("Tempo médio para calcular posição: " + timeCalculation + " milisegundos");
        System.out.println("Tempo médio de busca: " + timeSearch + " milisegundos");
        System.out.println("Tempo para do hash da lista: " + timeHash + " milisegundos");
        System.out.println("Tempo para calcular tamanho do hash: " + timePrime + " milisegundos");
        System.out.println("Total: " + (timePrime + timeHash) + " milisegundos");
    }

    //Métodos para buscar próximo número primo
    static boolean isPrime(int n) {
        int c = 1;
        for (int i = 2; i <= n; i++) {
            if (c > 2) {
                break;
            }
            if (n % i == 0) {
                c++;
            }
        }
        if (c == 2) {
            return true;
        } else {
            return false;
        }
    }

   
    // numero primo mais proximo 
    static Integer nearestPrimeAbove(int n) {
        if (n < 5) {
            return 2;
        }
        if (n % 2 == 0) {
            n++;
        }
        for (int i = n;; i = i + 2) {
            if (isPrime(i)) {
                return i;
            }
        }
    }

    //Gera lista a ser "hasheada"
    static List<Integer> generateList(int difficulty) {
        List<Integer> ret = new ArrayList<>();
        Random rand = new Random();

        Integer maxNum;
        switch (difficulty) {
            default:
                maxNum = rand.nextInt(11) + 5;
                break;
            case 1:
                maxNum = rand.nextInt(1001) + 500;
                break;
            case 2:
                maxNum = rand.nextInt(500000) + 100000;
                break;
            case 3:
                maxNum = 1500000;
                break;
        }

        for (int i = 0; i < maxNum; i++) {
            ret.add(abs(rand.nextInt()));
        }
        return ret;
    }

    //Busca potência 10 mais próxima pra baixo
    static int nearestTenPotency(Integer x) {
        int retPot = 0;
        while (pow(10, retPot) < x) {
            retPot += 3;
        }
        retPot -= 3;
        return retPot / 3;
    }
}
