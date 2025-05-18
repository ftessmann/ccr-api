package br.com.ccr.repositories;

import br.com.ccr.entities.Usuario;
import br.com.ccr.entities.Endereco;
import br.com.ccr.entities.Cargo;
import br.com.ccr.entities.Setor;
import br.com.ccr.infrastructure.DatabaseConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UsuarioRepository {

    @Inject
    private EnderecoRepository enderecoRepository;

    public UsuarioRepository() {
    }

    public Usuario save(Usuario usuario) throws SQLException {
        if (usuario.getEndereco() != null) {
            if (usuario.getEndereco().getId() == null) {
                Endereco savedEndereco = enderecoRepository.save(usuario.getEndereco());
                usuario.setEndereco(savedEndereco);
            }
        }

        String sql = "INSERT INTO tb_mvp_usuario (nome, cpf, email, senha, telefone, endereco_id, cargo, setor, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getSenha());
            stmt.setString(5, usuario.getTelefone());

            if (usuario.getEndereco() != null) {
                stmt.setInt(6, usuario.getEndereco().getId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            stmt.setString(7, usuario.getCargo().name());
            stmt.setString(8, usuario.getSetor().name());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar usuário, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    usuario.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Falha ao criar usuário, nenhum ID obtido.");
                }
            }
        }

        return usuario;
    }

    public Optional<Usuario> findById(Integer id) throws SQLException {
        String sql = "SELECT u.id, u.nome, u.cpf, u.email, u.senha, u.telefone, " +
                "u.endereco_id, u.cargo, u.setor, u.created_at, u.updated_at " +
                "FROM tb_mvp_usuario u " +
                "WHERE u.id = ? AND u.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = mapResultSetToUsuario(rs);
                    return Optional.of(usuario);
                }
            }
        }

        return Optional.empty();
    }

    public List<Usuario> findAll() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();

        String sql = "SELECT u.id, u.nome, u.cpf, u.email, u.senha, u.telefone, " +
                "u.endereco_id, u.cargo, u.setor, u.created_at, u.updated_at " +
                "FROM tb_mvp_usuario u " +
                "WHERE u.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Usuario usuario = mapResultSetToUsuario(rs);
                usuarios.add(usuario);
            }
        }

        return usuarios;
    }

    public Usuario update(Usuario usuario) throws SQLException {
        if (usuario.getEndereco() != null) {
            enderecoRepository.update(usuario.getEndereco());
        }

        String sql = "UPDATE tb_mvp_usuario SET nome = ?, cpf = ?, email = ?, " +
                "senha = ?, telefone = ?, endereco_id = ?, cargo = ?, setor = ?, " +
                "updated_at = CURRENT_TIMESTAMP " +
                "WHERE id = ? AND deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setString(3, usuario.getEmail());
            stmt.setString(4, usuario.getSenha());
            stmt.setString(5, usuario.getTelefone());

            if (usuario.getEndereco() != null) {
                stmt.setInt(6, usuario.getEndereco().getId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            stmt.setString(7, usuario.getCargo().name());
            stmt.setString(8, usuario.getSetor().name());
            stmt.setInt(9, usuario.getId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao atualizar usuário, nenhuma linha afetada.");
            }
        }

        return usuario;
    }

    public boolean deleteById(Integer id) throws SQLException {
        String sql = "UPDATE tb_mvp_usuario SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public Optional<Usuario> findByEmail(String email) throws SQLException {
        String sql = "SELECT u.id, u.nome, u.cpf, u.email, u.senha, u.telefone, " +
                "u.endereco_id, u.cargo, u.setor, u.created_at, u.updated_at " +
                "FROM tb_mvp_usuario u " +
                "WHERE u.email = ? AND u.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = mapResultSetToUsuario(rs);
                    return Optional.of(usuario);
                }
            }
        }

        return Optional.empty();
    }

    public Optional<Usuario> findByCpf(String cpf) throws SQLException {
        String sql = "SELECT u.id, u.nome, u.cpf, u.email, u.senha, u.telefone, " +
                "u.endereco_id, u.cargo, u.setor, u.created_at, u.updated_at " +
                "FROM tb_mvp_usuario u " +
                "WHERE u.cpf = ? AND u.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, cpf);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = mapResultSetToUsuario(rs);
                    return Optional.of(usuario);
                }
            }
        }

        return Optional.empty();
    }

    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setNome(rs.getString("nome"));
        usuario.setCpf(rs.getString("cpf"));
        usuario.setEmail(rs.getString("email"));
        usuario.setSenha(rs.getString("senha"));
        usuario.setTelefone(rs.getString("telefone"));

        Integer enderecoId = rs.getInt("endereco_id");
        if (!rs.wasNull()) {
            Optional<Endereco> endereco = enderecoRepository.findById(enderecoId);
            endereco.ifPresent(usuario::setEndereco);
        }

        String cargoStr = rs.getString("cargo");
        if (cargoStr != null && !cargoStr.isEmpty()) {
            usuario.setCargo(Cargo.valueOf(cargoStr));
        }

        String setorStr = rs.getString("setor");
        if (setorStr != null && !setorStr.isEmpty()) {
            usuario.setSetor(Setor.valueOf(setorStr));
        }

        return usuario;
    }


    public Optional<Usuario> autenticar(String email, String senha) throws SQLException {
        String sql = "SELECT u.id, u.nome, u.cpf, u.email, u.senha, u.telefone, " +
                "u.endereco_id, u.cargo, u.setor " +
                "FROM tb_mvp_usuario u " +
                "WHERE u.email = ? AND u.senha = ? AND u.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, senha);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = mapResultSetToUsuario(rs);
                    return Optional.of(usuario);
                }
            }
        }

        return Optional.empty();
    }

}
