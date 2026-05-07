package caixa_eletronico_aura67;

/**
 * Implementacao do contrato ICaixaEletronico.
 *
 * Responsabilidades:
 *   - Gerenciar o estoque de cedulas em uma matriz 6x2
 *   - Realizar saques usando o menor numero de cedulas possivel (programacao dinamica)
 *   - Controlar a cota minima de atendimento
 *   - Registrar historico de operacoes para o extrato final
 *
 * Estrutura da matriz de cedulas:
 *   cedulas[i][0] = valor da nota
 *   cedulas[i][1] = quantidade disponivel no estoque
 */
public class CaixaEletronico implements ICaixaEletronico {

    // Matriz 6x2: coluna 0 = valor da nota | coluna 1 = quantidade em estoque
    private int[][] cedulas = {
        {100, 100},
        { 50, 200},
        { 20, 300},
        { 10, 350},
        {  5, 450},
        {  2, 500}
    };

    // Valor minimo que o caixa precisa ter para continuar atendendo clientes
    private int cotaMinima = 0;

    // Vetor de historico de operacoes da sessao (saques e reposicoes)
    private String[] historico = new String[200];

    // Aponta para a proxima posicao livre no vetor de historico
    private int contadorHistorico = 0;

    // Numero maximo de cedulas permitidas em um unico saque
    private static final int MAX_CEDULAS_POR_SAQUE = 30;

    // ------------------------------------------------------------------
    // Metodo auxiliar: soma (valor * quantidade) de cada tipo de cedula
    // ------------------------------------------------------------------
    private int calcularTotal() {
        int total = 0;
        for (int i = 0; i < cedulas.length; i++) {
            total += cedulas[i][0] * cedulas[i][1];
        }
        return total;
    }

    // ------------------------------------------------------------------
    // Metodo auxiliar: verifica se o caixa esta ativo (acima da cota minima)
    // ------------------------------------------------------------------
    private boolean caixaAtivo() {
        return calcularTotal() > cotaMinima;
    }

    // ------------------------------------------------------------------
    // Metodo auxiliar: registra uma operacao no historico da sessao
    // ------------------------------------------------------------------
    private void registrarHistorico(String operacao) {
        if (contadorHistorico < historico.length) {
            historico[contadorHistorico] = operacao;
            contadorHistorico++;
        }
    }

    // ------------------------------------------------------------------
    // ICaixaEletronico: retorna o valor total disponivel no caixa
    // ------------------------------------------------------------------
    @Override
    public String pegaValorTotalDisponivel() {
        if (!caixaAtivo()) {
            return "Caixa Vazio: Chame o Operador";
        }
        return "Valor total disponivel no caixa:\n\nR$ " + calcularTotal();
    }

    // ------------------------------------------------------------------
    // ICaixaEletronico: retorna relatorio com estoque de cada cedula
    // ------------------------------------------------------------------
    @Override
    public String pegaRelatorioCedulas() {
        StringBuilder sb = new StringBuilder();
        sb.append("============================\n");
        sb.append("    RELATORIO DE CEDULAS\n");
        sb.append("============================\n\n");
        sb.append(String.format("%-12s %-12s %s%n", "Cedula", "Quantidade", "Subtotal"));
        sb.append("----------------------------------------\n");
        for (int i = 0; i < cedulas.length; i++) {
            sb.append(String.format("R$ %-9d %-12d R$ %d%n",
                    cedulas[i][0],
                    cedulas[i][1],
                    cedulas[i][0] * cedulas[i][1]));
        }
        sb.append("----------------------------------------\n");
        sb.append("TOTAL: R$ ").append(calcularTotal());
        return sb.toString();
    }

    // ------------------------------------------------------------------
    // ICaixaEletronico: efetua o saque usando programacao dinamica
    //
    // Estrategia DP:
    //   dp[v] guarda o vetor de quantidades de cada cedula necessarias
    //   para compor exatamente o valor v.
    //   dp[v] == null significa que o valor v e inalcancavel com as
    //   cedulas disponiveis no estoque atual.
    //   A construcao minimiza o numero total de cedulas usadas.
    // ------------------------------------------------------------------
    @Override
    public String sacar(Integer valor) {

        // Validacao basica do valor solicitado
        if (valor == null || valor <= 0) {
            return "Valor invalido! Digite um valor maior que zero.";
        }

        // Verifica se o caixa esta ativo (acima da cota minima)
        if (!caixaAtivo()) {
            return "Caixa Vazio: Chame o Operador";
        }

        // dp[v] = vetor com a quantidade de cada cedula para somar exatamente v
        // dp[0] = vetor zerado (nenhuma cedula para somar zero)
        int[][] dp = new int[valor + 1][];
        dp[0] = new int[cedulas.length];

        // Constroi a tabela de baixo para cima: de 1 ate o valor pedido
        for (int v = 1; v <= valor; v++) {
            int melhorTotal       = Integer.MAX_VALUE;
            int[] melhorCombinacao = null;

            // Testa adicionar uma unidade de cada tipo de cedula
            for (int i = 0; i < cedulas.length; i++) {
                int valorNota   = cedulas[i][0];
                int estoqueNota = cedulas[i][1];

                // A cedula deve caber no valor e o subproblema (v - valorNota) precisa ter solucao
                if (valorNota <= v && dp[v - valorNota] != null) {

                    // Verifica se ainda ha estoque disponivel desse tipo
                    int qtdJaUsada = dp[v - valorNota][i];
                    if (qtdJaUsada < estoqueNota) {

                        // Conta o total de cedulas que essa combinacao candidata usaria
                        int totalNotas = 0;
                        for (int k = 0; k < cedulas.length; k++) {
                            totalNotas += dp[v - valorNota][k];
                        }
                        totalNotas++; // conta a cedula que estamos testando agora

                        // Salva apenas se essa opcao usa menos cedulas que a melhor ate agora
                        if (totalNotas < melhorTotal) {
                            melhorTotal       = totalNotas;
                            melhorCombinacao  = new int[cedulas.length];
                            for (int k = 0; k < cedulas.length; k++) {
                                melhorCombinacao[k] = dp[v - valorNota][k];
                            }
                            melhorCombinacao[i]++;
                        }
                    }
                }
            }

            dp[v] = melhorCombinacao; // null se nenhuma combinacao foi encontrada
        }

        // Se dp[valor] ainda e null, o saque e impossivel com as cedulas atuais
        if (dp[valor] == null) {
            return "Nao Temos Notas Para Este Saque.";
        }

        // Conta o total de cedulas que serao entregues
        int totalNotasNoSaque = 0;
        for (int i = 0; i < cedulas.length; i++) {
            totalNotasNoSaque += dp[valor][i];
        }

        // Regra: nao pode sair mais de 30 cedulas em uma unica operacao
        if (totalNotasNoSaque > MAX_CEDULAS_POR_SAQUE) {
            return "Saque nao realizado: seria necessario mais de 30 cedulas.";
        }

        // Tudo certo: desconta as cedulas do estoque e monta o comprovante
        StringBuilder sb = new StringBuilder();
        sb.append("=== SAQUE REALIZADO ===\n\n");
        sb.append("Valor sacado: R$ ").append(valor).append("\n\n");
        sb.append("Cedulas entregues:\n");

        for (int i = 0; i < cedulas.length; i++) {
            if (dp[valor][i] > 0) {
                cedulas[i][1] -= dp[valor][i]; // desconta do estoque
                sb.append(String.format("  R$ %3d  x %2d  =  R$ %d%n",
                        cedulas[i][0],
                        dp[valor][i],
                        cedulas[i][0] * dp[valor][i]));
            }
        }

        sb.append("\nTotal de cedulas: ").append(totalNotasNoSaque);
        sb.append("\nSaldo restante:   R$ ").append(calcularTotal());

        // Registra a operacao no historico
        registrarHistorico("SAQUE: R$ " + valor + " | Saldo: R$ " + calcularTotal());

        // Avisa se o caixa passou a ficar abaixo da cota minima apos o saque
        if (!caixaAtivo()) {
            sb.append("\n\n*** Caixa Vazio: Chame o Operador ***");
        }

        return sb.toString();
    }

    // ------------------------------------------------------------------
    // ICaixaEletronico: repoe cedulas de um determinado valor
    // ------------------------------------------------------------------
    @Override
    public String reposicaoCedulas(Integer cedula, Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            return "Quantidade invalida. Digite um valor maior que zero.";
        }

        for (int i = 0; i < cedulas.length; i++) {
            if (cedulas[i][0] == cedula) {
                int antes    = cedulas[i][1];
                cedulas[i][1] += quantidade;

                registrarHistorico("REPOSICAO: " + quantidade
                        + "x R$ " + cedula
                        + " | Saldo: R$ " + calcularTotal());

                return "=== REPOSICAO REALIZADA ===\n\n"
                        + "Cedula:             R$ " + cedula + "\n"
                        + "Quantidade antes:   " + antes + "\n"
                        + "Quantidade reposta: +" + quantidade + "\n"
                        + "Quantidade atual:   " + cedulas[i][1] + "\n\n"
                        + "Saldo total: R$ " + calcularTotal();
            }
        }
        return "Cedula de R$ " + cedula + " nao existe.\nUse: 2, 5, 10, 20, 50 ou 100.";
    }

    // ------------------------------------------------------------------
    // ICaixaEletronico: define a cota minima de atendimento
    // ------------------------------------------------------------------
    @Override
    public String armazenaCotaMinima(Integer minimo) {
        if (minimo == null || minimo < 0) {
            return "Valor invalido. Digite um valor positivo.";
        }
        this.cotaMinima = minimo;
        String msg = "Cota minima definida: R$ " + minimo
                   + "\nSaldo atual: R$ " + calcularTotal();
        if (!caixaAtivo()) {
            msg += "\n\n*** Caixa Vazio: Chame o Operador ***";
        }
        return msg;
    }

    // ------------------------------------------------------------------
    // Metodo extra: gera extrato completo da sessao (saques + reposicoes)
    // Chamado pela GUI no evento do botao Sair
    // ------------------------------------------------------------------
    public String gerarExtrato() {
        StringBuilder sb = new StringBuilder();
        sb.append("===================================\n");
        sb.append("       EXTRATO DA SESSAO\n");
        sb.append("===================================\n\n");

        if (contadorHistorico == 0) {
            sb.append("Nenhuma operacao realizada.\n");
        } else {
            for (int i = 0; i < contadorHistorico; i++) {
                sb.append(String.format("%2d. %s%n", i + 1, historico[i]));
            }
        }

        sb.append("\n-----------------------------------\n");
        sb.append("Saldo final: R$ ").append(calcularTotal());
        sb.append("\n===================================");
        return sb.toString();
    }

    // ------------------------------------------------------------------
    // Ponto de entrada: cria a GUI passando esta classe como backend
    // ------------------------------------------------------------------
    public static void main(String[] args) {
        GUI janela = new GUI(CaixaEletronico.class);
        janela.show();
    }
}
