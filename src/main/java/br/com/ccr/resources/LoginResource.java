package br.com.ccr.resources;

import br.com.ccr.dtos.LoginDTO;
import br.com.ccr.dtos.TokenDTO;
import br.com.ccr.services.AuthService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Optional;

@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginResource {

    @Inject
    AuthService authService;

    @POST
    public Response login(LoginDTO loginDTO) {
        Optional<TokenDTO> tokenOpt = authService.autenticar(loginDTO);

        if (tokenOpt.isPresent()) {
            return Response.ok(tokenOpt.get()).build();
        }

        return Response.status(Response.Status.UNAUTHORIZED)
                .entity("Credenciais inválidas")
                .build();
    }
}
