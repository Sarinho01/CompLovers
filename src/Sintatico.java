public class Sintatico {
    private final Lexico lexico;
    private Token target;

    public Sintatico(Lexico lexico) {
        this.lexico = lexico;
        target = lexico.nextToken();
    }

    private Token nextToken() {
        return lexico.nextToken();
    }

    private void nextTarget() {
        this.target = nextToken();
    }

    private void verificarLexema(String metodo, String msg) {
        if (!target.getLexema().equals(msg))
            throw new RuntimeException("ERRO: " + metodo + " inválido. atual: " + target.getLexema() + "// esperado: " + msg);
        nextTarget();
    }

    public void iniciar() {
        programa();
    }

    private void programa() {
        verificarLexema("declaração de método", "int");
        verificarLexema("declaração de método", "main");
        verificarLexema("declaração de método", "(");
        verificarLexema("declaração de método", ")");
        bloco();
    }

    private void bloco() {
        verificarLexema("bloco", "{");
        while (target.getTipo() == Token.TIPO_PALAVRA_RESERVADA
                && (target.getLexema().equals("int")
                || target.getLexema().equals("float")
                || target.getLexema().equals("char"))) {
            declararVal();
        }
        verificarLexema("bloco", "}");
    }

    private void declararVal(){
       nextTarget();
       if(!(target.getTipo() == Token.TIPO_IDENTIFICADOR)) throw new RuntimeException("ERRO: declaração de variável inválido. atual: " + target.getLexema() + "// esperado: identificador");
       nextTarget();
       verificarLexema("declaração de variável", ";");
    }


}
