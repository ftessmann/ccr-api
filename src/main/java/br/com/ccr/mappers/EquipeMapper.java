package br.com.ccr.mappers;

import br.com.ccr.dtos.EquipeDTO;
import br.com.ccr.entities.Equipe;
import br.com.ccr.entities.Usuario;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.stream.Collectors;

@ApplicationScoped
public class EquipeMapper {

    private final EstacaoMapper estacaoMapper;
    private final UsuarioMapper usuarioMapper;

    public EquipeMapper() {
        this.estacaoMapper = new EstacaoMapper();
        this.usuarioMapper = new UsuarioMapper();
    }

    public EquipeDTO toDTO(Equipe equipe) {
        if (equipe == null) {
            return null;
        }

        EquipeDTO dto = new EquipeDTO();
        dto.setId(equipe.getId());
        dto.setNome(equipe.getNome());
        dto.setSetor(equipe.getSetor());

        if (equipe.getBase() != null) {
            dto.setBase(estacaoMapper.toDTO(equipe.getBase()));
        }

        if (equipe.getIntegrantes() != null) {
            dto.setIntegrantes(equipe.getIntegrantes().stream()
                    .map(usuarioMapper::toDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public Equipe toEntity(EquipeDTO dto) {
        if (dto == null) {
            return null;
        }

        Equipe equipe = new Equipe();
        equipe.setId(dto.getId());
        equipe.setNome(dto.getNome());
        equipe.setSetor(dto.getSetor());

        if (dto.getBase() != null) {
            equipe.setBase(estacaoMapper.toEntity(dto.getBase()));
        }

        if (dto.getIntegrantes() != null) {
            ArrayList<Usuario> integrantes = new ArrayList<>(
                    dto.getIntegrantes().stream()
                            .map(usuarioMapper::toEntity)
                            .collect(Collectors.toList())
            );
            equipe.setIntegrantes(integrantes);
        }

        return equipe;
    }
}
