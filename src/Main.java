import java.util.StringTokenizer;

public class Main {

    public static void main(String[] args) {
        System.out.println();
        StringTokenizer st = new StringTokenizer("1:id10",":");
        while (st.hasMoreTokens()) {
            System.out.println(st.nextToken());
        }
    }
}