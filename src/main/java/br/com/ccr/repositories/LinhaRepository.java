package br.com.ccr.repositories;

import br.com.ccr.entities.Estacao;
import br.com.ccr.entities.Linha;
import br.com.ccr.infrastructure.DatabaseConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class LinhaRepository {

    @Inject
    private EstacaoRepository estacaoRepository;

    public LinhaRepository() {
    }

    public Linha save(Linha linha) throws SQLException {
        String sql = "INSERT INTO tb_mvp_linha (nome, created_at) VALUES (?, CURRENT_TIMESTAMP)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, linha.getNome());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar linha, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    linha.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Falha ao criar linha, nenhum ID obtido.");
                }
            }

            if (linha.getEstacoes() != null && !linha.getEstacoes().isEmpty()) {
                saveEstacaoLinhaRelations(linha.getId(), linha.getEstacoes());
            }
        }

        return linha;
    }

    public Optional<Linha> findById(Integer id) throws SQLException {
        String sql = "SELECT l.id, l.nome, l.created_at, l.updated_at " +
                "FROM tb_mvp_linha l " +
                "WHERE l.id = ? AND l.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Linha linha = mapResultSetToLinha(rs);

                    linha.setEstacoes(findEstacoesByLinhaId(linha.getId()));

                    return Optional.of(linha);
                }
            }
        }

        return Optional.empty();
    }

    public List<Linha> findAll() throws SQLException {
        List<Linha> linhas = new ArrayList<>();

        String sql = "SELECT l.id, l.nome " +
                "FROM tb_mvp_linha l " +
                "WHERE l.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Linha linha = mapResultSetToLinha(rs);

                linhas.add(linha);
            }
        }

        return linhas;
    }


    public Linha update(Linha linha) throws SQLException {
        String sql = "UPDATE tb_mvp_linha SET nome = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE id = ? AND deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, linha.getNome());
            stmt.setInt(2, linha.getId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao atualizar linha, nenhuma linha afetada.");
            }

            if (linha.getEstacoes() != null) {
                deleteEstacaoLinhaRelations(linha.getId());

                saveEstacaoLinhaRelations(linha.getId(), linha.getEstacoes());
            }
        }

        return linha;
    }

    public boolean deleteById(Integer id) throws SQLException {
        String sql = "UPDATE tb_mvp_linha SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public List<Linha> findByNome(String nome) throws SQLException {
        List<Linha> linhas = new ArrayList<>();

        String sql = "SELECT l.id, l.nome " +
                "FROM tb_mvp_linha l " +
                "WHERE l.nome LIKE ? AND l.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, "%" + nome + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Linha linha = mapResultSetToLinha(rs);

                    linha.setEstacoes(findEstacoesByLinhaId(linha.getId()));

                    linhas.add(linha);
                }
            }
        }

        return linhas;
    }

    private ArrayList<Estacao> findEstacoesByLinhaId(Integer linhaId) throws SQLException {
        ArrayList<Estacao> estacoes = new ArrayList<>();

        String sql = "SELECT e.id " +
                "FROM tb_mvp_estacao e " +
                "JOIN tb_mvp_estacao_linha el ON e.id = el.estacao_id " +
                "WHERE el.linha_id = ? AND e.deleted_at IS NULL " +
                "ORDER BY e.id";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, linhaId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int estacaoId = rs.getInt("id");
                    Optional<Estacao> estacaoOpt = estacaoRepository.findById(estacaoId);
                    estacaoOpt.ifPresent(estacoes::add);
                }
            }
        }

        return estacoes;
    }

    private void saveEstacaoLinhaRelations(Integer linhaId, List<Estacao> estacoes) throws SQLException {
        String sql = "INSERT INTO tb_mvp_estacao_linha (estacao_id, linha_id) VALUES (?, ?)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            for (Estacao estacao : estacoes) {
                stmt.setInt(1, estacao.getId());
                stmt.setInt(2, linhaId);
                stmt.addBatch();
            }

            stmt.executeBatch();
        }
    }

    private void deleteEstacaoLinhaRelations(Integer linhaId) throws SQLException {
        String sql = "DELETE FROM tb_mvp_estacao_linha WHERE linha_id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, linhaId);
            stmt.executeUpdate();
        }
    }

    private Linha mapResultSetToLinha(ResultSet rs) throws SQLException {
        Linha linha = new Linha();
        linha.setId(rs.getInt("id"));
        linha.setNome(rs.getString("nome"));

        return linha;
    }
}
