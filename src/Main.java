public class Main {
    public static void main(String[] args) {
        System.out.println("LEXICO: ");
        Lexico lexico = new Lexico("src\\texto.txt");
        Token t = lexico.nextToken();
        while(t.getTipo() != 99){
            System.out.println(t);
            t = lexico.nextToken();
        }
        System.out.println(t);
        System.out.println("\nSINTÁTICO: ");
        Sintatico sintatico = new Sintatico(new Lexico("src\\texto.txt"));
        sintatico.iniciar();

        Compilador semantico = new Compilador(new Lexico("src\\texto.txt"));
        System.out.println("\nSEMÂNTICO: ");
        semantico.iniciar();


    }

}