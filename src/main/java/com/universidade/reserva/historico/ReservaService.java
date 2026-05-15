package com.universidade.reserva;

import com.universidade.reserva.factories.SalaFactory;
import com.universidade.reserva.historico.IHistoricoReservas;
import com.universidade.reserva.observers.ObservadorReserva;
import com.universidade.reserva.salas.Sala;
import com.universidade.reserva.strategies.PoliticaDeReserva;
import com.universidade.reserva.strategies.PoliticaPrimeiroAReservar;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ReservaService {
    private List<Reserva> reservas;
    private PoliticaDeReserva politicaDeReserva;
    private List<ObservadorReserva> observadores;
    private IHistoricoReservas historico;

    public ReservaService(IHistoricoReservas historico) {
        this.reservas = new ArrayList<>();
        this.observadores = new ArrayList<>();
        this.politicaDeReserva = new PoliticaPrimeiroAReservar();
        this.historico = historico;
    }

    public void setPoliticaDeReserva(PoliticaDeReserva politicaDeReserva) {
        this.politicaDeReserva = politicaDeReserva;
    }

    public void adicionarObservador(ObservadorReserva observador) {
        observadores.add(observador);
    }

    public void removerObservador(ObservadorReserva observador) {
        observadores.remove(observador);
    }

    private void notificarObservadores(Reserva reserva, String acao) {
        for (ObservadorReserva obs : observadores) {
            obs.atualizar(reserva, acao);
        }
    }

    public Reserva criarReserva(String tipoSala, String nomeSala, int capacidadeSala,
                                String usuario, LocalDateTime inicio, LocalDateTime fim) {
        Sala sala = SalaFactory.criarSala(tipoSala, nomeSala, capacidadeSala);
        String idReserva = UUID.randomUUID().toString();
        Reserva novaReserva = new Reserva(idReserva, sala, usuario, inicio, fim);

        if (politicaDeReserva.podeReservar(getReservasPorSala(nomeSala), novaReserva)) {
            reservas.add(novaReserva);
            System.out.println("Reserva criada com sucesso: " + novaReserva);
            historico.registrarEntrada(usuario, novaReserva, "criada");
            notificarObservadores(novaReserva, "criada");
            return novaReserva;
        } else {
            System.out.println("Não foi possível criar a reserva (Conflito de horário ou Política).");
            return null;
        }
    }

    public boolean cancelarReserva(String idReserva, String usuarioSolicitante) {
        Reserva reservaParaCancelar = null;
        for (Reserva r : reservas) {
            if (r.getId().equals(idReserva)) {
                reservaParaCancelar = r;
                break;
            }
        }

        if (reservaParaCancelar != null) {
            reservas.remove(reservaParaCancelar);
            System.out.println("Reserva cancelada: " + reservaParaCancelar);
            historico.registrarEntrada(usuarioSolicitante, reservaParaCancelar, "cancelada");
            notificarObservadores(reservaParaCancelar, "cancelada");
            return true;
        } else {
            System.out.println("Reserva com ID " + idReserva + " não encontrada.");
            return false;
        }
    }

    public List<Reserva> listarReservasPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return reservas.stream()
                .filter(r -> r.getInicio().isBefore(fim) && r.getFim().isAfter(inicio))
                .collect(Collectors.toList());
    }

    public List<Reserva> getReservasPorSala(String nomeSala) {
        return reservas.stream()
                .filter(r -> r.getSala().getNome().equals(nomeSala))
                .collect(Collectors.toList());
    }

    public List<Reserva> getAllReservas() {
        return new ArrayList<>(reservas);
    }

    public String getPoliticaDeReservaNome() {
        return politicaDeReserva.getClass().getSimpleName();
    }

    public IHistoricoReservas getHistorico() {
        return historico;
    }
}