import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class LZW{

    public static void gerenciadorDeCompactação()throws IOException{
        Scanner scanner = new Scanner(System.in);
        int opcao;

        System.out.println("\n=====================================");
        System.out.println("\tMenu de compactação");
        System.out.println("=====================================");
        System.out.println("1. Compactar os Arquivos");
        System.out.println("2. Descompactar substituindo os arquivos originais");
        System.out.println("3. Deletar uma versão da compactação");
        System.out.print("Escolha uma opção: ");

        // Verifica se a entrada é um número inteiro válido e dentro do intervalo esperado
        while (!scanner.hasNextInt()) {
            System.out.println("Por favor, insira um número válido.");
            scanner.next(); // Consome a entrada inválida
        }
        opcao = scanner.nextInt();

        switch (opcao) {
            case 1:
                int numCompac;
                System.out.print("\nQual versão está sendo compactada? ");
                numCompac = scanner.nextInt();

                /*--------------Compactação e calculos do arquivo base_titanic.bin----------------- */
                //Base de dados
                String ArqBase_titanic = "base_titanicCompressao" + numCompac;

                //Inicia o layout
                System.out.println("\n=====================================");
                System.out.println("- Informações de compressão");

                //Chama a função que manda compactar e calcula o tempo e a taxa de compressão
                CompactaCalcula("base_titanic.bin", ArqBase_titanic);

                /*--------------Compactação e calculos do arquivo indiceSeq.bin----------------- */
                //Indice Sequencial
                String ArqIndiceSeq = "indiceSeqCompressao" + numCompac;

                //Chama a função que manda compactar e calcula o tempo e a taxa de compressão
                CompactaCalcula("indiceSeq.bin", ArqIndiceSeq);
                         
                //--------------Compactação e calculos do arquivo indiceNome.bin----------------- 
                //Indice de Nomes (indireto)
                String ArqIndiceNome = "indiceNomeCompressao" + numCompac;

                //Chama a função que manda compactar e calcula o tempo e a taxa de compressão
                CompactaCalcula("indiceNome.bin", ArqIndiceNome);

                //--------------Compactação e calculos do arquivo Multilista.bin-----------------
                //Multilista
                String ArqMultilista = "MultilistaCompressao" + numCompac;

                //Chama a função que manda compactar e calcula o tempo e a taxa de compressão
                CompactaCalcula("Multilista.bin", ArqMultilista);

                System.out.println("=====================================");
                break;
            case 2:
                int numDescompac;
                System.out.print("\nQual versão está sendo descompactada? ");
                numDescompac = scanner.nextInt();
                
                /*--------------Compactação e calculos do arquivo base_titanic.bin----------------- */
                
                System.out.println("\n=====================================");
                System.out.println("- Informações de descompressão");

                /*--------------------------base_titanic.bin--------------------------------------- */

                //Arquivo de dados
                String ArqCompBaseTitanic = "base_titanicCompressao" + numDescompac;

                //Chama a função que manda descompactar e calcula o tempo
                DecompactaCalcula(ArqCompBaseTitanic, "base_titanic.bin");

                /*--------------Compactação e calculos do arquivo indiceSeq.bin----------------- */
                //Indice Sequencial
                String ArqCompIndiceSeq = "indiceSeqCompressao" + numDescompac;

                //Chama a função que manda descompactar e calcula o tempo
                DecompactaCalcula(ArqCompIndiceSeq, "indiceSeq.bin");
                         
                //--------------Compactação e calculos do arquivo indiceNome.bin----------------- 
                //Indice de Nomes (indireto)
                String ArqCompIndiceNome = "indiceNomeCompressao" + numDescompac;

                //Chama a função que manda descompactar e calcula o tempo
                DecompactaCalcula(ArqCompIndiceNome, "indiceNome.bin");

                //--------------Compactação e calculos do arquivo Multilista.bin-----------------
                //Multilista
                String ArqCompMultilista = "MultilistaCompressao" + numDescompac;

                //Chama a função que manda descompactar e calcula o tempo
                DecompactaCalcula(ArqCompMultilista, "Multilista.bin");

                System.out.println("=====================================");
                break;
            case 3:
                int versaoDeleta;
                System.out.println("\nQual versão deseja deletar?");
                versaoDeleta = scanner.nextInt();

                //Deleta a versão da compressão do arquivo base_titanic.bin
                String ArqToDeleteBase = "base_titanicCompressao" + versaoDeleta;
                deletarArquivo(ArqToDeleteBase);

                //Deleta a versão da compressão do arquivo indiceSeq.bin
                String ArqToDeleteSeq = "indiceSeqCompressao" + versaoDeleta;
                deletarArquivo(ArqToDeleteSeq);

                //Deleta a versão da compressão do arquivo indiceNome.bin
                String ArqToDeleteNome = "indiceNomeCompressao" + versaoDeleta;
                deletarArquivo(ArqToDeleteNome);

                //Deleta a versão da compressão do arquivo Multilista.bin
                String ArqToDeleteMult = "MultilistaCompressao" + versaoDeleta;
                deletarArquivo(ArqToDeleteMult);
                break;
            default:
                break;
        }
    }

    //Função para Compactar um arquivo usando LZW
    //Quando o dicionario chega em um limite pre definido (4096) ele substitui o primeiro elemento do dicionario
    public static void compactarLZW(String inputFile, String outputFile)throws IOException{
        RandomAccessFile arqDescompactado = new RandomAccessFile(inputFile, "r");
        RandomAccessFile arqCompactado = new RandomAccessFile(outputFile, "rw");

        //Dicionario inicial com todos os bytes unicos
        ArrayList<byte[]> dicionario = inicializarDicionario();

        byte[] prefixo = new byte[0];
        byte[] buffer = new byte[1];

        while(arqDescompactado.read(buffer) != -1){
            //Concatena o prefixo e o proximo byte lido
            byte[] concatenado = new byte[prefixo.length + 1];

            //Faz uma cópia de uma parte do array prefixo para o array concatenado
            System.arraycopy(prefixo, 0, concatenado, 0, prefixo.length);
            concatenado[concatenado.length - 1] = buffer[0];

            int index = encontraPosicao(dicionario, concatenado);
            if (index != -1) {
                // Se o padrão já estiver no dicionário, expande o prefixo
                prefixo = concatenado;
            } else {
                // Se não estiver, escreve o índice do prefixo e adiciona o novo padrão ao dicionário
                arqCompactado.writeShort(encontraPosicao(dicionario, prefixo));

                // Adiciona o novo padrão ao dicionário se houver espaço
                if (dicionario.size() < 4096) {
                    dicionario.add(concatenado);
                }

                // Redefine o prefixo para o último byte lido
                prefixo = new byte[]{buffer[0]};
            }
        }

        // Escreve o índice do último prefixo restante
        if (prefixo.length > 0) {
            arqCompactado.writeShort(encontraPosicao(dicionario, prefixo));
        }

        arqDescompactado.close();
        arqCompactado.close();
    }

    // Método para inicializar o dicionário com bytes únicos (0-255)
    private static ArrayList<byte[]> inicializarDicionario() {
        ArrayList<byte[]> dicionario = new ArrayList<>();
        for (int i = 0; i < 256; i++) {
            dicionario.add(new byte[]{(byte) i});
        }
        return dicionario;
    }

    // Método para encontrar o índice de um padrão no dicionário
    private static int encontraPosicao(ArrayList<byte[]> dicionario, byte[] padrao) {
        for (int i = 0; i < dicionario.size(); i++) {
            if (Arrays.equals(dicionario.get(i), padrao)) {
                return i;
            }
        }
        return -1;
    }

    
    public static void descompactarLZW(String inputFile, String outputFile)throws IOException{
        RandomAccessFile arqCompactado = new RandomAccessFile(inputFile, "r");
        RandomAccessFile arqDescompactado = new RandomAccessFile(outputFile, "rw");

        // Dicionário inicial com todos os bytes únicos
        ArrayList<byte[]> dicionario = inicializarDicionario();

        
        ArrayList<Byte> resultado = new ArrayList<>();

        // Lê o primeiro índice
        int indexAtual = arqCompactado.readShort();
        byte[] prefixo = dicionario.get(indexAtual);
        for (byte b : prefixo) {
            resultado.add(b);
        }

        while (arqCompactado.getFilePointer() < arqCompactado.length()) {
            // Lê o próximo índice
            indexAtual = arqCompactado.readShort();

            byte[] padrao;
            if (indexAtual < dicionario.size()) {
                padrao = dicionario.get(indexAtual);
            } else if (indexAtual == dicionario.size()) {
                // Caso seja um novo padrão, expande o prefixo
                padrao = new byte[prefixo.length + 1];
                //Faz uma cópia de uma parte do array prefixo para o array concatenado
                System.arraycopy(prefixo, 0, padrao, 0, prefixo.length);
                padrao[padrao.length - 1] = prefixo[0]; // A primeira letra do prefixo
            } else {
                throw new IOException("Índice inválido encontrado no arquivo de entrada");
            }

            // Escreve o padrão no arquivo de saída
            for (byte b : padrao) {
                resultado.add(b);
            }

            // Adiciona o novo padrão ao dicionário
            if (dicionario.size() < 4096) {
                byte[] novoPrefixo = new byte[prefixo.length + 1];
                System.arraycopy(prefixo, 0, novoPrefixo, 0, prefixo.length);
                novoPrefixo[novoPrefixo.length - 1] = padrao[0];
                dicionario.add(novoPrefixo);
            }

            // Atualiza o prefixo
            prefixo = padrao;
        }
        // Escreve o conteúdo descompactado no arquivo de saída
        for (Byte b : resultado) {
            arqDescompactado.write(b);
        }
        arqCompactado.close();
        arqDescompactado.close();
    }

    public static void CompactaCalcula(String ArqOriginal, String ArqCompactado) throws IOException{
        
        // Marca o tempo inicial de compactação
        long tempoInicioCom = System.currentTimeMillis();

        //Compacta
        compactarLZW(ArqOriginal, ArqCompactado);

        // Marca o tempo final e calcula a duração
        long tempoFimCom = System.currentTimeMillis();
        long tempoExecucaoCom = tempoFimCom - tempoInicioCom;

        // Exibe o tempo de execução
        System.out.println("\nArquivo: " + ArqOriginal);
        System.out.println("Tempo de execução: " + tempoExecucaoCom + " ms");

        // Calcula e exibe a taxa de compressão
        long tamanhoOriginalCom = ArqOriginal.length();
        long tamanhoComprimidoCom = ArqCompactado.length();
        double taxaCompressaoCom = ((1.0 - (double) tamanhoComprimidoCom / tamanhoOriginalCom) * 100)*-1;

        System.out.printf("Taxa de compressão: %.2f%%\n", taxaCompressaoCom);
        
    }

    public static void DecompactaCalcula(String ArqCompactado, String ArqDescompactado) throws IOException{
        //Deleta o arquivo de dados atual
        deletarArquivo(ArqDescompactado);

        // Marca o tempo inicial de descompactação
        long tempoInicioDes = System.currentTimeMillis();

        //Descompacta
        descompactarLZW(ArqCompactado, ArqDescompactado);

        // Marca o tempo final e calcula a duração
        long tempoFimDes = System.currentTimeMillis();
        long tempoExecucaoDes = tempoFimDes - tempoInicioDes;

        // Exibe o tempo de execução
        System.out.println("\nArquivo: base_titanic.bin");
        System.out.println("Tempo de execução: " + tempoExecucaoDes + " ms");
    }

    //Função que deleta o arquivo original para ser substituido pela versão descompactada
    public static void deletarArquivo(String ArqtoDelete)throws IOException{
        // Caminho do arquivo que será excluido
        File arquivo = new File(ArqtoDelete);

        // Verifica se o arquivo existe
        if (arquivo.exists()) {
            // Tenta excluir o arquivo
           arquivo.delete();
        } 
    }
}



