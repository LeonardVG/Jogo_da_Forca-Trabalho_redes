package Cliente;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClienteSimples {

    public static void main(String[] args) {
        String ipServidor = "127.0.0.1"; // IP do servidor (localhost)
        int porta = 6789;               // Porta do servidor

        // Usamos try para garantir que tudo seja fechado no final
        try (Socket socket = new Socket(ipServidor, porta);
             PrintWriter escrever_paraServidor = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader msg_doServidor = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner teclado = new Scanner(System.in)) {

            System.out.println("Conectado ao servidor em " + ipServidor + ":" + porta);

            // 1. Lê e exibe a mensagem de boas-vindas do servidor
            String saudacao = msg_doServidor.readLine();
            System.out.println("Servidor: " + saudacao);

            String entradaDoUsuario;
            // 2. Loop para ler a entrada do usuário e enviar ao servidor
            while (true) {
                System.out.print("Digite 'HORAS' ou 'SAIR': ");
                entradaDoUsuario = teclado.nextLine();

                // Envia a mensagem para o servidor
                escrever_paraServidor.println(entradaDoUsuario);

                if ("SAIR".equalsIgnoreCase(entradaDoUsuario)) {
                    break; // Sai do loop se o usuário digitou SAIR
                }

                // Lê e exibe a resposta do servidor
                String respostaServidor = msg_doServidor.readLine();
                System.out.println("Servidor: " + respostaServidor);
            }

        } catch (Exception e) {
            System.out.println("Erro na comunicação com o servidor: " + e.getMessage());
        }
        System.out.println("Conexão encerrada.");
    }
}