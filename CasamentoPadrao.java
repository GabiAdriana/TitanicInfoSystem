import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CasamentoPadrao {
    //Transformar o arquivo de dados em uma stringona e fazer os codigos de casamento de padrão em cima desta string, 
    //retornar a posição da string que o padrão se encontra

    public static void menuCasamentoPadrao()throws IOException{
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nInforme o padrão a ser encontrado: ");
        String padrao = scanner.nextLine();
        //System.out.println(padrao);

        String nomeArquivo = "base_titanic.bin";
        String arquivo = BinarioParaString(nomeArquivo);
        
        ForcaBruta(padrao, arquivo);
        KMPmelhorado(padrao, arquivo);
        BoyerMoore(padrao, arquivo);
    }

    // Método para transformar o arquivo binário em uma string
    public static String BinarioParaString(String caminhoArquivo) throws IOException {
        // Usando try-with-resources para gerenciar o arquivo
        try (RandomAccessFile arquivoBinario = new RandomAccessFile(caminhoArquivo, "r")) {
            // Lê todos os bytes do arquivo
            byte[] bytes = new byte[(int) arquivoBinario.length()];
            arquivoBinario.readFully(bytes);

            // Converte os bytes para uma string usando UTF-8
            return new String(bytes, "UTF-8");
        }
    }

    /*==============================Força Bruta============================================== */

    public static void ForcaBruta(String padrao, String arquivo) throws IOException{
        int n = arquivo.length();
        int m = padrao.length();
        List<Integer> posicoes = new ArrayList<>();
        //int posicao = -1

        // Marca o tempo inicial da busca
        long tempoInicioFB = System.currentTimeMillis();

        for(int i = 0; i<= n-m; i++){
            int j = 0;
            while(j < m && padrao.charAt(j) == arquivo.charAt(i+j)){
                j++;
            }
            if (j == m){
                posicoes.add(i);   //Se encontrou o padrão grava a posição inicial do padrão
                //posicao = i;
                //break;
            }
        }

        // Marca o tempo final e calcula a duração
        long tempoFimFB = System.currentTimeMillis();
        long tempoExecucaoFB = tempoFimFB - tempoInicioFB;

        //Apresentação do resultado

        System.out.println("=====================================");
        System.out.println("\tAlgoritmo Força Bruta");
        if (!posicoes.isEmpty()){ //if(posicao!=-1)
            System.out.println("\nQuantidade de ocorrências: " + posicoes.size()); //Não precisa dessa linha
            System.out.println("Posição do padrão: " + posicoes); //System.out.println("Posição do padrão: " + posicao);
            System.out.println("Tempo de execução: " + tempoExecucaoFB + "ms");
        }else{
            System.out.println("\nO padrão não foi encontrado no código!");
            System.out.println("Tempo de execução: " + tempoExecucaoFB + "ms");
        }
    }

    /*=================================KMP Melhorado============================= */

    public static int[] PiLinha(String padrao) throws IOException{
        int[] piLinha = new int[padrao.length()];
        piLinha[0] = -1;
        int k = -1;

        for(int i = 1; i<padrao.length(); i++){
            while(k>=0 && padrao.charAt(k+1) != padrao.charAt(i)){
                k = piLinha[k];
            }
            if(padrao.charAt(k+1) == padrao.charAt(i)){
                k++;
            }
            piLinha[i] = k;
        }
        return piLinha;
    }

    public static void KMPmelhorado(String padrao, String texto) throws IOException{
        //System.out.println("entrei no KMP");
        List<Integer> posicoes = new ArrayList<>();
        int[] piLinha = PiLinha(padrao);
        int k = 0;

        // Marca o tempo inicial da busca
        long tempoInicioKMP = System.currentTimeMillis();

        for(int i = 0; i<texto.length(); i++){
            while(k>=0 && padrao.charAt(k+1) != texto.charAt(i)){
                k = piLinha[k];
            }
            if(padrao.charAt(k+1) == texto.charAt(i)){
                k++;
            }
            if(k == padrao.length() -1){
                posicoes.add(i-k);
                k = piLinha[k];
            }
        }

        // Marca o tempo final e calcula a duração
        long tempoFimKMP = System.currentTimeMillis();
        long tempoExecucaoKMP = tempoFimKMP - tempoInicioKMP;

        System.out.println("\n\tAlgoritmo KMP Melhorado");
        if (!posicoes.isEmpty()){ //if(posicao!=-1)
            System.out.println("\nQuantidade de ocorrências: " + posicoes.size()); //Não precisa dessa linha
            System.out.println("Posição do padrão: " + posicoes); //System.out.println("Posição do padrão: " + posicao);
            System.out.println("Tempo de execução: " + tempoExecucaoKMP + "ms");
        }else{
            System.out.println("\nO padrão não foi encontrado no código!");
            System.out.println("Tempo de execução: " + tempoExecucaoKMP + "ms");
        }
    }

    /*============================Boyer Moore==================================== */

    /*Após uma incompatibilidade, as duas regras são avaliadas (Sufixo Bom e Caractere Ruim)
    e o padrão é deslocado pela maior distância sugerida entre as duas heurísticas. Essa 
    abordagem garante que o algoritmo aproveite ao máximo ambas as estratégias para reduzir 
    comparações desnecessárias. */

    public static Map<Character, Integer> CaractereRuim(String padrao) {
        Map<Character, Integer> deslocamento = new HashMap<>();
        int m = padrao.length();

        // Inicializa as entradas da tabela com o tamanho do padrão
        for (int i = 0; i < m - 1; i++) {
            deslocamento.put(padrao.charAt(i), m - i - 1);
        }
        return deslocamento;
    } 

    public static int[] SufixoBom(String padrao) {
        int m = padrao.length();
        int[] deslocamento = new int[m];
        int[] sufixo = new int[m];
        int g = m - 1, f = 0;

        sufixo[m - 1] = m;

        // Calcula os sufixos
        for (int i = m - 2; i >= 0; --i) {
            if (i > g && sufixo[i + m - 1 - f] < i - g) {
                sufixo[i] = sufixo[i + m - 1 - f];
            } else {
                if (i < g) g = i;
                f = i;
                while (g >= 0 && padrao.charAt(g) == padrao.charAt(g + m - 1 - f)) {
                    --g;
                }
                sufixo[i] = f - g;
            }
        }

        // Inicializa o deslocamento para o sufixo bom
        Arrays.fill(deslocamento, m);
        int j = 0;

        // Ajusta os deslocamentos com base nos sufixos
        for (int i = m - 1; i >= 0; --i) {
            if (sufixo[i] == i + 1) {
                for (; j < m - 1 - i; ++j) {
                    if (deslocamento[j] == m) {
                        deslocamento[j] = m - 1 - i;
                    }
                }
            }
        }

        for (int i = 0; i < m - 1; i++) {
            deslocamento[m - 1 - sufixo[i]] = m - 1 - i;
        }

        return deslocamento;
    }

    public static void BoyerMoore(String padrao, String texto) {
        Map<Character, Integer> deslocamentoCaractereRuim = CaractereRuim(padrao);
        int[] deslocamentoSufixoBom = SufixoBom(padrao);
        int n = texto.length();
        int m = padrao.length();
        int s = 0;
        List<Integer> posicoes = new ArrayList<>();

        long tempoInicioBM = System.currentTimeMillis();

        // Busca padrão no texto
        while (s <= (n - m)) {
            int j = m - 1;

            // Comparação do padrão com o texto
            while (j >= 0 && padrao.charAt(j) == texto.charAt(s + j)) {
                j--;
            }

            if (j < 0) {
                // Padrão encontrado
                posicoes.add(s);
                s += deslocamentoSufixoBom[0];
            } else {
                int deslocamentoCaractere = deslocamentoCaractereRuim.getOrDefault(texto.charAt(s + j), -1);
                if (deslocamentoCaractere == -1) {
                    deslocamentoCaractere = m; // Salto total caso caractere não esteja no padrão
                }

                int deslocamentoAtual = Math.max(deslocamentoSufixoBom[j], j - deslocamentoCaractere);
                s += deslocamentoAtual;
            }
        }

        long tempoFimBM = System.currentTimeMillis();
        long tempoExecucaoBM = tempoFimBM - tempoInicioBM;

        System.out.println("\n\tAlgoritmo Boyer-Moore");
        if (!posicoes.isEmpty()) {
            System.out.println("\nQuantidade de ocorrências: " + posicoes.size());
            System.out.println("Posições do padrão: " + posicoes);
            System.out.println("Tempo de execução: " + tempoExecucaoBM + "ms");
        } else {
            System.out.println("\nO padrão não foi encontrado no texto!");
            System.out.println("Tempo de execução: " + tempoExecucaoBM + "ms");
        }
    }
}

