package Servidor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/* =======VERSÃO COMENTADA É A ANTIGA SEM MUTITHREADS===========
public class ServidorSimples {

    public static void main(String[] args) {
        int porta = 6789; // Porta que o servidor vai usar

        // Usamos try para garantir que o ServerSocket seja fechado
        try (ServerSocket servidorSocket = new ServerSocket(porta)) {
            System.out.println("Servidor Multithread iniciado na porta " + porta + ". Aguardando conexão...");

            // O servidor fica em um loop infinito para aceitar conexões continuamente
            //ele atende um cliente de cada vez, n ta usando thread.
            while (true) {
               // O método accept() bloqueia a execução até que um cliente se conecte
                try (Socket clienteSocket = servidorSocket.accept()) {
                    System.out.println("Novo Cliente conectado: " + clienteSocket.getInetAddress().getHostAddress()); //getInetAddress():Retorna um objeto InetAddress com informações do endereço do cliente; getHostAddress():Retorna o endereço IP do host como uma String.

              //=========Preparando os canais de comunicação (leitura e escrita)==============
                    //clienteSocket.getOutputStream():os Sockets tem dois canais um para entrada e um para saída
                    //o canal de saída (OutputStream) envia dados na sua forma de sequência de bytes. Não é funcional, então
                    //Pra resolver isso usa PrintWriter
                    // O printWriter envolve o OutputStream e o transforma em uma ferramenta  para escrever texto.
                    // Ela adiciona funcionalidades úteis, como o método println()
                    //autoFlush para a msg ser enviada imediatamente e não entrar em nenhuma lista de espera

                    //depois constroi uma antena  que o servidor usará para receber mensagens de texto do cliente (InputStreamReader)
                    // porém ela lê em bytes e fica decodifiacando o que é ruim, então usamos BufferedReader
                    //que é mais eficiente e tem o metodo de ficar lendo "readLine()"

                    PrintWriter escreve_paraCliente  = new PrintWriter(clienteSocket.getOutputStream(), true);
                    BufferedReader msg_doCliente = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));

                    // 1. Envia a saudação inicial para o cliente
                    escreve_paraCliente.println("Bem-vindo! Conexão estabelecida.");

                    String linhaRecebida;
                    // 2. Fica em um loop lendo as mensagens do cliente
                    while ((linhaRecebida = msg_doCliente.readLine()) != null) {
                        System.out.println("Cliente enviou: " + linhaRecebida);

                        // 3. Processa a mensagem do cliente
                        if ("HORAS".equalsIgnoreCase(linhaRecebida)) {
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                            String dataHoraAtual = dtf.format(LocalDateTime.now());
                            escreve_paraCliente.println("Data e hora do servidor: " + dataHoraAtual);
                        } else if ("SAIR".equalsIgnoreCase(linhaRecebida)) {
                            break; // Encerra o loop se o cliente pedir para sair
                        } else {
                            escreve_paraCliente.println("Comando não reconhecido. Use 'HORAS' ou 'SAIR'.");
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Erro ao comunicar com o cliente: " + e.getMessage());
                }
                System.out.println("Cliente desconectado. Aguardando nova conexão...");
            }

        } catch (Exception e) {
            System.out.println("Erro ao iniciar o servidor: " + e.getMessage());
        }





    }
}

 //==============FIM VERSÃO ANTIGA ==========================
 */

public class ServidorSimples {

    public static void main(String[] args) {
        int porta = 6789;

        // O try garante que o ServerSocket seja fechado no final.
        try (ServerSocket servidorSocket = new ServerSocket(porta)) {
            System.out.println("Servidor Multithread iniciado na porta " + porta + ". Aguardando clientes...");

            // O loop infinito para o servidor ficar ouvindo .
            while (true) {
                // 1 - accept() bloqueia e espera por um novo cliente.
                Socket clienteSocket = servidorSocket.accept();
                System.out.println("Novo cliente conectado: " + clienteSocket.getInetAddress().getHostAddress());

                // 2 - Cria um "atendente" (PlayerHandler) para o novo cliente.
                PlayerHandler player = new PlayerHandler(clienteSocket);

                // 3 - Cria uma nova Thread para o atendente e a inicia.
                // O servidor não espera o atendente terminar e volta imediatamente
                // para o passo 1, pronto para aceitar o próximo cliente.
                new Thread(player).start();
            }
        } catch (Exception e) {
            System.out.println("Erro no servidor: " + e.getMessage());

        }
    }
}
