package br.com.ccr.repositories;

import br.com.ccr.entities.Gravidade;
import br.com.ccr.entities.Incidente;
import br.com.ccr.entities.Usuario;
import br.com.ccr.infrastructure.DatabaseConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class IncidenteRepository {

    @Inject
    private UsuarioRepository usuarioRepository;

    public IncidenteRepository() {
    }

    public Incidente save(Incidente incidente) throws SQLException {
        String sql = "INSERT INTO tb_mvp_incidente (latitude, longitude, descricao, gravidade, nome, criador_id, is_resolved, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, incidente.getLatitude());
            stmt.setString(2, incidente.getLongitude());
            stmt.setString(3, incidente.getDescricao());

            if (incidente.getGravidade() != null) {
                stmt.setString(4, incidente.getGravidade().name());
            } else {
                stmt.setNull(4, Types.VARCHAR);
            }

            stmt.setString(5, incidente.getNome());

            if (incidente.getCriador() != null && incidente.getCriador().getId() != null) {
                stmt.setInt(6, incidente.getCriador().getId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            stmt.setString(7, incidente.getIsResolved() ? "S" : "N");

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar incidente, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    incidente.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Falha ao criar incidente, nenhum ID obtido.");
                }
            }
        }

        return incidente;
    }

    public Optional<Incidente> findById(Integer id) throws SQLException {
        String sql = "SELECT i.id, i.latitude, i.longitude, i.descricao, i.gravidade, i.nome, " +
                "i.criador_id, i.is_resolved, i.created_at, i.updated_at " +
                "FROM tb_mvp_incidente i " +
                "WHERE i.id = ? AND i.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Incidente incidente = mapResultSetToIncidente(rs);
                    return Optional.of(incidente);
                }
            }
        }

        return Optional.empty();
    }

    public List<Incidente> findAll() throws SQLException {
        List<Incidente> incidentes = new ArrayList<>();

        String sql = "SELECT i.id, i.latitude, i.longitude, i.descricao, i.gravidade, i.nome, " +
                "i.criador_id, i.is_resolved, i.created_at, i.updated_at " +
                "FROM tb_mvp_incidente i " +
                "WHERE i.deleted_at IS NULL " +
                "ORDER BY i.created_at DESC";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Incidente incidente = mapResultSetToIncidente(rs);
                incidentes.add(incidente);
            }
        }

        return incidentes;
    }

    public Incidente update(Incidente incidente) throws SQLException {
        String sql = "UPDATE tb_mvp_incidente SET latitude = ?, longitude = ?, descricao = ?, " +
                "gravidade = ?, nome = ?, criador_id = ?, is_resolved = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE id = ? AND deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, incidente.getLatitude());
            stmt.setString(2, incidente.getLongitude());
            stmt.setString(3, incidente.getDescricao());

            if (incidente.getGravidade() != null) {
                stmt.setString(4, incidente.getGravidade().name());
            } else {
                stmt.setNull(4, Types.VARCHAR);
            }

            stmt.setString(5, incidente.getNome());

            if (incidente.getCriador() != null && incidente.getCriador().getId() != null) {
                stmt.setInt(6, incidente.getCriador().getId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            stmt.setString(7, incidente.getIsResolved() ? "S" : "N");
            stmt.setInt(8, incidente.getId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao atualizar incidente, nenhuma linha afetada.");
            }
        }

        return incidente;
    }

    public boolean deleteById(Integer id) throws SQLException {
        String sql = "UPDATE tb_mvp_incidente SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public List<Incidente> findByGravidade(Gravidade gravidade) throws SQLException {
        List<Incidente> incidentes = new ArrayList<>();

        String sql = "SELECT i.id, i.latitude, i.longitude, i.descricao, i.gravidade, i.nome, " +
                "i.criador_id, i.is_resolved, i.created_at, i.updated_at " +
                "FROM tb_mvp_incidente i " +
                "WHERE i.gravidade = ? AND i.deleted_at IS NULL " +
                "ORDER BY i.created_at DESC";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, gravidade.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Incidente incidente = mapResultSetToIncidente(rs);
                    incidentes.add(incidente);
                }
            }
        }

        return incidentes;
    }

    public List<Incidente> findByCriadorId(Integer criadorId) throws SQLException {
        List<Incidente> incidentes = new ArrayList<>();

        String sql = "SELECT i.id, i.latitude, i.longitude, i.descricao, i.gravidade, i.nome, " +
                "i.criador_id, i.is_resolved, i.created_at, i.updated_at " +
                "FROM tb_mvp_incidente i " +
                "WHERE i.criador_id = ? AND i.deleted_at IS NULL " +
                "ORDER BY i.created_at DESC";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, criadorId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Incidente incidente = mapResultSetToIncidente(rs);
                    incidentes.add(incidente);
                }
            }
        }

        return incidentes;
    }

    public List<Incidente> findByStatus(boolean resolvido) throws SQLException {
        List<Incidente> incidentes = new ArrayList<>();

        String sql = "SELECT i.id, i.latitude, i.longitude, i.descricao, i.gravidade, i.nome, " +
                "i.criador_id, i.is_resolved, i.created_at, i.updated_at " +
                "FROM tb_mvp_incidente i " +
                "WHERE i.is_resolved = ? AND i.deleted_at IS NULL " +
                "ORDER BY i.created_at DESC";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, resolvido ? "S" : "N");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Incidente incidente = mapResultSetToIncidente(rs);
                    incidentes.add(incidente);
                }
            }
        }

        return incidentes;
    }

    private Incidente mapResultSetToIncidente(ResultSet rs) throws SQLException {
        Incidente incidente = new Incidente();
        incidente.setId(rs.getInt("id"));
        incidente.setLatitude(rs.getString("latitude"));
        incidente.setLongitude(rs.getString("longitude"));
        incidente.setDescricao(rs.getString("descricao"));

        String gravidadeStr = rs.getString("gravidade");
        if (gravidadeStr != null) {
            incidente.setGravidade(Gravidade.valueOf(gravidadeStr));
        }

        incidente.setNome(rs.getString("nome"));

        Integer criadorId = rs.getInt("criador_id");
        if (!rs.wasNull()) {
            Optional<Usuario> criador = usuarioRepository.findById(criadorId);
            criador.ifPresent(incidente::setCriador);
        }

        String isResolvedStr = rs.getString("is_resolved");
        incidente.setIsResolved(isResolvedStr != null && isResolvedStr.equals("S"));

        incidente.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        incidente.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        return incidente;
    }
}
