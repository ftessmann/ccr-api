package br.com.ccr.resources;

import br.com.ccr.entities.Cargo;
import br.com.ccr.repositories.CargoRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/cargos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CargoResource {

    @Inject
    CargoRepository cargoRepository;

    @GET
    @Path("/{id}")
    public Response getCargoPorId(@PathParam("id") int id) {
        Cargo cargo = cargoRepository.getCargoById(id);
        if (cargo == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(cargo).build();
    }

    @GET
    @Path("/id/{nome}")
    public Response getIdPorCargo(@PathParam("nome") String nomeCargo) {
        try {
            Cargo cargo = Cargo.valueOf(nomeCargo.toUpperCase());
            int id = cargoRepository.getCargoId(cargo);
            return id != -1
                    ? Response.ok(id).build()
                    : Response.status(Response.Status.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Cargo inválido: " + nomeCargo)
                    .build();
        }
    }

    @GET
    @Path("/buscar")
    public Response buscarPorNome(@QueryParam("nome") String nome) {
        return cargoRepository.buscarPorNome(nome.toUpperCase())
                .map(cargo -> Response.ok(cargo).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    public Response listarTodos() {
        return Response.ok(cargoRepository.cargoIdMap.keySet()).build();
    }
}
