package br.com.ccr.mappers;

import br.com.ccr.dtos.IncidenteDTO;
import br.com.ccr.entities.Incidente;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class IncidenteMapper {

    private final UsuarioMapper usuarioMapper;

    public IncidenteMapper() {
        this.usuarioMapper = new UsuarioMapper();
    }

    public IncidenteDTO toDTO(Incidente incidente) {
        if (incidente == null) {
            return null;
        }

        IncidenteDTO dto = new IncidenteDTO();
        dto.setId(incidente.getId());
        dto.setLatitude(incidente.getLatitude());
        dto.setLongitude(incidente.getLongitude());
        dto.setDescricao(incidente.getDescricao());
        dto.setGravidade(incidente.getGravidade());
        dto.setNome(incidente.getNome());
        dto.setIsResolved(incidente.getIsResolved());

        if (incidente.getCriador() != null) {
            dto.setCriador(usuarioMapper.toDTO(incidente.getCriador()));
        }

        return dto;
    }

    public Incidente toEntity(IncidenteDTO dto) {
        if (dto == null) {
            return null;
        }

        Incidente incidente = new Incidente();
        incidente.setId(dto.getId());
        incidente.setLatitude(dto.getLatitude());
        incidente.setLongitude(dto.getLongitude());
        incidente.setDescricao(dto.getDescricao());
        incidente.setGravidade(dto.getGravidade());
        incidente.setNome(dto.getNome());
        incidente.setIsResolved(dto.getIsResolved());

        if (dto.getCriador() != null) {
            incidente.setCriador(usuarioMapper.toEntity(dto.getCriador()));
        }

        return incidente;
    }
}
