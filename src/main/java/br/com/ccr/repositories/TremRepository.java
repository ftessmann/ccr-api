package br.com.ccr.repositories;

import br.com.ccr.entities.Estacao;
import br.com.ccr.entities.Linha;
import br.com.ccr.entities.Trem;
import br.com.ccr.entities.Usuario;
import br.com.ccr.entities.Vagao;
import br.com.ccr.infrastructure.DatabaseConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TremRepository {

    @Inject
    private EstacaoRepository estacaoRepository;

    @Inject
    private LinhaRepository linhaRepository;

    @Inject
    private UsuarioRepository usuarioRepository;

    @Inject
    private VagaoRepository vagaoRepository;

    public TremRepository() {
    }

    public Trem save(Trem trem) throws SQLException {
        String sql = "INSERT INTO tb_mvp_trem (modelo, estacao_inicial_id, estacao_final_id, linha_id, created_at) " +
                "VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, trem.getModelo());

            if (trem.getEstacaoInicial() != null) {
                stmt.setInt(2, trem.getEstacaoInicial().getId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            if (trem.getEstacaoFinal() != null) {
                stmt.setInt(3, trem.getEstacaoFinal().getId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            if (trem.getLinha() != null) {
                stmt.setInt(4, trem.getLinha().getId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar trem, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    trem.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Falha ao criar trem, nenhum ID obtido.");
                }
            }

            if (trem.getCondutores() != null && !trem.getCondutores().isEmpty()) {
                saveTremCondutorRelations(trem.getId(), trem.getCondutores());
            }

            if (trem.getVagoes() != null && !trem.getVagoes().isEmpty()) {
                saveTremVagaoRelations(trem.getId(), trem.getVagoes());
            }
        }

        return trem;
    }

    public Optional<Trem> findById(Integer id) throws SQLException {
        String sql = "SELECT t.id, t.modelo, t.estacao_inicial_id, t.estacao_final_id, t.linha_id, " +
                "t.created_at, t.updated_at " +
                "FROM tb_mvp_trem t " +
                "WHERE t.id = ? AND t.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Trem trem = mapResultSetToTrem(rs);

                    trem.setCondutores(findCondutoresByTremId(trem.getId()));

                    trem.setVagoes(findVagoesByTremId(trem.getId()));

                    return Optional.of(trem);
                }
            }
        }

        return Optional.empty();
    }

    public List<Trem> findAll() throws SQLException {
        List<Trem> trens = new ArrayList<>();

        String sql = "SELECT t.id, t.modelo, t.estacao_inicial_id, t.estacao_final_id, t.linha_id, " +
                "t.created_at, t.updated_at " +
                "FROM tb_mvp_trem t " +
                "WHERE t.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Trem trem = mapResultSetToTrem(rs);

                trem.setCondutores(findCondutoresByTremId(trem.getId()));

                trem.setVagoes(findVagoesByTremId(trem.getId()));

                trens.add(trem);
            }
        }

        return trens;
    }

    public Trem update(Trem trem) throws SQLException {
        String sql = "UPDATE tb_mvp_trem SET modelo = ?, estacao_inicial_id = ?, estacao_final_id = ?, " +
                "linha_id = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE id = ? AND deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, trem.getModelo());

            if (trem.getEstacaoInicial() != null) {
                stmt.setInt(2, trem.getEstacaoInicial().getId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            if (trem.getEstacaoFinal() != null) {
                stmt.setInt(3, trem.getEstacaoFinal().getId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            if (trem.getLinha() != null) {
                stmt.setInt(4, trem.getLinha().getId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            stmt.setInt(5, trem.getId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao atualizar trem, nenhuma linha afetada.");
            }

            if (trem.getCondutores() != null) {
                deleteTremCondutorRelations(trem.getId());
                saveTremCondutorRelations(trem.getId(), trem.getCondutores());
            }

            if (trem.getVagoes() != null) {
                deleteTremVagaoRelations(trem.getId());
                saveTremVagaoRelations(trem.getId(), trem.getVagoes());
            }
        }

        return trem;
    }

    public boolean deleteById(Integer id) throws SQLException {
        String sql = "UPDATE tb_mvp_trem SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public List<Trem> findByLinhaId(Integer linhaId) throws SQLException {
        List<Trem> trens = new ArrayList<>();

        String sql = "SELECT t.id, t.modelo, t.estacao_inicial_id, t.estacao_final_id, t.linha_id, " +
                "t.created_at, t.updated_at " +
                "FROM tb_mvp_trem t " +
                "WHERE t.linha_id = ? AND t.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, linhaId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Trem trem = mapResultSetToTrem(rs);

                    trem.setCondutores(findCondutoresByTremId(trem.getId()));

                    trem.setVagoes(findVagoesByTremId(trem.getId()));

                    trens.add(trem);
                }
            }
        }

        return trens;
    }

    public List<Trem> findByEstacaoId(Integer estacaoId) throws SQLException {
        List<Trem> trens = new ArrayList<>();

        String sql = "SELECT t.id, t.modelo, t.estacao_inicial_id, t.estacao_final_id, t.linha_id, " +
                "t.created_at, t.updated_at " +
                "FROM tb_mvp_trem t " +
                "WHERE (t.estacao_inicial_id = ? OR t.estacao_final_id = ?) AND t.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, estacaoId);
            stmt.setInt(2, estacaoId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Trem trem = mapResultSetToTrem(rs);

                    trem.setCondutores(findCondutoresByTremId(trem.getId()));

                    trem.setVagoes(findVagoesByTremId(trem.getId()));

                    trens.add(trem);
                }
            }
        }

        return trens;
    }

    public List<Trem> findByCondutorId(Integer condutorId) throws SQLException {
        List<Trem> trens = new ArrayList<>();

        String sql = "SELECT t.id, t.modelo, t.estacao_inicial_id, t.estacao_final_id, t.linha_id, " +
                "t.created_at, t.updated_at " +
                "FROM tb_mvp_trem t " +
                "JOIN tb_mvp_trem_condutor tc ON t.id = tc.trem_id " +
                "WHERE tc.usuario_id = ? AND t.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, condutorId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Trem trem = mapResultSetToTrem(rs);

                    // Carregar os condutores relacionados
                    trem.setCondutores(findCondutoresByTremId(trem.getId()));

                    // Carregar os vagões relacionados
                    trem.setVagoes(findVagoesByTremId(trem.getId()));

                    trens.add(trem);
                }
            }
        }

        return trens;
    }

    private ArrayList<Usuario> findCondutoresByTremId(Integer tremId) throws SQLException {
        ArrayList<Usuario> condutores = new ArrayList<>();

        String sql = "SELECT u.id " +
                "FROM tb_mvp_usuario u " +
                "JOIN tb_mvp_trem_condutor tc ON u.id = tc.usuario_id " +
                "WHERE tc.trem_id = ? AND u.deleted_at IS NULL " +
                "ORDER BY u.id";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, tremId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int condutorId = rs.getInt("id");
                    Optional<Usuario> condutorOpt = usuarioRepository.findById(condutorId);
                    condutorOpt.ifPresent(condutores::add);
                }
            }
        }

        return condutores;
    }

    private ArrayList<Vagao> findVagoesByTremId(Integer tremId) throws SQLException {
        ArrayList<Vagao> vagoes = new ArrayList<>();

        String sql = "SELECT v.id " +
                "FROM tb_mvp_vagao v " +
                "JOIN tb_mvp_trem_vagao tv ON v.id = tv.vagao_id " +
                "WHERE tv.trem_id = ? AND v.deleted_at IS NULL " +
                "ORDER BY v.id";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, tremId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int vagaoId = rs.getInt("id");
                    Optional<Vagao> vagaoOpt = vagaoRepository.findById(vagaoId);
                    vagaoOpt.ifPresent(vagoes::add);
                }
            }
        }

        return vagoes;
    }

    private void saveTremCondutorRelations(Integer tremId, List<Usuario> condutores) throws SQLException {
        String sql = "INSERT INTO tb_mvp_trem_condutor (trem_id, usuario_id) VALUES (?, ?)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            for (Usuario condutor : condutores) {
                stmt.setInt(1, tremId);
                stmt.setInt(2, condutor.getId());
                stmt.addBatch();
            }

            stmt.executeBatch();
        }
    }

    private void deleteTremCondutorRelations(Integer tremId) throws SQLException {
        String sql = "DELETE FROM tb_mvp_trem_condutor WHERE trem_id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, tremId);
            stmt.executeUpdate();
        }
    }

    private void saveTremVagaoRelations(Integer tremId, List<Vagao> vagoes) throws SQLException {
        String sql = "INSERT INTO tb_mvp_trem_vagao (trem_id, vagao_id) VALUES (?, ?)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            for (Vagao vagao : vagoes) {
                stmt.setInt(1, tremId);
                stmt.setInt(2, vagao.getId());
                stmt.addBatch();
            }

            stmt.executeBatch();
        }
    }

    private void deleteTremVagaoRelations(Integer tremId) throws SQLException {
        String sql = "DELETE FROM tb_mvp_trem_vagao WHERE trem_id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, tremId);
            stmt.executeUpdate();
        }
    }

    private Trem mapResultSetToTrem(ResultSet rs) throws SQLException {
        Trem trem = new Trem();
        trem.setId(rs.getInt("id"));
        trem.setModelo(rs.getString("modelo"));

        Integer estacaoInicialId = rs.getInt("estacao_inicial_id");
        if (!rs.wasNull()) {
            Optional<Estacao> estacaoInicial = estacaoRepository.findById(estacaoInicialId);
            estacaoInicial.ifPresent(trem::setEstacaoInicial);
        }

        Integer estacaoFinalId = rs.getInt("estacao_final_id");
        if (!rs.wasNull()) {
            Optional<Estacao> estacaoFinal = estacaoRepository.findById(estacaoFinalId);
            estacaoFinal.ifPresent(trem::setEstacaoFinal);
        }

        Integer linhaId = rs.getInt("linha_id");
        if (!rs.wasNull()) {
            Optional<Linha> linha = linhaRepository.findById(linhaId);
            linha.ifPresent(trem::setLinha);
        }

        trem.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        trem.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        return trem;
    }
}
