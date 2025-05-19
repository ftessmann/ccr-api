package br.com.ccr.mappers;

import br.com.ccr.dtos.VagaoDTO;
import br.com.ccr.entities.Vagao;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class VagaoMapper {

    public VagaoDTO toDTO(Vagao vagao) {
        if (vagao == null) {
            return null;
        }

        VagaoDTO dto = new VagaoDTO();
        dto.setId(vagao.getId());
        dto.setNumeracao(vagao.getNumeracao());

        return dto;
    }

    public Vagao toEntity(VagaoDTO dto) {
        if (dto == null) {
            return null;
        }

        Vagao vagao = new Vagao();
        vagao.setId(dto.getId());
        vagao.setNumeracao(dto.getNumeracao());

        return vagao;
    }
}
