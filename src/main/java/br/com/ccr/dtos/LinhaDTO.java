package br.com.ccr.dtos;

import java.util.ArrayList;
import java.util.List;

public class LinhaDTO {
    private Integer id;
    private String nome;
    private List<EstacaoDTO> estacoes;

    public LinhaDTO() {
        this.estacoes = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<EstacaoDTO> getEstacoes() {
        return estacoes;
    }

    public void setEstacoes(List<EstacaoDTO> estacoes) {
        this.estacoes = estacoes;
    }
}
