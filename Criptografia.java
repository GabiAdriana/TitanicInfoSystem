
import java.io.IOException;

public class Criptografia{

    /*=======================Cifra de César==================================================== */

    public static String CriptografaCesar(String mensagem, int senha) throws IOException{
        StringBuilder resultado = new StringBuilder();
        char ascii;
        char x, y;

        // Ajusta a senha ao intervalo do alfabeto
        senha = senha % 26; 

        for (int i = 0; i<mensagem.length(); i++){
            //Código tratando apenas letras minusculas por formatação do arquivo original
            char caractere = mensagem.charAt(i);

            // Letras minúsculas
            if (caractere >= 'a' && caractere <= 'z') {
                if (caractere + senha > 'z') {
                    x = (char) (caractere + senha);
                    y = (char) (x - 'z');
                    ascii = (char) ('a' - 1 + y);
                } else {
                    ascii = (char) (caractere + senha);
                }
                resultado.append(ascii);
            } 
            // Letras maiúsculas
            else if (caractere >= 'A' && caractere <= 'Z') {
                if (caractere + senha > 'Z') {
                    x = (char) (caractere + senha);
                    y = (char) (x - 'Z');
                    ascii = (char) ('A' - 1 + y);
                } else {
                    ascii = (char) (caractere + senha);
                }
                resultado.append(ascii);
            } 
            // Outros caracteres (mantém inalterado)
            else {
                resultado.append(caractere);
            }
        }
        return resultado.toString();
    }

    public static String DescriptografaCesar(String mensagem, int senha) throws IOException{
        return CriptografaCesar(mensagem, 26 - (senha % 26)); // Reverso da cifra
    }

    /*========================Cifra de Vigenère================================== */

    // Método para criptografar a mensagem usando a Cifra de Vigenère
    public static String criptografaVigenere(String mensagem, String senha) throws IOException{
        StringBuilder resultado = new StringBuilder();
        int tamanhoSenha = senha.length();

        for (int i = 0, j = 0; i < mensagem.length(); i++) {
            char c = mensagem.charAt(i);
            
            // Letras minúsculas
            if (c >= 'a' && c <= 'z') {
                // Calcula deslocamento e aplica a cifra
                resultado.append((char) ((c + senha.charAt(j) - 2 * 'a') % 26 + 'a'));
                j = (j + 1) % tamanhoSenha;
            } 
            // Letras maiúsculas
            else if (c >= 'A' && c <= 'Z') {
                resultado.append((char) ((c + senha.charAt(j) - 2 * 'A') % 26 + 'A'));
                j = (j + 1) % tamanhoSenha;
            } 
            // Outros caracteres (mantém inalterado)
            else {
                resultado.append(c);
            }
        }
        return resultado.toString();
    }

    // Método para descriptografar a mensagem usando a Cifra de Vigenère
    public static String descriptografaVigenere(String mensagem, String senha) throws IOException {
        StringBuilder resultado = new StringBuilder();
        int tamanhoSenha = senha.length();
        
        for (int i = 0, j = 0; i < mensagem.length(); i++) {
            char c = mensagem.charAt(i);
            
            // Letras minúsculas
            if (c >= 'a' && c <= 'z') {
                // Calcula deslocamento inverso e aplica a cifra
                resultado.append((char) ((c - senha.charAt(j) + 26) % 26 + 'a'));
                j = (j + 1) % tamanhoSenha; // Avança na senha
            } 
            // Letras maiúsculas
            else if (c >= 'A' && c <= 'Z') {
                resultado.append((char) ((c - senha.charAt(j) + 26) % 26 + 'A'));
                j = (j + 1) % tamanhoSenha;
            } 
            // Outros caracteres (mantém inalterado)
            else {
                resultado.append(c);
            }
        }
        return resultado.toString();
    }

    /*======================Cifra de Colunas==================================== */

    // Método de criptografia Cifra de Colunas
    public static String criptografaColunas(String mensagem, int chave) {
        StringBuilder resultado = new StringBuilder();
        
        // Percorre cada coluna baseada na chave
        for (int i = 0; i < chave; i++) {
            for (int j = i; j < mensagem.length(); j += chave) {
                resultado.append(mensagem.charAt(j));  // Adiciona caracteres intercalados
            }
        }
        return resultado.toString();  // Retorna a mensagem criptografada
    }

    // Método para descriptografar usando Cifra de Colunas
    public static String descriptografaColunas(String mensagem, int chave) {
        int tamanhoMensagem = mensagem.length();
        int numLinhas = (int) Math.ceil((double) tamanhoMensagem / chave);  // Calcula linhas necessárias
        StringBuilder resultado = new StringBuilder(mensagem);
        
        int index = 0;
        // Preenche a matriz por linhas simuladas
        for (int i = 0; i < chave; i++) {
            for (int j = i; j < tamanhoMensagem; j += chave) {
                resultado.setCharAt(j, mensagem.charAt(index++));  // Reorganiza os caracteres
            }
        }
        return resultado.toString();  // Retorna a mensagem descriptografada
    }
}