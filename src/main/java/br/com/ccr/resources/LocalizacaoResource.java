package br.com.ccr.resources;

import br.com.ccr.entities.Localizacao;
import br.com.ccr.repositories.LocalizacaoRepository;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;

@Path("/localizacoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LocalizacaoResource {

    private final LocalizacaoRepository localizacaoRepository;

    @Inject
    public LocalizacaoResource(LocalizacaoRepository localizacaoRepository) {
        this.localizacaoRepository = localizacaoRepository;
    }

    @GET
    public Response listarTodas() {
        List<Localizacao> localizacoes = localizacaoRepository.listarTodos();
        return Response.ok(localizacoes).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id) {
        Optional<Localizacao> localizacao = localizacaoRepository.buscarPorId(id);
        return localizacao
                .map(l -> Response.ok(l).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Response criar(Localizacao localizacao) {
        localizacaoRepository.salvar(localizacao);
        return Response.status(Response.Status.CREATED).entity(localizacao).build();
    }

    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") int id, Localizacao localizacao) {
        if (localizacaoRepository.buscarPorId(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        localizacao.setId(id);
        localizacaoRepository.salvar(localizacao);
        return Response.ok(localizacao).build();
    }

    @DELETE
    @Path("/{id}")
    public Response remover(@PathParam("id") int id) {
        if (localizacaoRepository.buscarPorId(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        localizacaoRepository.remover(id);
        return Response.noContent().build();
    }
}
