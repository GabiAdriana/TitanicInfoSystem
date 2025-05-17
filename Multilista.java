import java.io.IOException;
import java.io.RandomAccessFile;

public class Multilista {
    public static String arq_multilista = "Multilista.bin";

    public static Passageiro criaMultilista(int classeNova, int posInicial, Passageiro passageiro) throws IOException{
        RandomAccessFile arqMultilista = new RandomAccessFile(arq_multilista, "rw");
        RandomAccessFile arq_bin = new RandomAccessFile("base_titanic.bin", "rw");
        
        if (arqMultilista.length() == 0){
            arqMultilista.seek(0);
            arqMultilista.writeInt(classeNova);
            arqMultilista.writeInt(1); // Quantidade
            arqMultilista.writeInt(posInicial);
            passageiro.setlinkC(-1);

        } else {
            boolean achou = false;
            int ultimoPassageiroPos = -1;

            // Varre a multilista para encontrar a classeNova correspondente
            while (arqMultilista.getFilePointer() < arqMultilista.length()) {
                int classeNovaMult = arqMultilista.readInt();
                int quantMult = arqMultilista.readInt();
                int posInicialMult = arqMultilista.readInt(); // Lê a posição inicial da lista da classeNova

                if (classeNovaMult == classeNova) {
                    // Atualiza o contador de passageiros da classeNova
                    arqMultilista.seek(arqMultilista.getFilePointer() - 8); // Volta para a posição do contador
                    arqMultilista.writeInt(quantMult + 1); // Incrementa a quantidade de passageiros na lista
                    achou = true;

                    // Encontrar o último passageiro da mesma classeNova para atualizar o linkC
                    int posAtual = posInicialMult;
                    while (posAtual != -1) {
                        arq_bin.seek(posAtual);
                        arq_bin.readByte(); // Lapide
                        int tamRegistro = arq_bin.readInt();
                        byte[] registro = new byte[tamRegistro];
                        arq_bin.readFully(registro);
                    
                        Passageiro passageiroAtual = new Passageiro();
                        passageiroAtual.fromByteArray(registro);
                    
                        if (passageiroAtual.getlinkC() == -1) {
                            ultimoPassageiroPos = posAtual;
                            break; // Achou o último passageiro da lista
                        }
                    
                        posAtual = passageiroAtual.getlinkC(); // Move para o próximo passageiro da lista
                    }
                    break;
                }
            }

            if (!achou) {
                // Se não encontrou a classeNova, cria um novo grupo para essa classeNova
                arqMultilista.seek(arqMultilista.length());
                arqMultilista.writeInt(classeNova); // Escreve a nova classeNova
                arqMultilista.writeInt(1); // Primeiro passageiro
                arqMultilista.writeInt(posInicial); // Posição do primeiro passageiro dessa classeNova
                passageiro.setlinkC(-1);

            } else if (ultimoPassageiroPos != -1) {
                // Se encontrou o último passageiro da classeNova, atualiza o linkC para apontar para o novo passageiro
                arq_bin.seek(ultimoPassageiroPos);
                arq_bin.readByte(); // Lapide
                int tamRegistro = arq_bin.readInt();
                byte[] registro = new byte[tamRegistro];
                arq_bin.readFully(registro);

                Passageiro ultimoPassageiro = new Passageiro();
                ultimoPassageiro.fromByteArray(registro);

                // Atualiza o linkC do último passageiro para apontar para o novo passageiro
                ultimoPassageiro.setlinkC(posInicial);
                arq_bin.seek(ultimoPassageiroPos);
                arq_bin.writeByte(0); // Lapide válida
                arq_bin.writeInt(tamRegistro);
                arq_bin.write(ultimoPassageiro.toByteArray());

                passageiro.setlinkC(-1); //Novo passageiro é o ultimo agora
            }
        }

        arqMultilista.close();
        arq_bin.close();
        return passageiro;
    }

    public static void lerMultilista(int classeNova) throws IOException{
        RandomAccessFile arqMultilista = new RandomAccessFile(arq_multilista, "r");
        RandomAccessFile arq_bin = new RandomAccessFile("base_titanic.bin", "r");
    
        boolean achou = false;
    
        // Varre a multilista para encontrar a classeNova correspondente
        while (arqMultilista.getFilePointer() < arqMultilista.length()) {
            int classeNovaMult = arqMultilista.readInt();
            int quantMult = arqMultilista.readInt();
            int posInicialMult = arqMultilista.readInt(); // Posição inicial da lista
    
            if (classeNovaMult == classeNova) {
                achou = true;
                int posAtual = posInicialMult;
                System.out.println("\nPassageiros da classeNova " + classeNova + ":");
    
                // Percorre a lista de passageiros da mesma classeNova
                while (posAtual != -1) {
                    arq_bin.seek(posAtual);
                    arq_bin.readByte(); // Lapide
                    int tamRegistro = arq_bin.readInt();
                    byte[] registro = new byte[tamRegistro];
                    arq_bin.readFully(registro);
    
                    Passageiro passageiroAtual = new Passageiro();
                    passageiroAtual.fromByteArray(registro);
                    
                    System.out.println(passageiroAtual); // Exibe o passageiro
    
                    posAtual = passageiroAtual.getlinkC(); // Move para o próximo passageiro da lista
                }
                break;
            }
        }
    
        if (!achou) {
            System.out.println("Nenhum passageiro encontrado para a classeNova " + classeNova + ".");
        }
    
        arqMultilista.close();
        arq_bin.close();
    }

    /*
     * Tentei mmanter o encadeamento organizado por id, assim a ideia é que mesmo que o 
     * passageiro troque de posição no arquivo de dados ele continuará sendo exibido na
     * mesma ordem pelo encadeamento da multilista.
     * 
     * É possivel fazer a modificação para a atualização para manter a ordem do arquivo de dados.
     */

    public static void atualizarMultilista(Passageiro novoPassageiro, int novaPosicao, int classeAntiga, int classeNova) throws IOException{
        RandomAccessFile arq_bin = new RandomAccessFile("base_titanic.bin", "rw");
        RandomAccessFile arqMultilista = new RandomAccessFile(arq_multilista, "rw");

        //Verifica se houve mudança de classe
        if(classeAntiga != classeNova){
            //Remove o passageiro do encadeamento da classe antiga
            deletarMultilista(novoPassageiro.getId(), classeAntiga);

            //Adiciona o passageiro ao encadeamento da nova classe
            criaMultilista(classeNova, novaPosicao, novoPassageiro);
        } else {
            
            int posAnterior = -1;
            int posAtual = -1;
    
            //System.out.println("classeNova passada:" + classeNova);
    
            //Percorre o arquivo da multilista para localizar a posição inicial do encadeamento da classeNova
            while(arqMultilista.getFilePointer() < arqMultilista.length()){
                int classeNovaMult = arqMultilista.readInt();
                int quantPassageiros = arqMultilista.readInt();
                int posInicial = arqMultilista.readInt();
    
                if(classeNovaMult == classeNova){
                    posAtual = posInicial;
                    break;
                }
            }
            //System.out.println("Achou o valor da posAtual:" + posAtual);
    
            //Loop para encontrar o passageiroo na lista encadeada no arquivo de passageiros 
            while (posAtual != -1){
                arq_bin.seek(posAtual); // Move o ponteiro para o inicio do encadeamento
                arq_bin.readByte(); //lapede
                Passageiro passageiroAtual = new Passageiro();
                int tamRegistro = arq_bin.readInt();
                //System.out.println("tamRegistro: "+tamRegistro);
                byte[] registroBytes = new byte [tamRegistro];
                arq_bin.readFully(registroBytes);
    
                passageiroAtual.fromByteArray(registroBytes);
    
                //Verifica se passageiro atual é o que estamos atualizando
                if (passageiroAtual.getId() == novoPassageiro.getId()){
                    //Se o passageiro estiver na primeira posição, atualiza o  ponteiro de inicio na multilista
                    //byte lapedeOriginal = lapide;
                    if(posAnterior == -1){
                        arqMultilista.seek(arqMultilista.getFilePointer() - 4);
                        arqMultilista.writeInt(novaPosicao);
                    } else {
                        //Se não estiver na primeira posição do encadeamento, atualiza o link C do passageiro anterior
                        arq_bin.seek(posAnterior); // Move o ponteiro para o registro anterior do encadeamento
                        arq_bin.readByte(); //lapide
                        int tamRegAnterior = arq_bin.readInt();
                        byte[] regAnteriorBytes = new byte[tamRegAnterior];
                        arq_bin.readFully(regAnteriorBytes);
    
                        Passageiro passageiroAnterior = new Passageiro();
                        passageiroAnterior.fromByteArray(regAnteriorBytes);
                        passageiroAnterior.setlinkC(novaPosicao); //Atualiza o linkC com a nova posição do proximo passageiro
    
                        //Sobrescreve o registro atualizaddo na nova posição
                        arq_bin.seek(posAnterior);
                        arq_bin.writeByte(0); // lapide
                        arq_bin.writeInt(regAnteriorBytes.length);
                        arq_bin.write(passageiroAnterior.toByteArray()); //Escreve o registro atualizado
                    }
    
                    novoPassageiro.setlinkC(passageiroAtual.getlinkC()); //Atualiza o linkC do registro atualizado
    
                    //Sobrescreve o registro atualizado na nova posição
                    arq_bin.seek(novaPosicao);
                    arq_bin.writeByte(0); //lapide
                    arq_bin.writeInt(novoPassageiro.toByteArray().length);
                    arq_bin.write(novoPassageiro.toByteArray()); //Escreve o registro atualizado
    
                    System.out.println("Passageiro atualizado na multilista com sucesso");
                    break;
                }
                //Atualiza a posição anterior
                posAnterior = posAtual;
                posAtual = passageiroAtual.getlinkC(); //vai para o proximo registro do encadeamento
            }
        }
        //Fecha os arquivos
        arqMultilista.close();;
        arq_bin.close();
    }


    public static void deletarMultilista(int idPassageiro, int classeNova) throws IOException{
        RandomAccessFile arqMultilista = new RandomAccessFile(arq_multilista, "rw");
        RandomAccessFile arq_bin = new RandomAccessFile("base_titanic.bin", "rw");

        int posAnterior = -1;
        int posAtual = -1;
        boolean achouclasseNova = false;
    
        arqMultilista.seek(0);

        while (arqMultilista.getFilePointer() < arqMultilista.length()) {
            int classeNovaMult = arqMultilista.readInt();
            int quantPassageiros = arqMultilista.readInt();
            int posInicial = arqMultilista.readInt();
    
            if (classeNovaMult == classeNova) {
                posAtual = posInicial;
                achouclasseNova = true;
    
                // Se a quantidade de passageiros for maior que zero, decrementa
                if (quantPassageiros > 0) {
                    arqMultilista.seek(arqMultilista.getFilePointer() - 8); // Retorna para a posição do contador
                    arqMultilista.writeInt(quantPassageiros - 1); // Decrementa a quantidade
                }
                break;
            }
        }
    
        if (!achouclasseNova) {
            System.out.println("classeNova não encontrada na multilista.");
            arqMultilista.close();
            arq_bin.close();
            return;
        }
    
        // Percorre a lista encadeada de passageiros para encontrar e remover o passageiro com o ID desejado
        while (posAtual != -1) {
            arq_bin.seek(posAtual); // Move o ponteiro para o inicio do encadeamento
            //System.out.println("posAtual: "+posAtual);
            arq_bin.readByte(); //lapede
            Passageiro passageiroAtual = new Passageiro();
            int tamRegistro = arq_bin.readInt();
            //System.out.println("tamRegistro: "+tamRegistro);
            byte[] registroBytes = new byte [tamRegistro];
            arq_bin.readFully(registroBytes);

            passageiroAtual.fromByteArray(registroBytes);
    
            if (passageiroAtual.getId() == idPassageiro) {
                // Se o passageiro estiver na primeira posição da lista, atualiza a posição inicial da classeNova na multilista
                if (posAnterior == -1) {
                    arqMultilista.seek(arqMultilista.getFilePointer() - 4);
                    arqMultilista.writeInt(passageiroAtual.getlinkC());
                } else {
                    // Atualiza o linkC do passageiro anterior para pular o passageiro deletado
                    arq_bin.seek(posAnterior);
                    arq_bin.readByte();
                    int tamRegistroAnterior = arq_bin.readInt();
                    byte[] regAnteriorBytes = new byte[tamRegistroAnterior];
                    arq_bin.readFully(regAnteriorBytes);
    
                    Passageiro passageiroAnterior = new Passageiro();
                    passageiroAnterior.fromByteArray(regAnteriorBytes);
                    passageiroAnterior.setlinkC(passageiroAtual.getlinkC());
    
                    // Sobrescreve o registro do passageiro anterior atualizado
                    arq_bin.seek(posAnterior);
                    arq_bin.writeByte(0);
                    arq_bin.writeInt(tamRegistroAnterior);
                    arq_bin.write(passageiroAnterior.toByteArray());
                }
                System.out.println("Passageiro deletado da multilista com sucesso.");
                
                // Ajuste do último linkC para -1 se o deletado era o último
                if (passageiroAtual.getlinkC() == -1) {
                    arq_bin.seek(posAnterior);
                    arq_bin.readByte();
                    int tamRegAnt = arq_bin.readInt();
                    byte[] regAntBytes = new byte[tamRegAnt];
                    arq_bin.readFully(regAntBytes);

                    Passageiro ultimoPassageiro = new Passageiro();
                    ultimoPassageiro.fromByteArray(regAntBytes);
                    ultimoPassageiro.setlinkC(-1);

                    arq_bin.seek(posAnterior);
                    arq_bin.writeByte(0);
                    arq_bin.writeInt(tamRegAnt);
                    arq_bin.write(ultimoPassageiro.toByteArray());
                }
                break;
            }
    
            posAnterior = posAtual;
            posAtual = passageiroAtual.getlinkC(); // Move para o próximo passageiro na lista
        }
    
        arqMultilista.close();
        arq_bin.close();
    }
}
