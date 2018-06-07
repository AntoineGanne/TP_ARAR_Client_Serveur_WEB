package TP_TCP;

import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;


public class Client extends Util {

    public static void main(String[] args) {
        Client c = new Client();
        c.connexion(ipServeur, portServeur);

        /*
        try {
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
        try {
            super.send(request);

            int car = br.read();
            while (car != -1 && (char)car != EOF) {
                System.out.print((char)car);
                car = br.read();
            }
            System.out.println("\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Permet au client d'envoyer au serveur une demande d'image et de
     * le récupérer dans un fichier .jpg.
     * @param request Requête à envoyer au serveur.
     * @param nomFichier
     * @throws IOException
     */
    //TODO: La fermeture de input déconne.
    public void sendGetImage(String request, String nomFichier) throws IOException {
        ImageInputStream input = null;
        BufferedImage img;
        String adresseFichier="src/Fichier/TP_TCP.Client/"+nomFichier;
        try {
            super.send(request);

            File fichierCree=new File(adresseFichier);
            BufferedWriter bwFichierCree=new BufferedWriter(new FileWriter(fichierCree));
            System.out.println("création du fichier "+adresseFichier);

//            input = ImageIO.createImageInputStream(in);
//            img = ImageIO.read(input);

            int car = br.read();
            while (car != -1 && (char)car!=EOF) {
//                System.out.print((char)car );
                bwFichierCree.write(car);
                car=br.read();
            }
            bwFichierCree.close();
            System.out.println("l'image en provenace du serveur a été enregistrée");
//            ImageIO.write(img, "jpg", new File("src/Fichier/imagePourClient.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // if (input != null) input.close();
        }
    }


    public void sendPut(String adresseFichierLocal, String nomFichier) throws IOException {
        String requete="PUT "+  nomFichier+CRLF;
        super.send(requete);

        FileInputStream fis = null;
        BufferedReader brFis = null;
        String response;
        try {
            fis = new FileInputStream(adresseFichierLocal);
            brFis = new BufferedReader(new InputStreamReader(fis, "UTF-8"), 2048);
//            response = getResponse(200, adresseFichierLocal);
//            out.write(response.getBytes());

            int c;
            while ((c = brFis.read()) != -1) {
                out.write(c);
            }
            out.write('\u001a');  //on écrit le caractère EOF
            out.flush();

            int car = br.read();
            while (car != -1 && (char)car != '\u001a') {
                System.out.print((char)car);
                car = br.read();
            }
        } catch (FileNotFoundException e) {
//            response = getResponse(404, address);
//            send(response);
            System.out.println("fichier introuvable");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (brFis != null) brFis.close();
            if (fis != null) fis.close();
        }
        //System.out.println("requète PUT envoyée");

    }


    private void boucleDeCommunication() {
        while (connexionEstActive()) {
            try {
                Scanner sc = new Scanner(System.in);
                System.out.println("Veuillez renseigner la nature de votre requete (GET / PUT / CLOSE)");
                String typeRequete = sc.next().toUpperCase();  // Permet au client de pouvoir écrire le type en minuscule.

                String nomFichier,requete;
                switch (typeRequete){
                    case "GET":
                        System.out.println("Veuillez renseigner le nom du fichier");
                        nomFichier = sc.next();
                        requete = typeRequete + " src/Fichier/TP_TCP.Serveur/" + nomFichier + " HTTP/1.1";
                        if (nomFichier.endsWith(".html") || nomFichier.endsWith(".txt")) sendGet(requete);
                        if (nomFichier.endsWith(".jpg") || nomFichier.endsWith(".jpeg")) sendGetImage(requete,nomFichier);
                        break;
                    case "PUT":
                        System.out.println("Veuillez renseigner le nom du fichier a transferer");
                         nomFichier = sc.next();
                         String adresseLocale ="src/Fichier/TP_TCP.Client/" + nomFichier;
                         sendPut(adresseLocale,nomFichier);
                        break;
                    case "CLOSE":
                        super.send(typeRequete);
                        super.fermerConnexion();
                        break;
                    default:
                        System.out.println("Type de requête non reconnue...");
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
