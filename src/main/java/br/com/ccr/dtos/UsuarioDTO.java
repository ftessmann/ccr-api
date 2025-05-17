package br.com.ccr.dtos;

import br.com.ccr.entities.Cargo;
import br.com.ccr.entities.Setor;

public class UsuarioDTO {
    private String nome;
    private String cpf;
    private String email;
    private String senha;
    private String telefone;
    private int enderecoId; // ID do endereço como campo separado
    private Cargo cargo;
    private Setor setor;

    public UsuarioDTO() {
    }

    public UsuarioDTO(String nome, String cpf, String email, String senha, String telefone, int enderecoId, Cargo cargo, Setor setor) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.telefone = telefone;
        this.enderecoId = enderecoId;
        this.cargo = cargo;
        this.setor = setor;
    }

    // Getters e Setters
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

    public int getEnderecoId() {
        return enderecoId;
    }

    public void setEnderecoId(int enderecoId) {
        this.enderecoId = enderecoId;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public Setor getSetor() {
        return setor;
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
    }
}
