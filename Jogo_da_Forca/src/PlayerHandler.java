import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PlayerHandler implements Runnable {
    private Socket clienteSocket;
    private GerenciadorDeSalas gerenciador;
    private PrintWriter paraCliente;
    private BufferedReader doCliente;

    // Estado específico deste jogador
    private String nickname;
    private Sala salaAtual;

    public PlayerHandler(Socket socket, GerenciadorDeSalas gerenciador) {
        this.clienteSocket = socket;
        this.gerenciador = gerenciador;
    }
    // Novo método público que permite que outras classes (como a Sala)
    // enviem uma mensagem para o cliente deste handler.
    public void enviarMensagem(String mensagem) {
        if (paraCliente != null) {
            paraCliente.println(mensagem);
        }
    }

    @Override
    public void run() {
        try {
            this.paraCliente = new PrintWriter(clienteSocket.getOutputStream(), true);
            this.doCliente = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));

            paraCliente.println("Bem-vindo! Por favor, defina seu nickname com: NICK <seu_nome>");

            String linhaRecebida;
            while ((linhaRecebida = doCliente.readLine()) != null) {
                String[] partes = linhaRecebida.split(" ", 3); // Divide o comando e argumentos
                String comando = partes[0].toUpperCase();

                // Lógica de estado: o que fazer com cada comando?
                switch (comando) {
                    case "NICK":
                        if (partes.length > 1) {
                            this.nickname = partes[1];
                            paraCliente.println("OK, nickname definido como '" + this.nickname + "'. Use CRIAR ou ENTRAR.");
                        } else {
                            paraCliente.println("ERRO: Formato incorreto. Use: NICK <seu_nome>");
                        }
                        break;
                    case "CRIAR":
                        if (nickname == null) {
                            paraCliente.println("ERRO: Defina um nickname primeiro.");
                            continue;
                        }
                        if (partes.length > 2) {
                            String nomeSala = partes[1];
                            int capacidade = Integer.parseInt(partes[2]);
                            String resposta = gerenciador.criarSala(nomeSala, capacidade);
                            paraCliente.println(resposta);
                        }
                        break;
                    case "ENTRAR":
                        if (nickname == null) {
                            paraCliente.println("ERRO: Defina um nickname primeiro.");
                            continue;
                        }
                        if (partes.length > 1) {
                            Sala sala = gerenciador.getSala(partes[1]);
                            if (sala == null) {
                                paraCliente.println("ERRO: Sala não existe.");
                            } else if (sala.estaCheia()) {
                                paraCliente.println("ERRO: Sala está cheia.");
                            } else {
                                this.salaAtual = sala;
                                sala.adicionarMembro(this); // Adiciona a si mesmo na lista da sala
                                paraCliente.println("OK, você entrou na sala '" + sala.getNome() + "'.");
                            }
                        }
                        break;
                    case "HORAS":
                        if (salaAtual != null) {
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                            String hora = dtf.format(LocalDateTime.now());

                            // 1. Monta a mensagem que todos irão receber.
                            String mensagemParaTodos = "[SALA " + salaAtual.getNome() + "] " + this.nickname + " pediu as horas. São " + hora;

                            // 2. Usa o novo método de broadcast da sala!
                            salaAtual.broadcast(mensagemParaTodos);

                        } else {
                            paraCliente.println("ERRO: Você precisa estar em uma sala para usar este comando.");
                        }
                        break;
                    case "SAIR":
                        return; // Sai do método run, encerrando a thread
                    default:
                        paraCliente.println("ERRO: Comando não reconhecido.");
                }
            }
        } catch (Exception e) {
            System.out.println("Erro no handler: " + e.getMessage());
        } finally {
            if (salaAtual != null) {
                salaAtual.removerMembro(this); // Garante que o jogador saia da sala ao desconectar
            }
            System.out.println("Cliente " + nickname + " desconectado.");
            try { clienteSocket.close(); } catch (Exception e) {}
        }
    }
}