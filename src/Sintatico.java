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
            throw new RuntimeException("ERRO: " + metodo + " inválido. atual: "
                    + target.getLexema() + "// esperado: " + msg);
        nextTarget();
    }

    private void verificarTipo(String metodo, int tipoEsperado) {
        if (target.getTipo() != tipoEsperado)
            throw new RuntimeException("ERRO: " + metodo + " inválido. Tipo atual: "
                    + Token.toString(target.getTipo()) + "// esperado: "
                    + Token.toString(tipoEsperado));
        nextTarget();
    }

    public void iniciar() {
        programa();
        System.out.println("Sintático passou com sucesso!!!");
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
        while (!target.getLexema().equals("}")) {
            if(target.getTipo() == Token.TIPO_PALAVRA_RESERVADA
                    && (target.getLexema().equals("int")
                    || target.getLexema().equals("float")
                    || target.getLexema().equals("char"))) declararVal();
            else comando();
        }

        verificarLexema("bloco", "}");
    }

    private void declararVal() {
        if (!(target.getLexema().equals("int")
                || target.getLexema().equals("float")
                || target.getLexema().equals("char"))) {
            throw new RuntimeException("ERRO: declaração de variável inválido. Atual: " + target.getLexema()
                    + "// Esperado: int/float/char");
        }
        nextTarget();
        verificarTipo("declaração de variável", Token.TIPO_IDENTIFICADOR);
        verificarLexema("declaração de variável", ";");
    }

    private void comando() {
        if (target.getTipo() == Token.TIPO_IDENTIFICADOR || target.getLexema().equals("{")) comandoBasico();
        else if (target.getLexema().equals("while")) iteracao();
        else if (target.getLexema().equals("if")) condicional();
        else throw new RuntimeException("ERRO: " + target.getLexema() + " colocado em um lugar incorreto.");
    }

    private void iteracao() {
        String metodo = "iteracao";
        verificarLexema(metodo, "while");
        verificarLexema(metodo, "(");
        expressaoRelacional();
        verificarLexema(metodo, ")");
        comando();
    }

    private void condicional() {
        String metodo = "condicional";

        verificarLexema(metodo, "if");
        verificarLexema(metodo, "(");
        expressaoRelacional();
        verificarLexema(metodo, ")");
        bloco();
        if (target.getLexema().equals("else")) {
            nextTarget();
            bloco();
        }
    }

    private void comandoBasico() {
        if (target.getTipo() == Token.TIPO_IDENTIFICADOR) atribuicao();
        else if (target.getLexema().equals("{")) bloco();
        else throw new RuntimeException("ERRO: Comando básico inválido, erro no " + target.getLexema());
    }

    private void atribuicao() {
        verificarTipo("Atribuição", Token.TIPO_IDENTIFICADOR);
        verificarLexema("Atribuição", "=");
        expressaoAritmetica();
        verificarLexema("Atribuição", ";");
    }

    private void expressaoRelacional() {
        expressaoAritmetica();
        verificarTipo("Expressão relacional", Token.TIPO_OPERADOR_RELACIONAL);
        expressaoAritmetica();
    }

    private void expressaoAritmetica() {
        termo();
        if (target.getLexema().equals("+") || target.getLexema().equals("-")) {
            nextTarget();
            expressaoAritmetica();
        }

    }

    private void termo() {
        fator();
        if (target.getLexema().equals("/") || target.getLexema().equals("*")) {
            nextTarget();
            termo();
        }

    }

    private void fator() {
        if (target.getLexema().equals("(")) {
            nextTarget();
            expressaoAritmetica();
            verificarLexema("Expressão aritmetica", ")");
        }
        if (target.getTipo() != Token.TIPO_IDENTIFICADOR &&
                target.getTipo() != Token.TIPO_REAL &&
                target.getTipo() != Token.TIPO_INTEIRO &&
                target.getTipo() != Token.TIPO_CHAR)
            throw new RuntimeException("ERRO: Expressão aritmetica inválida. Tipo atual: "
                    + Token.toString(target.getTipo()) + "// esperado: "
                    + Token.toString(Token.TIPO_INTEIRO) + " ou "
                    + Token.toString(Token.TIPO_REAL) + " ou "
                    + Token.toString(Token.TIPO_IDENTIFICADOR) + " ou "
                    + Token.toString(Token.TIPO_CHAR));
        else nextTarget();
    }


}
