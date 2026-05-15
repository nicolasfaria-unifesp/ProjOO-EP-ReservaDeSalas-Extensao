# Funcionalidade Adicional — Histórico de Reservas com Padrão Proxy

## Descrição

Implementa um histórico persistente em memória de todas as ações realizadas
sobre reservas (criação e cancelamento), com controle de acesso baseado em
perfil de usuário.

## Padrão de Projeto: Proxy (Proxy de Proteção)

### Justificativa

O padrão **Proxy** foi escolhido porque a funcionalidade exige duas
responsabilidades distintas:

1. **Armazenar e consultar o histórico** — responsabilidade do objeto real
   (`HistoricoReservasReal`).
2. **Controlar quem pode registrar e consultar** — responsabilidade do Proxy
   (`HistoricoReservasProxy`).

Separar essas responsabilidades mantém o objeto real simples e testável,
enquanto o Proxy encapsula toda a lógica de autorização sem poluir o domínio.

### Regras de Acesso

| Operação                          | Quem pode                                              |
|-----------------------------------|--------------------------------------------------------|
| Registrar entrada no histórico    | `admin` ou usuário cujo nome contenha "professor"      |
| Consultar histórico completo      | Apenas `admin`                                         |
| Consultar histórico por usuário   | `admin` ou o próprio usuário consultando a si mesmo    |

## Estrutura de Classes

```
IHistoricoReservas (interface)
    ├── HistoricoReservasReal   ← armazena e consulta sem restrições
    └── HistoricoReservasProxy  ← valida permissões antes de delegar ao Real
```

## Como Testar

1. Compile e execute o projeto normalmente.
2. Crie reservas com usuários variados (ex: `aluno1`, `professorFabio`, `admin`).
3. No menu, use a opção **7. Histórico de Reservas**.
4. Teste os cenários:
   - Login como `admin` → acesso total ao histórico completo.
   - Login como `aluno1` → só vê o próprio histórico (opção 2, alvo = `aluno1`).
   - Login como `aluno1` tentando ver histórico de `aluno2` → acesso negado.
   - Login como `professorJoao` → pode registrar (ao criar/cancelar reserva), mas não ver histórico completo.
