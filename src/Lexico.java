import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
public class Lexico {
    private char[] conteudo;
    private int indiceConteudo;

    public Lexico(String caminhoCodigoFonte) {
        try {
            String conteudoStr;
            conteudoStr = new String(Files.readAllBytes(Paths.get(caminhoCodigoFonte)));
            conteudoStr += '$';
            this.conteudo = conteudoStr.toCharArray();
            this.indiceConteudo = 0;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    //Retorna próximo char
    private char nextChar() {
        return this.conteudo[this.indiceConteudo++];
    }

    //Verifica existe próximo char ou chegou ao final do código fonte
    private boolean hasNextChar() {
        return indiceConteudo < this.conteudo.length;
    }

    //Retrocede o índice que aponta para o "char da vez" em uma unidade
    private void back() {
        this.indiceConteudo--;
    }

    //Identificar se char é letra minúscula
    private boolean isLetra(char c) {
        return ((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z')) ;
    }

    //Identificar se char é dígito
    private boolean isDigito(char c) {
        return (c >= '0') && (c <= '9');
    }

    private boolean isRelacional(char c){
        return (c == '=' || c == '>' || c == '<');
    }

    private boolean isAritmimetic(char c){
        return c == '+' || c == '-' || c == '/' || c == '*' || c == '%';
    }

    private boolean isSpecialCharacter(char c){
        return   c == ';' || c == ',' || c == ')' || c == '('  || c == '}'  || c == '{'   ;
    }
    private boolean isReservedWord(String c){
        return c.equals("if") || c.equals("int") || c.equals("float") || c.equals("else")
                              || c.equals("main") || c.equals("while") || c.equals("char");

    }

    //Método retorna próximo token válido ou retorna mensagem de erro.
    public Token nextToken() {
        char c =  nextChar();

        //RETIRAR ESPAÇOS
        c = spaceTrim(c);

        //FIM DA LEITURA
        if(c == '$' && !hasNextChar()) return new Token("$", 99);

        if (c == '\'') return enterChar(c);
        if (isDigito(c)) return enterDigit(c);
        if (isRelacional(c)) return enterRelational(c);
        if (isAritmimetic(c)) return enterAritimetic(c);
        if (isSpecialCharacter(c)) return enterSpecialCharacter(c);
        if (isLetra(c)) return enterIdentifier(c);
        throw new RuntimeException("ERRO: Caractere não reconhecido");

    }

    private char spaceTrim(char c){
        while(c == '\n' || c == ' ' || c == '\r') {
            c = this.nextChar();
        }
        return c;
    }

    private Token enterIdentifier(char c) {
        StringBuilder lexema = new StringBuilder();

        while ((isLetra(c) || isDigito(c))) {
            lexema.append(c);
            c = nextChar();
        }

        back();
        if(isReservedWord(lexema.toString())) return new Token (lexema.toString(), 7);
        return new Token(lexema.toString(), 3);
    }

    private Token enterSpecialCharacter(char c) {
        String lexema = ""+c;
        return new Token(lexema, 6);
    }

    private Token enterAritimetic(char c) {
        String lexema = ""+c;
        return new Token(lexema, 5);
    }

    private Token enterRelational(char c) {
        String lexma = ""+c;
        char nextChar = nextChar();

        if(nextChar == '='){
            return new Token(lexma + nextChar, 4);
        }
        if(c == '<' && nextChar == '>')return new Token(lexma + nextChar, 4);

        back();
        if(lexma.equals("=")) return new Token(lexma, 8);
        return new Token(lexma, 4);
    }

    private Token enterChar(char c) {
        String lexema = ""+c;
        c = nextChar();
        if (!isLetra(c) && !isDigito(c)) throw new RuntimeException("ERROR: Char inválido");
        lexema += c;
        c = nextChar();
        if (c != '\'') throw new RuntimeException("ERROR: Char inválido");
        lexema += c;

        return new Token(lexema, 2);
    }

    private Token enterDigit(char c) {
        StringBuilder lexema = new StringBuilder();

        do {
            lexema.append(c);
            c = nextChar();
        } while (isDigito(c));

        //INTEIRO
        if (c != '.'){
            back();
            return new Token(lexema.toString(), 0);
        }
        lexema.append(c);
        //DOUBLE
        c = nextChar();
        if(!isDigito(c)) throw new RuntimeException("ERRO: real inválido");

        do {
            lexema.append(c);
            c = nextChar();
        } while (isDigito(c));

        back();
        return new Token(lexema.toString(), 1);
    }

}