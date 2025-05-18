package br.com.ccr.repositories;

import br.com.ccr.entities.Endereco;
import br.com.ccr.infrastructure.DatabaseConfig;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class EnderecoRepository {

    public Endereco save(Endereco endereco) throws SQLException {
        String sql = "INSERT INTO tb_mvp_endereco (cep, rua, numero, bairro, cidade, estado, complemento, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, endereco.getCep());
            stmt.setString(2, endereco.getRua());
            stmt.setString(3, endereco.getNumero());
            stmt.setString(4, endereco.getBairro());
            stmt.setString(5, endereco.getCidade());
            stmt.setString(6, endereco.getEstado());
            stmt.setString(7, endereco.getComplemento());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar endereço, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    endereco.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Falha ao criar endereço, nenhum ID obtido.");
                }
            }
        }

        return endereco;
    }

    public Optional<Endereco> findById(Integer id) throws SQLException {
        String sql = "SELECT e.id, e.cep, e.rua, e.numero, e.bairro, e.cidade, e.estado, " +
                "e.complemento " +
                "FROM tb_mvp_endereco e " +
                "WHERE e.id = ? AND e.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Endereco endereco = mapResultSetToEndereco(rs);
                    return Optional.of(endereco);
                }
            }
        }

        return Optional.empty();
    }

    public List<Endereco> findAll() throws SQLException {
        List<Endereco> enderecos = new ArrayList<>();

        String sql = "SELECT e.id, e.cep, e.rua, e.numero, e.bairro, e.cidade, e.estado, " +
                "e.complemento, e.created_at, e.updated_at " +
                "FROM tb_mvp_endereco e " +
                "WHERE e.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Endereco endereco = mapResultSetToEndereco(rs);
                enderecos.add(endereco);
            }
        }

        return enderecos;
    }

    public Endereco update(Endereco endereco) throws SQLException {
        String sql = "UPDATE tb_mvp_endereco SET cep = ?, rua = ?, numero = ?, " +
                "bairro = ?, cidade = ?, estado = ?, complemento = ?, " +
                "updated_at = CURRENT_TIMESTAMP " +
                "WHERE id = ? AND deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, endereco.getCep());
            stmt.setString(2, endereco.getRua());
            stmt.setString(3, endereco.getNumero());
            stmt.setString(4, endereco.getBairro());
            stmt.setString(5, endereco.getCidade());
            stmt.setString(6, endereco.getEstado());
            stmt.setString(7, endereco.getComplemento());
            stmt.setInt(8, endereco.getId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao atualizar endereço, nenhuma linha afetada.");
            }
        }

        return endereco;
    }

    public boolean deleteById(Integer id) throws SQLException {
        String sql = "UPDATE tb_mvp_endereco SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public List<Endereco> findByCidade(String cidade) throws SQLException {
        List<Endereco> enderecos = new ArrayList<>();

        String sql = "SELECT e.id, e.cep, e.rua, e.numero, e.bairro, e.cidade, e.estado, " +
                "e.complemento, e.created_at, e.updated_at " +
                "FROM tb_mvp_endereco e " +
                "WHERE e.cidade LIKE ? AND e.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, "%" + cidade + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Endereco endereco = mapResultSetToEndereco(rs);
                    enderecos.add(endereco);
                }
            }
        }

        return enderecos;
    }

    public Optional<Endereco> findByCep(String cep) throws SQLException {
        String sql = "SELECT e.id, e.cep, e.rua, e.numero, e.bairro, e.cidade, e.estado, " +
                "e.complemento, e.created_at, e.updated_at " +
                "FROM tb_mvp_endereco e " +
                "WHERE e.cep = ? AND e.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, cep);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Endereco endereco = mapResultSetToEndereco(rs);
                    return Optional.of(endereco);
                }
            }
        }

        return Optional.empty();
    }

    private Endereco mapResultSetToEndereco(ResultSet rs) throws SQLException {
        Endereco endereco = new Endereco();
        endereco.setId(rs.getInt("id"));
        endereco.setCep(rs.getString("cep"));
        endereco.setRua(rs.getString("rua"));
        endereco.setNumero(rs.getString("numero"));
        endereco.setBairro(rs.getString("bairro"));
        endereco.setCidade(rs.getString("cidade"));
        endereco.setEstado(rs.getString("estado"));
        endereco.setComplemento(rs.getString("complemento"));

        return endereco;
    }
}
