package br.com.ccr.repositories;

import br.com.ccr.entities.Equipe;
import br.com.ccr.entities.Estacao;
import br.com.ccr.entities.Setor;
import br.com.ccr.entities.Usuario;
import br.com.ccr.infrastructure.DatabaseConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class EquipeRepository {

    @Inject
    private EstacaoRepository estacaoRepository;

    @Inject
    private UsuarioRepository usuarioRepository;

    public EquipeRepository() {
    }

    public Equipe save(Equipe equipe) throws SQLException {
        String sql = "INSERT INTO tb_mvp_equipe (nome, base_id, setor, created_at) " +
                "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, equipe.getNome());

            if (equipe.getBase() != null) {
                stmt.setInt(2, equipe.getBase().getId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            if (equipe.getSetor() != null) {
                stmt.setString(3, equipe.getSetor().name());
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar equipe, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    equipe.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Falha ao criar equipe, nenhum ID obtido.");
                }
            }

            if (equipe.getIntegrantes() != null && !equipe.getIntegrantes().isEmpty()) {
                saveEquipeUsuarioRelations(equipe.getId(), equipe.getIntegrantes());
            }
        }

        return equipe;
    }

    public Optional<Equipe> findById(Integer id) throws SQLException {
        String sql = "SELECT e.id, e.nome, e.base_id, e.setor, e.created_at, e.updated_at " +
                "FROM tb_mvp_equipe e " +
                "WHERE e.id = ? AND e.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Equipe equipe = mapResultSetToEquipe(rs);

                    equipe.setIntegrantes(findIntegrantesByEquipeId(equipe.getId()));

                    return Optional.of(equipe);
                }
            }
        }

        return Optional.empty();
    }

    public List<Equipe> findAll() throws SQLException {
        List<Equipe> equipes = new ArrayList<>();

        String sql = "SELECT e.id, e.nome, e.base_id, e.setor, e.created_at, e.updated_at " +
                "FROM tb_mvp_equipe e " +
                "WHERE e.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Equipe equipe = mapResultSetToEquipe(rs);

                equipe.setIntegrantes(findIntegrantesByEquipeId(equipe.getId()));

                equipes.add(equipe);
            }
        }

        return equipes;
    }

    public Equipe update(Equipe equipe) throws SQLException {
        String sql = "UPDATE tb_mvp_equipe SET nome = ?, base_id = ?, setor = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE id = ? AND deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, equipe.getNome());

            if (equipe.getBase() != null) {
                stmt.setInt(2, equipe.getBase().getId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            if (equipe.getSetor() != null) {
                stmt.setString(3, equipe.getSetor().name());
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }

            stmt.setInt(4, equipe.getId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao atualizar equipe, nenhuma linha afetada.");
            }

            if (equipe.getIntegrantes() != null) {
                deleteEquipeUsuarioRelations(equipe.getId());

                saveEquipeUsuarioRelations(equipe.getId(), equipe.getIntegrantes());
            }
        }

        return equipe;
    }

    public boolean deleteById(Integer id) throws SQLException {
        String sql = "UPDATE tb_mvp_equipe SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public List<Equipe> findByNome(String nome) throws SQLException {
        List<Equipe> equipes = new ArrayList<>();

        String sql = "SELECT e.id, e.nome, e.base_id, e.setor, e.created_at, e.updated_at " +
                "FROM tb_mvp_equipe e " +
                "WHERE e.nome LIKE ? AND e.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, "%" + nome + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Equipe equipe = mapResultSetToEquipe(rs);

                    equipe.setIntegrantes(findIntegrantesByEquipeId(equipe.getId()));

                    equipes.add(equipe);
                }
            }
        }

        return equipes;
    }

    public List<Equipe> findBySetor(Setor setor) throws SQLException {
        List<Equipe> equipes = new ArrayList<>();

        String sql = "SELECT e.id, e.nome, e.base_id, e.setor, e.created_at, e.updated_at " +
                "FROM tb_mvp_equipe e " +
                "WHERE e.setor = ? AND e.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, setor.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Equipe equipe = mapResultSetToEquipe(rs);

                    equipe.setIntegrantes(findIntegrantesByEquipeId(equipe.getId()));

                    equipes.add(equipe);
                }
            }
        }

        return equipes;
    }

    public List<Equipe> findByBaseId(Integer baseId) throws SQLException {
        List<Equipe> equipes = new ArrayList<>();

        String sql = "SELECT e.id, e.nome, e.base_id, e.setor, e.created_at, e.updated_at " +
                "FROM tb_mvp_equipe e " +
                "WHERE e.base_id = ? AND e.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, baseId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Equipe equipe = mapResultSetToEquipe(rs);

                    equipe.setIntegrantes(findIntegrantesByEquipeId(equipe.getId()));

                    equipes.add(equipe);
                }
            }
        }

        return equipes;
    }

    public List<Equipe> findByIntegranteId(Integer integranteId) throws SQLException {
        List<Equipe> equipes = new ArrayList<>();

        String sql = "SELECT e.id, e.nome, e.base_id, e.setor, e.created_at, e.updated_at " +
                "FROM tb_mvp_equipe e " +
                "JOIN tb_mvp_equipe_usuario eu ON e.id = eu.equipe_id " +
                "WHERE eu.usuario_id = ? AND e.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, integranteId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Equipe equipe = mapResultSetToEquipe(rs);

                    // Carregar os integrantes relacionados
                    equipe.setIntegrantes(findIntegrantesByEquipeId(equipe.getId()));

                    equipes.add(equipe);
                }
            }
        }

        return equipes;
    }

    private ArrayList<Usuario> findIntegrantesByEquipeId(Integer equipeId) throws SQLException {
        ArrayList<Usuario> integrantes = new ArrayList<>();

        String sql = "SELECT u.id " +
                "FROM tb_mvp_usuario u " +
                "JOIN tb_mvp_equipe_usuario eu ON u.id = eu.usuario_id " +
                "WHERE eu.equipe_id = ? AND u.deleted_at IS NULL " +
                "ORDER BY u.id";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, equipeId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int integranteId = rs.getInt("id");
                    Optional<Usuario> integranteOpt = usuarioRepository.findById(integranteId);
                    integranteOpt.ifPresent(integrantes::add);
                }
            }
        }

        return integrantes;
    }

    private void saveEquipeUsuarioRelations(Integer equipeId, List<Usuario> integrantes) throws SQLException {
        String sql = "INSERT INTO tb_mvp_equipe_usuario (equipe_id, usuario_id) VALUES (?, ?)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            for (Usuario integrante : integrantes) {
                stmt.setInt(1, equipeId);
                stmt.setInt(2, integrante.getId());
                stmt.addBatch();
            }

            stmt.executeBatch();
        }
    }

    private void deleteEquipeUsuarioRelations(Integer equipeId) throws SQLException {
        String sql = "DELETE FROM tb_mvp_equipe_usuario WHERE equipe_id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, equipeId);
            stmt.executeUpdate();
        }
    }

    private Equipe mapResultSetToEquipe(ResultSet rs) throws SQLException {
        Equipe equipe = new Equipe();
        equipe.setId(rs.getInt("id"));
        equipe.setNome(rs.getString("nome"));

        Integer baseId = rs.getInt("base_id");
        if (!rs.wasNull()) {
            Optional<Estacao> base = estacaoRepository.findById(baseId);
            base.ifPresent(equipe::setBase);
        }

        String setorStr = rs.getString("setor");
        if (setorStr != null) {
            equipe.setSetor(Setor.valueOf(setorStr));
        }

        equipe.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        equipe.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        return equipe;
    }
}
