package caixa_eletronico_aura67;

import java.text.NumberFormat; //bibliotecas para conseguir por o valor em R$ e a localidade, alguns computador estão em ingles, o que inverteria o . e a , 
import java.util.Locale;


// Classe principal do Caixa Eletronico com formatacao de moeda brasileira.

public class CaixaEletronico implements ICaixaEletronico {

    // Matriz 6x2: valor da nota | quantidade em estoque - 32.750,00
    private int[][] cedulas = {
            {100, 100},
            { 50, 200},
            { 20, 300},
            { 10, 350},
            {  5, 450},
            {  2, 500}
    };

    private int cotaMinima = 0; //cota minima 0 mas é possivel mudar no caixa
    private String[] historico = new String[200];
    private int contadorHistorico = 0;
    private static final int MAX_CEDULAS_POR_SAQUE = 30; //o caixa só podera sacar 30 cedulas ex: 30 de 100 = 3000


    private String formatarMoeda(int valor) { //usado para transformar 32750 que estava antes em 32.750,00
        return NumberFormat.getCurrencyInstance(new Locale("pt", "BR")).format(valor);
    }

    private int calcularTotal() {
        int total = 0;
        for (int i = 0; i < cedulas.length; i++) {
            total += cedulas[i][0] * cedulas[i][1];
        }
        return total;
    }

    private boolean caixaAtivo() {
        return calcularTotal() > cotaMinima;
    }

    private void registrarHistorico(String operacao) {
        if (contadorHistorico < historico.length) {
            historico[contadorHistorico] = operacao;
            contadorHistorico++;
        }
    }

    @Override
    public String pegaValorTotalDisponivel() {
        if (!caixaAtivo()) {
            return "Caixa Vazio: Chame o Operador";
        }
        return "Valor total disponível no caixa:\n\n" + formatarMoeda(calcularTotal());
    }

    @Override
    public String pegaRelatorioCedulas() { //O StringBuilder é uma classe projetada especificamente para montar textos grandes
        StringBuilder sb = new StringBuilder(); //de forma dinâmica. Ele é mutável.
        sb.append("========================================\n");
        sb.append("         RELATÓRIO DE CÉDULAS\n");
        sb.append("========================================\n\n");
        sb.append(String.format("%-12s %-12s %s%n", "Cédula", "Quantidade", "Subtotal"));//%s = onde o texto vai entrar
        sb.append("--------------------------------------------------\n");//%-12s comunicando o sistema para guardar 12 caracteres a esquerda por causa do -
        for (int i = 0; i < cedulas.length; i++) {//%n para quebrar linha
            sb.append(String.format("R$ %-9d %-12d %s%n",//%d que ali vai ficar o numero inteiro
                    cedulas[i][0],
                    cedulas[i][1],
                    formatarMoeda(cedulas[i][0] * cedulas[i][1])));
        }
        sb.append("--------------------------------------------------\n");
        sb.append("TOTAL EM CAIXA: ").append(formatarMoeda(calcularTotal()));//utilizando o formatar moemda para o valor aparecer em R$
        return sb.toString();
    }

    @Override
    public String sacar(Integer valor) {//Integer usado pq é mais robusto que o int, o int não retornar null, o o java lê como 0
        if (valor == null || valor <= 0) return "Valor inválido!";//já o Integer lê o null e volta pro usuario, e é mais facil utiliza-lo
        if (!caixaAtivo()) return "Caixa Vazio: Chame o Operador";//para conversão

        int[][] dp = new int[valor + 1][];
        dp[0] = new int[cedulas.length];//algoritmo dinamico, faz com que leia todas as cedulas e combine da melhor forma para entregar
                                        //o que foi pedido dentro do saque
        for (int v = 1; v <= valor; v++) {
            int melhorTotal = Integer.MAX_VALUE;
            int[] melhorCombinacao = null;

            for (int i = 0; i < cedulas.length; i++) {
                int valorNota = cedulas[i][0];
                int estoqueNota = cedulas[i][1];

                if (valorNota <= v && dp[v - valorNota] != null) {
                    int qtdJaUsada = dp[v - valorNota][i];
                    if (qtdJaUsada < estoqueNota) {
                        int totalNotas = 0;
                        for (int k = 0; k < cedulas.length; k++) totalNotas += dp[v - valorNota][k];
                        totalNotas++;

                        if (totalNotas < melhorTotal) {
                            melhorTotal = totalNotas;
                            melhorCombinacao = new int[cedulas.length];
                            System.arraycopy(dp[v - valorNota], 0, melhorCombinacao, 0, cedulas.length);
                            melhorCombinacao[i]++;
                        }
                    }
                }
            }
            dp[v] = melhorCombinacao;
        }

        if (dp[valor] == null) return "Não Temos Notas Para Este Saque.";

        int totalNotasNoSaque = 0;
        for (int i = 0; i < cedulas.length; i++) totalNotasNoSaque += dp[valor][i];

        if (totalNotasNoSaque > MAX_CEDULAS_POR_SAQUE) return "Saque negado: Limite de 30 cédulas excedido.";

        StringBuilder sb = new StringBuilder();
        sb.append("=== SAQUE REALIZADO ===\n\n");
        sb.append("Valor sacado: ").append(formatarMoeda(valor)).append("\n\n");
        sb.append("Cédulas entregues:\n");

        for (int i = 0; i < cedulas.length; i++) {
            if (dp[valor][i] > 0) {
                cedulas[i][1] -= dp[valor][i];
                sb.append(String.format("  R$ %3d  x %2d  =  %s%n",
                        cedulas[i][0], dp[valor][i], formatarMoeda(cedulas[i][0] * dp[valor][i])));
            }
        }

        sb.append("\nTotal de cédulas: ").append(totalNotasNoSaque);
        sb.append("\nSaldo restante: ").append(formatarMoeda(calcularTotal()));

        registrarHistorico("SAQUE: " + formatarMoeda(valor) + " | Saldo: " + formatarMoeda(calcularTotal()));
        if (!caixaAtivo()) sb.append("\n\n*** Caixa Vazio: Chame o Operador ***");

        return sb.toString();
    }

    @Override
    public String reposicaoCedulas(Integer cedula, Integer quantidade) {
        if (quantidade == null || quantidade <= 0) return "Quantidade inválida.";
        for (int i = 0; i < cedulas.length; i++) {
            if (cedulas[i][0] == cedula) {
                cedulas[i][1] += quantidade;
                registrarHistorico("REPOSIÇÃO: " + quantidade + "x R$" + cedula + " | Saldo: " + formatarMoeda(calcularTotal()));
                return "=== REPOSIÇÃO REALIZADA ===\n\n" + "Cédula: R$ " + cedula + "\nQuantidade atual: " + cedulas[i][1] + "\n\nSaldo total: " + formatarMoeda(calcularTotal());
            }
        }
        return "Cédula inválida.";
    }

    @Override
    public String armazenaCotaMinima(Integer minimo) {
        if (minimo == null || minimo < 0) return "Valor inválido.";
        this.cotaMinima = minimo;
        return "Cota mínima definida: " + formatarMoeda(minimo) + "\nSaldo atual: " + formatarMoeda(calcularTotal());
    }

    public String gerarExtrato() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("           EXTRATO DA SESSÃO\n");
        sb.append("========================================\n\n");
        if (contadorHistorico == 0) sb.append("Nenhuma operação realizada.\n");
        else for (int i = 0; i < contadorHistorico; i++) sb.append(String.format("%2d. %s%n", i + 1, historico[i]));
        sb.append("\n----------------------------------------\n");
        sb.append("Saldo final: ").append(formatarMoeda(calcularTotal()));
        return sb.toString();
    }

    public static void main(String[] args) {
        GUI janela = new GUI(CaixaEletronico.class);
        janela.show();
    }
}
