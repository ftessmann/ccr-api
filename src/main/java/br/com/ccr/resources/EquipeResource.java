package br.com.ccr.resources;

import br.com.ccr.dtos.EquipeDTO;
import br.com.ccr.entities.Equipe;
import br.com.ccr.entities.Setor;
import br.com.ccr.entities.Usuario;
import br.com.ccr.mappers.EquipeMapper;
import br.com.ccr.repositories.EquipeRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/equipes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EquipeResource {

    @Inject
    private EquipeRepository equipeRepository;

    @Inject
    private EquipeMapper equipeMapper;

    @POST
    public Response createEquipe(EquipeDTO equipeDTO) {
        try {
            Equipe equipe = equipeMapper.toEntity(equipeDTO);

            Equipe savedEquipe = equipeRepository.save(equipe);

            EquipeDTO savedEquipeDTO = equipeMapper.toDTO(savedEquipe);

            return Response.status(Response.Status.CREATED)
                    .entity(savedEquipeDTO)
                    .build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao criar equipe: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getEquipeById(@PathParam("id") Integer id) {
        try {
            Optional<Equipe> equipe = equipeRepository.findById(id);

            if (equipe.isPresent()) {
                EquipeDTO equipeDTO = equipeMapper.toDTO(equipe.get());
                return Response.ok(equipeDTO).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Equipe não encontrada")
                        .build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar equipe: " + e.getMessage())
                    .build();
        }
    }

    @GET
    public Response getAllEquipes() {
        try {
            List<Equipe> equipes = equipeRepository.findAll();
            List<EquipeDTO> equipeDTOs = equipes.stream()
                    .map(equipeMapper::toDTO)
                    .collect(Collectors.toList());

            return Response.ok(equipeDTOs).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar equipes: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateEquipe(@PathParam("id") Integer id, EquipeDTO equipeDTO) {
        try {
            Optional<Equipe> existingEquipe = equipeRepository.findById(id);

            if (!existingEquipe.isPresent()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Equipe não encontrada")
                        .build();
            }

            // Converter DTO para entidade
            Equipe equipe = equipeMapper.toEntity(equipeDTO);
            equipe.setId(id);

            // Atualizar no banco
            Equipe updatedEquipe = equipeRepository.update(equipe);

            // Converter entidade atualizada para DTO
            EquipeDTO updatedEquipeDTO = equipeMapper.toDTO(updatedEquipe);

            return Response.ok(updatedEquipeDTO).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao atualizar equipe: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteEquipe(@PathParam("id") Integer id) {
        try {
            boolean deleted = equipeRepository.deleteById(id);

            if (deleted) {
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Equipe não encontrada")
                        .build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao excluir equipe: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/nome/{nome}")
    public Response getEquipesByNome(@PathParam("nome") String nome) {
        try {
            List<Equipe> equipes = equipeRepository.findByNome(nome);
            List<EquipeDTO> equipeDTOs = equipes.stream()
                    .map(equipeMapper::toDTO)
                    .collect(Collectors.toList());

            return Response.ok(equipeDTOs).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar equipes por nome: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/setor/{setor}")
    public Response getEquipesBySetor(@PathParam("setor") String setorStr) {
        try {
            Setor setor = Setor.valueOf(setorStr.toUpperCase());
            List<Equipe> equipes = equipeRepository.findBySetor(setor);
            List<EquipeDTO> equipeDTOs = equipes.stream()
                    .map(equipeMapper::toDTO)
                    .collect(Collectors.toList());

            return Response.ok(equipeDTOs).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Setor inválido: " + setorStr)
                    .build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar equipes por setor: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/base/{baseId}")
    public Response getEquipesByBaseId(@PathParam("baseId") Integer baseId) {
        try {
            List<Equipe> equipes = equipeRepository.findByBaseId(baseId);
            List<EquipeDTO> equipeDTOs = equipes.stream()
                    .map(equipeMapper::toDTO)
                    .collect(Collectors.toList());

            return Response.ok(equipeDTOs).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar equipes por base: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/integrante/{integranteId}")
    public Response getEquipesByIntegranteId(@PathParam("integranteId") Integer integranteId) {
        try {
            List<Equipe> equipes = equipeRepository.findByIntegranteId(integranteId);
            List<EquipeDTO> equipeDTOs = equipes.stream()
                    .map(equipeMapper::toDTO)
                    .collect(Collectors.toList());

            return Response.ok(equipeDTOs).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar equipes por integrante: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/{id}/integrantes")
    public Response addIntegranteToEquipe(@PathParam("id") Integer equipeId, Integer integranteId) {
        try {
            Optional<Equipe> equipeOpt = equipeRepository.findById(equipeId);

            if (!equipeOpt.isPresent()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Equipe não encontrada")
                        .build();
            }

            Equipe equipe = equipeOpt.get();

            ArrayList<Usuario> integrantes = equipe.getIntegrantes();
            if (integrantes == null) {
                integrantes = new ArrayList<>();
                equipe.setIntegrantes(integrantes);
            }

            Usuario novoIntegrante = new Usuario();
            novoIntegrante.setId(integranteId);
            integrantes.add(novoIntegrante);

            Equipe updatedEquipe = equipeRepository.update(equipe);

            EquipeDTO updatedEquipeDTO = equipeMapper.toDTO(updatedEquipe);

            return Response.ok(updatedEquipeDTO).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao adicionar integrante à equipe: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}/integrantes/{integranteId}")
    public Response removeIntegranteFromEquipe(@PathParam("id") Integer equipeId, @PathParam("integranteId") Integer integranteId) {
        try {
            Optional<Equipe> equipeOpt = equipeRepository.findById(equipeId);

            if (!equipeOpt.isPresent()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Equipe não encontrada")
                        .build();
            }

            Equipe equipe = equipeOpt.get();

            ArrayList<Usuario> integrantes = equipe.getIntegrantes();
            if (integrantes != null) {
                integrantes.removeIf(u -> u.getId().equals(integranteId));
            }

            Equipe updatedEquipe = equipeRepository.update(equipe);

            EquipeDTO updatedEquipeDTO = equipeMapper.toDTO(updatedEquipe);

            return Response.ok(updatedEquipeDTO).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao remover integrante da equipe: " + e.getMessage())
                    .build();
        }
    }
}
