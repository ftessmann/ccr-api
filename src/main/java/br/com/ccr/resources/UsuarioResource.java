package br.com.ccr.resources;

import br.com.ccr.dtos.UsuarioDTO;
import br.com.ccr.entities.Usuario;
import br.com.ccr.repositories.UsuarioRepository;
import br.com.ccr.mappers.UsuarioMapper;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    @Inject
    private UsuarioRepository usuarioRepository;

    @Inject
    private UsuarioMapper usuarioMapper;

    @POST
    public Response createUsuario(UsuarioDTO usuarioDTO) {
        try {
            Optional<Usuario> existingByEmail = usuarioRepository.findByEmail(usuarioDTO.getEmail());
            if (existingByEmail.isPresent()) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("Já existe um usuário com este email")
                        .build();
            }

            Optional<Usuario> existingByCpf = usuarioRepository.findByCpf(usuarioDTO.getCpf());
            if (existingByCpf.isPresent()) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("Já existe um usuário com este CPF")
                        .build();
            }

            Usuario usuario = usuarioMapper.toEntity(usuarioDTO);

            Usuario savedUsuario = usuarioRepository.save(usuario);

            UsuarioDTO savedUsuarioDTO = usuarioMapper.toDTO(savedUsuario);

            return Response.status(Response.Status.CREATED)
                    .entity(savedUsuarioDTO)
                    .build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao criar usuário: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getUsuarioById(@PathParam("id") Integer id) {
        try {
            Optional<Usuario> usuario = usuarioRepository.findById(id);

            if (usuario.isPresent()) {
                UsuarioDTO usuarioDTO = usuarioMapper.toDTO(usuario.get());
                return Response.ok(usuarioDTO).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Usuário não encontrado")
                        .build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar usuário: " + e.getMessage())
                    .build();
        }
    }

    @GET
    public Response getAllUsuarios() {
        try {
            List<Usuario> usuarios = usuarioRepository.findAll();
            List<UsuarioDTO> usuarioDTOs = usuarios.stream()
                    .map(usuarioMapper::toDTO)
                    .collect(Collectors.toList());

            return Response.ok(usuarioDTOs).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar usuários: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/me")
    public Response getCurrentUser(@Context SecurityContext securityContext) {
        try {
            String email = securityContext.getUserPrincipal().getName();

            Optional<Usuario> usuario = usuarioRepository.findByEmail(email);

            if (usuario.isPresent()) {
                UsuarioDTO usuarioDTO = usuarioMapper.toDTO(usuario.get());
                return Response.ok(usuarioDTO).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Usuário autenticado não encontrado")
                        .build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar usuário autenticado: " + e.getMessage())
                    .build();
        }
    }


    @PUT
    @Path("/{id}")
    public Response updateUsuario(@PathParam("id") Integer id, UsuarioDTO usuarioDTO) {
        try {
            Optional<Usuario> existingUsuario = usuarioRepository.findById(id);

            if (!existingUsuario.isPresent()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Usuário não encontrado")
                        .build();
            }

            if (!usuarioDTO.getEmail().equals(existingUsuario.get().getEmail())) {
                Optional<Usuario> existingByEmail = usuarioRepository.findByEmail(usuarioDTO.getEmail());
                if (existingByEmail.isPresent() && !existingByEmail.get().getId().equals(id)) {
                    return Response.status(Response.Status.CONFLICT)
                            .entity("Já existe um usuário com este email")
                            .build();
                }
            }

            if (!usuarioDTO.getCpf().equals(existingUsuario.get().getCpf())) {
                Optional<Usuario> existingByCpf = usuarioRepository.findByCpf(usuarioDTO.getCpf());
                if (existingByCpf.isPresent() && !existingByCpf.get().getId().equals(id)) {
                    return Response.status(Response.Status.CONFLICT)
                            .entity("Já existe um usuário com este CPF")
                            .build();
                }
            }

            // Converter DTO para entidade
            Usuario usuario = usuarioMapper.toEntity(usuarioDTO);
            usuario.setId(id);

            // Atualizar no banco
            Usuario updatedUsuario = usuarioRepository.update(usuario);

            // Converter entidade atualizada para DTO
            UsuarioDTO updatedUsuarioDTO = usuarioMapper.toDTO(updatedUsuario);

            return Response.ok(updatedUsuarioDTO).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao atualizar usuário: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUsuario(@PathParam("id") Integer id) {
        try {
            boolean deleted = usuarioRepository.deleteById(id);

            if (deleted) {
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Usuário não encontrado")
                        .build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao excluir usuário: " + e.getMessage())
                    .build();
        }
    }
}
