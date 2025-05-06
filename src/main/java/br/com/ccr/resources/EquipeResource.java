package br.com.ccr.resources;

import br.com.ccr.entities.Equipe;
import br.com.ccr.entities.Usuario;
import br.com.ccr.repositories.EquipeRepository;
import br.com.ccr.repositories.UsuarioRepository;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

@Path("/equipes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EquipeResource {

    private static final Logger log = LogManager.getLogger(EquipeResource.class);

    @Inject
    EquipeRepository equipeRepository;

    @Inject
    UsuarioRepository usuarioRepository;

    @GET
    public Response listar(
            @QueryParam("nome") String nome,
            @QueryParam("localizacaoId") Integer localizacaoId,
            @QueryParam("estacaoId") Integer estacaoId
    ) {
        try {
            List<Equipe> equipes;

            if (nome != null && !nome.isEmpty()) {
                log.info("Buscando equipes por nome: {}", nome);
                equipes = equipeRepository.buscarPorNome(nome);
            } else if (localizacaoId != null) {
                log.info("Buscando equipes por localização: {}", localizacaoId);
                equipes = equipeRepository.buscarPorLocalizacao(localizacaoId);
            } else if (estacaoId != null) {
                log.info("Buscando equipes por estação base: {}", estacaoId);
                equipes = equipeRepository.buscarPorEstacaoBase(estacaoId);
            } else {
                log.info("Listando todas as equipes");
                equipes = equipeRepository.listarTodos();
            }

            return Response.ok(equipes).build();
        } catch (Exception e) {
            log.error("Erro ao listar equipes", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao listar equipes: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id) {
        try {
            Optional<Equipe> equipe = equipeRepository.buscarPorId(id);

            if (equipe.isPresent()) {
                return Response.ok(equipe.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Equipe não encontrada com ID: " + id)
                        .build();
            }
        } catch (Exception e) {
            log.error("Erro ao buscar equipe por ID", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar equipe: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Transactional
    public Response adicionar(Equipe equipe) {
        try {
            Equipe novaEquipe = equipeRepository.salvar(equipe);

            return Response.created(
                            UriBuilder.fromResource(EquipeResource.class)
                                    .path(String.valueOf(novaEquipe.getId()))
                                    .build())
                    .entity(novaEquipe)
                    .build();
        } catch (Exception e) {
            log.error("Erro ao adicionar equipe", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao adicionar equipe: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response atualizar(@PathParam("id") int id, Equipe equipe) {
        try {
            Optional<Equipe> equipeExistente = equipeRepository.buscarPorId(id);

            if (equipeExistente.isPresent()) {
                equipe.setId(id);
                equipeRepository.atualizar(id, equipe);
                return Response.ok(equipe).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Equipe não encontrada com ID: " + id)
                        .build();
            }
        } catch (Exception e) {
            log.error("Erro ao atualizar equipe", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao atualizar equipe: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response remover(@PathParam("id") int id) {
        try {
            Optional<Equipe> equipe = equipeRepository.buscarPorId(id);

            if (equipe.isPresent()) {
                equipeRepository.remover(id);
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Equipe não encontrada com ID: " + id)
                        .build();
            }
        } catch (Exception e) {
            log.error("Erro ao remover equipe", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao remover equipe: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/{equipeId}/integrantes/{usuarioId}")
    @Transactional
    public Response adicionarIntegrante(
            @PathParam("equipeId") int equipeId,
            @PathParam("usuarioId") int usuarioId
    ) {
        try {
            Optional<Equipe> equipeOpt = equipeRepository.buscarPorId(equipeId);
            Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorId(usuarioId);

            if (equipeOpt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Equipe não encontrada com ID: " + equipeId)
                        .build();
            }

            if (usuarioOpt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Usuário não encontrado com ID: " + usuarioId)
                        .build();
            }

            equipeRepository.adicionarIntegrante(equipeOpt.get(), usuarioOpt.get());
            return Response.ok().build();
        } catch (Exception e) {
            log.error("Erro ao adicionar integrante à equipe", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao adicionar integrante: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{equipeId}/integrantes/{usuarioId}")
    @Transactional
    public Response removerIntegrante(
            @PathParam("equipeId") int equipeId,
            @PathParam("usuarioId") int usuarioId
    ) {
        try {
            Optional<Equipe> equipeOpt = equipeRepository.buscarPorId(equipeId);
            Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorId(usuarioId);

            if (equipeOpt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Equipe não encontrada com ID: " + equipeId)
                        .build();
            }

            if (usuarioOpt.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Usuário não encontrado com ID: " + usuarioId)
                        .build();
            }

            equipeRepository.removerIntegrante(equipeOpt.get(), usuarioOpt.get());
            return Response.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao remover integrante da equipe", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao remover integrante: " + e.getMessage())
                    .build();
        }
    }
}
