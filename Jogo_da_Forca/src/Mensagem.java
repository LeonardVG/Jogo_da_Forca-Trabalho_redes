import java.util.Map;

// Esta classe é um "Data Transfer Object" (DTO)
// Sua única finalidade é carregar dados de forma estruturada
public class Mensagem {
    String type;
    Map<String, Object> payload;

    // Getters para que o Gson possa acessá-los ao criar o JSON
    public String getType() {
        return type;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }
}
