package br.com.ccr.resources;

import br.com.ccr.dtos.IncidenteDTO;
import br.com.ccr.entities.Gravidade;
import br.com.ccr.entities.Incidente;
import br.com.ccr.mappers.IncidenteMapper;
import br.com.ccr.repositories.IncidenteRepository;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/incidentes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IncidenteResource {

    @Inject
    private IncidenteRepository incidenteRepository;

    @Inject
    private IncidenteMapper incidenteMapper;

    @POST
    public Response createIncidente(IncidenteDTO incidenteDTO) {
        try {
            Incidente incidente = incidenteMapper.toEntity(incidenteDTO);

            Incidente savedIncidente = incidenteRepository.save(incidente);

            IncidenteDTO savedIncidenteDTO = incidenteMapper.toDTO(savedIncidente);

            return Response.status(Response.Status.CREATED)
                    .entity(savedIncidenteDTO)
                    .build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao criar incidente: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getIncidenteById(@PathParam("id") Integer id) {
        try {
            Optional<Incidente> incidente = incidenteRepository.findById(id);

            if (incidente.isPresent()) {
                IncidenteDTO incidenteDTO = incidenteMapper.toDTO(incidente.get());
                return Response.ok(incidenteDTO).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Incidente não encontrado")
                        .build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar incidente: " + e.getMessage())
                    .build();
        }
    }

    @GET
    public Response getAllIncidentes() {
        try {
            List<Incidente> incidentes = incidenteRepository.findAll();
            List<IncidenteDTO> incidenteDTOs = incidentes.stream()
                    .map(incidenteMapper::toDTO)
                    .collect(Collectors.toList());

            return Response.ok(incidenteDTOs).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar incidentes: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateIncidente(@PathParam("id") Integer id, IncidenteDTO incidenteDTO) {
        try {
            Optional<Incidente> existingIncidente = incidenteRepository.findById(id);

            if (!existingIncidente.isPresent()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Incidente não encontrado")
                        .build();
            }

            Incidente incidente = incidenteMapper.toEntity(incidenteDTO);
            incidente.setId(id);

            Incidente updatedIncidente = incidenteRepository.update(incidente);

            IncidenteDTO updatedIncidenteDTO = incidenteMapper.toDTO(updatedIncidente);

            return Response.ok(updatedIncidenteDTO).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao atualizar incidente: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteIncidente(@PathParam("id") Integer id) {
        try {
            boolean deleted = incidenteRepository.deleteById(id);

            if (deleted) {
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Incidente não encontrado")
                        .build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao excluir incidente: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/gravidade/{gravidade}")
    public Response getIncidentesByGravidade(@PathParam("gravidade") String gravidadeStr) {
        try {
            Gravidade gravidade = Gravidade.valueOf(gravidadeStr.toUpperCase());
            List<Incidente> incidentes = incidenteRepository.findByGravidade(gravidade);
            List<IncidenteDTO> incidenteDTOs = incidentes.stream()
                    .map(incidenteMapper::toDTO)
                    .collect(Collectors.toList());

            return Response.ok(incidenteDTOs).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Gravidade inválida: " + gravidadeStr)
                    .build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar incidentes por gravidade: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/criador/{criadorId}")
    public Response getIncidentesByCriador(@PathParam("criadorId") Integer criadorId) {
        try {
            List<Incidente> incidentes = incidenteRepository.findByCriadorId(criadorId);
            List<IncidenteDTO> incidenteDTOs = incidentes.stream()
                    .map(incidenteMapper::toDTO)
                    .collect(Collectors.toList());

            return Response.ok(incidenteDTOs).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar incidentes por criador: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/status/{resolvido}")
    public Response getIncidentesByStatus(@PathParam("resolvido") boolean resolvido) {
        try {
            List<Incidente> incidentes = incidenteRepository.findByStatus(resolvido);
            List<IncidenteDTO> incidenteDTOs = incidentes.stream()
                    .map(incidenteMapper::toDTO)
                    .collect(Collectors.toList());

            return Response.ok(incidenteDTOs).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar incidentes por status: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}/resolver")
    public Response resolverIncidente(@PathParam("id") Integer id) {
        try {
            Optional<Incidente> existingIncidente = incidenteRepository.findById(id);

            if (!existingIncidente.isPresent()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Incidente não encontrado")
                        .build();
            }

            Incidente incidente = existingIncidente.get();
            incidente.setIsResolved(true);

            Incidente updatedIncidente = incidenteRepository.update(incidente);

            IncidenteDTO updatedIncidenteDTO = incidenteMapper.toDTO(updatedIncidente);

            return Response.ok(updatedIncidenteDTO).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao resolver incidente: " + e.getMessage())
                    .build();
        }
    }
}
