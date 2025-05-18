package br.com.ccr.entities;

import lombok.*;

import java.util.ArrayList;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Linha extends BaseModel {
    private String nome;
    private ArrayList<Estacao> estacoes;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ArrayList<Estacao> getEstacoes() {
        return estacoes;
    }

    public void setEstacoes(ArrayList<Estacao> estacoes) {
        this.estacoes = estacoes;
    }

}
