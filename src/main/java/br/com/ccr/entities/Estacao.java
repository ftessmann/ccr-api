package br.com.ccr.entities;

import lombok.*;

import java.util.ArrayList;

@AllArgsConstructor
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Estacao extends BaseModel {
    private String nome;
    private Endereco endereco;
    private ArrayList<Plataforma> plataformas;
    private ArrayList<Linha> linhas;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public ArrayList<Plataforma> getPlataformas() {
        return plataformas;
    }

    public void setPlataformas(ArrayList<Plataforma> plataformas) {
        this.plataformas = plataformas;
    }

    public ArrayList<Linha> getLinhas() {
        return linhas;
    }

    public void setLinhas(ArrayList<Linha> linhas) {
        this.linhas = linhas;
    }
}
