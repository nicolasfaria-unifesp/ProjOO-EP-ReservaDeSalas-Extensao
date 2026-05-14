package com.universidade.reserva.historico;

import com.universidade.reserva.Reserva;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HistoricoReservasReal implements IHistoricoReservas {
    private final List<EntradaHistorico> entradas = new ArrayList<>();

    @Override
    public void registrarEntrada(String usuarioSolicitante, Reserva reserva, String acao) {
        entradas.add(new EntradaHistorico(reserva, acao));
        System.out.println("[Histórico] Entrada registrada: " + acao + " | Reserva ID=" + reserva.getId());
    }

    @Override
    public List<EntradaHistorico> consultarHistorico(String usuarioSolicitante) {
        return new ArrayList<>(entradas);
    }

    @Override
    public List<EntradaHistorico> consultarHistoricoPorUsuario(String usuarioSolicitante, String usuarioAlvo) {
        return entradas.stream()
            .filter(e -> e.getUsuario().equalsIgnoreCase(usuarioAlvo))
            .collect(Collectors.toList());
    }
}
