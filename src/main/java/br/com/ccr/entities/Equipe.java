package br.com.ccr.entities;

import lombok.*;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Equipe extends BaseModel {
    private String nome;
    private ArrayList<Usuario> integrantes;
    private Localizacao localizacao;
    private Estacao base;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public ArrayList<Usuario> getIntegrantes() {
        return integrantes;
    }

    public void setIntegrantes(ArrayList<Usuario> integrantes) {
        this.integrantes = integrantes;
    }

    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }

    public Estacao getBase() {
        return base;
    }

    public void setBase(Estacao base) {
        this.base = base;
    }
}
