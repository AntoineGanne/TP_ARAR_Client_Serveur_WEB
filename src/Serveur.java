import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.lang.String;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

public class Serveur extends Util {

    private ServerSocket socServ;

    public static void main(String[] args) {
        Serveur s = new Serveur();
        s.connexion(portServeur);

        try {
            //s.streamToFile("src/Fichier/TestClient.txt");
            s.boucleDeCommunication();
            //s.streamToImage("src/Fichier/test.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        s.fermerConnexion();
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

    /**
     * Ferme la connexion avec le client.
     */
    public void fermerConnexion() {
        try {
            System.out.println("connexion fermée");
            socServ.close();
            super.fermerConnexion();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Permet au serveur d'écouter une requête de son client et de la traîter.
     * @throws IOException
     * @see #fileFromServerToClient(String)
     */
    //TODO: Boucler sur plusieurs requêtes.
    //TODO: Compléter le traîtement des requêtes (PUT / déconnexion) et leurs réponses.
    public void listen() throws IOException {
        //BufferedReader br = null;
        try {
            //br = new BufferedReader(new InputStreamReader(in, "UTF-8"), 2048);
            String request;
            StringTokenizer st;

            // On récupère la requête du client.
            request = br.readLine();
            if (request != null && !request.equals(CRLF) && !request.equals("")) {
                System.out.println(request);

                // On sépare la requête mot par mot.
                st = new StringTokenizer(request);
                // On récupère la méthode souhaitée (premier mot).
                String method = st.nextToken();
                // Si la méthode est reconnue, on traîte la requête.
                if (method.equals("GET") || method.equals("PUT")) {
                    if (method.equals("GET")) {
                        // On récupère le second mot de la requête : l'URI.
                        String address = st.nextToken();
                        if (address.endsWith("html") || (address.endsWith(".txt"))) fileFromServerToClient(address);
                        if (address.endsWith(".jpeg") || (address.endsWith(".jpg")) || (address.endsWith(".png")) ) imageFromServerToClient(address);
                    } else if (method.equals("PUT")) {
                        System.out.println("A faire...");
                    }
                }
                /* Si la requête ne commence pas par une méthode reconnue on renvoie au client
                l'erreur 501. */
                else {
                    send(getResponse(501, null));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //if (br != null) br.close();
        }
    }

    /**
     * Permet au serveur de traîter la demande de fichier d'un client.
     * @param address Adresse du fichier à envoyer (depuis le poste de l'émetteur)
     * @throws IOException
     * @see #getResponse(int, String)
     */
    public void fileFromServerToClient(String address) throws IOException {
        FileInputStream fis = null;
        //BufferedReader br = null;
        String response;
        try {
            fis = new FileInputStream(address);
            //br = new BufferedReader(new InputStreamReader(fis, "UTF-8"), 2048);
            response = getResponse(200, address);
            out.write(response.getBytes());

            int c;
            while ((c = br.read()) != -1) {
                out.write(c);
            }
            out.flush();
        } catch (FileNotFoundException e) {
            response = getResponse(404, address);
            send(response);
        } finally {
            //if (br != null) br.close();
            if (fis != null) fis.close();
        }
    }

    public void imageFromServerToClient(String address) {
        BufferedImage img;
        String response;
        try {
            img = ImageIO.read(new File(address));
            //response = getResponse(200, address);
            //out.write(response.getBytes());
            ImageIO.write(img, "jpg", out);
            out.flush();
        } catch (IOException e) {
            //response = getResponse(404);
            //send(response);
            e.printStackTrace();
        }
    }

    /**
     * Permet de construire une réponse du serveur à partir du code et son URI.
     * @param code Code HTTP
     * @param address URI
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
        if (address != null) {
            // On récupère la date de dernière modification du fichier.
            File file = new File(address);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            long lastModified = file.lastModified();
            String dateLastModified = sdf.format(lastModified);
            // On récupère la taille du fichier (en octets)
            long size = file.length();

            response.append("Date: ").append(new Date()).append(CRLF);
            response.append("Server: Java HTTP Server 1.1"  + CRLF);
            response.append("Last-Modified: ").append(dateLastModified).append(CRLF);
            response.append("Content-Length: ").append(size).append(CRLF);
            response.append("Connection: keep-alive").append(CRLF);
            response.append("Content-Type: ").append(contentType).append(CRLF);
            response.append(CRLF);
        }
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

    /**
     * contient le while() qui permet de lire les requetes tant que la connexion est ouverte
     */
    protected void boucleDeCommunication() throws IOException {
        while(connexionEstActive()){
            System.out.println("en attente d'une requete");
            listen();
        }
    }

}
