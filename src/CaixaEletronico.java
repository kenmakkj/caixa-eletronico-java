package caixa_eletronico_aura67;


public class CaixaEletronico implements ICaixaEletronico {

 
     //Matriz 6x2 que armazena os dados das cédulas.
     //Coluna 0: Valor da nota (primeira coluna) | Coluna 1: Quantidade em estoque (segunda coluna).
 
    private int[][] cedulas = {
        {100, 100},
        {50,  200},
        {20,  300},
        {10,  350},
        {5,   450},
        {2,   500}
    };

    // Variável para armazenamento do limite de segurança para operação do caixa
    private int cotaMinima = 0;

    // Vetor do tipo [Strings] para armazenar a descrição de cada operação realizada
    private String[] historico = new String[100];
    
    // Índice que controla a posição atual de inserção no vetor de histórico
    private int contadorHistorico = 0;

    
      //Realiza a soma de todos os valores contidos na matriz de cédulas.
      //@return O valor total em Reais disponível no caixa.
     
    private int calcularTotal() {
        int total = 0;
        for (int i = 0; i < cedulas.length; i++) {
            // Multiplica o valor da nota pela sua quantidade e soma ao totalizador
            total = total + (cedulas[i][0] * cedulas[i][1]);
        }
        return total;
    }

    @Override
    public String pegaValorTotalDisponivel() {
        return "Valor total disponivel no caixa: R$ " + calcularTotal();
    }

    @Override
    public String pegaRelatorioCedulas() {
        String relatorio = "-- Relatorio de Cedulas --\n";
        for (int i = 0; i < cedulas.length; i++) {
            // Percorre a matriz montando uma listagem do estoque de cada nota
            relatorio = relatorio + "R$ " + cedulas[i][0] + " -> " + cedulas[i][1] + " cedulas\n";
        }
        return relatorio;
    }

    @Override
    public String sacar(Integer valor) {

        // Impede o processamento de valores nulos, zero ou negativos
        if (valor <= 0) {
            return "Valor invalido! Digite um valor maior que zero.";
        }

        // Valida se o saldo atual do caixa permite novas operações conforme a cota
        if (calcularTotal() < cotaMinima) {
            return "Caixa Vazio: Chame o Operador";
        }

        // Vetor para auxiliar no registro da quantidade de notas de cada tipo no saque atual
        int[] notasUsadas = new int[6];
        int valorRestante = valor;
        int totalNotasNoSaque = 0;

        // Implementação do Algoritmo Guloso (busca sempre a maior cédula disponível)
        for (int i = 0; i < cedulas.length; i++) {
            int valorDaNota = cedulas[i][0];
            int qtdNoCaixa = cedulas[i][1];

            // Calcula quantas notas desse valor cabem na solicitação
            int qtdNecessaria = valorRestante / valorDaNota;
            
            // Verifica se o caixa possui a quantidade necessária de notas
            int qtdParaUsar;
            if (qtdNecessaria < qtdNoCaixa) {
                qtdParaUsar = qtdNecessaria;
            } else {
                qtdParaUsar = qtdNoCaixa;
            }

            // Armazena a decisão no vetor temporário e subtrai do valor restante
            notasUsadas[i] = qtdParaUsar;
            totalNotasNoSaque = totalNotasNoSaque + qtdParaUsar;
            valorRestante = valorRestante - (qtdParaUsar * valorDaNota);
        }

        // [IF] Caso o valor não possa ser decomposto pelas notas disponíveis
        if (valorRestante > 0) {
            return "Saque nao realizado por falta de cedulas";
        }

        // Restrição técnica: impede a saída de mais de 30 cédulas por operação como regra do caixa
        if (totalNotasNoSaque > 30) {
            return "Saque nao realizado: seria necessario mais de 30 cedulas";
        }

        // Processamento final: atualiza o estoque real na matriz e gera o comprovante
        String resposta = "Saque de R$ " + valor + " realizado!\n";
        for (int i = 0; i < cedulas.length; i++) {
            if (notasUsadas[i] > 0) {
                // Decrementa a quantidade de notas na matriz principal
                cedulas[i][1] = cedulas[i][1] - notasUsadas[i];
                resposta = resposta + notasUsadas[i] + " nota(s) de R$ " + cedulas[i][0] + "\n";
            }
        }

        // Adiciona o registro da operação ao vetor de histórico se houver espaço
        if (contadorHistorico < 100) {
            historico[contadorHistorico] = "Saque: R$ " + valor + " | Saldo: R$ " + calcularTotal();
            contadorHistorico++;
        }

        return resposta;
    }

    @Override
    public String reposicaoCedulas(Integer cedula, Integer quantidade) {
        // Localiza a cédula correspondente na matriz e incrementa o estoque
        for (int i = 0; i < cedulas.length; i++) {
            if (cedulas[i][0] == cedula) {
                cedulas[i][1] = cedulas[i][1] + quantidade;
                return "Reposicao feita: " + quantidade + " cedulas de R$ " + cedula;
            }
        }
        return "Cedula invalida.";
    }

    @Override
    public String armazenaCotaMinima(Integer minimo) {
        // Define o patamar mínimo para operação do terminal
        this.cotaMinima = minimo;
        return "Cota minima definida: R$ " + minimo;
    }

    
     //Consolida todos os registros armazenados no vetor de histórico.
     //@return Uma String formatada com todos os saques da sessão.
     
    public String gerarExtrato() {
        if (contadorHistorico == 0) {
            return "Nenhum saque realizado.";
        }

        String textoExtrato = "===== EXTRATO =====\n";
        for (int i = 0; i < contadorHistorico; i++) {
            // Concatena cada posição preenchida do vetor ao texto final
            textoExtrato = textoExtrato + (i + 1) + ". " + historico[i] + "\n";
        }
        textoExtrato = textoExtrato + "Saldo final: R$ " + calcularTotal();
        return textoExtrato;
    }

    public static void main(String[] args) {
        // GUI janela = new GUI(CaixaEletronico.class);
        // janela.show();
    }
}
