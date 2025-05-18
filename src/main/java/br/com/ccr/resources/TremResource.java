package br.com.ccr.resources;

import br.com.ccr.dtos.TremDTO;
import br.com.ccr.entities.Trem;
import br.com.ccr.mappers.TremMapper;
import br.com.ccr.repositories.TremRepository;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/trens")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TremResource {

    @Inject
    private TremRepository tremRepository;

    @Inject
    private TremMapper tremMapper;

    @POST
    public Response createTrem(TremDTO tremDTO) {
        try {
            Trem trem = tremMapper.toEntity(tremDTO);

            Trem savedTrem = tremRepository.save(trem);

            TremDTO savedTremDTO = tremMapper.toDTO(savedTrem);

            return Response.status(Response.Status.CREATED)
                    .entity(savedTremDTO)
                    .build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao criar trem: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getTremById(@PathParam("id") Integer id) {
        try {
            Optional<Trem> trem = tremRepository.findById(id);

            if (trem.isPresent()) {
                TremDTO tremDTO = tremMapper.toDTO(trem.get());
                return Response.ok(tremDTO).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Trem não encontrado")
                        .build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar trem: " + e.getMessage())
                    .build();
        }
    }

    @GET
    public Response getAllTrens() {
        try {
            List<Trem> trens = tremRepository.findAll();
            List<TremDTO> tremDTOs = trens.stream()
                    .map(tremMapper::toDTO)
                    .collect(Collectors.toList());

            return Response.ok(tremDTOs).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar trens: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateTrem(@PathParam("id") Integer id, TremDTO tremDTO) {
        try {
            Optional<Trem> existingTrem = tremRepository.findById(id);

            if (!existingTrem.isPresent()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Trem não encontrado")
                        .build();
            }

            Trem trem = tremMapper.toEntity(tremDTO);
            trem.setId(id);

            Trem updatedTrem = tremRepository.update(trem);

            TremDTO updatedTremDTO = tremMapper.toDTO(updatedTrem);

            return Response.ok(updatedTremDTO).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao atualizar trem: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteTrem(@PathParam("id") Integer id) {
        try {
            boolean deleted = tremRepository.deleteById(id);

            if (deleted) {
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Trem não encontrado")
                        .build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao excluir trem: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/linha/{linhaId}")
    public Response getTrensByLinhaId(@PathParam("linhaId") Integer linhaId) {
        try {
            List<Trem> trens = tremRepository.findByLinhaId(linhaId);
            List<TremDTO> tremDTOs = trens.stream()
                    .map(tremMapper::toDTO)
                    .collect(Collectors.toList());

            return Response.ok(tremDTOs).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar trens por linha: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/estacao/{estacaoId}")
    public Response getTrensByEstacaoId(@PathParam("estacaoId") Integer estacaoId) {
        try {
            List<Trem> trens = tremRepository.findByEstacaoId(estacaoId);
            List<TremDTO> tremDTOs = trens.stream()
                    .map(tremMapper::toDTO)
                    .collect(Collectors.toList());

            return Response.ok(tremDTOs).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar trens por estação: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/condutor/{condutorId}")
    public Response getTrensByCondutorId(@PathParam("condutorId") Integer condutorId) {
        try {
            List<Trem> trens = tremRepository.findByCondutorId(condutorId);
            List<TremDTO> tremDTOs = trens.stream()
                    .map(tremMapper::toDTO)
                    .collect(Collectors.toList());

            return Response.ok(tremDTOs).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar trens por condutor: " + e.getMessage())
                    .build();
        }
    }
}
