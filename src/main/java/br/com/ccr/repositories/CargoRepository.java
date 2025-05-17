package br.com.ccr.repositories;

import br.com.ccr.entities.Cargo;
import br.com.ccr.infrastructure.DatabaseConfig;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.*;

@ApplicationScoped
public class CargoRepository {

    private static final Logger log = LogManager.getLogger(CargoRepository.class);

    public final Map<Cargo, Integer> cargoIdMap = new HashMap<>();
    private final Map<Integer, Cargo> idCargoMap = new HashMap<>();

    public CargoRepository() {
        carregarCargos();
    }

    public List<Cargo> carregarCargos() {
        try (var connection = DatabaseConfig.getConnection();
             var stmt = connection.prepareStatement("SELECT id_cargo_tipo, nm_cargo FROM T_CCR_CARGO_TIPO WHERE dt_exclusao IS NULL")) {

            try (var rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_cargo_tipo");
                    String nome = rs.getString("nm_cargo");

                    try {
                        Cargo cargo = Cargo.valueOf(nome);
                        cargoIdMap.put(cargo, id);
                        idCargoMap.put(id, cargo);
                    } catch (IllegalArgumentException e) {
                        log.error("Cargo inválido encontrado no banco: " + nome, e);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Erro ao carregar cargos do banco de dados", e);
        }
        return null;
    }

    public int getCargoId(Cargo cargo) {
        Integer id = cargoIdMap.get(cargo);
        if (id == null) {
            log.error("ID não encontrado para o cargo: " + cargo);
            return -1;
        }
        return id;
    }

    public Cargo getCargoById(int id) {
        return idCargoMap.get(id);
    }


    public Optional<Cargo> buscarPorNome(String nome) {
        return Arrays.stream(Cargo.values())
                .filter(cargo -> cargo.name().equalsIgnoreCase(nome))
                .findFirst();
    }
}
