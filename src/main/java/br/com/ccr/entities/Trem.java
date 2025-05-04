package br.com.ccr.entities;

import lombok.*;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Trem extends BaseModel {
    private String modelo;
    private Estacao estacaoInicial;
    private Estacao estacaoFinal;
    private Linha linha;
    private ArrayList<Usuario> condutores;
    private ArrayList<Vagao> vagoes;

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Estacao getEstacaoInicial() {
        return estacaoInicial;
    }

    public void setEstacaoInicial(Estacao estacaoInicial) {
        this.estacaoInicial = estacaoInicial;
    }

    public Estacao getEstacaoFinal() {
        return estacaoFinal;
    }

    public void setEstacaoFinal(Estacao estacaoFinal) {
        this.estacaoFinal = estacaoFinal;
    }

    public Linha getLinha() {
        return linha;
    }

    public void setLinha(Linha linha) {
        this.linha = linha;
    }

    public ArrayList<Usuario> getCondutores() {
        return condutores;
    }

    public void setCondutores(ArrayList<Usuario> condutores) {
        this.condutores = condutores;
    }

    public ArrayList<Vagao> getVagoes() {
        return vagoes;
    }

    public void setVagoes(ArrayList<Vagao> vagoes) {
        this.vagoes = vagoes;
    }
}
