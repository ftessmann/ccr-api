package br.com.ccr.resources;

import br.com.ccr.entities.Estacao;
import br.com.ccr.entities.Plataforma;
import br.com.ccr.repositories.EstacaoRepository;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;

@Path("/estacoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EstacaoResource {

    private final EstacaoRepository estacaoRepository;

    @Inject
    public EstacaoResource(EstacaoRepository estacaoRepository) {
        this.estacaoRepository = estacaoRepository;
    }

    @GET
    public Response listarTodas() {
        List<Estacao> estacoes = estacaoRepository.listarTodos();
        return Response.ok(estacoes).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id) {
        Optional<Estacao> estacao = estacaoRepository.buscarPorId(id);
        return estacao
                .map(e -> Response.ok(e).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Response criar(Estacao estacao) {
        estacaoRepository.salvar(estacao);
        return Response.status(Response.Status.CREATED).entity(estacao).build();
    }

    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") int id, Estacao estacao) {
        if (estacaoRepository.buscarPorId(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        estacao.setId(id);
        estacaoRepository.salvar(estacao);
        return Response.ok(estacao).build();
    }

    @DELETE
    @Path("/{id}")
    public Response remover(@PathParam("id") int id) {
        if (estacaoRepository.buscarPorId(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        estacaoRepository.remover(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/nome/{nome}")
    public Response buscarPorNome(@PathParam("nome") String nome) {
        Optional<Estacao> estacao = estacaoRepository.buscarPorNome(nome);
        return estacao
                .map(e -> Response.ok(e).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/busca")
    public Response buscarPorNomeParcial(@QueryParam("nome") String nome) {
        List<Estacao> estacoes = estacaoRepository.buscarPorNomeParcial(nome);
        return Response.ok(estacoes).build();
    }

    @GET
    @Path("/endereco/{enderecoId}")
    public Response buscarPorEndereco(@PathParam("enderecoId") int enderecoId) {
        List<Estacao> estacoes = estacaoRepository.buscarPorEndereco(enderecoId);
        return Response.ok(estacoes).build();
    }

    @GET
    @Path("/linha/{linhaId}")
    public Response buscarPorLinha(@PathParam("linhaId") int linhaId) {
        List<Estacao> estacoes = estacaoRepository.buscarPorLinha(linhaId);
        return Response.ok(estacoes).build();
    }

    @POST
    @Path("/{estacaoId}/plataformas")
    public Response adicionarPlataforma(@PathParam("estacaoId") int estacaoId, Plataforma plataforma) {
        Optional<Estacao> estacaoOpt = estacaoRepository.buscarPorId(estacaoId);
        if (estacaoOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        Estacao estacao = estacaoOpt.get();
        estacaoRepository.adicionarPlataforma(estacao, plataforma);
        return Response.status(Response.Status.CREATED).entity(plataforma).build();
    }
}
