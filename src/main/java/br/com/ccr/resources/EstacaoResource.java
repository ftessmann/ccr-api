package br.com.ccr.resources;

import br.com.ccr.dtos.EstacaoDTO;
import br.com.ccr.entities.Estacao;
import br.com.ccr.mappers.EstacaoMapper;
import br.com.ccr.repositories.EstacaoRepository;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/estacoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EstacaoResource {

    @Inject
    private EstacaoRepository estacaoRepository;

    @Inject
    private EstacaoMapper estacaoMapper;

    @POST
    public Response createEstacao(EstacaoDTO estacaoDTO) {
        try {
            Estacao estacao = estacaoMapper.toEntity(estacaoDTO);

            Estacao savedEstacao = estacaoRepository.save(estacao);

            EstacaoDTO savedEstacaoDTO = estacaoMapper.toDTO(savedEstacao);

            return Response.status(Response.Status.CREATED)
                    .entity(savedEstacaoDTO)
                    .build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao criar estação: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getEstacaoById(@PathParam("id") Integer id) {
        try {
            Optional<Estacao> estacao = estacaoRepository.findById(id);

            if (estacao.isPresent()) {
                EstacaoDTO estacaoDTO = estacaoMapper.toDTO(estacao.get());
                return Response.ok(estacaoDTO).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Estação não encontrada")
                        .build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar estação: " + e.getMessage())
                    .build();
        }
    }

    @GET
    public Response getAllEstacoes() {
        try {
            List<Estacao> estacoes = estacaoRepository.findAll();
            List<EstacaoDTO> estacaoDTOs = estacoes.stream()
                    .map(estacaoMapper::toDTO)
                    .collect(Collectors.toList());

            return Response.ok(estacaoDTOs).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar estações: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateEstacao(@PathParam("id") Integer id, EstacaoDTO estacaoDTO) {
        try {
            Optional<Estacao> existingEstacao = estacaoRepository.findById(id);

            if (!existingEstacao.isPresent()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Estação não encontrada")
                        .build();
            }

            Estacao estacao = estacaoMapper.toEntity(estacaoDTO);
            estacao.setId(id);

            Estacao updatedEstacao = estacaoRepository.update(estacao);

            EstacaoDTO updatedEstacaoDTO = estacaoMapper.toDTO(updatedEstacao);

            return Response.ok(updatedEstacaoDTO).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao atualizar estação: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteEstacao(@PathParam("id") Integer id) {
        try {
            boolean deleted = estacaoRepository.deleteById(id);

            if (deleted) {
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Estação não encontrada")
                        .build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao excluir estação: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/nome/{nome}")
    public Response getEstacoesByNome(@PathParam("nome") String nome) {
        try {
            List<Estacao> estacoes = estacaoRepository.findByNome(nome);
            List<EstacaoDTO> estacaoDTOs = estacoes.stream()
                    .map(estacaoMapper::toDTO)
                    .collect(Collectors.toList());

            return Response.ok(estacaoDTOs).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar estações por nome: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/linha/{linhaId}")
    public Response getEstacoesByLinhaId(@PathParam("linhaId") Integer linhaId) {
        try {
            List<Estacao> estacoes = estacaoRepository.findByLinhaId(linhaId);
            List<EstacaoDTO> estacaoDTOs = estacoes.stream()
                    .map(estacaoMapper::toDTO)
                    .collect(Collectors.toList());

            return Response.ok(estacaoDTOs).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar estações por linha: " + e.getMessage())
                    .build();
        }
    }
}
