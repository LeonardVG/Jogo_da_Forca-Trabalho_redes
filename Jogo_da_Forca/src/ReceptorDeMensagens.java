import java.io.BufferedReader;
import java.io.IOException;

public class ReceptorDeMensagens implements Runnable {
    private BufferedReader doServidor;

    public ReceptorDeMensagens(BufferedReader reader) {
        this.doServidor = reader;
    }

    @Override
    public void run() {
        try {
            String mensagemDoServidor;
            while ((mensagemDoServidor = doServidor.readLine()) != null) {
                // Simplesmente imprime qualquer mensagem que chegar do servidor
                System.out.println(mensagemDoServidor);
                System.out.print("> "); // Reimprime o prompt para o usuário
            }
        } catch (IOException e) {
            System.out.println("Você foi desconectado do servidor.");
        }
    }
}