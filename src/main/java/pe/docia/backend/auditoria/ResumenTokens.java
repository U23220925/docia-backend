package pe.docia.backend.auditoria;

public class ResumenTokens {
    private final int tokensEntrada;
    private final int tokensSalida;
    private final int totalConsultas;

    public ResumenTokens(int tokensEntrada, int tokensSalida, int totalConsultas) {
        this.tokensEntrada = tokensEntrada;
        this.tokensSalida = tokensSalida;
        this.totalConsultas = totalConsultas;
    }
    public int getTokensEntrada() { return tokensEntrada; }
    public int getTokensSalida() { return tokensSalida; }
    public int getTotalConsultas() { return totalConsultas; }
}