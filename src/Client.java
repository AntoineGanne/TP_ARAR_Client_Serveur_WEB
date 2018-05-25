import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;

public class Client extends Util {

    public static void main(String[] args) {
        Client c = new Client();
        c.connexion(ipServeur, portServeur);

        try {
            c.sendGet("GET src/Fichier/TestServeur.txt HTTP/1.1");
        } catch (IOException e) {
            e.printStackTrace();
        }

        c.fermerConnexion();
    }

    public Client(){ super(); }

    /**
     * Permet d'initialiser une connexion avec un serveur.
     * Ouvre de plus les flux si la connexion s'est effectuée.
     * @param ip Adresse IP de l'utilisateur avec lequel se connecter.
     * @param port Port de l'utilisateur sur lequel on se connecte.
     * @see #initialiserStreams()
     */
    public void connexion(String ip, int port) {
        try {
            System.out.println("Connexion avec le serveur " + ipServeur + " initialisée sur le port " + portServeur);
            connexion = new Socket(ip, port);
            initialiserStreams();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Permet au client d'envoyer au serveur sur lequel il s'est connecté
     * une demande de fichier.
     * @param request Requête à envoyer au serveur.
     * @throws IOException
     */
    public void sendGet(String request) throws IOException {
        request += CRLF;
        BufferedReader br = null;
        try {
            super.send(request);

            br = new BufferedReader(new InputStreamReader(in, "UTF-8"), 2048);
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) br.close();
        }
    }

    //TODO: A implémenter.
    public void sendPut(String request) {}

}
