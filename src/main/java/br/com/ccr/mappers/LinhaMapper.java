package br.com.ccr.mappers;

import br.com.ccr.dtos.LinhaDTO;
import br.com.ccr.entities.Estacao;
import br.com.ccr.entities.Linha;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.stream.Collectors;

@ApplicationScoped
public class LinhaMapper {

    @Inject
    private EstacaoMapper estacaoMapper;

    public LinhaMapper() {
    }

    public LinhaDTO toDTO(Linha linha) {
        if (linha == null) {
            return null;
        }

        LinhaDTO dto = new LinhaDTO();
        dto.setId(linha.getId());
        dto.setNome(linha.getNome());

        if (linha.getEstacoes() != null) {
            dto.setEstacoes(linha.getEstacoes().stream()
                    .map(estacaoMapper::toDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public Linha toEntity(LinhaDTO dto) {
        if (dto == null) {
            return null;
        }

        Linha linha = new Linha();
        linha.setId(dto.getId());
        linha.setNome(dto.getNome());

        if (dto.getEstacoes() != null) {
            ArrayList<Estacao> estacoes = new ArrayList<>(
                    dto.getEstacoes().stream()
                            .map(estacaoMapper::toEntity)
                            .collect(Collectors.toList())
            );
            linha.setEstacoes(estacoes);
        }

        return linha;
    }
}
