package br.com.ccr.mappers;

import br.com.ccr.dtos.TremDTO;
import br.com.ccr.entities.Trem;
import br.com.ccr.entities.Usuario;
import br.com.ccr.entities.Vagao;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.stream.Collectors;

@ApplicationScoped
public class TremMapper {

    @Inject
    private EstacaoMapper estacaoMapper;
    @Inject
    private LinhaMapper linhaMapper;
    @Inject
    private UsuarioMapper usuarioMapper;
    @Inject
    private VagaoMapper vagaoMapper;

    public TremMapper() {
    }

    public TremDTO toDTO(Trem trem) {
        if (trem == null) {
            return null;
        }

        TremDTO dto = new TremDTO();
        dto.setId(trem.getId());
        dto.setModelo(trem.getModelo());

        if (trem.getEstacaoInicial() != null) {
            dto.setEstacaoInicial(estacaoMapper.toDTO(trem.getEstacaoInicial()));
        }

        if (trem.getEstacaoFinal() != null) {
            dto.setEstacaoFinal(estacaoMapper.toDTO(trem.getEstacaoFinal()));
        }

        if (trem.getLinha() != null) {
            dto.setLinha(linhaMapper.toDTO(trem.getLinha()));
        }

        if (trem.getCondutores() != null) {
            dto.setCondutores(trem.getCondutores().stream()
                    .map(usuarioMapper::toDTO)
                    .collect(Collectors.toList()));
        }

        if (trem.getVagoes() != null) {
            dto.setVagoes(trem.getVagoes().stream()
                    .map(vagaoMapper::toDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public Trem toEntity(TremDTO dto) {
        if (dto == null) {
            return null;
        }

        Trem trem = new Trem();
        trem.setId(dto.getId());
        trem.setModelo(dto.getModelo());

        if (dto.getEstacaoInicial() != null) {
            trem.setEstacaoInicial(estacaoMapper.toEntity(dto.getEstacaoInicial()));
        }

        if (dto.getEstacaoFinal() != null) {
            trem.setEstacaoFinal(estacaoMapper.toEntity(dto.getEstacaoFinal()));
        }

        if (dto.getLinha() != null) {
            trem.setLinha(linhaMapper.toEntity(dto.getLinha()));
        }

        if (dto.getCondutores() != null) {
            ArrayList<Usuario> condutores = new ArrayList<>(
                    dto.getCondutores().stream()
                            .map(usuarioMapper::toEntity)
                            .collect(Collectors.toList())
            );
            trem.setCondutores(condutores);
        }

        if (dto.getVagoes() != null) {
            ArrayList<Vagao> vagoes = new ArrayList<>(
                    dto.getVagoes().stream()
                            .map(vagaoMapper::toEntity)
                            .collect(Collectors.toList())
            );
            trem.setVagoes(vagoes);
        }

        return trem;
    }
}
