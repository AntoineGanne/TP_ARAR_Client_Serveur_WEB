import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;


public class Client extends Util {

    public static void main(String[] args) {
        Client c = new Client();
        c.connexion(ipServeur, portServeur);

        //c.imageToStream("src/Fichier/yugioh.jpg");
/*
        try {
            //c.sendGet("GET src/Fichier/TestServeur.txt HTTP/1.1");
            c.sendGetImage("GET src/Fichier/yugioh.jpg");


        } catch (IOException e) {
            e.printStackTrace();
        }
*/
        c.boucleDeCommunication();

        c.fermerConnexion();
    }

    public Client(){ super(); }

    /**
     * Permet d'initialiser une connexion avec un serveur.
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
     * Permet au client d'envoyer au serveur une demande de fichier et de
     * récupérer le contenu de celui-ci lorsque le serveur lui a répondu.
     * @param request Requête à envoyer au serveur.
     * @throws IOException
     */
    public void sendGet(String request) throws IOException {
        //BufferedReader br = null;
        try {
            super.send(request);

            //br = new BufferedReader(new InputStreamReader(in, "UTF-8"), 2048);
            String line;
            int car=br.read();
            while (car != -1 && (char)car!='\u001a') {
                System.out.print((char)car );
                car=br.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //if (br != null) br.close();
        }
        System.out.println("\n ////////// fin du fichier ////////// \n");
    }

    /**
     * Permet au client d'envoyer au serveur une demande d'image et de
     * le récupérer dans un fichier .jpg.
     * @param request Requête à envoyer au serveur.
     * @throws IOException
     */
    //TODO: La fermeture de input déconne.
    public void sendGetImage(String request) throws IOException {
        ImageInputStream input = null;
        BufferedImage img;
        try {
            super.send(request);

            input = ImageIO.createImageInputStream(in);
            img = ImageIO.read(input);
            ImageIO.write(img, "jpg", new File("src/Fichier/imagePourClient.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // if (input != null) input.close();
        }
    }

    private void boucleDeCommunication() {
        while (connexionEstActive()) {
            try {
                //c.fileToStream("src/Fichier/TestServeur.txt");
                Scanner sc = new Scanner(System.in);
                System.out.println("Veuillez renseigner la nature de votre requete (GET/PUT/CLOSE)");
                String typeRequete = sc.next().toUpperCase();  //le toUpperCase permet au client de pouvoir ecrire le type en minuscule

                switch (typeRequete){
                    case "GET":
                        System.out.println("Veuillez renseigner le nom du fichier");
                        String nomFichier = sc.next();

                        String requete = typeRequete + " src/Fichier/" + nomFichier + " HTTP/1.1";
                        sendGet(requete);
                        break;
                    case "PUT":
                        System.out.println("requete pas encore implementée");
                        break;
                    case "CLOSE":
                        super.send(typeRequete);
                        super.fermerConnexion();
                        break;
                    default:
                        System.out.println("type de requète non reconnue");
                        break;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //TODO: A implémenter.
    public void sendPut(String request) {

    }

}
