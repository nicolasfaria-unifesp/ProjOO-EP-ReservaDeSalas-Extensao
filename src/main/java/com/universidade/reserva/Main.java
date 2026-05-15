package com.universidade.reserva;

import com.universidade.reserva.decorators.ReservaComEquipamentoMultimidia;
import com.universidade.reserva.decorators.ReservaComServicoLimpeza;
import com.universidade.reserva.historico.EntradaHistorico;
import com.universidade.reserva.historico.HistoricoReservasProxy;
import com.universidade.reserva.historico.IHistoricoReservas;
import com.universidade.reserva.observers.NotificacaoEmailObserver;
import com.universidade.reserva.observers.RelatorioDiarioObserver;
import com.universidade.reserva.salas.Sala;
import com.universidade.reserva.factories.SalaFactory;
import com.universidade.reserva.strategies.PoliticaPrimeiroAReservar;
import com.universidade.reserva.strategies.PoliticaPrioridadeProfessor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

public class Main {
    private static ReservaService reservaService;
    private static Scanner scanner;
    private static RelatorioDiarioObserver relatorioObserver;

    public static void main(String[] args) {
        System.out.println("Sistema iniciado.\n");

        ConfigurationManager config = ConfigurationManager.getInstance();
        System.out.println("Salas disponíveis configuradas: " + config.getAvailableRooms() + "\n");

        // PROXY
        Set<String> admins = Set.of("admin");
        IHistoricoReservas historico = new HistoricoReservasProxy(admins);

        reservaService = new ReservaService();
        scanner = new Scanner(System.in);

        reservaService.adicionarObservador(new NotificacaoEmailObserver());
        relatorioObserver = new RelatorioDiarioObserver();
        reservaService.adicionarObservador(relatorioObserver);

        menuPrincipal();

        scanner.close();
        System.out.println("\nSistema finalizado.");
    }

    private static void menuPrincipal() {
        int opcao;
        do {
            System.out.println("\n--- Menu Principal ---");
            System.out.println("1. Criar Reserva");
            System.out.println("2. Cancelar Reserva");
            System.out.println("3. Listar Todas as Reservas");
            System.out.println("4. Gerar Relatório Diário");
            System.out.println("5. Mudar Política de Reserva (Atual: " + reservaService.getPoliticaDeReservaNome() + ")");
            System.out.println("6. Demonstrar Padrão Decorator");
            System.out.println("7. Histórico de Reservas");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");
            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Por favor, digite um número.");
                opcao = -1;
            }

            switch (opcao) {
                case 1:
                    criarReserva();
                    break;
                case 2:
                    cancelarReserva();
                    break;
                case 3:
                    listarTodasReservas();
                    break;
                case 4:
                    gerarRelatorioDiario();
                    break;
                case 5:
                    mudarPoliticaDeReserva();
                    break;
                case 6:
                    demonstrarDecorator();
                    break;
                case 7:
                    consultarHistorico();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcao != 0);
    }

    private static void criarReserva() {
        System.out.println("\n--- Criar Nova Reserva ---");
        System.out.print("Tipo da Sala (individual, grupo, laboratorio): ");
        String tipoSala = scanner.nextLine();
        System.out.print("Nome da Sala: ");
        String nomeSala = scanner.nextLine();
        int capacidadeSala = 0;
        try {
            System.out.print("Capacidade da Sala (apenas para grupo/laboratorio, digite 0 para individual): ");
            capacidadeSala = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Capacidade inválida. Usando 0.");
        }
        System.out.print("Nome do Usuário: ");
        String usuario = scanner.nextLine();
        System.out.print("Início (AAAA-MM-DD HH:MM): ");
        String inicioStr = scanner.nextLine();
        System.out.print("Fim (AAAA-MM-DD HH:MM): ");
        String fimStr = scanner.nextLine();

        try {
            LocalDateTime inicio = LocalDateTime.parse(inicioStr.replace(" ", "T"));
            LocalDateTime fim = LocalDateTime.parse(fimStr.replace(" ", "T"));

            reservaService.criarReserva(tipoSala, nomeSala, capacidadeSala, usuario, inicio, fim);
        } catch (DateTimeParseException e) {
            System.out.println("Formato de data/hora inválido. Use AAAA-MM-DD HH:MM.");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void cancelarReserva() {
        System.out.println("\n--- Cancelar Reserva ---");
        System.out.print("Seu nome de usuário: ");
        String usuarioSolicitante = scanner.nextLine();
        System.out.print("ID da Reserva a cancelar: ");
        String idReserva = scanner.nextLine();
        reservaService.cancelarReserva(idReserva, usuarioSolicitante);
    }

    private static void listarTodasReservas() {
        System.out.println("\n--- Todas as Reservas ---");
        List<Reserva> todasReservas = reservaService.getAllReservas();
        if (todasReservas.isEmpty()) {
            System.out.println("Nenhuma reserva encontrada.");
        } else {
            for (Reserva r : todasReservas) {
                System.out.println(r);
            }
        }
    }

    private static void gerarRelatorioDiario() {
        System.out.println("\n--- Gerar Relatório Diário ---");
        System.out.print("Data do relatório (AAAA-MM-DD): ");
        String dataStr = scanner.nextLine();
        try {
            LocalDate data = LocalDate.parse(dataStr);
            relatorioObserver.gerarRelatorioDiario(data);
        } catch (DateTimeParseException e) {
            System.out.println("Formato de data inválido. Use AAAA-MM-DD.");
        }
    }

    private static void mudarPoliticaDeReserva() {
        System.out.println("\n--- Mudar Política de Reserva ---");
        System.out.println("1. Política 'Primeiro a Reservar'");
        System.out.println("2. Política 'Prioridade para Professor'");
        System.out.print("Escolha a nova política: ");
        int escolha = -1;
        try {
            escolha = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor, digite um número.");
        }

        switch (escolha) {
            case 1:
                reservaService.setPoliticaDeReserva(new PoliticaPrimeiroAReservar());
                System.out.println("Política de reserva alterada para 'Primeiro a Reservar'.");
                break;
            case 2:
                reservaService.setPoliticaDeReserva(new PoliticaPrioridadeProfessor());
                System.out.println("Política de reserva alterada para 'Prioridade para Professor'.");
                break;
            default:
                System.out.println("Opção inválida. A política de reserva não foi alterada.");
        }
    }

    private static void demonstrarDecorator() {
        System.out.println("\n--- Demonstração do Padrão Decorator ---");

        Sala salaExemplo = SalaFactory.criarSala("grupo", "Sala de Grupo 305", 10);

        IReserva reservaBase = new Reserva(
            UUID.randomUUID().toString(), salaExemplo, "alunoDecorator",
            LocalDateTime.of(2026, 5, 12, 14, 0),
            LocalDateTime.of(2026, 5, 12, 16, 0)
        );
        System.out.println("Reserva Base: " + reservaBase.getDescricao() + " | Custo: " + String.format("%.2f", reservaBase.getCusto()));

        IReserva reservaComMultimidia = new ReservaComEquipamentoMultimidia(reservaBase);
        System.out.println("Reserva com Multimídia: " + reservaComMultimidia.getDescricao() + " | Custo: " + String.format("%.2f", reservaComMultimidia.getCusto()));

        IReserva reservaCompleta = new ReservaComServicoLimpeza(reservaComMultimidia);
        System.out.println("Reserva Completa (Multimídia + Limpeza): " + reservaCompleta.getDescricao() + " | Custo: " + String.format("%.2f", reservaCompleta.getCusto()));

        IReserva outraReserva = new Reserva(
            UUID.randomUUID().toString(), salaExemplo, "professorDecorator",
            LocalDateTime.of(2026, 5, 12, 16, 0),
            LocalDateTime.of(2026, 5, 12, 18, 0)
        );
        outraReserva = new ReservaComServicoLimpeza(outraReserva);
        outraReserva = new ReservaComEquipamentoMultimidia(outraReserva);
        System.out.println("Outra Reserva (Limpeza + Multimídia): " + outraReserva.getDescricao() + " | Custo: " + String.format("%.2f", outraReserva.getCusto()));

        System.out.println("\nObservação: As reservas decoradas acima são apenas para demonstração do padrão Decorator e não são persistidas no ReservaService.");
    }

    private static void consultarHistorico() {
        System.out.println("\n--- Histórico de Reservas ---");
        System.out.println("Dica: use 'admin' para acesso total, 'professor...' para registrar,");
        System.out.println("      ou seu próprio nome para ver seu histórico.");
        System.out.print("Seu nome de usuário: ");
        String usuarioConsulta = scanner.nextLine();

        System.out.println("1. Ver histórico completo");
        System.out.println("2. Ver histórico de um usuário específico");
        System.out.print("Escolha: ");
        int opcao = -1;
        try {
            opcao = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
            return;
        }

        List<EntradaHistorico> entradas;
        switch (opcao) {
            case 1:
                entradas = reservaService.getHistorico().consultarHistorico(usuarioConsulta);
                break;
            case 2:
                System.out.print("Usuário alvo: ");
                String alvo = scanner.nextLine();
                entradas = reservaService.getHistorico().consultarHistoricoPorUsuario(usuarioConsulta, alvo);
                break;
            default:
                System.out.println("Opção inválida.");
                return;
        }

        if (entradas.isEmpty()) {
            System.out.println("Nenhuma entrada encontrada (ou acesso negado).");
        } else {
            System.out.println("\n" + entradas.size() + " entrada(s) encontrada(s):");
            entradas.forEach(System.out::println);
        }
    }
}
