package br.com.ccr.resources;

import br.com.ccr.entities.Estacao;
import br.com.ccr.entities.Linha;
import br.com.ccr.repositories.EstacaoRepository;
import br.com.ccr.repositories.LinhaRepository;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;

@Path("/linhas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LinhaResource {

    private final LinhaRepository linhaRepository;
    private final EstacaoRepository estacaoRepository;

    @jakarta.inject.Inject
    public LinhaResource(LinhaRepository linhaRepository, EstacaoRepository estacaoRepository) {
        this.linhaRepository = linhaRepository;
        this.estacaoRepository = estacaoRepository;
    }

    @GET
    public Response listarTodas() {
        List<Linha> linhas = linhaRepository.listarTodos();
        return Response.ok(linhas).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id) {
        Optional<Linha> linha = linhaRepository.buscarPorId(id);
        return linha
                .map(l -> Response.ok(l).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Response criar(Linha linha) {
        linhaRepository.salvar(linha);
        return Response.status(Response.Status.CREATED).entity(linha).build();
    }

    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") int id, Linha linha) {
        if (linhaRepository.buscarPorId(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        linha.setId(id);
        linhaRepository.salvar(linha);
        return Response.ok(linha).build();
    }

    @DELETE
    @Path("/{id}")
    public Response remover(@PathParam("id") int id) {
        if (linhaRepository.buscarPorId(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        linhaRepository.remover(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/nome/{nome}")
    public Response buscarPorNome(@PathParam("nome") String nome) {
        Optional<Linha> linha = linhaRepository.buscarPorNome(nome);
        return linha
                .map(l -> Response.ok(l).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("/{id}/estacoes")
    public Response listarEstacoes(@PathParam("id") int id) {
        Optional<Linha> linha = linhaRepository.buscarPorId(id);
        if (linha.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(linha.get().getEstacoes()).build();
    }

    @POST
    @Path("/{linhaId}/estacoes/{estacaoId}")
    public Response adicionarEstacao(
            @PathParam("linhaId") int linhaId,
            @PathParam("estacaoId") int estacaoId,
            @QueryParam("ordem") int ordem) {

        Optional<Linha> linhaOpt = linhaRepository.buscarPorId(linhaId);
        Optional<Estacao> estacaoOpt = estacaoRepository.buscarPorId(estacaoId);

        if (linhaOpt.isEmpty() || estacaoOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        linhaRepository.adicionarEstacao(linhaOpt.get(), estacaoOpt.get(), ordem);
        return Response.status(Response.Status.CREATED).build();
    }

    @DELETE
    @Path("/{linhaId}/estacoes/{estacaoId}")
    public Response removerEstacao(
            @PathParam("linhaId") int linhaId,
            @PathParam("estacaoId") int estacaoId) {

        Optional<Linha> linhaOpt = linhaRepository.buscarPorId(linhaId);
        Optional<Estacao> estacaoOpt = estacaoRepository.buscarPorId(estacaoId);

        if (linhaOpt.isEmpty() || estacaoOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        linhaRepository.removerEstacao(linhaOpt.get(), estacaoOpt.get());
        return Response.noContent().build();
    }

    @PUT
    @Path("/{id}/estacoes/reordenar")
    public Response reordenarEstacoes(@PathParam("id") int id, List<Estacao> novaOrdem) {
        Optional<Linha> linhaOpt = linhaRepository.buscarPorId(id);

        if (linhaOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        linhaRepository.reordenarEstacoes(linhaOpt.get(), novaOrdem);
        return Response.ok().build();
    }

    @GET
    @Path("/{id}/trens")
    public Response listarTrens(@PathParam("id") int id) {
        Optional<Linha> linha = linhaRepository.buscarPorId(id);
        if (linha.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(linha.get().getTrens()).build();
    }
}
