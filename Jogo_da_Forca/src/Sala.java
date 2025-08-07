import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Sala {
    // --- Campos existentes ---
    private String nome;
    private int capacidadeMaxima;
    private List<PlayerHandler> membros = new ArrayList<>();
    private final Gson gson = new Gson(); // Instância do Gson para criar JSON

    // --- Novos campos para o estado do Jogo da Forca ---
    private boolean jogoEmAndamento = false;
    private String palavraSecreta;
    private StringBuilder palavraAtual;
    private Set<Character> letrasTentadas;
    private int tentativasRestantes;
    private int indiceJogadorDaVez;

    public Sala(String nome, int capacidade) {
        this.nome = nome;
        this.capacidadeMaxima = capacidade;
    }

    // --- Métodos de Jogo ---

    public synchronized void iniciarJogo() {
        if (jogoEmAndamento) return;

        String[] palavras = {"PROTOCOLO", "SOCKET", "SERVIDOR", "CLIENTE", "REDE"};
        this.palavraSecreta = palavras[(int) (Math.random() * palavras.length)];
        this.palavraAtual = new StringBuilder("_".repeat(palavraSecreta.length()));
        this.letrasTentadas = new LinkedHashSet<>(); // Mantém a ordem de inserção
        this.tentativasRestantes = 6;
        this.jogoEmAndamento = true;
        this.indiceJogadorDaVez = 0;

        broadcast(montarMensagemDeAtualizacao("O jogo da forca começou!"));
    }

    public synchronized void processarJogada(PlayerHandler jogador, char letra) {
        if (!jogoEmAndamento) return; // O jogo não começou

        // Validação do turno
        if (!membros.get(indiceJogadorDaVez).equals(jogador)) {
            jogador.enviarMensagem(montarMensagemDeErro("Não é a sua vez de jogar."));
            return;
        }

        letra = Character.toUpperCase(letra);
        if (letrasTentadas.contains(letra)) {
            jogador.enviarMensagem(montarMensagemDeErro("A letra '" + letra + "' já foi tentada."));
            return;
        }

        letrasTentadas.add(letra);

        // Lógica da jogada
        if (palavraSecreta.indexOf(letra) >= 0) {
            // Acertou a letra
            for (int i = 0; i < palavraSecreta.length(); i++) {
                if (palavraSecreta.charAt(i) == letra) {
                    palavraAtual.setCharAt(i, letra);
                }
            }
        } else {
            // Errou a letra
            tentativasRestantes--;
        }

        // Verifica condição de vitória ou derrota
        if (palavraAtual.toString().equals(palavraSecreta)) {
            broadcast(montarMensagemFimDeJogo("VITORIA", "Parabéns, vocês adivinharam a palavra!"));
            resetarJogo();
        } else if (tentativasRestantes <= 0) {
            broadcast(montarMensagemFimDeJogo("DERROTA", "Fim de jogo! A palavra era " + palavraSecreta));
            resetarJogo();
        } else {
            // O jogo continua, passa o turno e atualiza a todos
            indiceJogadorDaVez = (indiceJogadorDaVez + 1) % membros.size();
            broadcast(montarMensagemDeAtualizacao("'" + jogador.getNickname() + "' jogou a letra '" + letra + "'."));
        }
    }

    private void resetarJogo() {
        this.jogoEmAndamento = false;
    }

    // --- Métodos de Membros e Broadcast ---

    public synchronized void adicionarMembro(PlayerHandler membro) {
        if (!estaCheia()) {
            this.membros.add(membro);
            broadcast(montarMensagemDeServidor("[SALA " + nome + "] O jogador '" + membro.getNickname() + "' entrou."));
        }
    }

    public synchronized void removerMembro(PlayerHandler membro) {
        this.membros.remove(membro);
        broadcast(montarMensagemDeServidor("[SALA " + nome + "] O jogador '" + membro.getNickname() + "' saiu."));
    }

    public void broadcast(String jsonMensagem) {
        for (PlayerHandler membro : this.membros) {
            membro.enviarMensagem(jsonMensagem);
        }
    }

    // --- Métodos Auxiliares para criar JSON ---

    private String montarMensagemDeAtualizacao(String texto) {
        Mensagem msg = new Mensagem();
        msg.type = "ATUALIZACAO_JOGO";
        Map<String, Object> payload = new HashMap<>();
        payload.put("mensagem", texto);
        payload.put("palavra", palavraAtual.toString().replace("", " ").trim());
        payload.put("letrasTentadas", letrasTentadas);
        payload.put("tentativasRestantes", tentativasRestantes);
        payload.put("turnoDe", membros.get(indiceJogadorDaVez).getNickname());
        msg.payload = payload;
        return gson.toJson(msg);
    }

    private String montarMensagemFimDeJogo(String resultado, String texto) {
        Mensagem msg = new Mensagem();
        msg.type = "FIM_DE_JOGO";
        Map<String, Object> payload = new HashMap<>();
        payload.put("resultado", resultado);
        payload.put("mensagem", texto);
        payload.put("palavraCorreta", this.palavraSecreta);
        msg.payload = payload;
        return gson.toJson(msg);
    }

    private String montarMensagemDeErro(String texto) {
        Mensagem msg = new Mensagem();
        msg.type = "RESPOSTA_SERVIDOR";
        Map<String, Object> payload = new HashMap<>();
        payload.put("sucesso", false);
        payload.put("mensagem", texto);
        msg.payload = payload;
        return gson.toJson(msg);
    }

    private String montarMensagemDeServidor(String texto) {
        // ... similar ao de erro, mas pode ter um tipo diferente se quiser
        return montarMensagemDeErro(texto); // Reutilizando para simplicidade
    }

    // --- Getters ---
    public String getNome() { return nome; }
    public boolean estaCheia() { return membros.size() >= capacidadeMaxima; }
}