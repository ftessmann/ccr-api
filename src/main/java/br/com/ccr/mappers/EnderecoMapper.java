package br.com.ccr.mappers;

import br.com.ccr.dtos.EnderecoDTO;
import br.com.ccr.entities.Endereco;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EnderecoMapper {

    public EnderecoDTO toDTO(Endereco endereco) {
        if (endereco == null) {
            return null;
        }

        br.com.ccr.dtos.EnderecoDTO dto = new EnderecoDTO();
        dto.setId(endereco.getId());
        dto.setCep(endereco.getCep());
        dto.setRua(endereco.getRua());
        dto.setNumero(endereco.getNumero());
        dto.setBairro(endereco.getBairro());
        dto.setCidade(endereco.getCidade());
        dto.setEstado(endereco.getEstado());
        dto.setComplemento(endereco.getComplemento());

        return dto;
    }

    public Endereco toEntity(EnderecoDTO dto) {
        if (dto == null) {
            return null;
        }

        Endereco endereco = new Endereco();
        endereco.setId(dto.getId());
        endereco.setCep(dto.getCep());
        endereco.setRua(dto.getRua());
        endereco.setNumero(dto.getNumero());
        endereco.setBairro(dto.getBairro());
        endereco.setCidade(dto.getCidade());
        endereco.setEstado(dto.getEstado());
        endereco.setComplemento(dto.getComplemento());

        return endereco;
    }
}
