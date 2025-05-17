# 📊 TitanicDB – Sistema de Gerenciamento e Análise de Dados Binários do Titanic

Este projeto tem como objetivo o estudo e implementação de estruturas de dados e técnicas de armazenamento, indexação e manipulação de arquivos binários utilizando como base um conjunto de dados dos passageiros do navio Titanic.

Através da leitura de um arquivo CSV original, os dados dos passageiros são convertidos e armazenados em arquivos `.bin`, com suporte a operações de CRUD, criptografia básica e sistemas de indexação sequencial e por nome. O projeto inclui ainda o uso de estruturas como **Multilistas**, **índices primários e secundários**, além da compactação de dados com o algoritmo **LZW**.

---

## ✨ Funcionalidades

- ✅ Conversão de dados CSV para arquivos binários  
- ✅ Operações de CRUD em registros binários  
- ✅ Indexação por nome e por ID  
- ✅ Estrutura de dados com Multilistas  
- ✅ Compactação com o algoritmo LZW  
- ✅ Suporte a criptografia simples dos dados  
- ✅ Leitura eficiente e organizada das informações de passageiros  

---

## 🛠️ Tecnologias e Conceitos

- **Java**
- Estruturas de dados aplicadas (Multilistas, Índices)
- Manipulação de arquivos `.bin`
- Algoritmos de compressão (LZW)
- Conceitos de criptografia e indexação

---

## 📁 Estrutura do Projeto

- `base_titanic.bin`: Base de dados binária dos passageiros  
- `titanic_original1.csv`: Arquivo CSV original com os dados  
- `Passageiro.java`: Classe de representação dos passageiros  
- `CRUD.java`: Lógica para criação, leitura, atualização e exclusão de registros  
- `IndiceNome.java` / `IndiceSeq.java`: Lógicas de indexação por nome e por sequência  
- `Multilista.java`: Implementação da estrutura de multilista  
- `LZW.java`: Implementação do algoritmo de compressão LZW  
- `Criptografia.java`: Rotinas de criptografia simples  
- `Data.java`: Classe auxiliar para manipulação de datas  

---

## 📌 Objetivo Acadêmico

Este projeto foi desenvolvido com fins didáticos, com o intuito de aprofundar conhecimentos em estruturas de dados, persistência de informações em arquivos binários e estratégias de otimização de acesso a dados.

---

## 📄 Licença

Este projeto é de uso acadêmico e está sob uma licença livre. Sinta-se à vontade para estudar, modificar e contribuir.

