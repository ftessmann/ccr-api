package br.com.ccr.resources;

import br.com.ccr.dtos.LoginDTO;
import br.com.ccr.dtos.TokenDTO;
import br.com.ccr.dtos.UsuarioDTO;
import br.com.ccr.entities.Usuario;
import br.com.ccr.mappers.UsuarioMapper;
import br.com.ccr.services.AuthService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginResource {

    @Inject
    AuthService authService;

    @Inject
    UsuarioMapper usuarioMapper;

    @POST
    public Response login(LoginDTO loginDTO) {
        try {
            Optional<TokenDTO> tokenOpt = authService.autenticar(loginDTO);

            if (tokenOpt.isPresent()) {
                TokenDTO tokenDTO = tokenOpt.get();

                Optional<Usuario> usuarioOpt = authService.getUsuarioAutenticado(loginDTO.getEmail());

                if (usuarioOpt.isPresent()) {
                    UsuarioDTO usuarioDTO = usuarioMapper.toDTO(usuarioOpt.get());

                    Map<String, Object> response = new HashMap<>();
                    response.put("token", tokenDTO.getToken());
                    response.put("tipo", tokenDTO.getTipo());
                    response.put("usuario", usuarioDTO);

                    return Response.ok(response).build();
                }

                return Response.ok(tokenDTO).build();
            }

            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Credenciais inválidas")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao realizar login: " + e.getMessage())
                    .build();
        }
    }
}
