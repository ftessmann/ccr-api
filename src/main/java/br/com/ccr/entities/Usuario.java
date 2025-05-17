package br.com.ccr.entities;

import java.util.Objects;

public class Usuario extends BaseModel {
    private String nome;
    private String cpf;
    private String email;
    private String senha;
    private String telefone;
    private Endereco endereco;
    private Cargo cargo;
    private Setor setor;

    public Usuario() {}

    public Usuario(String nome, String cpf, String email, String senha, String telefone,
                   Endereco endereco, Cargo cargo, Setor setor
    ) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.endereco = endereco;
        this.cargo = cargo;
        this.setor = setor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public Setor getSetor() { return setor; }

    public void setSetor(Setor setor) { this.setor = setor; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(nome, usuario.nome) && Objects.equals(cpf, usuario.cpf) && Objects.equals(email, usuario.email) && Objects.equals(senha, usuario.senha) && Objects.equals(telefone, usuario.telefone) && Objects.equals(endereco, usuario.endereco) && cargo == usuario.cargo && setor == usuario.setor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), nome, cpf, email, senha, telefone, endereco, cargo, setor);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                ", email='" + email + '\'' +
                ", senha='" + senha + '\'' +
                ", telefone='" + telefone + '\'' +
                ", endereco=" + endereco +
                ", cargo=" + cargo +
                ", setor=" + setor +
                '}';
    }

}
