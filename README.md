# Sistema de Reserva de Salas de Estudo

## Descrição do Projeto

Este projeto simula um sistema de reserva de salas de estudo para uma universidade, atendendo a requisitos específicos de funcionalidade e aplicando diversos padrões de projeto de software. O objetivo é gerenciar a disponibilidade de diferentes tipos de salas, permitir reservas e cancelamentos, detectar colisões de horários e notificar usuários sobre eventos importantes, além de gerar relatórios. A implementação visa demonstrar um desenvolvimento orgânico e colaborativo, utilizando o GitHub para controle de versão.

## Requisitos Funcionais Implementados

### O sistema atende aos seguintes requisitos funcionais:

- **RF-01 — Gerenciamento de Salas:** O sistema permite a criação e gerenciamento de diferentes tipos de salas (individuais, de grupo, laboratórios), cada uma com suas características específicas.

- **RF-02 — Reserva e Cancelamento:** Usuários podem reservar salas para períodos específicos e cancelar reservas existentes.

- **RF-03 — Detecção de Colisões:** O sistema detecta automaticamente conflitos de horário para a mesma sala e aplica políticas de reserva para resolver ou impedir tais colisões.

- **RF-04 — Notificações:** Usuários são notificados (simuladamente via console) sobre a confirmação ou cancelamento de suas reservas.

- **RF-05 — Relatórios Diários:** O sistema gera relatórios diários das reservas realizadas, fornecendo um resumo das atividades.

- **RF-06 — Serviços Adicionais (Extensão — Decorator):** Permite adicionar serviços extras às reservas (e.g., equipamento multimídia, serviço de limpeza) de forma dinâmica, com custos associados.

- **RF-07 — Histórico de Reservas com Controle de Acesso (Extensão — Proxy):** O sistema mantém um histórico persistente em memória de todas as ações realizadas sobre reservas (criação e cancelamento). O acesso ao histórico é controlado por perfil de usuário por meio do padrão Proxy de Proteção, segundo as regras abaixo:

  | Operação | Quem pode |
  |---|---|
  | Registrar entrada no histórico | `admin` ou usuário cujo nome contenha `"professor"` |
  | Consultar histórico completo | Apenas `admin` |
  | Consultar histórico por usuário | `admin` ou o próprio usuário consultando a si mesmo |

## Padrões de Projeto Utilizados

| Padrão | Classe(s) principal(is) | Finalidade |
|---|---|---|
| Singleton | `ConfigurationManager` | Instância única de configuração global |
| Factory Method | `SalaFactory` | Criação dos diferentes tipos de sala |
| Strategy | `PoliticaDeReserva`, `PoliticaPrimeiroAReservar`, `PoliticaPrioridadeProfessor` | Políticas intercambiáveis de reserva |
| Observer | `ObservadorReserva`, `NotificacaoEmailObserver`, `RelatorioDiarioObserver` | Notificações e relatórios automáticos |
| Decorator | `ReservaDecorator`, `ReservaComEquipamentoMultimidia`, `ReservaComServicoLimpeza` | Serviços adicionais às reservas |
| Proxy | `HistoricoReservasProxy`, `HistoricoReservasReal` | Controle de acesso ao histórico de reservas |

## Estrutura do Projeto

```
src/
  main/java/com/universidade/reserva/
    Main.java                        # Ponto de entrada e menu interativo
    Reserva.java                     # Entidade de reserva
    IReserva.java                    # Interface de reserva
    ReservaService.java              # Serviço central de reservas
    ConfigurationManager.java        # Padrão Singleton
    factories/
      SalaFactory.java               # Padrão Factory
    salas/
      Sala.java
      SalaIndividual.java
      SalaDeGrupo.java
      Laboratorio.java
    strategies/
      PoliticaDeReserva.java         # Interface Strategy
      PoliticaPrimeiroAReservar.java
      PoliticaPrioridadeProfessor.java
    observers/
      ObservadorReserva.java         # Interface Observer
      NotificacaoEmailObserver.java
      RelatorioDiarioObserver.java
    decorators/
      ReservaDecorator.java          # Padrão Decorator
      ReservaComEquipamentoMultimidia.java
      ReservaComServicoLimpeza.java
    historico/                       # ★ novo — Padrão Proxy
      IHistoricoReservas.java
      HistoricoReservasProxy.java
      HistoricoReservasReal.java
      EntradaHistorico.java
docs/
  DiagramaSequencia.png
  DivisaoDeTrabalho.md
  funcionalidade_adicional.md
README.md
```

## Como Rodar o Projeto

Este projeto é um aplicativo Java simples de console. Para compilá-lo e executá-lo, siga os passos abaixo.

### Pré-requisitos

- **Java Development Kit (JDK) 11 ou superior**: Certifique-se de ter o JDK instalado e configurado em sua máquina. Você pode verificar a versão com `java -version` e `javac -version`.

### Compilação

1. Navegue até o diretório raiz do projeto no seu terminal:
   ```bash
   cd ProjOO-EP-ReservaDeSalas-Extensao
   ```

2. Compile todos os arquivos Java (incluindo o novo pacote `historico`):
   ```bash
   javac -encoding UTF-8 -d out \
     src/main/java/com/universidade/reserva/*.java \
     src/main/java/com/universidade/reserva/factories/*.java \
     src/main/java/com/universidade/reserva/observers/*.java \
     src/main/java/com/universidade/reserva/salas/*.java \
     src/main/java/com/universidade/reserva/strategies/*.java \
     src/main/java/com/universidade/reserva/decorators/*.java \
     src/main/java/com/universidade/reserva/historico/*.java
   ```
   Isso criará um diretório `out` com os arquivos `.class` compilados, mantendo a estrutura de pacotes.

### Execução

1. Após a compilação, execute o programa a partir do diretório raiz:
   ```bash
   java -cp out com.universidade.reserva.Main
   ```

2. O programa iniciará um menu interativo no console, onde você poderá criar e cancelar reservas, listar reservas, gerar relatórios, mudar a política de reserva, demonstrar o padrão Decorator e consultar o histórico de reservas.

### Exemplo de Interação

```
Sistema de Reserva de Salas de Estudo iniciado.

Salas disponíveis configuradas: [Sala Individual 101, Sala de Grupo 201, Laboratório 301]

--- Menu Principal ---
1. Criar Reserva
2. Cancelar Reserva
3. Listar Todas as Reservas
4. Gerar Relatório Diário
5. Mudar Política de Reserva (Atual: PoliticaPrimeiroAReservar)
6. Demonstrar Padrão Decorator
7. Histórico de Reservas
0. Sair
Escolha uma opção: 1

--- Criar Nova Reserva ---
Tipo da Sala (individual, grupo, laboratorio): individual
Nome da Sala: Sala Individual 101
Capacidade da Sala (apenas para grupo/laboratorio, digite 0 para individual): 0
Nome do Usuário: alunoTeste
Início (AAAA-MM-DD HH:MM): 2026-05-12 10:00
Fim (AAAA-MM-DD HH:MM): 2026-05-12 11:00
Reserva criada com sucesso: Reserva [ID=..., Sala=Sala Individual 101, Usuário=alunoTeste, ...]
[Notificação por Email] Enviando email para alunoTeste sobre a ação 'criada' na reserva: ...
[Relatório Diário] Atualizado para a data: 2026-05-12

--- Menu Principal ---
...
Escolha uma opção: 7

--- Histórico de Reservas ---
Seu nome de usuário: admin
1. Ver histórico completo
2. Ver histórico por usuário
Escolha: 1
[2026-05-12 10:05:00] Reserva ID=... | Sala=Sala Individual 101 | Usuário=alunoTeste | Ação=criada

--- Menu Principal ---
...
Escolha uma opção: 7

--- Histórico de Reservas ---
Seu nome de usuário: aluno1
1. Ver histórico completo
2. Ver histórico por usuário
Escolha: 1
[Histórico - Acesso Negado] Apenas administradores podem ver o histórico completo.

--- Menu Principal ---
...
Escolha uma opção: 0
Saindo...
Sistema de Reserva de Salas de Estudo finalizado.
```

### Testando o Controle de Acesso do Histórico (RF-07)

| Cenário | Como testar |
|---|---|
| Acesso total | Entre na opção 7 com nome de usuário `admin` → veja o histórico completo |
| Acesso próprio | Entre com `aluno1` → opção "por usuário" com alvo `aluno1` → sucesso |
| Acesso negado (outro usuário) | Entre com `aluno1` → opção "por usuário" com alvo `aluno2` → negado |
| Registro permitido (professor) | Crie uma reserva com usuário `professorJoao` → entrada é registrada no histórico |
| Consulta negada (professor) | Entre com `professorJoao` → opção "histórico completo" → negado |

## Autores

- Enrico Manzolli Bertoni — RA: 176259
- Nicolas Almeida Faria — RA: 178194
- Gabriel Delgado Panovich de Barros — RA: 176313
