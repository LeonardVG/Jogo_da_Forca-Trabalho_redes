import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

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
                    String tipoMsg = msg.getType();
                    Map<String, Object> payload = msg.getPayload();

                    if ("RESPOSTA_SERVIDOR".equals(tipoMsg)) {
                        System.out.println("Servidor: " + payload.get("mensagem"));
                    } else if ("ATUALIZACAO_JOGO".equals(tipoMsg)) {
                        System.out.println("=== JOGO DA FORCA ===");
                        System.out.println("Palavra: " + payload.get("palavra"));
                        System.out.println("Letras tentadas: " + payload.get("letrasTentadas"));
                        System.out.println("Tentativas restantes: " + payload.get("tentativasRestantes"));

                        System.out.println("--> É a vez de: " + payload.get("turnoDe"));
                        System.out.println("=====================");
                    } else if ("FIM_DE_JOGO".equals(tipoMsg)) {
                        System.out.println("### FIM DE JOGO ###");
                        System.out.println("Resultado: " + payload.get("resultado"));
                        System.out.println(payload.get("mensagem"));
                        System.out.println("###################");
                    } else {
                        // Para broadcasts genéricos (como entrada/saída de jogadores)
                        System.out.println(payload.get("mensagem"));
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
