public class Main {
    public static void main(String[] args) {
        Lexico lexico = new Lexico("src\\texto.txt");
        Token t = lexico.nextToken();
        while(t.getTipo() != 99){
            System.out.println(t);
            t = lexico.nextToken();
        }
        System.out.println(t);

    }

}