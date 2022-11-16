import java.util.HashMap;
import java.util.HashSet;

public class Compilador {
    private final Lexico lexico;
    private Token target;
    private final HashMap<String, Integer> variaveis;//
    private Integer tipoAtual;


    public Compilador(Lexico lexico) {
        this.lexico = lexico;
        target = lexico.nextToken();
        variaveis = new HashMap<>();
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

    private int tipoAbsolutoTarget() {
        if (target.getTipo() == Token.TIPO_IDENTIFICADOR) {
            if(!variaveis.containsKey(target.getLexema())) throw new RuntimeException(("ERRO: variável " + target.getLexema() + " não existe"));
            return variaveis.get(target.getLexema());
        } else return target.getTipo();
    }

    private void removerVariaveis(HashSet<String> variaveisRemover) {
        for (String variavel : variaveisRemover) {
            variaveis.remove(variavel);//
        }
    }


    public void iniciar() {
        programa();
        if (target.getTipo() != Token.TIPO_FIM_CODIGO)
            throw new RuntimeException("ERRO: \"" + target.getLexema() + "\" colocado em um lugar incorreto.");
        System.out.println("Semântico passou com sucesso!!!");
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
        HashSet<String> variaveisEscopo = new HashSet<>();
        while (!target.getLexema().equals("}") && target.getTipo() != Token.TIPO_FIM_CODIGO) {
            if (target.getTipo() == Token.TIPO_PALAVRA_RESERVADA
                    && (target.getLexema().equals("int")
                    || target.getLexema().equals("float")
                    || target.getLexema().equals("char"))) declararVal(variaveisEscopo);//
            else comando();
        }

        verificarLexema("fim do bloco", "}");
        removerVariaveis(variaveisEscopo);//
    }

    private void declararVal(HashSet<String> variaveisEscopo) {
        int tipo = switch (target.getLexema()) {
            case "int" -> 0;
            case "float" -> 1;
            case "char" -> 2;
            default -> throw new RuntimeException("ERRO: declaração de variável inválido. Atual: " + target.getLexema()
                    + "// Esperado: int/float/char");
        };
        nextTarget();

        String identificadorNome = target.getLexema();//
        verificarTipo("declaração de variável", Token.TIPO_IDENTIFICADOR);
        verificarLexema("declaração de variável", ";");

        if(variaveis.containsKey(identificadorNome)) throw new RuntimeException("ERRO: variável \""+ identificadorNome+ "\" já existe");
        variaveisEscopo.add(identificadorNome);
        variaveis.put(identificadorNome, tipo);
    }

    private void comando() {
        if (target.getTipo() == Token.TIPO_IDENTIFICADOR || target.getLexema().equals("{")) comandoBasico();
        else if (target.getLexema().equals("while")) iteracao();
        else if (target.getLexema().equals("if")) condicional();
        else throw new RuntimeException("ERRO: \"" + target.getLexema() + "\" colocado em um lugar incorreto.");
    }

    private void iteracao() {
        String metodo = "iteracao";
        verificarLexema(metodo, "while");
        verificarLexema(metodo, "(");
        expressaoRelacional();
        verificarLexema(metodo, ")");
        bloco();
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
        String identificador = target.getLexema();
        verificarTipo("Atribuição", Token.TIPO_IDENTIFICADOR);

        if (!variaveis.containsKey(identificador))
            throw new RuntimeException("ERRO: variável " + identificador + " não existe");
        tipoAtual = variaveis.get(identificador);

        verificarLexema("Atribuição", "=");
        expressaoAritmetica();
        tipoAtual = null;
        verificarLexema("Atribuição", ";");
    }

    private void expressaoRelacional() {
        expressaoAritmetica();
        tipoAtual = null;
        verificarTipo("Expressão relacional", Token.TIPO_OPERADOR_RELACIONAL);
        expressaoAritmetica();
        tipoAtual = null;
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
            return;
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

        if (tipoAtual == null) tipoAtual = tipoAbsolutoTarget();

        else if (tipoAtual != tipoAbsolutoTarget())
            throw new RuntimeException("ERRO: valor \"" + target.getLexema() + "\" inválido para o parâmetro");
        nextTarget();

    }


}
