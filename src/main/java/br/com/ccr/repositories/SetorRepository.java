package br.com.ccr.repositories;

import br.com.ccr.entities.Setor;
import br.com.ccr.infrastructure.DatabaseConfig;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class SetorRepository {

    private static final Logger log = LogManager.getLogger(SetorRepository.class);
    private static final Map<String, Integer> setorIdMap = new HashMap<>();
    private static final Map<Integer, Setor> idSetorMap = new HashMap<>();

    public SetorRepository() {
        carregarSetores();
    }

    private void carregarSetores() {
        try (var connection = DatabaseConfig.getConnection();
             var stmt = connection.prepareStatement("SELECT id_setor, nm_setor FROM T_CCR_SETOR");
             var rs = stmt.executeQuery()) {

            setorIdMap.clear();
            idSetorMap.clear();

            while (rs.next()) {
                int id = rs.getInt("id_setor");
                String nome = rs.getString("nm_setor");

                try {
                    Setor setor = Setor.valueOf(nome.toUpperCase());
                    setorIdMap.put(setor.name(), id);
                    idSetorMap.put(id, setor);
                } catch (IllegalArgumentException e) {
                    log.warn("Setor no banco de dados não corresponde a nenhum valor do enum: {}", nome);
                }
            }
        } catch (SQLException e) {
            log.error("Erro ao carregar mapeamento de setores", e);
        }
    }

    public int getSetorId(Setor setor) {
        if (setor == null) {
            return -1;
        }

        Integer id = setorIdMap.get(setor.name());
        if (id != null) {
            return id;
        }

        try (var connection = DatabaseConfig.getConnection();
             var stmt = connection.prepareStatement(
                     "SELECT id_setor FROM T_CCR_SETOR WHERE nm_setor = ?")) {

            stmt.setString(1, setor.name());

            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int setorId = rs.getInt("id_setor");
                    setorIdMap.put(setor.name(), setorId);
                    idSetorMap.put(setorId, setor);
                    return setorId;
                }
            }
        } catch (SQLException e) {
            log.error("Erro ao buscar ID do setor", e);
        }

        return -1;
    }

    public Setor getSetorById(int id) {
        Setor setor = idSetorMap.get(id);
        if (setor != null) {
            return setor;
        }

        try (var connection = DatabaseConfig.getConnection();
             var stmt = connection.prepareStatement(
                     "SELECT nm_setor FROM T_CCR_SETOR WHERE id_setor = ?")) {

            stmt.setInt(1, id);

            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String nome = rs.getString("nm_setor");
                    try {
                        setor = Setor.valueOf(nome.toUpperCase());
                        // Atualiza o cache
                        idSetorMap.put(id, setor);
                        setorIdMap.put(setor.name(), id);
                        return setor;
                    } catch (IllegalArgumentException e) {
                        log.warn("Valor de setor no banco não corresponde ao enum: {}", nome);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Erro ao buscar setor por ID", e);
        }

        return null;
    }
}
