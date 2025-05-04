package br.com.ccr.entities;

import lombok.*;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Plataforma extends BaseModel {
    private String numero;
    private Estacao estacao;

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Estacao getEstacao() {
        return estacao;
    }

    public void setEstacao(Estacao estacao) {
        this.estacao = estacao;
    }
}
