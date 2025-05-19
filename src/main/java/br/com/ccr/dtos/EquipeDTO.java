package br.com.ccr.dtos;

import br.com.ccr.entities.Setor;
import java.util.ArrayList;
import java.util.List;

public class EquipeDTO {
    private Integer id;
    private String nome;
    private List<UsuarioDTO> integrantes;
    private EstacaoDTO base;
    private Setor setor;

    public EquipeDTO() {
        this.integrantes = new ArrayList<>();
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

    public List<UsuarioDTO> getIntegrantes() {
        return integrantes;
    }

    public void setIntegrantes(List<UsuarioDTO> integrantes) {
        this.integrantes = integrantes;
    }

    public EstacaoDTO getBase() {
        return base;
    }

    public void setBase(EstacaoDTO base) {
        this.base = base;
    }

    public Setor getSetor() {
        return setor;
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
    }
}
