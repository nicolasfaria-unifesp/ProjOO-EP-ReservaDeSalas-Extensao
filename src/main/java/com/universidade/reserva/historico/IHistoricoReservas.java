package com.universidade.reserva.historico;

import com.universidade.reserva.Reserva;
import java.util.List;

public interface IHistoricoReservas {
    void registrarEntrada(String usuarioSolicitante, Reserva reserva, String acao);
    List<EntradaHistorico> consultarHistorico(String usuarioSolicitante);
    List<EntradaHistorico> consultarHistoricoPorUsuario(String usuarioSolicitante, String usuarioAlvo);
}
