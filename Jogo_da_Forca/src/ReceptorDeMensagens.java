import java.io.BufferedReader;
import java.io.IOException;
import com.google.gson.Gson;

//Classe para é receber dados do servidor
public class ReceptorDeMensagens implements Runnable {
    private BufferedReader doServidor;
    private final Gson gson = new Gson(); // Instância do Gson para esta classe

    public ReceptorDeMensagens(BufferedReader reader) {
        this.doServidor = reader;
    }

    @Override
    public void run() {
        try {
            String linhaJson;
            while ((linhaJson = doServidor.readLine()) != null) {
                // Tenta desserializar a linha recebida como um objeto Mensagem
                try {
                    Mensagem msg = gson.fromJson(linhaJson, Mensagem.class);

                    // Verifica o tipo da mensagem para saber como exibi-la
                    if ("RESPOSTA_SERVIDOR".equals(msg.getType())) {
                        // Se for uma resposta direta, extrai a mensagem do payload
                        String textoMensagem = (String) msg.getPayload().get("mensagem");
                        System.out.println("\nServidor: " + textoMensagem);
                    } else {
                        // Se for outro tipo (como um broadcast), imprime a mensagem bruta por enquanto
                        System.out.println("\n" + linhaJson);
                    }
                } catch (Exception e) {
                    // Se não for um JSON válido ou tiver outro erro, apenas imprime a linha
                    System.out.println("\n" + linhaJson);
                }

                // Reimprime o prompt para o usuário não se perder
                System.out.print("> ");
            }
        } catch (IOException e) {
            System.out.println("\nVocê foi desconectado do servidor.");
        }
    }
}
