import java.util.ArrayList;
import java.util.List;

public class Sala {
    private String nome;
    private int capacidadeMaxima;
    private List<PlayerHandler> membros = new ArrayList<>();

    public Sala(String nome, int capacidade) {
        this.nome = nome;
        this.capacidadeMaxima = capacidade;
    }

    public synchronized void adicionarMembro(PlayerHandler membro) {
        this.membros.add(membro);
    }

    public synchronized void removerMembro(PlayerHandler membro) {
        this.membros.remove(membro);
    }

    public boolean estaCheia() {
        return membros.size() >= capacidadeMaxima;
    }


    public synchronized void broadcast(String mensagem) {
        for (PlayerHandler membro : this.membros) {
            membro.enviarMensagem(mensagem);
        }
    }

    public String getNome() {
        return nome;
    }
}