package br.com.ccr.repositories;

import br.com.ccr.entities.Endereco;
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
public class EstacaoRepository {

    @Inject
    private EnderecoRepository enderecoRepository;

    @Inject
    private LinhaRepository linhaRepository;

    public EstacaoRepository() {
    }

    public Estacao save(Estacao estacao) throws SQLException {
        if (estacao.getEndereco() != null) {
            if (estacao.getEndereco().getId() == null) {
                Endereco savedEndereco = enderecoRepository.save(estacao.getEndereco());
                estacao.setEndereco(savedEndereco);
            }
        }

        String sql = "INSERT INTO tb_mvp_estacao (nome, endereco_id, created_at) " +
                "VALUES (?, ?, CURRENT_TIMESTAMP)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, estacao.getNome());

            if (estacao.getEndereco() != null) {
                stmt.setInt(2, estacao.getEndereco().getId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar estação, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    estacao.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Falha ao criar estação, nenhum ID obtido.");
                }
            }

            if (estacao.getLinhas() != null && !estacao.getLinhas().isEmpty()) {
                saveEstacaoLinhaRelations(estacao.getId(), estacao.getLinhas());
            }
        }

        return estacao;
    }

    public Optional<Estacao> findById(Integer id) throws SQLException {
        String sql = "SELECT e.id, e.nome, e.endereco_id, e.created_at, e.updated_at " +
                "FROM tb_mvp_estacao e " +
                "WHERE e.id = ? AND e.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Estacao estacao = mapResultSetToEstacao(rs);

                    estacao.setLinhas(findLinhasByEstacaoId(estacao.getId()));

                    return Optional.of(estacao);
                }
            }
        }

        return Optional.empty();
    }

    public List<Estacao> findAll() throws SQLException {
        List<Estacao> estacoes = new ArrayList<>();

        String sql = "SELECT e.id, e.nome, e.endereco_id, e.created_at, e.updated_at " +
                "FROM tb_mvp_estacao e " +
                "WHERE e.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Estacao estacao = mapResultSetToEstacao(rs);

                estacao.setLinhas(findLinhasByEstacaoId(estacao.getId()));

                estacoes.add(estacao);
            }
        }

        return estacoes;
    }

    public Estacao update(Estacao estacao) throws SQLException {
        if (estacao.getEndereco() != null) {
            enderecoRepository.update(estacao.getEndereco());
        }

        String sql = "UPDATE tb_mvp_estacao SET nome = ?, endereco_id = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE id = ? AND deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, estacao.getNome());

            if (estacao.getEndereco() != null) {
                stmt.setInt(2, estacao.getEndereco().getId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            stmt.setInt(3, estacao.getId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao atualizar estação, nenhuma linha afetada.");
            }

            if (estacao.getLinhas() != null) {
                deleteEstacaoLinhaRelations(estacao.getId());

                saveEstacaoLinhaRelations(estacao.getId(), estacao.getLinhas());
            }
        }

        return estacao;
    }

    public boolean deleteById(Integer id) throws SQLException {
        String sql = "UPDATE tb_mvp_estacao SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public List<Estacao> findByNome(String nome) throws SQLException {
        List<Estacao> estacoes = new ArrayList<>();

        String sql = "SELECT e.id, e.nome, e.endereco_id, e.created_at, e.updated_at " +
                "FROM tb_mvp_estacao e " +
                "WHERE e.nome LIKE ? AND e.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, "%" + nome + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Estacao estacao = mapResultSetToEstacao(rs);

                    estacao.setLinhas(findLinhasByEstacaoId(estacao.getId()));

                    estacoes.add(estacao);
                }
            }
        }

        return estacoes;
    }

    public List<Estacao> findByLinhaId(Integer linhaId) throws SQLException {
        List<Estacao> estacoes = new ArrayList<>();

        String sql = "SELECT e.id, e.nome, e.endereco_id, e.created_at, e.updated_at " +
                "FROM tb_mvp_estacao e " +
                "JOIN tb_mvp_estacao_linha el ON e.id = el.estacao_id " +
                "WHERE el.linha_id = ? AND e.deleted_at IS NULL";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, linhaId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Estacao estacao = mapResultSetToEstacao(rs);

                    // Carregar as linhas relacionadas
                    estacao.setLinhas(findLinhasByEstacaoId(estacao.getId()));

                    estacoes.add(estacao);
                }
            }
        }

        return estacoes;
    }

    private ArrayList<Linha> findLinhasByEstacaoId(Integer estacaoId) throws SQLException {
        ArrayList<Linha> linhas = new ArrayList<>();

        String sql = "SELECT l.id " +
                "FROM tb_mvp_linha l " +
                "JOIN tb_mvp_estacao_linha el ON l.id = el.linha_id " +
                "WHERE el.estacao_id = ? AND l.deleted_at IS NULL " +
                "ORDER BY l.id";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, estacaoId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int linhaId = rs.getInt("id");
                    Optional<Linha> linhaOpt = linhaRepository.findById(linhaId);
                    linhaOpt.ifPresent(linhas::add);
                }
            }
        }

        return linhas;
    }

    private void saveEstacaoLinhaRelations(Integer estacaoId, List<Linha> linhas) throws SQLException {
        String sql = "INSERT INTO tb_mvp_estacao_linha (estacao_id, linha_id) VALUES (?, ?)";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            for (Linha linha : linhas) {
                stmt.setInt(1, estacaoId);
                stmt.setInt(2, linha.getId());
                stmt.addBatch();
            }

            stmt.executeBatch();
        }
    }

    private void deleteEstacaoLinhaRelations(Integer estacaoId) throws SQLException {
        String sql = "DELETE FROM tb_mvp_estacao_linha WHERE estacao_id = ?";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, estacaoId);
            stmt.executeUpdate();
        }
    }

    private Estacao mapResultSetToEstacao(ResultSet rs) throws SQLException {
        Estacao estacao = new Estacao();
        estacao.setId(rs.getInt("id"));
        estacao.setNome(rs.getString("nome"));

        Integer enderecoId = rs.getInt("endereco_id");
        if (!rs.wasNull()) {
            Optional<Endereco> endereco = enderecoRepository.findById(enderecoId);
            endereco.ifPresent(estacao::setEndereco);
        }

        estacao.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        estacao.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        return estacao;
    }
}
