import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

public class Client extends Util {

    public static void main(String[] args) {
        Client c = new Client();
        c.connexion(ipServeur, portServeur);
        c.send("GET TestServeur.txt HTTP/1.1");

        /*
        try {
            c.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        /*
        try {
            c.fileToStream("TestServeur.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        c.fermerConnexion();
    }

    //TODO: Je n'arrive pas à faire en sorte que le client puisse écouter la réponse du serveur.
    public void listen() throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(in, "UTF-8"), 2048);
            String line;
            line = br.readLine();
            System.out.println(line);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) br.close();
        }
    }

    public Client(){
        super();
    }
}
