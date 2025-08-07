package Servidor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// PlayerHandler implementa a interface Runnable
// para ser executado por uma thread
public class PlayerHandler implements Runnable {

    // cada PlayerHandler tem seu próprio socket para conversar com um cliente
    private Socket clienteSocket;

    // o construtor recebe o socket do cliente que essa classe vai atender
    public PlayerHandler(Socket socket) {
        this.clienteSocket = socket;
    }

    // O método run() contém toda a lógica que será executada na thread
    @Override
    public void run() {
        // O try garante que o socket deste cliente seja fechado
        // quando o loop de comunicação terminar
        try (
                PrintWriter paraCliente = new PrintWriter(clienteSocket.getOutputStream(), true);
                BufferedReader doCliente = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()))
        ) {
            // envia a saudação inicial para o cliente
            paraCliente.println("Bem-vindo! Conexão estabelecida com o servidor multithread");

            String linhaRecebida;
            // ele lê as mensagens de um cliente especkfico
            while ((linhaRecebida = doCliente.readLine()) != null) {
                System.out.println("Mensagem de " + clienteSocket.getInetAddress().getHostAddress() + ": " + linhaRecebida);

                if ("HORAS".equalsIgnoreCase(linhaRecebida)) {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    String dataHoraAtual = dtf.format(LocalDateTime.now());
                    paraCliente.println("Data e hora do servidor: " + dataHoraAtual);
                } else if ("SAIR".equalsIgnoreCase(linhaRecebida)) {
                    break;
                } else {
                    paraCliente.println("Comando não reconhecido. Use 'HORAS' ou 'SAIR'.");
                }
            }
        } catch (Exception e) {
            System.out.println("Erro no handler do cliente " + clienteSocket.getInetAddress().getHostAddress() + ": " + e.getMessage());
        } finally {
            System.out.println("Cliente " + clienteSocket.getInetAddress().getHostAddress() + " desconectado.");
        }
    }
}