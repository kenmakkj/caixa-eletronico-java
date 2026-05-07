package caixa_eletronico_aura67;


public class CaixaEletronico implements ICaixaEletronico {

    // Matriz 6x2 que guarda as cedulas do caixa
    // Coluna 0 = valor da nota | Coluna 1 = quantidade disponivel no estoque
    private int[][] cedulas = {
        {100, 100},
        {50,  200},
        {20,  300},
        {10,  350},
        {5,   450},
        {2,   500}
    };

    // Valor minimo que o caixa precisa ter para continuar atendendo
    private int cotaMinima = 0;

    // Vetor que guarda o registro de cada saque feito durante a sessao
    private String[] historico = new String[100];

    // Controla em qual posicao do vetor o proximo registro vai ser salvo
    private int contadorHistorico = 0;


    // Percorre a matriz somando (valor da nota * quantidade) de cada tipo
    // retorna o total em reais que existe no caixa no momento
    private int calcularTotal() {
        int total = 0;
        for (int i = 0; i < cedulas.length; i++) {
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
            // monta linha por linha mostrando o valor e o estoque de cada nota
            relatorio = relatorio + "R$ " + cedulas[i][0] + " -> " + cedulas[i][1] + " cedulas\n";
        }
        return relatorio;
    }

    @Override
    public String sacar(Integer valor) {

        // nao faz sentido sacar zero ou negativo
        if (valor <= 0) {
            return "Valor invalido! Digite um valor maior que zero.";
        }

        // se o saldo esta abaixo da cota minima, o caixa para de atender
        if (calcularTotal() <= cotaMinima) {
            return "Caixa Vazio: Chame o Operador";
        }

        //   Como a tabela funciona:
        //   dp[v] guarda quantas notas de cada tipo foram usadas para chegar exatamente no valor v
        //   Se dp[v] for null, significa que e impossivel montar esse valor com as notas do caixa
        //   Comecamos do zero e construimos ate chegar no valor pedido pelo cliente

        // dp[v] = vetor de 6 posicoes com a quantidade de cada nota usada para somar exatamente v
        // null = ainda nao foi possivel chegar neste valor com as notas disponiveis
        int[][] dp = new int[valor + 1][];
        dp[0] = new int[cedulas.length]; // para somar zero, nao precisa de nenhuma nota

        // constroi a tabela de baixo para cima, do valor 1 ate o valor pedido
        for (int v = 1; v <= valor; v++) {
            int melhorTotal = Integer.MAX_VALUE;
            int[] melhorCombinacao = null;

            // testa adicionar uma nota de cada tipo para tentar chegar no valor v
            for (int i = 0; i < cedulas.length; i++) {
                int valorNota = cedulas[i][0];
                int estoqueNota = cedulas[i][1];

                // so vale testar se a nota cabe no valor e se ja existe solucao para o restante
                if (valorNota <= v && dp[v - valorNota] != null) {

                    // checa se ainda tem notas desse tipo disponivel no estoque
                    int qtdJaUsada = dp[v - valorNota][i];
                    if (qtdJaUsada < estoqueNota) {

                        // conta quantas notas no total essa combinacao candidata precisaria
                        int totalNotas = 0;
                        for (int k = 0; k < cedulas.length; k++) {
                            totalNotas += dp[v - valorNota][k];
                        }
                        totalNotas += 1; // mais a nota que estamos testando agora

                        // se essa opcao usa menos notas que a melhor encontrada ate agora, salva ela
                        if (totalNotas < melhorTotal) {
                            melhorTotal = totalNotas;
                            melhorCombinacao = new int[cedulas.length];
                            for (int k = 0; k < cedulas.length; k++) {
                                melhorCombinacao[k] = dp[v - valorNota][k];
                            }
                            melhorCombinacao[i]++; // registra o uso dessa nota na combinacao
                        }
                    }
                }
            }

            // salva a melhor combinacao encontrada para esse valor (null se nao achou nenhuma)
            dp[v] = melhorCombinacao;
        }

        // se dp[valor] continua null, nao existe combinacao possivel com as notas do caixa
        if (dp[valor] == null) {
            return "Saque nao realizado por falta de cedulas";
        }

        // soma o total de cedulas que vao sair nesse saque
        int totalNotasNoSaque = 0;
        for (int i = 0; i < cedulas.length; i++) {
            totalNotasNoSaque += dp[valor][i];
        }

        // regra do caixa: nao pode sair mais de 30 cedulas em uma unica operacao
        if (totalNotasNoSaque > 30) {
            return "Saque nao realizado: seria necessario mais de 30 cedulas";
        }

        // tudo certo, desconta as notas do estoque e monta o comprovante para o cliente
        String resposta = "Saque de R$ " + valor + " realizado!\n";
        for (int i = 0; i < cedulas.length; i++) {
            if (dp[valor][i] > 0) {
                cedulas[i][1] = cedulas[i][1] - dp[valor][i];
                resposta = resposta + dp[valor][i] + " nota(s) de R$ " + cedulas[i][0] + "\n";
            }
        }

        // salva o registro no historico se ainda tiver espaco no vetor
        if (contadorHistorico < 100) {
            historico[contadorHistorico] = "Saque: R$ " + valor + " | Saldo: R$ " + calcularTotal();
            contadorHistorico++;
        }

        return resposta;
    }

    @Override
    public String reposicaoCedulas(Integer cedula, Integer quantidade) {
        // procura o tipo de nota na matriz e soma a quantidade informada ao estoque
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
        // guarda o valor minimo, abaixo dele o caixa para de atender
        this.cotaMinima = minimo;
        return "Cota minima definida: R$ " + minimo;
    }

    // percorre o vetor de historico e monta o extrato completo da sessao
    public String gerarExtrato() {
        if (contadorHistorico == 0) {
            return "Nenhum saque realizado.";
        }

        String textoExtrato = "===== EXTRATO =====\n";
        for (int i = 0; i < contadorHistorico; i++) {
            textoExtrato = textoExtrato + (i + 1) + ". " + historico[i] + "\n";
        }
        textoExtrato = textoExtrato + "Saldo final: R$ " + calcularTotal();
        return textoExtrato;
    }

    public static void main(String[] args) {
        GUI janela = new GUI(CaixaEletronico.class);
        janela.show();
    }
}
