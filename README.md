🚀 Projeto Java: Simulador de Terminal Bancário (ATM)
Este projeto consiste na implementação da lógica de um Caixa Eletrônico desenvolvida para a disciplina de Programação Orientada a Objetos da UNICID. O sistema gerencia um estoque finito de cédulas e processa saques utilizando uma estratégia de otimização de notas.

📌 Visão Geral
O simulador opera baseado em um contrato (Interface Java), garantindo a comunicação entre a lógica de negócio e uma interface gráfica (GUI) fornecida. O foco principal é o controle rigoroso de estoque e a validação de regras bancárias.

⚙️ Regras de Negócio Implementadas
Para garantir o funcionamento correto e a nota máxima nos critérios de funcionalidade, o sistema segue estas diretrizes:

Matriz de Estoque (6x2): Gerenciamento centralizado de valores (R$ 100, 50, 20, 10, 5 e 2) e suas respectivas quantidades.

Algoritmo Guloso: O sistema calcula a menor quantidade de notas possível para um saque, priorizando sempre as cédulas de maior valor.

Limite de Cédulas: Travamento de segurança que impede saques que resultem em mais de 30 notas por operação.

Trava de Cota Mínima: O sistema monitora o saldo total e interrompe o atendimento caso o valor em caixa seja inferior ao limite definido pelo operador.

Histórico e Extrato: Registro em memória de todas as operações para exibição de um extrato consolidado ao encerrar o programa.

🛠️ Especificações Técnicas
Linguagem: Java.

Estruturas utilizadas: Matrizes multidimensionais, Vetores (Arrays), Estruturas de Repetição (for), Condicionais Compostas e Encapsulamento.

Arquitetura: Baseada em Interface (Contrato), facilitando o desacoplamento entre a lógica e a interface visual.

📖 Como Funciona a Lógica de Saque
Validação Inicial: O sistema verifica se o valor solicitado é positivo e se o caixa não atingiu a cota mínima.

Cálculo de Notas: O algoritmo percorre a matriz de cédulas. Para cada tipo de nota, ele verifica quantas podem ser utilizadas sem exceder o saldo disponível daquela cédula específica.

Validação de Fechamento: Se após percorrer todas as notas ainda restar algum valor, o saque é cancelado com a mensagem: "Saque não realizado por falta de cédulas".

Verificação de Volume: Se o saque for possível, mas exigir mais de 30 notas, a operação é bloqueada para evitar sobrecarga física do terminal.

Atualização de Estado: Somente após todas as validações, a quantidade de notas na matriz é decrementada.

📂 Organização do Repositório
Plaintext
caixa-eletronico-aura67/
├── src/
│   └── caixa_eletronico_aura67/
│       ├── CaixaEletronico.java    # Implementação da lógica (Minha Autoria)
│       ├── ICaixaEletronico.java   # Interface de contrato (Fornecida)
│       └── GUI.class               # Interface Gráfica (Fornecida)
└── README.md
👨‍🏫 Critérios Acadêmicos Atendidos
Documentação: Código 100% comentado explicando o fluxo de dados.

Organização: Código limpo e indentado conforme os padrões da folha de exercícios.

Conformidade: Implementação exata das mensagens de erro e alertas exigidos no PDF da atividade.

Nota: Este projeto é parte integrante da avaliação presencial da disciplina de POO. Todos os direitos sobre a interface gráfica pertencem à instituição de ensino.
