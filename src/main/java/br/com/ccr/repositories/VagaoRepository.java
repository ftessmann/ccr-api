package br.com.ccr.repositories;

import br.com.ccr.entities.Vagao;
import br.com.ccr.infrastructure.DatabaseConfig;
import jakarta.enterprise.context.ApplicationScoped;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class VagaoRepository {

    public Vagao save(Vagao vagao) throws SQLException {
        String sql = "INSERT INTO tb_mvp_vagao (numeracao, created_at) VALUES (?, CURRENT_TIMESTAMP)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, vagao.getNumeracao());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar vagão, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    vagao.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Falha ao criar vagão, nenhum ID obtido.");
                }
            }
        }

        return vagao;
    }

    public Optional<Vagao> findById(Integer id) throws SQLException {
        String sql = "SELECT v.id, v.numeracao " +
                "FROM tb_mvp_vagao v " +
                "WHERE v.id = ? AND v.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Vagao vagao = mapResultSetToVagao(rs);
                    return Optional.of(vagao);
                }
            }
        }

        return Optional.empty();
    }

    public List<Vagao> findAll() throws SQLException {
        List<Vagao> vagoes = new ArrayList<>();

        String sql = "SELECT v.id, v.numeracao " +
                "FROM tb_mvp_vagao v " +
                "WHERE v.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Vagao vagao = mapResultSetToVagao(rs);
                vagoes.add(vagao);
            }
        }

        return vagoes;
    }

    public Vagao update(Vagao vagao) throws SQLException {
        String sql = "UPDATE tb_mvp_vagao SET numeracao = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE id = ? AND deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, vagao.getNumeracao());
            stmt.setInt(2, vagao.getId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao atualizar vagão, nenhuma linha afetada.");
            }
        }

        return vagao;
    }

    public boolean deleteById(Integer id) throws SQLException {
        String sql = "UPDATE tb_mvp_vagao SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public List<Vagao> findByNumeracao(String numeracao) throws SQLException {
        List<Vagao> vagoes = new ArrayList<>();

        String sql = "SELECT v.id, v.numeracao " +
                "FROM tb_mvp_vagao v " +
                "WHERE v.numeracao LIKE ? AND v.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, "%" + numeracao + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Vagao vagao = mapResultSetToVagao(rs);
                    vagoes.add(vagao);
                }
            }
        }

        return vagoes;
    }

    public List<Vagao> findByTremId(Integer tremId) throws SQLException {
        List<Vagao> vagoes = new ArrayList<>();

        String sql = "SELECT v.id, v.numeracao " +
                "FROM tb_mvp_vagao v " +
                "JOIN tb_mvp_trem_vagao tv ON v.id = tv.vagao_id " +
                "WHERE tv.trem_id = ? AND v.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, tremId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Vagao vagao = mapResultSetToVagao(rs);
                    vagoes.add(vagao);
                }
            }
        }

        return vagoes;
    }

    private Vagao mapResultSetToVagao(ResultSet rs) throws SQLException {
        Vagao vagao = new Vagao();
        vagao.setId(rs.getInt("id"));
        vagao.setNumeracao(rs.getString("numeracao"));

        return vagao;
    }
}
