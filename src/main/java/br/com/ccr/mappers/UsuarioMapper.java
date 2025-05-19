package br.com.ccr.mappers;

import br.com.ccr.dtos.UsuarioDTO;
import br.com.ccr.entities.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UsuarioMapper {

    @Inject
    private EnderecoMapper enderecoMapper;

    public UsuarioDTO toDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setCpf(usuario.getCpf());
        dto.setEmail(usuario.getEmail());
        dto.setTelefone(usuario.getTelefone());

        if (usuario.getEndereco() != null) {
            dto.setEndereco(enderecoMapper.toDTO(usuario.getEndereco()));
        }

        dto.setCargo(usuario.getCargo());
        dto.setSetor(usuario.getSetor());

        return dto;
    }

    public Usuario toEntity(br.com.ccr.dtos.UsuarioDTO dto) {
        if (dto == null) {
            return null;
        }

        Usuario usuario = new Usuario();
        usuario.setId(dto.getId());
        usuario.setNome(dto.getNome());
        usuario.setCpf(dto.getCpf());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha());
        usuario.setTelefone(dto.getTelefone());

        if (dto.getEndereco() != null) {
            usuario.setEndereco(enderecoMapper.toEntity(dto.getEndereco()));
        }

        usuario.setCargo(dto.getCargo());
        usuario.setSetor(dto.getSetor());

        return usuario;
    }
}
