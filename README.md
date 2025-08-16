# Jogo da Forca Multiplayer em Java

[cite_start]Este projeto é a implementação de um Jogo da Forca multiplayer cooperativo, desenvolvido como trabalho prático final para a disciplina de Redes de Computadores. [cite: 2]

A aplicação utiliza uma arquitetura Cliente-Servidor sobre sockets TCP, permitindo que múltiplos jogadores se conectem em salas, interajam e colaborem em tempo real para adivinhar uma palavra secreta gerenciada pelo servidor. A comunicação é realizada através de um protocolo de aplicação customizado com mensagens no formato JSON.

## Autores

* [cite_start]Luciani Queiroz [cite: 41]
* [cite_start]Leonardo Vieira [cite: 41]

*(Grupo 13)*

## Disciplina

* [cite_start]**Curso:** Ciência da Computação / Engenharia de Computação [cite: 2]
* [cite_start]**Disciplina:** Redes de Computadores [cite: 2]
* [cite_start]**Professor:** Prof. Guilherme Corrêa [cite: 2]
* [cite_start]**Instituição:** Universidade Federal de Pelotas - Centro de Desenvolvimento Tecnológico [cite: 1]

## Pré-requisitos

Para compilar e executar o projeto, você precisará de:

1.  **Java Development Kit (JDK)** - Versão 8 ou superior.
2.  **Biblioteca Gson** - O arquivo `.jar` da biblioteca Gson (ex: `gson-2.10.1.jar`) deve estar presente na mesma pasta dos arquivos fonte (`.java`).

## Estrutura dos Arquivos Fonte

[cite_start]O projeto está dividido nas seguintes classes principais, conforme solicitado na entrega do trabalho. [cite: 25]

* **Lado Servidor:**
    * `ServidorPrincipal.java`: Classe principal que inicia o servidor, aceita conexões e delega para os handlers.
    * `PlayerHandler.java`: Thread que gerencia a comunicação com um único cliente.
    * `GerenciadorDeSalas.java`: Classe central que gerencia todas as salas de jogo.
    * `Sala.java`: Classe que representa uma sala e contém a lógica do jogo da forca.
* **Lado Cliente (Terminal):**
    * `Cliente.java`: Classe principal que inicia a conexão e a thread de envio de mensagens.
    * `ReceptorDeMensagens.java`: Thread que gerencia o recebimento de mensagens do servidor.
* **Comum:**
    * `Mensagem.java`: DTO (Data Transfer Object) que representa a estrutura do nosso protocolo JSON.

## Como Compilar e Executar

[cite_start]Este projeto inclui um arquivo README com o manual de uso da aplicação[cite: 27]. Siga os passos abaixo para rodar a aplicação. Todos os comandos devem ser executados a partir de um terminal (CMD, PowerShell, Terminal, etc.) na pasta que contém os arquivos `.java` e o `.jar` do Gson.

**1. Navegue até o Diretório do Projeto**

Use o comando `cd` para entrar na pasta com os arquivos fonte.
```bash
cd caminho/para/o/seu/projeto
```

**2. Compile o Código-Fonte**

Execute o comando `javac` para compilar todos os arquivos `.java`. O argumento `-cp` é essencial para incluir a biblioteca Gson durante a compilação.

* **No Windows:**
    ```bash
    javac -cp .;gson-2.10.1.jar *.java
    ```
* **No Linux ou macOS:**
    ```bash
    javac -cp .:gson-2.10.1.jar *.java
    ```

**3. Inicie o Servidor**

O servidor deve ser iniciado primeiro, pois ele precisa estar "ouvindo" para que os clientes possam se conectar.

* **No Windows:**
    ```bash
    java -cp .;gson-2.10.1.jar ServidorPrincipal
    ```
* **No Linux ou macOS:**
    ```bash
    java -cp .:gson-2.10.1.jar ServidorPrincipal
    ```
    Você verá a mensagem `Servidor Principal iniciado na porta 6789`.

**4. Inicie um ou Mais Clientes**

Abra **um novo terminal para cada cliente** que você deseja conectar. Navegue até a mesma pasta do projeto e execute o comando abaixo.

* **No Windows:**
    ```bash
    java -cp .;gson-2.10.1.jar Cliente
    ```
* **No Linux ou macOS:**
    ```bash
    java -cp .:gson-2.10.1.jar Cliente
    ```
    Você verá a mensagem de boas-vindas do servidor e poderá começar a interagir.

## Comandos do Cliente

A interação com o servidor é feita através dos seguintes comandos no terminal do cliente:

| Comando         | Formato                          | Descrição                                                  |
| :-------------- | :------------------------------- | :--------------------------------------------------------- |
| **Nickname** | `NICK <seu_nick>`                | Define seu apelido no servidor. Deve ser o primeiro comando. |
| **Criar Sala** | `CRIAR <nome_sala> <capacidade>` | Cria uma nova sala de jogo com um nome e capacidade máxima.  |
| **Entrar em Sala**| `ENTRAR <nome_sala>`             | Entra em uma sala já existente.                            |
| **Iniciar Jogo**| `INICIAR_JOGO`                   | Inicia uma partida de forca na sala em que você está.      |
| **Jogar** | `JOGAR <letra>`                  | Envia uma letra como palpite durante seu turno.            |
| **Sair** | `SAIR`                           | Desconecta você do servidor.                               |

## Observações para Execução em Rede

[cite_start]Para executar o servidor e os clientes em computadores diferentes na mesma rede local, conforme os requisitos de teste[cite: 39]:

1.  **Descubra o IP do Servidor:** No computador que rodará o `ServidorPrincipal`, use o comando `ipconfig` (Windows) ou `ifconfig` (Linux/macOS) para encontrar o "Endereço IPv4" da rede local (ex: `192.168.1.10`).
2.  **Altere o IP no Cliente:** No código do `Cliente.java`, altere o IP "127.0.0.1" para o IP do servidor que você descobriu.
3.  **Firewall:** Lembre-se de configurar o **firewall do computador servidor** para permitir conexões de entrada na porta TCP `6789`.
