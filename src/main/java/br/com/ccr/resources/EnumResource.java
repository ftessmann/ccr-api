package br.com.ccr.resources;

import br.com.ccr.entities.Cargo;
import br.com.ccr.entities.Gravidade;
import br.com.ccr.entities.Setor;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/enums")
@Produces(MediaType.APPLICATION_JSON)
public class EnumResource {

    @GET
    @Path("/cargos")
    public Response getCargos() {
        return Response.ok(Cargo.values()).build();
    }

    @GET
    @Path("/setores")
    public Response getSetores() {
        return Response.ok(Setor.values()).build();
    }

    @GET
    @Path("/gravidades")
    public Response getGravidades() {
        return Response.ok(Gravidade.values()).build();
    }
}
