import java.net.ServerSocket;
import java.net.Socket;

public class Servidor_Game {
    public static void main(String[] args) throws Exception {
        int porta = 6789;
        try (ServerSocket servidorSocket = new ServerSocket(porta)) {
            System.out.println("Servidor Principal iniciado na porta " + porta);

            // 1. cria UM UNICO gerenciador de salas para t odo o servidor
            GerenciadorDeSalas gerenciador = new GerenciadorDeSalas();

            while (true) {
                Socket clienteSocket = servidorSocket.accept();

                // 2. Passa a referência do mesmo gerenciador para cada novo handler
                new Thread(new PlayerHandler(clienteSocket, gerenciador)).start();
            }
        }
    }
}