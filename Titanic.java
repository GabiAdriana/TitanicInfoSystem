import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

public class Titanic {
    static String arquivo_binario = "base_titanic.bin";    //Cria o nome do arquivo de dados
    static String arq_indiceSeq = "indiceSeq.bin";         //Cria o nome do arquivo de indice Sequencial
    static String arq_indiceNome = "indiceNome.bin";       //Cria o nome do arquivo de indice de nomes
    static String arq_Multilista = "Multilista.bin";       //Cria o nome do arquivo Multilista

    /*===================================================================
    Main: Controla o fluxo principal do programa, exibindo um menu de 
            opções e executando as ações conforme a escolha do usuário.
    ===================================================================== */
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        int opcao;

        do {
            System.out.println("\n=====================================");
            System.out.println("             ,:',:`,:',:'");
            System.out.println("        _____||_||_||_||_____");
            System.out.println("  _____|'''''''''''''''''''''|___");
            System.out.println("  \\ '''''''''''''''''''''''''''' \\");
            System.out.println("~^~^~^~^~^^~^~^~^~^~^~^~^~~^~^~^~^~~^");
            System.out.println("             <3 TITANIC <3          ");
            System.out.println("=========== Menu de Opções ==========");
            System.out.println("1. Realizar a carga da base de dados");
            System.out.println("2. Criar um registro de passageiro");
            System.out.println("3. Ler um registro de passageiro");
            System.out.println("4. Ler um conjunto de registros");
            System.out.println("5. Atualizar o registro de um passageiro");
            System.out.println("6. Deletar o registro de um passageiro");
            System.out.println("7. Pesquisar passageiro por classe (Multilista)");
            System.out.println("8. Opções de compactação e descompactação");
            System.out.println("9. Casamento de Padrão");
            System.out.println("10. Criptografar/Descriptografar uma mensagem");
            System.out.println("11. Deletar aquivos de dados e índices originais");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");
            
            // Verifica se a entrada é um número inteiro válido e dentro do intervalo esperado
            while (!scanner.hasNextInt()) {
                System.out.println("Por favor, insira um número válido.");
                scanner.next(); // Consome a entrada inválida
            }
            opcao = scanner.nextInt();
            
            switch (opcao) {
                case 1:
                    carregarBaseDados();
                    break;
                case 2:
                    CRUD.criaRegistro();
                    break;
                case 3:
                    buscarRegistro();
                    break;
                case 4:
                    buscarConjRegistros();
                    break;
                case 5:
                    Passageiro novoPassageiro = new Passageiro();

                    System.out.print("\nInforme o ID do passageiro a ser atualizado: ");
                    int id_atualizar;
                    // Verifica se a entrada é um inteiro positivo para o ID
                    while (!scanner.hasNextInt() || (id_atualizar = scanner.nextInt()) < 0) {
                        System.out.println("ID inválido. Por favor, insira um número inteiro positivo.");
                        scanner.next();
                    }
                    novoPassageiro.setId(id_atualizar);

                    // Verifica se a entrada para "sobreviveu" é 0 ou 1
                    int sobreviveu;
                    System.out.print("Informe se sobreviveu (0 = não, 1 = sim): ");
                    while (!scanner.hasNextInt() || (sobreviveu = scanner.nextInt()) < 0 || sobreviveu > 1) {
                        System.out.println("Entrada inválida. Informe 0 ou 1.");
                    }
                    novoPassageiro.setSobreviveu(sobreviveu);

                    // Verifica se a classe é válida (1, 2 ou 3)
                    int classe;
                    System.out.print("Informe a classe (1, 2, ou 3): ");
                    while (!scanner.hasNextInt() || (classe = scanner.nextInt()) < 1 || classe > 3) {
                        System.out.println("Classe inválida. Escolha entre 1, 2 ou 3.");
                    }
                    novoPassageiro.setClasse(classe);

                    scanner.nextLine(); // Limpa o buffer

                    //Verifica a entrada do nome
                    System.out.print("Informe o nome: ");
                    novoPassageiro.setNome(scanner.nextLine());

                    // Verifica a entrada do sexo
                    System.out.print("Informe o sexo (male/female): ");
                    String sexo = scanner.nextLine().toLowerCase();
                    while (!sexo.equals("male") && !sexo.equals("female")) {
                        System.out.println("Entrada inválida. Informe 'male' ou 'female'.");
                        sexo = scanner.nextLine().toLowerCase();
                    }
                    novoPassageiro.setSexo(sexo);

                    // Verifica o formato da data de nascimento
                    System.out.print("Informe a data de nascimento (dd/mm/aaaa): ");
                    String dataNasc = scanner.nextLine();
                    while (!dataNasc.matches("\\d{2}/\\d{2}/\\d{4}")) {
                        System.out.println("Formato inválido. Use dd/mm/aaaa.");
                        dataNasc = scanner.nextLine();
                    }
                    String[] dados_data = dataNasc.split("/");
                    Data novaData = new Data();
                    novoPassageiro.setNascimento(novaData.diasAteData(Integer.parseInt(dados_data[0]), Integer.parseInt(dados_data[1]), Integer.parseInt(dados_data[2])));

                    //Tratamento das preferencias
                    System.out.print("Informe as preferências separadas por ' - ': ");
                    String preferenciasStr = scanner.nextLine();
                    novoPassageiro.setPreferencias(preferenciasStr.split(" - "));

                    novoPassageiro.setlinkC(-1);
                    
                    CRUD.atualizarRegistro(novoPassageiro);
                    break;
                case 6:
                    System.out.print("\nInforme o ID do passageiro a ser deletado: ");
                    int id_deletar = scanner.nextInt();
                    CRUD.deletarRegistro(id_deletar);
                    break;
                case 7:
                    System.out.println("Informe a classe que está procurando: ");
                    int op = scanner.nextInt();
                    Multilista.lerMultilista(op);
                    break;
                case 8:
                    LZW.gerenciadorDeCompactação();
                    break;
                case 9:
                    CasamentoPadrao.menuCasamentoPadrao();
                    break;
                case 10:
                    String mensagem;
                    System.out.print("Digite a mensagem: ");
                    scanner.nextLine(); //Limpa o buffer
                    mensagem = scanner.nextLine();

                    //Teste em cifra de Cesar --------------------------------------
                    System.out.println("\nCifra de Cesar: ");
                    String resultado = Criptografia.CriptografaCesar(mensagem, 19);
                    System.out.println("\nCriptografado: " + resultado);
                    resultado = Criptografia.DescriptografaCesar(resultado, 19);
                    System.out.println("Descriptografado: " + resultado);

                    //Teste em cifra de Cesar --------------------------------------
                    System.out.println("\nCifra de Vigenere: ");
                    String senha = "gabi";
                    resultado = Criptografia.criptografaVigenere(mensagem, senha);
                    System.out.println("\nCriptografado: " + resultado);
                    resultado = Criptografia.descriptografaVigenere(resultado, senha);
                    System.out.println("Descriptografado: " + resultado);

                    //Teste em cifra de Cesar --------------------------------------
                    System.out.println("\nCifra de Colunas: ");
                    resultado = Criptografia.criptografaColunas(mensagem, 2);
                    System.out.println("\nCriptografado: " + resultado);
                    resultado = Criptografia.descriptografaColunas(resultado, 2);
                    System.out.println("Descriptografado: " + resultado);
                    break;
                case 11:
                    LZW.deletarArquivo(arquivo_binario);
                    LZW.deletarArquivo(arq_indiceSeq);
                    LZW.deletarArquivo(arq_indiceNome);
                    LZW.deletarArquivo(arq_Multilista);

                    System. out.println("\nArquivos deletados com sucesso!");
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        } while (opcao != 0);

        scanner.close();
    }

    /*===================================================================
    carregarBaseDados: Carrega dados de um arquivo CSV e grava os 
                        registros no arquivo binário correspondente.
     ====================================================================*/

    public static void carregarBaseDados() throws IOException{
        RandomAccessFile arq = new RandomAccessFile("titanic_original1.csv", "rw");
        RandomAccessFile arq_bin = new RandomAccessFile(arquivo_binario, "rw");
        Passageiro passageiro = new Passageiro();

        // Verifica o tamanho do arquivo binário e inicializa o cabeçalho com o último ID (caso não exista)
        long t = arq_bin.length();
        if (t < 1) {
            arq_bin.writeInt(0);  // Escreve o cabeçalho inicial com ID 0
        }

        arq.readLine(); //Pula a primeira linha (cabeçalho do arquivo .csv)

        //Variáveis auxiliares
        String linha;
        String[] dados;
        String[] dados_data;

        while((linha = arq.readLine()) != null){
            linha = linha.replace("\"", "");
            dados = linha.split(";");

            //Constroi o passageiro com os dados
            passageiro.setId(Integer.parseInt(dados[0]));
            passageiro.setSobreviveu(Integer.parseInt(dados[1]));
            passageiro.setClasse(Integer.parseInt(dados[2]));
            passageiro.setNome(dados[3]);
            passageiro.setSexo(dados[4]);

            //Data
            dados_data = dados[5].split("/");
            Data data = new Data();
            passageiro.nascimento = data.diasAteData(Integer.parseInt(dados_data[0]), Integer.parseInt(dados_data[1]), Integer.parseInt(dados_data[2])); 

            //Preferencias
            passageiro.setPreferencias(dados[6].split(" - "));

            //LinkC
            passageiro.setlinkC(-1);

            // Escreve no arquivo binario
            CRUD.criaRegistro(passageiro, arq_bin);
        }

        // fecha arquivos
        arq.close();
        arq_bin.close();
        System.out.print("\nOperação concluida\n");
    }

    public static void buscarRegistro() throws IOException {
        Scanner scanner = new Scanner(System.in);
        int op;
        System.out.println("\n=====================================");
        System.out.println("Escolha: ");
        System.out.println("1. Pesquisar passageiro por ID.");
        System.out.println("2. Pesquisar passageiro por Nome.");
        System.out.print("Sua opção: ");
        op = scanner.nextInt();

        if (op == 1) {
            System.out.println("\nInforme o id do passageiro: ");
            int id_passageiro = scanner.nextInt();
            CRUD.lerRegistro(id_passageiro);
        } else if (op == 2) {
            System.out.println("\nInforme o nome do passageiro: ");
            scanner.nextLine();
            String nomePass = scanner.nextLine();
            int idPass = IndiceNome.buscaIndiceNome(nomePass);
            CRUD.lerRegistro(idPass);
        }
        //scanner.close();
    }

    public static void buscarConjRegistros() throws IOException{
        Scanner scanner = new Scanner(System.in);
        int quant;
        System.out.println("Quantos registros gostaria de ler? ");
        quant = scanner.nextInt();
        int op;
        System.out.println("\nEscolha: ");
        System.out.println("1. Pesquisar passageiros por ID.");
        System.out.println("2. Pesquisar passageiros por Nome.");
        op = scanner.nextInt();
        if(op == 1){
            int[] criterios = new int[quant];

            for(int i=0; i<quant; i++){
                System.out.println("Digite o " + (i+1) + "º ID: ");
                criterios[i] = scanner.nextInt();
            }
            CRUD.lerConjuntoDeRegistros(criterios);
        } else if (op == 2){
            String[] nomes = new String[quant];
            scanner.nextLine();
            for (int i = 0; i<quant; i++){
                System.out.println("Digite o " + (i+1) + "º nome: ");
                nomes[i] = scanner.nextLine();
            }
            int[] idsNomes = IndiceNome.lerConjuntoDeRegNomes(nomes);
            CRUD.lerConjuntoDeRegistros(idsNomes);
        }
        //scanner.close();
    }
}

