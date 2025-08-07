import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.Map;
/*
* Função é ser o "atendente" que fala diretamente com um cliente
* Cada cliente tem seu próprio PlayerHandler
*  Ele recebe os pedidos do cliente e, quando precisa de informações sobre outras salas ou para criar uma nova,
*  ele consulta o GerenciadorDeSalas. Quando precisa falar com todos na sua sala atual,
* ele pede para a Sala fazer isso.
* */
public class PlayerHandler implements Runnable {
    private Socket clienteSocket;
    private GerenciadorDeSalas gerenciador;
    private PrintWriter paraCliente;
    private BufferedReader doCliente;

    // Estado específico deste jogador
    private String nickname;
    private Sala salaAtual;

    private final Gson gson = new Gson(); // Crie uma instância do Gson

    public PlayerHandler(Socket socket, GerenciadorDeSalas gerenciador) {
        this.clienteSocket = socket;
        this.gerenciador = gerenciador;
    }

    // método para outras classes (até agra a classe Sala)
    // enviem uma mensagem para o cliente deste handler.
    public void enviarMensagem(String mensagem) {
        if (paraCliente != null) {
            paraCliente.println(mensagem);
        }
    }

    // Método auxiliar para enviar respostas JSON padronizadas
    private void responder(boolean sucesso, String mensagem, Map<String, Object> dadosAdicionais) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("sucesso", sucesso);
        payload.put("mensagem", mensagem);
        if (dadosAdicionais != null) {
            payload.putAll(dadosAdicionais);
        }

        Mensagem resposta = new Mensagem();
        resposta.type = "RESPOSTA_SERVIDOR";
        resposta.payload = payload;

        paraCliente.println(gson.toJson(resposta));
    }

    @Override
    public void run() {
        try {
            // Inicializa os canais de comunicação
            this.paraCliente = new PrintWriter(clienteSocket.getOutputStream(), true);
            this.doCliente = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));

            // Primeira mensagem para o cliente
            responder(true, "Bem-vindo! Por favor, defina seu nickname com o comando: NICK <seu_nome>", null);

            String linhaJsonRecebida;
            // Loop principal que lê as mensagens JSON do cliente
            while ((linhaJsonRecebida = doCliente.readLine()) != null) {
                try {
                    // Converte a string JSON em um objeto Mensagem
                    Mensagem msg = gson.fromJson(linhaJsonRecebida, Mensagem.class);

                    if (msg == null || msg.getType() == null) {
                        responder(false, "Formato da mensagem inválido.", null);
                        continue;
                    }

                    String comando = msg.getType().toUpperCase();
                    Map<String, Object> payload = msg.getPayload();

                    // Processa o comando recebido
                    switch (comando) {
                        case "NICK":
                            this.nickname = (String) payload.get("nickname");
                            System.out.println("Cliente " + clienteSocket.getInetAddress().getHostAddress() + " definiu o nick para: " + this.nickname);
                            responder(true, "Nickname definido como '" + this.nickname + "'. Use CRIAR ou ENTRAR.", null);
                            break;

                        case "CRIAR":
                            if (nickname == null) {
                                responder(false, "ERRO: Defina um nickname primeiro.", null);
                                continue;
                            }
                            String nomeSala = (String) payload.get("nomeSala");
                            int capacidade = ((Double) payload.get("capacidade")).intValue(); // JSON trata números como Double
                            String respostaCriacao = gerenciador.criarSala(nomeSala, capacidade);
                            responder(true, respostaCriacao, null);
                            break;

                        case "ENTRAR":
                            if (nickname == null) {
                                responder(false, "ERRO: Defina um nickname primeiro.", null);
                                continue;
                            }
                            String nomeSalaEntrar = (String) payload.get("nomeSala");
                            Sala sala = gerenciador.getSala(nomeSalaEntrar);
                            if (sala == null) {
                                responder(false, "ERRO: Sala não existe.", null);
                            } else if (sala.estaCheia()) {
                                responder(false, "ERRO: Sala está cheia.", null);
                            } else {
                                this.salaAtual = sala;
                                sala.adicionarMembro(this);
                                responder(true, "OK, você entrou na sala '" + sala.getNome() + "'.", null);
                                sala.broadcast("[SALA " + sala.getNome() + "] O jogador '" + this.nickname + "' entrou.");
                            }
                            break;

                        case "HORAS":
                            if (salaAtual != null) {
                                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
                                String hora = dtf.format(LocalDateTime.now());
                                String msgBroadcast = "[SALA " + salaAtual.getNome() + "] " + this.nickname + " pediu as horas. São " + hora;
                                salaAtual.broadcast(msgBroadcast);
                            } else {
                                responder(false, "ERRO: Você precisa estar em uma sala para usar este comando.", null);
                            }
                            break;

                        case "SAIR":
                            // O loop será encerrado pois o cliente fechará a conexão
                            return;

                        default:
                            responder(false, "ERRO: Comando não reconhecido.", null);
                    }
                } catch (JsonSyntaxException e) {
                    responder(false, "ERRO: A mensagem enviada não é um JSON válido.", null);
                } catch (Exception e) {
                    responder(false, "ERRO: Ocorreu um problema ao processar sua solicitação.", null);
                }
            }
        } catch (Exception e) {
            // Erro geral na thread, como perda de conexão
            System.out.println("Erro na thread do handler para " + nickname + ": " + e.getMessage());
        } finally {
            // Bloco de limpeza: garante que o jogador seja removido da sala ao desconectar
            if (salaAtual != null) {
                salaAtual.removerMembro(this);
                salaAtual.broadcast("[SALA " + salaAtual.getNome() + "] O jogador '" + this.nickname + "' saiu.");
            }
            System.out.println("Cliente " + nickname + " desconectado.");
            try {
                clienteSocket.close();
            } catch (Exception e) {
                // Ignorar erro ao fechar socket que já pode estar fechado
            }
        }
    }
}