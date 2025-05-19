package br.com.ccr.resources;

import br.com.ccr.dtos.VagaoDTO;
import br.com.ccr.entities.Vagao;
import br.com.ccr.mappers.VagaoMapper;
import br.com.ccr.repositories.VagaoRepository;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/vagoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VagaoResource {

    @Inject
    private VagaoRepository vagaoRepository;

    @Inject
    private VagaoMapper vagaoMapper;

    @POST
    public Response createVagao(VagaoDTO vagaoDTO) {
        try {
            Vagao vagao = vagaoMapper.toEntity(vagaoDTO);

            Vagao savedVagao = vagaoRepository.save(vagao);

            VagaoDTO savedVagaoDTO = vagaoMapper.toDTO(savedVagao);

            return Response.status(Response.Status.CREATED)
                    .entity(savedVagaoDTO)
                    .build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao criar vagão: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getVagaoById(@PathParam("id") Integer id) {
        try {
            Optional<Vagao> vagao = vagaoRepository.findById(id);

            if (vagao.isPresent()) {
                VagaoDTO vagaoDTO = vagaoMapper.toDTO(vagao.get());
                return Response.ok(vagaoDTO).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Vagão não encontrado")
                        .build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar vagão: " + e.getMessage())
                    .build();
        }
    }

    @GET
    public Response getAllVagoes() {
        try {
            List<Vagao> vagoes = vagaoRepository.findAll();
            List<VagaoDTO> vagaoDTOs = vagoes.stream()
                    .map(vagaoMapper::toDTO)
                    .collect(Collectors.toList());

            return Response.ok(vagaoDTOs).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar vagões: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateVagao(@PathParam("id") Integer id, VagaoDTO vagaoDTO) {
        try {
            Optional<Vagao> existingVagao = vagaoRepository.findById(id);

            if (existingVagao.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Vagão não encontrado")
                        .build();
            }

            Vagao vagao = vagaoMapper.toEntity(vagaoDTO);
            vagao.setId(id);

            Vagao updatedVagao = vagaoRepository.update(vagao);

            VagaoDTO updatedVagaoDTO = vagaoMapper.toDTO(updatedVagao);

            return Response.ok(updatedVagaoDTO).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao atualizar vagão: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteVagao(@PathParam("id") Integer id) {
        try {
            boolean deleted = vagaoRepository.deleteById(id);

            if (deleted) {
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Vagão não encontrado")
                        .build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao excluir vagão: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/numeracao/{numeracao}")
    public Response getVagoesByNumeracao(@PathParam("numeracao") String numeracao) {
        try {
            List<Vagao> vagoes = vagaoRepository.findByNumeracao(numeracao);
            List<VagaoDTO> vagaoDTOs = vagoes.stream()
                    .map(vagaoMapper::toDTO)
                    .collect(Collectors.toList());

            return Response.ok(vagaoDTOs).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar vagões por numeração: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/trem/{tremId}")
    public Response getVagoesByTremId(@PathParam("tremId") Integer tremId) {
        try {
            List<Vagao> vagoes = vagaoRepository.findByTremId(tremId);
            List<VagaoDTO> vagaoDTOs = vagoes.stream()
                    .map(vagaoMapper::toDTO)
                    .collect(Collectors.toList());

            return Response.ok(vagaoDTOs).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar vagões por trem: " + e.getMessage())
                    .build();
        }
    }
}
