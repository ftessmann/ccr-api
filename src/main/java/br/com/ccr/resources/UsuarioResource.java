package br.com.ccr.resources;

import br.com.ccr.dtos.AlterarSenhaDTO;
import br.com.ccr.dtos.CredenciaisDTO;
import br.com.ccr.dtos.UsuarioDTO;
import br.com.ccr.entities.Cargo;
import br.com.ccr.entities.Endereco;
import br.com.ccr.entities.Usuario;
import br.com.ccr.repositories.EnderecoRepository;
import br.com.ccr.repositories.UsuarioRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    @Inject
    UsuarioRepository usuarioRepository;

    @Inject
    EnderecoRepository enderecoRepository;

    @Inject
    JsonWebToken jwt;

    @GET
    @RolesAllowed({"ADMIN", "GERENTE"})
    public Response listar(
            @QueryParam("cargo") String cargo,
            @QueryParam("nome") String nome,
            @QueryParam("email") String email,
            @QueryParam("cpf") String cpf,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("limit") @DefaultValue("20") int limit
    ) {
        List<Usuario> usuarios = usuarioRepository.listarTodos();

        if (cargo != null && !cargo.isEmpty()) {
            try {
                Cargo cargoEnum = Cargo.valueOf(cargo.toUpperCase());
                usuarios = usuarios.stream()
                        .filter(u -> u.getCargo() != null && u.getCargo().equals(cargoEnum))
                        .toList();
            } catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Cargo inválido: " + cargo)
                        .build();
            }
        }

        if (nome != null && !nome.isEmpty()) {
            usuarios = usuarios.stream()
                    .filter(u -> u.getNome() != null && u.getNome().contains(nome))
                    .toList();
        }

        if (email != null && !email.isEmpty()) {
            usuarios = usuarios.stream()
                    .filter(u -> u.getEmail() != null && u.getEmail().equals(email))
                    .toList();
        }

        if (cpf != null && !cpf.isEmpty()) {
            usuarios = usuarios.stream()
                    .filter(u -> u.getCpf() != null && u.getCpf().equals(cpf))
                    .toList();
        }

        int totalElements = usuarios.size();

        int fromIndex = page * limit;
        if (fromIndex > totalElements) {
            return Response.ok(List.of())
                    .header("X-Total-Count", totalElements)
                    .header("X-Page", page)
                    .header("X-Size", limit)
                    .header("X-Total-Pages", (int) Math.ceil((double) totalElements / limit))
                    .build();
        }

        int toIndex = Math.min(fromIndex + limit, totalElements);
        List<Usuario> paginatedUsuarios = usuarios.subList(fromIndex, toIndex);

        return Response.ok(paginatedUsuarios)
                .header("X-Total-Count", totalElements)
                .header("X-Page", page)
                .header("X-Size", limit)
                .header("X-Total-Pages", (int) Math.ceil((double) totalElements / limit))
                .build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id, @Context SecurityContext securityContext) {
        if (!temPermissaoParaAcessarUsuario(id, securityContext)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        Optional<Usuario> usuario = usuarioRepository.buscarPorId(id);
        if (usuario.isPresent()) {
            return Response.ok(usuario.get()).build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @RolesAllowed({"ADMIN", "MANAGER"})
    public Response criar(UsuarioDTO usuarioDTO) {
        try {
            Usuario usuario = new Usuario();
            usuario.setNome(usuarioDTO.getNome());
            usuario.setCpf(usuarioDTO.getCpf());
            usuario.setEmail(usuarioDTO.getEmail());
            usuario.setSenha(usuarioDTO.getSenha());
            usuario.setTelefone(usuarioDTO.getTelefone());
            usuario.setCargo(usuarioDTO.getCargo());
            usuario.setSetor(usuarioDTO.getSetor());

            usuario.setCreatedAt(LocalDateTime.now());

            if (usuarioDTO.getEnderecoId() != 0) {
                Endereco endereco = new Endereco();
                endereco.setId(usuarioDTO.getEnderecoId());
                usuario.setEndereco(endereco);
            }

            Usuario usuarioSalvo = usuarioRepository.salvar(usuario);
            if (usuarioSalvo == null) {
                return Response.status(422)
                        .entity("Erro ao criar usuário: entidade inválida")
                        .build();
            }
            return Response.status(Response.Status.CREATED).entity(usuarioSalvo).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") int id, Usuario usuario, @Context SecurityContext securityContext) {
        if (!temPermissaoParaAcessarUsuario(id, securityContext)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        if (usuarioRepository.buscarPorId(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        usuarioRepository.atualizar(id, usuario);
        return Response.ok(usuario).build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({"ADMIN", "GERENTE"})
    public Response remover(@PathParam("id") int id) {
        if (usuarioRepository.buscarPorId(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        usuarioRepository.remover(id);
        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}/senha")
    public Response alterarSenha(@PathParam("id") int id, AlterarSenhaDTO senhaDTO, @Context SecurityContext securityContext) {
        if (!temPermissaoParaAcessarUsuario(id, securityContext)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        boolean sucesso = usuarioRepository.alterarSenha(id, senhaDTO.getSenhaAtual(), senhaDTO.getNovaSenha());
        if (sucesso) {
            return Response.ok().build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    private boolean temPermissaoParaAcessarUsuario(int id, SecurityContext securityContext) {
        if (securityContext.isUserInRole("ADMIN") || securityContext.isUserInRole("GERENTE")) {
            return true;
        }

        try {
            int usuarioId = Integer.parseInt(jwt.getClaim("id").toString());
            return usuarioId == id;
        } catch (Exception e) {
            return false;
        }
    }
}
