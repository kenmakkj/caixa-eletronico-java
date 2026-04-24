# 🏦 Caixa Eletronico - ATM Simulator in Java

Software em Java projetado para gerenciar a lógica de um terminal bancário, automatizando o controle de inventário de cédulas e a otimização de saques via algoritmo.

---

## 📖 Sobre o Sistema

Este projeto simula as operações internas de um caixa eletrônico real. O software gerencia um estoque composto por seis denominações de notas (**R$ 100, R$ 50, R$ 20, R$ 10, R$ 5 e R$ 2**), realizando o controle preciso de saldo e garantindo que as regras de negócio bancárias sejam respeitadas a cada transação.

---

## 🚀 Capacidades do Software

* **Gestão de Inventário:** Controle matricial das quantidades de cada cédula.
* **Estratégia de Saque Otimizada:** Implementação de algoritmo para entrega do menor número possível de notas.
* **Segurança de Estoque:** Monitoramento de saldo total e bloqueio automático em caso de nível crítico (Cota Mínima).
* **Validação em Tempo Real:** Verificação de viabilidade de saque antes da dedução dos valores.
* **Logs de Operação:** Sistema preparado para gerar extratos detalhados das movimentações da sessão.

---

## 🧠 Inteligência do Algoritmo

O motor de saque utiliza uma **estratégia gulosa** para selecionar as cédulas. O fluxo de decisão segue a hierarquia de valor decrescente:

1.  Calcula a necessidade de notas de **R$ 100**;
2.  Passa para as denominações menores (**R$ 50, 20, 10, 5**) sucessivamente;
3.  Finaliza com notas de **R$ 2**.

### Mensagens de Controle:
* **Indisponibilidade:** Caso a combinação de notas no estoque não atenda ao valor exato solicitado:  
    `"Saque não realizado por falta de cédulas"`
* **Nível Crítico:** Se o valor global do caixa for inferior ao limite operacional:  
    `"Caixa Vazio: Chame o Operador"`

---

## 📋 Organização Técnica

O projeto foi construído sobre uma arquitetura de **Interface (Contrato)**, o que permite que a lógica de negócio (Back-end) se comunique de forma transparente com qualquer interface de usuário (Front-end/GUI).

### Estrutura de Diretórios:
```text
caixa-eletronico-aura67/
├── src/
│   └── caixa_eletronico_aura67/
│       ├── CaixaEletronico.java    # Motor de regras e lógica de negócio
│       └── ICaixaEletronico.java   # Contrato de métodos obrigatórios
├── .gitignore
└── README.md
