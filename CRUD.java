import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

/*===================================================================================
                                    Sistema CRUD 
==================================================================================== */
    

public class CRUD {
    static String arquivo_binario = "base_titanic.bin";

    /*===================================================================
    criaRegistro: Adiciona um novo registro de passageiro no arquivo 
                  binário, a partir de um objeto Passageiro.
     ====================================================================*/

    public static void criaRegistro(Passageiro passageiro, RandomAccessFile arq) throws IOException{
        Passageiro novoPassageiro = Multilista.criaMultilista(passageiro.getClasse(), (int) arq.length(), passageiro);

        arq.seek(0);

        int ult_id = arq.readInt();
        passageiro.setId(ult_id + 1);

        arq.seek(0);
        arq.writeInt(novoPassageiro.getId());

        arq.seek(arq.length());
        int posicao = (int) arq.getFilePointer(); //Posição do registro no arquivo de dados
        arq.writeByte(0);      //Lapede
        arq.writeInt(novoPassageiro.toByteArray().length);
        arq.write(novoPassageiro.toByteArray());

        //cria o indice
        IndiceSeq.criaIndice(novoPassageiro.getId(), posicao);
        IndiceNome.criaIndiceNome(novoPassageiro.getNome(), passageiro.getId());
    }

    /*===================================================================
    criaRegistro (Create): Solicita os dados de um novo passageiro ao 
                           usuário e o registra no arquivo binário.
     ====================================================================*/

    public static void criaRegistro() throws IOException{
        Scanner scanner = new Scanner(System.in);

        Passageiro passageiro = new Passageiro();

        // Pede informações ao usuário
        System.out.print("\nInforme se sobreviveu (0 = não, 1 = sim): ");
        passageiro.setSobreviveu(scanner.nextInt());

        System.out.print("Informe a classe (1, 2, ou 3): ");
        passageiro.setClasse(scanner.nextInt());

        scanner.nextLine(); // Limpa o buffer

        System.out.print("Informe o nome: ");
        passageiro.setNome(scanner.nextLine());

        System.out.print("Informe o sexo (male/female): ");
        passageiro.setSexo(scanner.nextLine());

        //Tratamento da data
        System.out.print("Informe a data de nascimento (dd/mm/aaaa): ");
        String dataNasc = scanner.nextLine();
        String[] dados_data = dataNasc.split("/");
        Data data = new Data();
        passageiro.setNascimento(data.diasAteData(Integer.parseInt(dados_data[0]), Integer.parseInt(dados_data[1]), Integer.parseInt(dados_data[2])));

        //Tratamento das preferencias
        System.out.print("Informe as preferências separadas por ' - ': ");
        String preferenciasStr = scanner.nextLine();
        passageiro.setPreferencias(preferenciasStr.split(" - "));

        passageiro.setlinkC(-1);

        // Abre o arquivo binário para escrever o novo registro
        RandomAccessFile arq_bin = new RandomAccessFile(arquivo_binario, "rw");

        // Escreve no arquivo binário
        criaRegistro(passageiro, arq_bin);

        // Fecha o arquivo binário
        arq_bin.close();

    
        System.out.println("\nRegistro criado com sucesso!\n");
    }

    /*===================================================================
    lerRegistro (Read): Lê e exibe o registro de um passageiro específico 
                        com base no seu ID. (Utilizando o arquivo de indice
                        para localizar o registro)
     ====================================================================*/

     public static void lerRegistro(int id_passageiro) throws IOException{
        RandomAccessFile arq_bin = new RandomAccessFile(arquivo_binario, "r");
        
        int posicao = IndiceSeq.buscaIndice(id_passageiro);

        if (posicao == -1){
            System.out.println("\nRegistro " + id_passageiro + " não encontrado!");
            arq_bin.close();
            return;
        }

        arq_bin.seek(posicao);
        byte lapide = arq_bin.readByte();
        int tam_registro = arq_bin.readInt();
        byte[] b = new byte [tam_registro];
        arq_bin.readFully(b); //Armazena todo o registro lido

        if (lapide == 1){
            System.out.println("\nRegistro " + id_passageiro + " foi deletado.");
        } else {
            Passageiro passageiro = new Passageiro();
            passageiro.fromByteArray(b);
            System.out.println(passageiro);
        }

        arq_bin.close();
    }

    /*========================================================================
    lerConjuntoDeRegistros (Read): Lê e exibe vários registros de passageiros 
                                   com base em IDs fornecidos pelo usuário.
     =========================================================================*/

     public static void lerConjuntoDeRegistros(int[] criterios) throws IOException{
        for (int i = 0; i < criterios.length; i++){
            int criterio = criterios[i];
            lerRegistro(criterio);
            System.out.println("\n");
        }
    }

        /*========================================================================
    atualizarRegistro (Update): Atualiza o registro de um passageiro existente 
                                com novos dados fornecidos pelo usuário.
     =========================================================================*/

     public static void atualizarRegistro(Passageiro novoPassageiro) throws IOException{
        RandomAccessFile arq_bin = new RandomAccessFile(arquivo_binario, "rw");
        
        byte[] b;
        Passageiro passageiro = new Passageiro();
        int posicao = IndiceSeq.buscaIndice(novoPassageiro.getId());
        arq_bin.seek(posicao);
        byte lapide = arq_bin.readByte();
        int tam_registro = arq_bin.readInt();
        //int linkCAntigo = passageiro.getlinkC();
        //novoPassageiro.setlinkC(linkCAntigo);

        if (lapide == 0x00){ //Varifica se o registro é valido por precaução
            Multilista.atualizarMultilista(novoPassageiro, posicao,passageiro.getClasse(), novoPassageiro.getClasse());
            b = new byte[tam_registro];
            arq_bin.readFully(b);
            passageiro.fromByteArray(b);
            String nomeAntigo = passageiro.getNome();

            byte[] novoRegistroBytes = novoPassageiro.toByteArray();
            int novoTamRegistro = novoRegistroBytes.length;

            //Verifica se o novo registro é menor ou igual ao antigo
            if(novoTamRegistro <= tam_registro){
                //Se o novo registro couber no espaço do registro antigo salva o novo no mesmo lugar
                arq_bin.seek(posicao);
                arq_bin.writeByte(0);
                arq_bin.writeInt(tam_registro); //Mantem o tamanho anterior
                arq_bin.write(novoRegistroBytes);

                IndiceSeq.atualizaIndice(novoPassageiro.getId(), posicao);
                IndiceNome.atualizaIndiceNome(novoPassageiro.getNome(), nomeAntigo);
                //Multilista.atualizarMultilista(novoPassageiro, posicao,passageiro.getClasse(), novoPassageiro.getClasse());
            } else {
                //Se o novo registro for maior, marcar a lapide do atual e salvar o novo no fim do arquivo
                arq_bin.seek(posicao);
                arq_bin.writeByte(1); //Marca lapede como deletada

                //Move o ponteiro para o fim do araquivo
                arq_bin.seek(arq_bin.length());
                int novaPosicao = (int) arq_bin.length();
                arq_bin.writeByte(0);
                arq_bin.writeInt(novoTamRegistro);
                arq_bin.write(novoRegistroBytes);

                IndiceSeq.atualizaIndice(novoPassageiro.getId(), novaPosicao);
                IndiceNome.atualizaIndiceNome(novoPassageiro.getNome(), nomeAntigo);
                //Multilista.atualizarMultilista(novoPassageiro, novaPosicao,passageiro.getClasse(), novoPassageiro.getClasse());
            }

            arq_bin.close();
            System.out.println("\nRegistro atualizado com sucesso!\n");
            return;

        } 

        arq_bin.close();
        System.out.println("\nRegistro não encontrado\n");
    }

    /*========================================================================
    deletarRegistro (Delete):  Marca o registro de um passageiro como deletado 
                               no arquivo binário.
     =========================================================================*/

     public static void deletarRegistro(int id_passageiro) throws IOException{
        RandomAccessFile arq_bin = new RandomAccessFile(arquivo_binario, "rw");
        
        int posicao = IndiceSeq.buscaIndice(id_passageiro);

        if (posicao == -1){
            System.out.println("\nRegistro não encontrado.\n");
            arq_bin.close();
            return;
        }

        //Move o ponteiro no arquivo de dados para a posição do registro
        arq_bin.seek(posicao);
        byte lapide = arq_bin.readByte();
        int tamRegistroDeletado = arq_bin.readInt();
        byte[] registroBytes = new byte[tamRegistroDeletado];
        arq_bin.readFully(registroBytes);

        Passageiro passageiroDeletado = new Passageiro();
        passageiroDeletado.fromByteArray(registroBytes);


        if(lapide == 1){ //Verificação por precaução
            System.out.println("\nEste registro já foi deletado. \n");
        } else {
            //Marca o registro como deletado
            arq_bin.seek(posicao);
            arq_bin.writeByte(0x01);
            System.out.println("\nRegistro deletado com sucesso!\n");

            //Remove o id do registro do arquivo de indice
            IndiceSeq.deletarIndice(id_passageiro);
            IndiceNome.deletaIndiceNome(id_passageiro);
            Multilista.deletarMultilista(id_passageiro, passageiroDeletado.getClasse());
        }

        arq_bin.close();
    }
}
