package br.com.ccr.entities;

import lombok.*;

@Data
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Vagao extends BaseModel {
    private Trem trem;
    private String numeracao;

    public Trem getTrem() {
        return trem;
    }

    public void setTrem(Trem trem) {
        this.trem = trem;
    }

    public String getNumeracao() {
        return numeracao;
    }

    public void setNumeracao(String numeracao) {
        this.numeracao = numeracao;
    }
}
