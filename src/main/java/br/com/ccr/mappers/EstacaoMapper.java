package br.com.ccr.mappers;

import br.com.ccr.dtos.EstacaoDTO;
import br.com.ccr.entities.Estacao;
import br.com.ccr.entities.Linha;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.stream.Collectors;

@ApplicationScoped
public class EstacaoMapper {

    private final EnderecoMapper enderecoMapper;
    private final LinhaMapper linhaMapper;

    public EstacaoMapper() {
        this.enderecoMapper = new EnderecoMapper();
        this.linhaMapper = new LinhaMapper();
    }

    public EstacaoDTO toDTO(Estacao estacao) {
        if (estacao == null) {
            return null;
        }

        EstacaoDTO dto = new EstacaoDTO();
        dto.setId(estacao.getId());
        dto.setNome(estacao.getNome());

        if (estacao.getEndereco() != null) {
            dto.setEndereco(enderecoMapper.toDTO(estacao.getEndereco()));
        }

        if (estacao.getLinhas() != null) {
            dto.setLinhas(estacao.getLinhas().stream()
                    .map(linhaMapper::toDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public Estacao toEntity(EstacaoDTO dto) {
        if (dto == null) {
            return null;
        }

        Estacao estacao = new Estacao();
        estacao.setId(dto.getId());
        estacao.setNome(dto.getNome());

        if (dto.getEndereco() != null) {
            estacao.setEndereco(enderecoMapper.toEntity(dto.getEndereco()));
        }

        if (dto.getLinhas() != null) {
            ArrayList<Linha> linhas = dto.getLinhas().stream()
                    .map(linhaMapper::toEntity).collect(Collectors.toCollection(ArrayList::new));
            estacao.setLinhas(linhas);
        }

        return estacao;
    }
}
