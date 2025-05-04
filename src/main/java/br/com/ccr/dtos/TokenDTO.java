package br.com.ccr.dtos;

public class TokenDTO {
    private String token;
    private String tipo;

    public TokenDTO(String token) {
        this.token = token;
        this.tipo = "Bearer";
    }

    public String getToken() {
        return token;
    }

    public String getTipo() {
        return tipo;
    }
}