import java.io.*;
import java.net.ServerSocket;
import java.lang.String;
import java.util.StringTokenizer;

public class Serveur extends Util {

    private ServerSocket socServ;

    public static void main(String[] args){
        Serveur s = new Serveur();
        s.connexion(portServeur);
        try {
            s.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        try {
            s.streamToFile("TestClient.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        // s.fermerConnexion();
    }

    public Serveur(){
        super();
    }

    /**
     * Permet à un utilisateur de servir de serveur et d'accepter
     * ainsi la connexion d'un autre utilisateur.
     * @param  port Port du serveur sur lequel on autorise une connexion d'un utilisateur.
     */
    public void connexion(int port) {
        System.out.println("Démarrage du serveur " + ipServeur);
        System.out.println("Ouverture du port " + portServeur);
        System.out.println("--------------------");
        try {
            socServ = new ServerSocket(port);
            connexion = socServ.accept();
            initialiserStreams();
            System.out.println("Connexion acceptée avec " + connexion.getInetAddress()+ " sur le port " + connexion.getPort());
            System.out.println("--------------------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fermerConnexion(){
        try {
            socServ.close();
            super.fermerConnexion();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Permet au serveur d'envoyer un fichier à un client.
     * Les données du fichier sont stockées dans le flux d'entrée du socket du client.
     * @param address Adresse du fichier à envoyer (depuis le poste de l'émetteur)
     * @throws IOException
     */
    public void fileFromServerToClient(String address) throws IOException {
        FileInputStream fis = null;
        BufferedReader br = null;
        String response;
        try {
            fis = new FileInputStream(address);
            br = new BufferedReader(new InputStreamReader(fis, "UTF-8"), 2048);
            response = "HTTP/1.0 200 OK" + CRLF + CRLF;
            out.write(response.getBytes());

            int c;
            while ((c = br.read()) != -1) {
                out.write(c);
            }
        } catch (FileNotFoundException e) {
            response = "HTTP/1.0 404 Not Found" + CRLF + CRLF;
            out.write(response.getBytes());
        } finally {
            out.flush();
            if (br != null) br.close();
            if (fis != null) fis.close();
        }
    }

    // TODO : la partie "le serveur répond au client" ne marche pas...
    public void listen() throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(in, "UTF-8"), 2048);
            String header;
            StringTokenizer st;
            //while (true) {
                header = br.readLine();
                if (header != null && !header.equals(CRLF) && !header.equals("")) {
                    System.out.println(header);
                    st = new StringTokenizer(header);
                    out.write("prout".getBytes());
                    out.flush();

                    if (st.nextToken().equals("GET")) {
                        String address = st.nextToken();
                        // fileFromServerToClient(address);
                    }
                }
            //}
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) br.close();
        }
    }

}
