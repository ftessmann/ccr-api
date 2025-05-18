package br.com.ccr.services;

import br.com.ccr.dtos.LoginDTO;
import br.com.ccr.dtos.TokenDTO;
import br.com.ccr.entities.Usuario;
import br.com.ccr.repositories.UsuarioRepository;

import io.smallrye.jwt.build.Jwt;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class AuthService {

    @Inject
    UsuarioRepository usuarioRepository;

    public Optional<TokenDTO> autenticar(LoginDTO loginDTO) {
        try {
            Optional<Usuario> usuarioOpt = usuarioRepository.autenticar(loginDTO.getEmail(), loginDTO.getSenha());

            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                String token = gerarToken(usuario);
                return Optional.of(new TokenDTO(token));
            }

            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<Usuario> getUsuarioAutenticado(String email) {
        try {
            return usuarioRepository.findByEmail(email);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private String gerarToken(Usuario usuario) {
        Instant now = Instant.now();
        Instant expiry = now.plus(Duration.ofHours(8));

        Set<String> roles = new HashSet<>();

        if (usuario.getCargo() != null) {
            roles.add(usuario.getCargo().name());
        }

        if (usuario.getSetor() != null) {
            roles.add("SETOR_" + usuario.getSetor().name());
        }

        return Jwt.issuer("ccr-sistema")
                .subject(usuario.getEmail())
                .upn(usuario.getEmail())
                .groups(roles)
                .claim("id", usuario.getId())
                .claim("nome", usuario.getNome())
                .claim("cargo", usuario.getCargo() != null ? usuario.getCargo().name() : null)
                .claim("setor", usuario.getSetor() != null ? usuario.getSetor().name() : null)
                .issuedAt(now)
                .expiresAt(expiry)
                .sign();
    }
}
