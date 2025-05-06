package br.com.ccr.resources;

import br.com.ccr.entities.Plataforma;
import br.com.ccr.repositories.EstacaoRepository;
import br.com.ccr.repositories.PlataformaRepository;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;

@Path("/plataformas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PlataformaResource {

    private final PlataformaRepository plataformaRepository;
    private final EstacaoRepository estacaoRepository;

    @Inject
    public PlataformaResource(PlataformaRepository plataformaRepository, EstacaoRepository estacaoRepository) {
        this.plataformaRepository = plataformaRepository;
        this.estacaoRepository = estacaoRepository;
    }

    @GET
    public Response listarTodas() {
        List<Plataforma> plataformas = plataformaRepository.listarTodos();
        return Response.ok(plataformas).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id) {
        Optional<Plataforma> plataforma = plataformaRepository.buscarPorId(id);
        return plataforma
                .map(p -> Response.ok(p).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Response criar(Plataforma plataforma) {
        plataformaRepository.salvar(plataforma);
        return Response.status(Response.Status.CREATED).entity(plataforma).build();
    }

    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") int id, Plataforma plataforma) {
        if (plataformaRepository.buscarPorId(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        plataforma.setId(id);
        plataformaRepository.salvar(plataforma);
        return Response.ok(plataforma).build();
    }

    @DELETE
    @Path("/{id}")
    public Response remover(@PathParam("id") int id) {
        if (plataformaRepository.buscarPorId(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        plataformaRepository.remover(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/numero/{numero}")
    public Response buscarPorNumero(@PathParam("numero") String numero) {
        List<Plataforma> plataformas = plataformaRepository.buscarPorNumero(numero);
        return Response.ok(plataformas).build();
    }

    @GET
    @Path("/estacao/{estacaoId}")
    public Response buscarPorEstacao(@PathParam("estacaoId") int estacaoId) {
        List<Plataforma> plataformas = plataformaRepository.buscarPorEstacao(estacaoId);
        return Response.ok(plataformas).build();
    }
}
