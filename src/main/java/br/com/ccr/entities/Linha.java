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
    private ArrayList<Trem> trens;

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

    public ArrayList<Trem> getTrens() {
        return trens;
    }

    public void setTrens(ArrayList<Trem> trens) {
        this.trens = trens;
    }
}
