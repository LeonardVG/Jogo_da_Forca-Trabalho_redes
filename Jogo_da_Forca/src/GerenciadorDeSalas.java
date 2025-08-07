import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/*
* Existe apenas um dele para t odo o servidor
*  Ele não fala diretamente com os clientes, mas sabe de tudo o que acontece
*  Sua principal responsabilidade é manter um mapa de todas as salas existentes para que ninguém se perca
*
* */
public class GerenciadorDeSalas {
    // Usamos ConcurrentHashMap por ser seguro para threads.
    private Map<String, Sala> salas = new ConcurrentHashMap<>();

    public String criarSala(String nomeSala, int capacidade) {
        // O método computeIfAbsent garante que a operação seja thread-safe.
        // k fica o msm vlor q novaSala e será utilizado para criar uma sala nova com esse nome
        Sala novaSala = salas.computeIfAbsent(nomeSala, k -> new Sala(k, capacidade));

        if (novaSala.getNome().equals(nomeSala)) {
            return "OK, sala '" + nomeSala + "' criada.";
        } else {
            return "ERRO: Não foi possível criar a sala (conflito de nome?).";
        }
    }

    public Sala getSala(String nomeSala) {
        return salas.get(nomeSala);
    }
}