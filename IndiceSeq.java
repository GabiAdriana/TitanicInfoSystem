import java.io.IOException;
import java.io.RandomAccessFile;

public class IndiceSeq {
    public static String arq_indiceSeq = "indiceSeq.bin";
    
        /*===================================================================================
     *                              Indice Sequencial (direto e denso)
     ====================================================================================*/
     
     /*==================================================================================
      * criaIndice: [...]
      ===================================================================================*/
     
      public static void criaIndice(int id, int posicao) throws IOException{
        RandomAccessFile arqIndiceSeq = new RandomAccessFile(arq_indiceSeq, "rw");
        arqIndiceSeq.seek(arqIndiceSeq.length()); //Vai para o final do arquivo
        arqIndiceSeq.writeInt(id);                //Escreve o id no arquivo de indice
        arqIndiceSeq.writeInt(posicao);          //Escreve a posição no arquivo de indice
        arqIndiceSeq.close();
    }  

    public static int buscaIndice(int id) throws IOException{
        RandomAccessFile arqIndiceSeq = new RandomAccessFile(arq_indiceSeq, "r");
        int posicao = -1;

        //Faz a busca
        while (arqIndiceSeq.getFilePointer() < arqIndiceSeq.length()){
            int idIndice = arqIndiceSeq.readInt();
            int posicaoIndice = arqIndiceSeq.readInt();
            if (idIndice == id){
                posicao = posicaoIndice;
                break;
            }
        }

        arqIndiceSeq.close();
        return posicao;
    }

    public static void atualizaIndice(int id, int novaPosicao) throws IOException{
        RandomAccessFile arqIndiceSeq = new RandomAccessFile(arq_indiceSeq, "rw");
        
        // Busca o registro no arquivo de indice
        while(arqIndiceSeq.getFilePointer() < arqIndiceSeq.length()){
            int idIndice = arqIndiceSeq.readInt();
            if (idIndice == id){
                //Atualiza a posição no arquivo
                arqIndiceSeq.writeInt(novaPosicao);
                break;
            }
        }

        arqIndiceSeq.close();
    }

    public static void deletarIndice(int id) throws IOException{
        RandomAccessFile arqIndiceSeq = new RandomAccessFile(arq_indiceSeq, "rw");

        int posicao = -1;

        //Faz a busca
        while (arqIndiceSeq.getFilePointer() < arqIndiceSeq.length()){
            posicao = (int) arqIndiceSeq.getFilePointer(); //Armazena a posição atual do indice
            int idIndice = arqIndiceSeq.readInt();
            if (idIndice == id){
                break;
            }
        }


        //Move o conteúdo após a posição do índice deletado
        arqIndiceSeq.seek(posicao + 8); //4 bytes para o id + 4 bytes para a posição
        byte[] buffer = new byte[(int) (arqIndiceSeq.length() - arqIndiceSeq.getFilePointer())];
        arqIndiceSeq.readFully(buffer);

        arqIndiceSeq.setLength(posicao);   //Reduz o tamanho do arquivo para antes do registro deletado 
        arqIndiceSeq.seek(posicao);
        arqIndiceSeq.write(buffer);              //Escreve de volta o conteúdo após o registro deletado
    
        arqIndiceSeq.close();
    }
}