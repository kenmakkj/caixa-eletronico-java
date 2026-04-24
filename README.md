# 🏧 Caixa Eletrônico — ATM Simulator

Programa desenvolvido em Java para simular o funcionamento de um caixa eletrônico, controlando o estoque de notas e realizando saques de forma otimizada.

---

## 📋 Descrição do projeto

O sistema simula um caixa eletrônico com 6 tipos de notas disponíveis: **R$ 2, R$ 5, R$ 10, R$ 20, R$ 50 e R$ 100**. O programa realiza o abastecimento inicial do caixa, entra em operação contínua atendendo clientes e gerencia o estoque de notas a cada saque realizado.

---

## ⚙️ Funcionalidades

- Abastecimento inicial do caixa com quantidade definida de cada tipo de nota
- Atendimento contínuo de clientes em sequência
- Cálculo automático das notas a serem entregues, priorizando sempre as de **maior valor**
- Decremento do estoque a cada saque realizado
- Validação de saque antes de efetivá-lo
- Emissão de mensagens de erro e alerta ao operador

---

## 🧠 Lógica de Saque

O programa sempre tenta pagar com as **maiores notas possíveis**, seguindo a ordem de prioridade:

```
R$ 100 → R$ 50 → R$ 20 → R$ 10 → R$ 5 → R$ 2
```

Antes de confirmar o saque, o sistema verifica se é possível atender ao valor solicitado com as notas disponíveis. Caso não seja possível, exibe a mensagem:

```
Não Temos Notas Para Este Saque
```

Caso o caixa fique abaixo do estoque mínimo, o atendimento é encerrado e o sistema exibe:

```
Caixa Vazio: Chame o Operador
```

---

## 🖥️ Interface

A interface com o usuário é fornecida externamente e integrada ao programa por meio de um contrato de utilização (Programa 2), conforme especificação do projeto.

---

## 📁 Estrutura do Projeto
````
caixa-eletronico-java/
├── src/
│   └── caixaeletronico/
│       ├── CaixaEletronico.java    # Classe principal com a lógica do caixa
│       └── ICaixaEletronico.java   # Interface (contrato) fornecida pelo professor
├── .gitignore
└── README.md
````

---

## 🛠️ Tecnologias

- Java (JDK 8+)

---

## 📌 Observações

- O programa foi desenvolvido como exercício acadêmico de estruturas de repetição e controle de fluxo em Java.
- A interface gráfica/textual foi fornecida pelo professor e integrada conforme o contrato de uso especificado.
