package br.com.ccr.resources;

import br.com.ccr.entities.Vagao;
import br.com.ccr.repositories.TremRepository;
import br.com.ccr.repositories.VagaoRepository;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;

@Path("/vagoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VagaoResource {

    private final VagaoRepository vagaoRepository;
    private final TremRepository tremRepository;

    @Inject
    public VagaoResource(VagaoRepository vagaoRepository, TremRepository tremRepository) {
        this.vagaoRepository = vagaoRepository;
        this.tremRepository = tremRepository;
    }

    @GET
    public Response listarTodos() {
        List<Vagao> vagoes = vagaoRepository.listarTodos();
        return Response.ok(vagoes).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id) {
        Optional<Vagao> vagao = vagaoRepository.buscarPorId(id);
        return vagao
                .map(v -> Response.ok(v).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Response criar(Vagao vagao) {
        vagaoRepository.salvar(vagao);
        return Response.status(Response.Status.CREATED).entity(vagao).build();
    }

    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") int id, Vagao vagao) {
        if (vagaoRepository.buscarPorId(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        vagao.setId(id);
        vagaoRepository.salvar(vagao);
        return Response.ok(vagao).build();
    }

    @DELETE
    @Path("/{id}")
    public Response remover(@PathParam("id") int id) {
        if (vagaoRepository.buscarPorId(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        vagaoRepository.remover(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/numeracao/{numeracao}")
    public Response buscarPorNumeracao(@PathParam("numeracao") String numeracao) {
        List<Vagao> vagoes = vagaoRepository.buscarPorNumeracao(numeracao);
        return Response.ok(vagoes).build();
    }

    @GET
    @Path("/trem/{tremId}")
    public Response buscarPorTrem(@PathParam("tremId") int tremId) {
        List<Vagao> vagoes = vagaoRepository.buscarPorTrem(tremId);
        return Response.ok(vagoes).build();
    }
}
