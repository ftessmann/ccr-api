package br.com.ccr.entities;

import java.util.ArrayList;
import java.util.Objects;

public class Equipe extends BaseModel {
    private String nome;
    private ArrayList<Usuario> integrantes;
    private Estacao base;

    public Equipe() {}

    public Equipe(String nome, ArrayList<Usuario> integrantes, Estacao base) {
        this.nome = nome;
        this.integrantes = integrantes;
        this.base = base;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Equipe equipe = (Equipe) o;
        return Objects.equals(nome, equipe.nome) && Objects.equals(integrantes, equipe.integrantes) && Objects.equals(base, equipe.base);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), nome, integrantes, base);
    }

    @Override
    public String toString() {
        return "Equipe{" +
                "nome='" + nome + '\'' +
                ", integrantes=" + integrantes +
                ", base=" + base +
                '}';
    }
}
