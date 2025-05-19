package br.com.ccr.dtos;

import java.util.ArrayList;
import java.util.List;

public class TremDTO {
    private Integer id;
    private String modelo;
    private EstacaoDTO estacaoInicial;
    private EstacaoDTO estacaoFinal;
    private LinhaDTO linha;
    private List<UsuarioDTO> condutores;
    private List<VagaoDTO> vagoes;

    public TremDTO() {
        this.condutores = new ArrayList<>();
        this.vagoes = new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public EstacaoDTO getEstacaoInicial() {
        return estacaoInicial;
    }

    public void setEstacaoInicial(EstacaoDTO estacaoInicial) {
        this.estacaoInicial = estacaoInicial;
    }

    public EstacaoDTO getEstacaoFinal() {
        return estacaoFinal;
    }

    public void setEstacaoFinal(EstacaoDTO estacaoFinal) {
        this.estacaoFinal = estacaoFinal;
    }

    public LinhaDTO getLinha() {
        return linha;
    }

    public void setLinha(LinhaDTO linha) {
        this.linha = linha;
    }

    public List<UsuarioDTO> getCondutores() {
        return condutores;
    }

    public void setCondutores(List<UsuarioDTO> condutores) {
        this.condutores = condutores;
    }

    public List<VagaoDTO> getVagoes() {
        return vagoes;
    }

    public void setVagoes(List<VagaoDTO> vagoes) {
        this.vagoes = vagoes;
    }
}
