package com.universidade.reserva.historico;

import com.universidade.reserva.Reserva;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HistoricoReservasProxy implements IHistoricoReservas {
    private final IHistoricoReservas real = new HistoricoReservasReal();
    private final Set<String> admins;

    public HistoricoReservasProxy(Set<String> admins) {
        this.admins = admins;
    }

    private boolean isAdmin(String usuario) {
        return admins.stream().anyMatch(a -> a.equalsIgnoreCase(usuario));
    }

    private boolean isProfessor(String usuario) {
        return usuario != null && usuario.toLowerCase().contains("professor");
    }

    @Override
    public void registrarEntrada(String usuarioSolicitante, Reserva reserva, String acao) {
        if (isAdmin(usuarioSolicitante) || isProfessor(usuarioSolicitante)) {
            real.registrarEntrada(usuarioSolicitante, reserva, acao);
        } else {
            System.out.println("[Histórico - Acesso Negado] Usuário '" + usuarioSolicitante
                + "' não tem permissão para registrar no histórico.");
        }
    }

    @Override
    public List<EntradaHistorico> consultarHistorico(String usuarioSolicitante) {
        if (isAdmin(usuarioSolicitante)) {
            return real.consultarHistorico(usuarioSolicitante);
        }
        System.out.println("[Histórico - Acesso Negado] Apenas administradores podem ver o histórico completo.");
        return new ArrayList<>();
    }

    @Override
    public List<EntradaHistorico> consultarHistoricoPorUsuario(String usuarioSolicitante, String usuarioAlvo) {
        if (isAdmin(usuarioSolicitante) || usuarioSolicitante.equalsIgnoreCase(usuarioAlvo)) {
            return real.consultarHistoricoPorUsuario(usuarioSolicitante, usuarioAlvo);
        }
        System.out.println("[Histórico - Acesso Negado] Usuário '" + usuarioSolicitante
            + "' só pode consultar seu próprio histórico.");
        return new ArrayList<>();
    }
}
