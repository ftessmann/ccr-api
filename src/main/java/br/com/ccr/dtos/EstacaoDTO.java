package br.com.ccr.dtos;

import java.util.ArrayList;
import java.util.List;

public class EstacaoDTO {
    private Integer id;
    private String nome;
    private EnderecoDTO endereco;
    private List<LinhaDTO> linhas;

    public EstacaoDTO() {
        this.linhas = new ArrayList<>();
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

    public EnderecoDTO getEndereco() {
        return endereco;
    }

    public void setEndereco(EnderecoDTO endereco) {
        this.endereco = endereco;
    }

    public List<LinhaDTO> getLinhas() {
        return linhas;
    }

    public void setLinhas(List<LinhaDTO> linhas) {
        this.linhas = linhas;
    }
}
