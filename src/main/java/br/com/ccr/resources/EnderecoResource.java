package br.com.ccr.resources;

import br.com.ccr.dtos.EnderecoDTO;
import br.com.ccr.entities.Endereco;
import br.com.ccr.mappers.EnderecoMapper;
import br.com.ccr.repositories.EnderecoRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/enderecos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EnderecoResource {

    @Inject
    private EnderecoRepository enderecoRepository;

    @Inject
    private EnderecoMapper enderecoMapper;

    @POST
    public Response createEndereco(EnderecoDTO enderecoDTO) {
        try {
            Endereco endereco = enderecoMapper.toEntity(enderecoDTO);
            Endereco savedEndereco = enderecoRepository.save(endereco);
            EnderecoDTO savedEnderecoDTO = enderecoMapper.toDTO(savedEndereco);

            return Response.status(Response.Status.CREATED)
                    .entity(savedEnderecoDTO)
                    .build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao criar endereço: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response getEnderecoById(@PathParam("id") Integer id) {
        try {
            Optional<Endereco> endereco = enderecoRepository.findById(id);

            if (endereco.isPresent()) {
                EnderecoDTO enderecoDTO = enderecoMapper.toDTO(endereco.get());
                return Response.ok(enderecoDTO).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Endereço não encontrado")
                        .build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar endereço: " + e.getMessage())
                    .build();
        }
    }

    @GET
    public Response getAllEnderecos() {
        try {
            List<Endereco> enderecos = enderecoRepository.findAll();
            List<EnderecoDTO> enderecoDTOs = enderecos.stream()
                    .map(enderecoMapper::toDTO)
                    .collect(Collectors.toList());

            return Response.ok(enderecoDTOs).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar endereços: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response updateEndereco(@PathParam("id") Integer id, EnderecoDTO enderecoDTO) {
        try {
            Optional<Endereco> existingEndereco = enderecoRepository.findById(id);

            if (existingEndereco.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Endereço não encontrado")
                        .build();
            }

            Endereco endereco = enderecoMapper.toEntity(enderecoDTO);
            endereco.setId(id);

            Endereco updatedEndereco = enderecoRepository.update(endereco);

            EnderecoDTO updatedEnderecoDTO = enderecoMapper.toDTO(updatedEndereco);

            return Response.ok(updatedEnderecoDTO).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao atualizar endereço: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response deleteEndereco(@PathParam("id") Integer id) {
        try {
            boolean deleted = enderecoRepository.deleteById(id);

            if (deleted) {
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Endereço não encontrado")
                        .build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao excluir endereço: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/cidade/{cidade}")
    public Response getEnderecosByCidade(@PathParam("cidade") String cidade) {
        try {
            List<Endereco> enderecos = enderecoRepository.findByCidade(cidade);
            List<EnderecoDTO> enderecoDTOs = enderecos.stream()
                    .map(enderecoMapper::toDTO)
                    .collect(Collectors.toList());

            return Response.ok(enderecoDTOs).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar endereços por cidade: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/cep/{cep}")
    public Response getEnderecoByCep(@PathParam("cep") String cep) {
        try {
            Optional<Endereco> endereco = enderecoRepository.findByCep(cep);

            if (endereco.isPresent()) {
                EnderecoDTO enderecoDTO = enderecoMapper.toDTO(endereco.get());
                return Response.ok(enderecoDTO).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Endereço não encontrado para o CEP informado")
                        .build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar endereço por CEP: " + e.getMessage())
                    .build();
        }
    }
}
