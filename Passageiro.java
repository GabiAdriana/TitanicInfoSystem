import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Passageiro{
    
    int id; // Identificador único do passageiro
    int sobreviveu; // Indica se o passageiro sobreviveu (1) ou não (0)
    int classe; // Classe do passageiro (1ª, 2ª ou 3ª classe)
    String nome; // Nome do passageiro
    String sexo; // Sexo do passageiro
    int nascimento; // Data de nascimento do passageiro
    String[] preferencias; // Preferências adicionais do passageiro
    Data data = new Data();
    int linkC; //Link para o proximo passageiro com a mesma classe, usado na multilista

    public Passageiro(){}

    
    public Passageiro( int id, int sobreviveu, int classe, String nome, String sexo, int nascimento, String[] preferencias, int linkC) {
        this.id = id;
        this.sobreviveu = sobreviveu;
        this.classe = classe;
        this.nome = nome;
        this.sexo = sexo;
        this.nascimento = nascimento;
        this.preferencias = preferencias;
        this.linkC = linkC;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSobreviveu() {
        return sobreviveu;
    }

    public void setSobreviveu(int sobreviveu) {
        this.sobreviveu = sobreviveu;
    }

    public int getClasse() {
        return classe;
    }

    public void setClasse(int classe) {
        this.classe = classe;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public int getNascimento() {
        return nascimento;
    }

    public void setNascimento(int nascimento) {
        this.nascimento = nascimento;
    }

    public String[] getPreferencias() {
        return preferencias;
    }

    public void setPreferencias(String[] preferencias) {
        this.preferencias = preferencias;
    }

    public int getlinkC(){
        return linkC;
    }

    public void setlinkC(int linkC){
        this.linkC = linkC;
    }

    @Override
    public String toString() {
        String preferenciasStr = (preferencias != null && preferencias.length > 0) ? String.join(", ", preferencias) : "Nenhuma";

        return "\nID: " + id + 
            "\nSobreviveu: " + sobreviveu +
            "\nClasse: " + classe +
            "\nNome: " + nome + 
            "\nSexo: " + sexo +  
            "\nNascimento: " + data.diasParaData(nascimento) + 
            "\nPreferências: " + preferenciasStr;
    }

    /*========================================================================
    toByteArray: Serializa os dados de um passageiro em um array de bytes.
     =========================================================================*/

    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        dos.writeInt(this.sobreviveu);
        dos.writeInt(this.classe);
        dos.writeUTF(this.nome);

        //Cripitografa o campo sexo
        String cripCesar = Criptografia.CriptografaCesar(this.sexo, 19);
        String cripCesarVigenere = Criptografia.criptografaVigenere(cripCesar, "gabi");
        String cripCesarvigenereColunas = Criptografia.criptografaColunas(cripCesarVigenere, 2);
        dos.writeUTF(cripCesarvigenereColunas);

        dos.writeInt(this.nascimento);
        
        // Serializar o número de preferências
        dos.writeInt(preferencias.length);
        for (String preferencia : preferencias) {
            dos.writeUTF(preferencia);
        }

        dos.writeInt(linkC);

        dos.flush();
        return baos.toByteArray();
    }


    /*========================================================================
    fromByteArray: Desserializa um array de bytes e converte de volta em 
                   um objeto Passageiro.
     =========================================================================*/

    public void fromByteArray(byte[] b) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInputStream dis = new DataInputStream(bais);
        this.id = dis.readInt();
        this.sobreviveu = dis.readInt();
        this.classe = dis.readInt();
        this.nome = dis.readUTF();
        
        String cripCesarvigenereColunas = Criptografia.descriptografaColunas(dis.readUTF(), 2);
        String cripCesarVigenere = Criptografia.descriptografaVigenere(cripCesarvigenereColunas, "gabi");
        String cripCesar = Criptografia.DescriptografaCesar(cripCesarVigenere, 19);
        this.sexo = cripCesar;

        this.nascimento = dis.readInt();

        // Desserializar o número de preferências
        int numPreferencias = dis.readInt();
        this.preferencias = new String[numPreferencias];
        for (int i = 0; i < numPreferencias; i++) {
            this.preferencias[i] = dis.readUTF();
        }

        this.linkC = dis.readInt();
    }

}
