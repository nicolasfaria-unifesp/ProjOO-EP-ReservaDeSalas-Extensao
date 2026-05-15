package com.universidade.reserva.historico;

import com.universidade.reserva.Reserva;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EntradaHistorico {
    private final String idReserva;
    private final String nomeSala;
    private final String usuario;
    private final String acao;
    private final LocalDateTime momento;

    public EntradaHistorico(Reserva reserva, String acao) {
        this.idReserva = reserva.getId();
        this.nomeSala  = reserva.getSala().getNome();
        this.usuario   = reserva.getUsuario();
        this.acao      = acao;
        this.momento   = LocalDateTime.now();
    }

    public String        getIdReserva() { return idReserva; }
    public String        getNomeSala()  { return nomeSala;  }
    public String        getUsuario()   { return usuario;   }
    public String        getAcao()      { return acao;      }
    public LocalDateTime getMomento()   { return momento;   }

    @Override
    public String toString() {
        return String.format("[%s] Reserva ID=%s | Sala=%s | Usuário=%s | Ação=%s",
            momento.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            idReserva, nomeSala, usuario, acao);
    }
}
