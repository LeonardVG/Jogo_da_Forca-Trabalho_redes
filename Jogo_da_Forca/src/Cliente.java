import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {

    public static void main(String[] args) {
        String ipServidor = "127.0.0.1";
        int porta = 6789;

        try {
            Socket socket = new Socket(ipServidor, porta);
            PrintWriter paraServidor = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader doServidor = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner teclado = new Scanner(System.in);

            // 1. Cria e inicia a thread "receptora" de mensagens.
            // A partir de agora, ela fica ouvindo o servidor em paralelo.
            new Thread(new ReceptorDeMensagens(doServidor)).start();

            System.out.println("Conectado! Digite suas mensagens.");
            System.out.print("> ");

            // 2. A thread principal fica em um loop apenas para ler o teclado e ENVIAR.
            while (teclado.hasNextLine()) {
                String entradaDoUsuario = teclado.nextLine();
                paraServidor.println(entradaDoUsuario);

                if ("SAIR".equalsIgnoreCase(entradaDoUsuario)) {
                    break;
                }
            }

            // Fecha o socket ao sair do loop
            socket.close();

        } catch (Exception e) {
            System.out.println("Erro na comunicação: " + e.getMessage());
        }
        System.out.println("Conexão encerrada.");
    }
}