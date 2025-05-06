package br.com.ccr.resources;

import br.com.ccr.entities.Trem;
import br.com.ccr.entities.Usuario;
import br.com.ccr.entities.Vagao;
import br.com.ccr.repositories.TremRepository;
import br.com.ccr.repositories.UsuarioRepository;
import br.com.ccr.repositories.VagaoRepository;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;

@Path("/trens")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TremResource {

    private final TremRepository tremRepository;
    private final UsuarioRepository usuarioRepository;
    private final VagaoRepository vagaoRepository;

    @Inject
    public TremResource(TremRepository tremRepository,
                        UsuarioRepository usuarioRepository,
                        VagaoRepository vagaoRepository) {
        this.tremRepository = tremRepository;
        this.usuarioRepository = usuarioRepository;
        this.vagaoRepository = vagaoRepository;
    }

    @GET
    public Response listarTodos() {
        List<Trem> trens = tremRepository.listarTodos();
        return Response.ok(trens).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id) {
        Optional<Trem> trem = tremRepository.buscarPorId(id);
        return trem
                .map(t -> Response.ok(t).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Response criar(Trem trem) {
        tremRepository.salvar(trem);
        return Response.status(Response.Status.CREATED).entity(trem).build();
    }

    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") int id, Trem trem) {
        if (tremRepository.buscarPorId(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        trem.setId(id);
        tremRepository.salvar(trem);
        return Response.ok(trem).build();
    }

    @DELETE
    @Path("/{id}")
    public Response remover(@PathParam("id") int id) {
        if (tremRepository.buscarPorId(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        tremRepository.remover(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/modelo/{modelo}")
    public Response buscarPorModelo(@PathParam("modelo") String modelo) {
        List<Trem> trens = tremRepository.buscarPorModelo(modelo);
        return Response.ok(trens).build();
    }

    @GET
    @Path("/linha/{linhaId}")
    public Response buscarPorLinha(@PathParam("linhaId") int linhaId) {
        List<Trem> trens = tremRepository.buscarPorLinha(linhaId);
        return Response.ok(trens).build();
    }

    @POST
    @Path("/{tremId}/condutores/{usuarioId}")
    public Response adicionarCondutor(@PathParam("tremId") int tremId, @PathParam("usuarioId") int usuarioId) {
        Optional<Trem> tremOpt = tremRepository.buscarPorId(tremId);
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorId(usuarioId);

        if (tremOpt.isEmpty() || usuarioOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        tremRepository.adicionarCondutor(tremOpt.get(), usuarioOpt.get());
        return Response.status(Response.Status.CREATED).build();
    }

    @DELETE
    @Path("/{tremId}/condutores/{usuarioId}")
    public Response removerCondutor(@PathParam("tremId") int tremId, @PathParam("usuarioId") int usuarioId) {
        Optional<Trem> tremOpt = tremRepository.buscarPorId(tremId);
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorId(usuarioId);

        if (tremOpt.isEmpty() || usuarioOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        tremRepository.removerCondutor(tremOpt.get(), usuarioOpt.get());
        return Response.noContent().build();
    }

    @POST
    @Path("/{tremId}/vagoes")
    public Response adicionarVagao(@PathParam("tremId") int tremId, Vagao vagao) {
        Optional<Trem> tremOpt = tremRepository.buscarPorId(tremId);

        if (tremOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        tremRepository.adicionarVagao(tremOpt.get(), vagao);
        return Response.status(Response.Status.CREATED).entity(vagao).build();
    }
}
