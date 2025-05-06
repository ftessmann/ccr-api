package br.com.ccr.resources;

import br.com.ccr.entities.Equipe;
import br.com.ccr.entities.Incidente;
import br.com.ccr.repositories.EquipeRepository;
import br.com.ccr.repositories.IncidenteRepository;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Optional;

@Path("/incidentes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IncidenteResource {

    private final IncidenteRepository incidenteRepository;
    private final EquipeRepository equipeRepository;

    @Inject
    public IncidenteResource(IncidenteRepository incidenteRepository, EquipeRepository equipeRepository) {
        this.incidenteRepository = incidenteRepository;
        this.equipeRepository = equipeRepository;
    }

    @GET
    public Response listarTodos() {
        List<Incidente> incidentes = incidenteRepository.listarTodos();
        return Response.ok(incidentes).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id) {
        Optional<Incidente> incidente = incidenteRepository.buscarPorId(id);
        return incidente
                .map(i -> Response.ok(i).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Response criar(Incidente incidente) {
        incidenteRepository.salvar(incidente);
        return Response.status(Response.Status.CREATED).entity(incidente).build();
    }

    @PUT
    @Path("/{id}")
    public Response atualizar(@PathParam("id") int id, Incidente incidente) {
        if (incidenteRepository.buscarPorId(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        incidente.setId(id);
        incidenteRepository.salvar(incidente);
        return Response.ok(incidente).build();
    }

    @DELETE
    @Path("/{id}")
    public Response remover(@PathParam("id") int id) {
        if (incidenteRepository.buscarPorId(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        incidenteRepository.remover(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/gravidade/{gravidadeId}")
    public Response buscarPorGravidade(@PathParam("gravidadeId") int gravidadeId) {
        List<Incidente> incidentes = incidenteRepository.buscarPorGravidade(gravidadeId);
        return Response.ok(incidentes).build();
    }

    @GET
    @Path("/{id}/equipes")
    public Response listarEquipesDoIncidente(@PathParam("id") int id) {
        List<Equipe> equipes = incidenteRepository.listarEquipesDoIncidente(id);
        return Response.ok(equipes).build();
    }

    @POST
    @Path("/{incidenteId}/equipes/{equipeId}")
    public Response associarEquipe(@PathParam("incidenteId") int incidenteId, @PathParam("equipeId") int equipeId) {
        Optional<Incidente> incidenteOpt = incidenteRepository.buscarPorId(incidenteId);
        Optional<Equipe> equipeOpt = equipeRepository.buscarPorId(equipeId);

        if (incidenteOpt.isEmpty() || equipeOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        incidenteRepository.associarEquipe(incidenteOpt.get(), equipeOpt.get());
        return Response.status(Response.Status.CREATED).build();
    }

    @DELETE
    @Path("/{incidenteId}/equipes/{equipeId}")
    public Response desassociarEquipe(@PathParam("incidenteId") int incidenteId, @PathParam("equipeId") int equipeId) {
        Optional<Incidente> incidenteOpt = incidenteRepository.buscarPorId(incidenteId);
        Optional<Equipe> equipeOpt = equipeRepository.buscarPorId(equipeId);

        if (incidenteOpt.isEmpty() || equipeOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        incidenteRepository.desassociarEquipe(incidenteOpt.get(), equipeOpt.get());
        return Response.noContent().build();
    }
}
