import java.io.*;
import java.net.ServerSocket;
import java.lang.String;
import java.util.Date;
import java.util.StringTokenizer;

public class Serveur extends Util {

    // Pas sûr que ça vale le coup de mettre ça en attribut...
    private ServerSocket socServ;

    public static void main(String[] args) {
        Serveur s = new Serveur();
        s.connexion(portServeur);
        try {
            s.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // s.fermerConnexion();
    }

    public Serveur() { super(); }

    /**
     * Permet à un utilisateur de servir de serveur et d'accepter
     * ainsi la connexion d'un autre utilisateur.
     * @param port Port du serveur sur lequel on autorise une connexion d'un utilisateur.
     * @see #initialiserStreams()
     */
    public void connexion(int port) {
        System.out.println("Démarrage du serveur " + ipServeur);
        System.out.println("Ouverture du port " + portServeur);
        System.out.println("--------------------");
        try {
            socServ = new ServerSocket(port);
            connexion = socServ.accept();
            initialiserStreams();
            System.out.println("Connexion acceptée avec " + connexion.getInetAddress() + " sur le port " + connexion.getPort());
            System.out.println("--------------------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void fermerConnexion() {
        try {
            socServ.close();
            super.fermerConnexion();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Permet au serveur d'écouter une requête de son client.
     * @throws IOException
     * @see #fileFromServerToClient(String)
     * @see #getResponse(int)
     */
    //TODO: Boucler sur plusieurs requêtes.
    //TODO: Compléter le traîtement des requêtes (PUT / déconnexion) et leurs réponses.
    public void listen() throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(in, "UTF-8"), 2048);
            String request;
            StringTokenizer st;

            request = br.readLine();
            if (request != null && !request.equals(CRLF) && !request.equals("")) {
                System.out.println(request);
                st = new StringTokenizer(request);

                String command = st.nextToken();
                if (command.equals("GET") || command.equals("PUT")) {
                    if (command.equals("GET")) {
                        String address = st.nextToken();
                        fileFromServerToClient(address);
                    } else if (command.equals("PUT")) {
                        System.out.println("A faire...");
                    }
                } else {
                    send(getResponse(501));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) br.close();
        }
    }

    /**
     * Permet au serveur d'envoyer un fichier à un client.
     * Les données du fichier sont stockées dans le flux d'entrée du socket du client.
     * @param address Adresse du fichier à envoyer (depuis le poste de l'émetteur)
     * @throws IOException
     * @see #getResponse(int)
     * @see #getResponse(int, String)
     */
    public void fileFromServerToClient(String address) throws IOException {
        FileInputStream fis = null;
        BufferedReader br = null;
        String response;
        try {
            fis = new FileInputStream(address);
            br = new BufferedReader(new InputStreamReader(fis, "UTF-8"), 2048);
            response = getResponse(200, address);
            out.write(response.getBytes());

            int c;
            while ((c = br.read()) != -1) {
                out.write(c);
            }
            out.flush();
        } catch (FileNotFoundException e) {
            response = getResponse(404);
            send(response);
        } finally {
            if (br != null) br.close();
            if (fis != null) fis.close();
        }
    }

    /**
     * Permet de construire une réponse du serveur à partir du code.
     * @param code Code HTTP
     * @return Réponse
     */
    public String getResponse(int code) {
        StringBuilder response = new StringBuilder();
        String contentType = "text/html";
        switch (code) {
            case 200:
                response.append("HTTP/1.0 200 OK" + CRLF);
                break;
            case 404:
                response.append("HTTP/1.0 404 Not Found" + CRLF);
                break;
            case 501:
                response.append("HTTP/1.0 501 Not Implemented" + CRLF);
                break;
        }
        response.append("Server: Java HTTP Server 1.1"  + CRLF);
        response.append("Date: ").append(new Date()).append(CRLF);
        response.append("Content-Type: ").append(contentType).append(CRLF);
        response.append("Connection: keep-alive");
        response.append(CRLF);
        return response.toString();
    }

    /**
     * Permet de construire une réponse du serveur à partir du code et son addresse.
     * @param code Code HTTP
     * @param address Adresse du fichier (contenant son extension).
     * @return Réponse
     */
    public String getResponse(int code, String address) {
        StringBuilder response = new StringBuilder();
        String contentType = "text/html";
        switch (code) {
            case 200:
                response.append("HTTP/1.0 200 OK" + CRLF);
                contentType = getContentType(address);
                break;
            case 404:
                response.append("HTTP/1.0 404 Not Found" + CRLF);
                break;
            case 501:
                response.append("HTTP/1.0 501 Not Implemented" + CRLF);
                break;
        }
        response.append("Server: Java HTTP Server 1.1"  + CRLF);
        response.append("Date: ").append(new Date()).append(CRLF);
        response.append("Content-Type: ").append(contentType).append(CRLF);
        response.append("Connection: keep-alive");
        response.append(CRLF);
        return response.toString();
    }

    /**
     * Renvoie le MIME d'un document à partir de son nom.
     * @param address Addresse du fichier.
     * @return MIME du document.
     */
    public String getContentType(String address) {
        String contentType;
        if (address.endsWith(".html") || address.endsWith(".htm")) {
            contentType = "text/html";
        } else if (address.endsWith(".jpg") || address.endsWith(".jpeg")) {
            contentType = "image/jpeg";
        } else if (address.endsWith(".png")) {
            contentType = "image/png";
        } else if (address.endsWith(".gif")) {
            contentType = "image/gif";
        } else {
            contentType = "text/plain";
        }
        return contentType;
    }

}
