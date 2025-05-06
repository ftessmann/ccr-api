package br.com.ccr.resources;

import br.com.ccr.entities.Endereco;
import br.com.ccr.repositories.EnderecoRepository;

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

@Path("/enderecos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EnderecoResource {

    private static final Logger log = LogManager.getLogger(EnderecoResource.class);

    @Inject
    EnderecoRepository enderecoRepository;

    @GET
    public Response listar(
            @QueryParam("cep") String cep,
            @QueryParam("cidade") String cidade
    ) {
        try {
            List<Endereco> enderecos;

            if (cep != null && !cep.isEmpty()) {
                log.info("Buscando endereços por CEP: {}", cep);
                enderecos = enderecoRepository.buscarPorCep(cep);
            } else if (cidade != null && !cidade.isEmpty()) {
                log.info("Buscando endereços por cidade: {}", cidade);
                enderecos = enderecoRepository.buscarPorCidade(cidade);
            } else {
                log.info("Listando todos os endereços");
                enderecos = enderecoRepository.listarTodos();
            }

            return Response.ok(enderecos).build();
        } catch (Exception e) {
            log.error("Erro ao listar endereços", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao listar endereços: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id) {
        try {
            log.info("Buscando endereço por ID: {}", id);
            Optional<Endereco> endereco = enderecoRepository.buscarPorId(id);

            if (endereco.isPresent()) {
                return Response.ok(endereco.get()).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Endereço não encontrado com ID: " + id)
                        .build();
            }
        } catch (Exception e) {
            log.error("Erro ao buscar endereço por ID", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar endereço: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Transactional
    public Response adicionar(Endereco endereco) {
        try {
            log.info("Adicionando novo endereço");
            Endereco novoEndereco = enderecoRepository.salvar(endereco);

            return Response.created(
                            UriBuilder.fromResource(EnderecoResource.class)
                                    .path(String.valueOf(novoEndereco.getId()))
                                    .build())
                    .entity(novoEndereco)
                    .build();
        } catch (Exception e) {
            log.error("Erro ao adicionar endereço", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao adicionar endereço: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response atualizar(@PathParam("id") int id, Endereco endereco) {
        try {
            log.info("Atualizando endereço com ID: {}", id);
            Optional<Endereco> enderecoExistente = enderecoRepository.buscarPorId(id);

            if (enderecoExistente.isPresent()) {
                enderecoRepository.atualizar(id, endereco);
                return Response.ok(endereco).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Endereço não encontrado com ID: " + id)
                        .build();
            }
        } catch (Exception e) {
            log.error("Erro ao atualizar endereço", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao atualizar endereço: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response remover(@PathParam("id") int id) {
        try {
            log.info("Removendo endereço com ID: {}", id);
            Optional<Endereco> endereco = enderecoRepository.buscarPorId(id);

            if (endereco.isPresent()) {
                enderecoRepository.remover(id);
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Endereço não encontrado com ID: " + id)
                        .build();
            }
        } catch (Exception e) {
            log.error("Erro ao remover endereço", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao remover endereço: " + e.getMessage())
                    .build();
        }
    }
}
