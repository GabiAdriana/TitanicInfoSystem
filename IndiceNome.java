import java.io.IOException;
import java.io.RandomAccessFile;

public class IndiceNome {
    public static String arq_indiceNome = "indiceNome.bin";


/*===================================================================================
*                       Indice Nome (indireto e denso)
====================================================================================*/

     public static void criaIndiceNome(String nome, int id) throws IOException{
        RandomAccessFile arqIndiceNome = new RandomAccessFile(arq_indiceNome, "rw");
        arqIndiceNome.seek(arqIndiceNome.length()); //Vai para o final do arquivo
        arqIndiceNome.writeUTF(nome);                //Escreve o id no arquivo de indice
        arqIndiceNome.writeInt(id);          //Escreve a posição no arquivo de indice
        arqIndiceNome.close();
    }

    public static int buscaIndiceNome(String nome) throws IOException{
        RandomAccessFile arqIndiceNome = new RandomAccessFile(arq_indiceNome, "r");
        int id = -1;
        System.out.println(nome);

        //Faz a busca
        while (arqIndiceNome.getFilePointer() < arqIndiceNome.length()){
            String nomeArq = arqIndiceNome.readUTF();
            int idIndice = arqIndiceNome.readInt();
            //System.out.println(nomeArq);
            if (nome.equals(nomeArq)){
                id = idIndice;
                break;
            }
        }

        arqIndiceNome.close();
        return id;
    }

    public static int[] lerConjuntoDeRegNomes(String[] nomes) throws IOException{
        int[] idsNomes = new int[nomes.length];
        for(int i = 0; i<nomes.length; i++){
            idsNomes[i] = buscaIndiceNome(nomes[i]);
        }
        return idsNomes;
    }

    public static void atualizaIndiceNome(String novoNome, String nomeAntigo) throws IOException{
        RandomAccessFile arqIndiceNome = new RandomAccessFile(arq_indiceNome, "rw");
        //System.out.println(nomeAntigo);
        //System.out.println(novoNome);
        
        while(arqIndiceNome.getFilePointer() < arqIndiceNome.length()){
            int posicao = (int) arqIndiceNome.getFilePointer();
            String nome = arqIndiceNome.readUTF();
            int id = arqIndiceNome.readInt();

            if (nome.equals(nomeAntigo)){
                //Verifica se o novo nome tem tamanho diferente do nome antigo
                if(novoNome.length() != nomeAntigo.length()){
                    //Se o novo nome for maior ou menor, será necessário mover os registros subsequentes

                    //Vai para a posição logo após o nome antigo
                    arqIndiceNome.seek(posicao + nome.length() + 2 + 4); //2 bytes para o tamanho da sstring e 4 bytes para o ID
                    //Lê o retsante do arquivo após o registro atual
                    byte[] buffer = new byte[(int) (arqIndiceNome.length() - arqIndiceNome.getFilePointer())];
                    arqIndiceNome.readFully(buffer);

                    //Volta para a posição original e escreve o nome e o id e volta o cconteúdo que estava após o registro atualizado
                    arqIndiceNome.seek(posicao);
                    arqIndiceNome.writeUTF(novoNome);
                    arqIndiceNome.writeInt(id);
                    arqIndiceNome.write(buffer);

                } else{
                    //Se o tamanho for o mesmo, basta sobrescrever o nome
                    arqIndiceNome.seek(posicao);
                    arqIndiceNome.writeUTF(novoNome);
                }
                break; //Nome atualizado, saimos do loop
            }
        }

        arqIndiceNome.close();
    }

    public static void deletaIndiceNome(int id) throws IOException{
        RandomAccessFile arqIndiceNome = new RandomAccessFile(arq_indiceNome, "rw");
        int posicao = -1;
        // Variáveis para armazenar a posição e o nome associado ao ID
        String nomeAssociado = null;

        // Faz a busca para encontrar o nome associado ao ID
        while (arqIndiceNome.getFilePointer() < arqIndiceNome.length()) {
            posicao = (int) arqIndiceNome.getFilePointer(); // Armazena a posição atual do indice
            String nome = arqIndiceNome.readUTF();    // Lê o nome
            int idIndice = arqIndiceNome.readInt();   // Lê o ID associado

            if (idIndice == id) {
                nomeAssociado = nome; // Armazena o nome associado ao ID
                break; // Sai do loop se o ID for encontrado
            }
        }

        //Se o nome for encontrado remover o registro
        if (posicao != -1) {
            // Move o conteúdo após o registro deletado
            arqIndiceNome.seek(posicao + nomeAssociado.length() + 4 + 2); // 2 bytes extras para o tamanho da string
            byte[] buffer = new byte[(int) (arqIndiceNome.length() - arqIndiceNome.getFilePointer())];
            arqIndiceNome.readFully(buffer);
            
            // Reduz o tamanho do arquivo para antes do registro deletado
            arqIndiceNome.setLength(posicao); 
            arqIndiceNome.seek(posicao);

            // Escreve de volta o conteúdo após o registro deletado
            arqIndiceNome.write(buffer);
        }

        arqIndiceNome.close();
    }

}
