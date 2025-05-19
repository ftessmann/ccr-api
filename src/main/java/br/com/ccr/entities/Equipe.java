package br.com.ccr.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Equipe extends BaseModel {
    private String nome;
    private ArrayList<Usuario> integrantes;
    private Estacao base;
    private Setor setor;

    public Equipe() {
    }

    public Equipe(String nome, ArrayList<Usuario> integrantes, Estacao base, Setor setor) {
        this.nome = nome;
        this.integrantes = integrantes;
        this.base = base;
        this.setor = setor;
    }

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

    public Estacao getBase() {
        return base;
    }

    public void setBase(Estacao base) {
        this.base = base;
    }

    public Setor getSetor() {
        return setor;
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
    }
}
