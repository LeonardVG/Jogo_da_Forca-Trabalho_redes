import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;


//estabelece a conexão e gerencia as duas threads (a principal e a receptora).
public class Cliente {
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        //String ipServidor = "127.0.0.1";
        String ipServidor = "192.168.191.142";
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

            // 2. A thread principal fica em um loop apenas para ler o teclado e ENVIAR
            //hasNextLine(): verifica se existe uma próxima linha de texto para ser lida na entrada
            // retorna true se o usuário digitou algo e pressionou Enter, ou se ainda há dados no buffer de entrada
            while (teclado.hasNextLine()) {
                String entradaDoUsuario = teclado.nextLine();
                Mensagem msgParaEnviar = parseComando(entradaDoUsuario);

                if (msgParaEnviar != null) {
                    // 1. Serializa o objeto Mensagem para uma string JSON
                    String jsonParaEnviar = gson.toJson(msgParaEnviar);
                    paraServidor.println(jsonParaEnviar);
                } else {
                    System.out.println("Comando inválido.");
                    System.out.print("> ");
                }

                if ("SAIR".equalsIgnoreCase(entradaDoUsuario)) break;
            }

            // Fecha o socket ao sair do loop
            socket.close();

        } catch (Exception e) {
            System.out.println("Erro na comunicação: " + e.getMessage());
        }
        System.out.println("Conexão encerrada.");
    }

    // Método auxiliar para converter o comando do usuário em um objeto Mensagem
    private static Mensagem parseComando(String entrada) {
        String[] partes = entrada.split(" ", 3);
        String comando = partes[0].toUpperCase();

        Mensagem msg = new Mensagem();
        msg.type = comando;
        Map<String, Object> payload = new HashMap<>();

        try {
            switch (comando) {
                case "NICK":
                    payload.put("nickname", partes[1]);
                    break;
                case "CRIAR":
                    payload.put("nomeSala", partes[1]);
                    payload.put("capacidade", Integer.parseInt(partes[2]));
                    break;
                case "ENTRAR":
                    payload.put("nomeSala", partes[1]);
                    break;
                case "HORAS":
                case "SAIR":
                    // Sem payload necessário
                    break;
                case "INICIAR_JOGO":
                    // Não precisa de payload
                    break;

                case "JOGAR": // Vamos usar "JOGAR" como o comando do usuário
                    msg.type = "JOGAR_LETRA"; // O tipo do protocolo
                    payload.put("letra", partes[1]);
                    break;
                default:
                    return null; // Comando inválido
            }
        } catch (Exception e) {
            return null; // Erro de formato
        }

        msg.payload = payload;
        return msg;
    }
}