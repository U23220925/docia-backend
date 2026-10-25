package pe.docia.backend.chat;

public class LlmRespuesta {
    private final String texto;
    private final int tokensEntrada;
    private final int tokensSalida;
    private final String proveedor;

    public LlmRespuesta(String texto, int tokensEntrada, int tokensSalida, String proveedor) {
        this.texto = texto;
        this.tokensEntrada = tokensEntrada;
        this.tokensSalida = tokensSalida;
        this.proveedor = proveedor;
    }
    public String getTexto() { return texto; }
    public int getTokensEntrada() { return tokensEntrada; }
    public int getTokensSalida() { return tokensSalida; }
    public String getProveedor() { return proveedor; }
}