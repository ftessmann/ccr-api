package br.com.ccr.resources;

import br.com.ccr.dtos.LinhaDTO;
import br.com.ccr.entities.Linha;
import br.com.ccr.mappers.LinhaMapper;
import br.com.ccr.repositories.LinhaRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/linhas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LinhaResource {

    @Inject
    private LinhaRepository linhaRepository;

    @Inject
    private LinhaMapper linhaMapper;

    @POST
    public Response createLinha(LinhaDTO linhaDTO) {
        try {
            // Converter DTO para entidade
            Linha linha = linhaMapper.toEntity(linhaDTO);

            // Salvar no banco
            Linha savedLinha = linhaRepository.save(linha);

            // Converter entidade salva para DTO
            LinhaDTO savedLinhaDTO = linhaMapper.toDTO(savedLinha);

            return Response.status(Response.Status.CREATED)
                    .entity(savedLinhaDTO)
                    .build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao criar linha: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getLinhaById(@PathParam("id") Integer id) {
        try {
            Optional<Linha> linha = linhaRepository.findById(id);

            if (linha.isPresent()) {
                LinhaDTO linhaDTO = linhaMapper.toDTO(linha.get());
                return Response.ok(linhaDTO).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Linha não encontrada")
                        .build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar linha: " + e.getMessage())
                    .build();
        }
    }

    @GET
    public Response getAllLinhas() {
        try {
            List<Linha> linhas = linhaRepository.findAll();
            List<LinhaDTO> linhaDTOs = linhas.stream()
                    .map(linhaMapper::toDTO)
                    .collect(Collectors.toList());

            return Response.ok(linhaDTOs).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar linhas: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateLinha(@PathParam("id") Integer id, LinhaDTO linhaDTO) {
        try {
            Optional<Linha> existingLinha = linhaRepository.findById(id);

            if (!existingLinha.isPresent()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Linha não encontrada")
                        .build();
            }

            Linha linha = linhaMapper.toEntity(linhaDTO);
            linha.setId(id);

            Linha updatedLinha = linhaRepository.update(linha);

            LinhaDTO updatedLinhaDTO = linhaMapper.toDTO(updatedLinha);

            return Response.ok(updatedLinhaDTO).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao atualizar linha: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteLinha(@PathParam("id") Integer id) {
        try {
            boolean deleted = linhaRepository.deleteById(id);

            if (deleted) {
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Linha não encontrada")
                        .build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao excluir linha: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/nome/{nome}")
    public Response getLinhasByNome(@PathParam("nome") String nome) {
        try {
            List<Linha> linhas = linhaRepository.findByNome(nome);
            List<LinhaDTO> linhaDTOs = linhas.stream()
                    .map(linhaMapper::toDTO)
                    .collect(Collectors.toList());

            return Response.ok(linhaDTOs).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar linhas por nome: " + e.getMessage())
                    .build();
        }
    }
}
