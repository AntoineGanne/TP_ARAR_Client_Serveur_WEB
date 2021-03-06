package TP_TFTP_RECEIVE;

import java.io.*;
import java.net.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

/**
 * <p>Un client est principalement caractérisé par :
 * <ul>
 *     <li>son datagram socket.</li>
 *     <li>le dernier datagram packet reçu par le serveur.</li>
 *     <li>la taille maximale des paquets datagrammes qu'il peut recevoir.</li>
 *     <li>l'inet address et le port du serveur avec qui il communique.</li>
 * </ul>
 * </p>
 * @see #serverPumpkin
 * @see #portPumpkin
 * @see #ds
 * @see #dp
 * @see #sizePackets
 */
public class Client {

    /**
     * OPCODE Read Request.
     */
    private static final byte RRQ = 1;
    /**
     * OPCODE Data.
     */
    private static final byte DATA = 3;
    /**
     * OPCODE Acknowledgment.
     */
    private static final byte ACK = 4;
    /**
     * OPCODE Error.
     */
    private static final byte ERROR = 5;
    /**
     * Adresse IP du serveur. Par défaut, il s'agit de l'adresse locale 127.0.0.1.
     */
    private static String serverPumpkin = "127.0.0.1";
    /**
     * Port d'écoute du serveur.
     */
    private static int portPumpkin = 69;
    /**
     * Datagramme socket du client.
     */
    private DatagramSocket ds;
    /**
     * Dernier paquet reçu du serveur par le client.
     */
    private DatagramPacket dp;
    /**
     * Taille maximale des paquets que le client peut recevoir.
     */
    private static final int sizePackets = 516;

    public static void main(String[] arg) {
        Client c = new Client();

        // Récupération des noms de fichier.
        String fichierServeur, fichierLocal, adresseServeur;
        Scanner sc = new Scanner(System.in);
        System.out.println("Quel est votre serveur ? Tapez son adresse IP :");
        serverPumpkin = sc.nextLine();
        System.out.println("Veuillez renseigner le fichier du serveur à récupérer : ");
        fichierServeur = "src/Fichier/Serveur/"+sc.nextLine();
        System.out.println("Où voulez-vous récupérer votre fichier ? Renseignez votre adresse locale : ");
        fichierLocal = "src/Fichier/Client/"+sc.nextLine();
        sc.close();

        int cr_rv = c.receiveFile(fichierLocal, fichierServeur, serverPumpkin);
        // int cr_rv = c.receiveFile("src/Fichier/Client/JojoGood.gif", "src/Fichier/Serveur/JojoGood.gif", serverPumpkin);
        System.out.println("cr_rv : " + cr_rv);
    }

    /**
     * Constructeur de Client.
     */
    public Client() {
        try {
            ds = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * Réception d'un fichier distant sur la machine cliente.
     * @param fichierLocal Fichier qui sera reçu.
     * @param fichierDistant Fichier du serveur à récupérer.
     * @param adresseDistante Adresse du serveur.
     * @return 0 si le transfert s'est bien passé, 1 s'il y a une erreur côté serveur ou -1 s'il y a une erreur côté client.
     */
    public int receiveFile(String fichierLocal, String fichierDistant, String adresseDistante) {
        int cr_rv;

        if (fileNotExists(fichierLocal)) {
            byte[] request = demanderFichier(fichierDistant);
            if ((cr_rv = send(request, adresseDistante)) == 0) {
                System.out.println("Envoi de la demande de transfert du fichier " + fichierDistant + " du serveur Pumpkin réussie.\n");
                cr_rv = receive(fichierLocal, adresseDistante);
            }
        } else {
            cr_rv = -1;
        }
        return cr_rv;
    }

    /**
     * Détermine si un fichier existe ou non.
     * @param nomFichier Adresse du fichier.
     * @return True si le fichier indiqué n'existe pas, false s'il existe ou le repértoire parent n'existe pas.
     */
    public boolean fileNotExists(String nomFichier) {

        if (!nomFichier.contains(".")) {
            System.err.println("L'adresse locale donnée ne correspond pas à celle d'un fichier !");
            return false;
        }

        File f = new File(nomFichier);
        String parent = f.getParent();
        if (parent != null) {
            File dir = new File(parent);
            if (!(dir.isDirectory() && dir.exists())) {
                System.err.println("Le repértoire parent " + parent + " n'existe même pas ! Une petite faute de frappe sûrement ?");
                return false;
            }
        }

        if (!f.exists() && !f.isDirectory()) return true;
        else System.err.println("Le fichier local " + nomFichier + " indiqué existe déjà ! Créez-en un nouveau pour éviter d'effacer vos précieuses données...");

        return false;
    }

    /**
     * Envoie un datagramme au serveur.
     * @param request Message à envoyer au serveur.
     * @param adresseDistante Adresse du serveur.
     * @return 0 si l'envoi du datagramme s'est bien passé, 1 s'il y a une erreur côté serveur, -1 s'il y a une erreur côté client.
     */
    public int send(byte[] request, String adresseDistante) {
        int cr_rv;
        try {
            InetAddress ip = InetAddress.getByName(adresseDistante);
            DatagramPacket dp = new DatagramPacket(request, request.length, ip, portPumpkin);
            ds.send(dp);
            cr_rv = 0;
        } catch (UnknownHostException e) {
            System.err.println("IP du serveur Pumpkin introuvable !");
            cr_rv = -1;
        } catch (IOException e) {
            System.err.println("Le datagramme n'a pas pu être envoyé au serveur Pumpkin !");
            cr_rv = 1;
        }
        return cr_rv;
    }

    /**
     * Permet d'écrire les données reçues par le serveur dans un fichier.
     * @param fichierLocal Fichier dans lequel écrire.
     * @param adresseDistante Adresse du serveur.
     * @return 0 si le transfert s'est bien passé, 1 s'il y a une erreur côté serveur ou -1 s'il y a une erreur côté client.
     */
    public int receive(String fichierLocal, String adresseDistante) {
        byte[] buffer;
        byte[] datas = new byte[512];
        int compteurDTG = 0;
        int cr_rv = 0;
        boolean end = false;
        try {
            OutputStream out = new FileOutputStream(fichierLocal);
            while (!end) {
                buffer = new byte[sizePackets];
                dp = new DatagramPacket(buffer, buffer.length);
                ds.receive(dp);

                compteurDTG++;
                System.out.println("Réception du paquet TFTP n°" + compteurDTG + ".");

                // On récupère le nouveau port de Pumpkin.
                portPumpkin = dp.getPort();
                System.out.println("Taille du paquet : " + dp.getLength() + " octets.");

                byte[] opCode = {buffer[0], buffer[1]}; // OPCODE & ERROR CODE
                if (opCode[1] == DATA) {
                    // On récupère la partie "datas" du paquet TFTP datas.
                    System.arraycopy(buffer, 4, datas, 0, buffer.length - 4);
                    // On les écrit dans le fichier local si le numéro DATA est différent.
                    System.out.println("Ecriture de données dans le fichier local...\n");
                    out.write(datas);

                    // On envoie le paquet ACK.
                    byte[] ack = {0, ACK, buffer[2], buffer[3]};
                    send(ack, adresseDistante);

                    // On vérifie si le DTG reçu est le dernier ou no.
                    end = (dp.getLength() < sizePackets);
                } else if (opCode[1] == ERROR) {
                    int err = buffer[3];
                    System.arraycopy(buffer, 4, datas, 0, buffer.length - 4);
                    String message = new String(datas);
                    System.err.println("Erreur " + err + " du serveur : " + message + "\n");
                    end = true;
                    cr_rv = 1;
                }
            }
            out.close();
        } catch (FileNotFoundException e) {
            cr_rv = -1;
            System.err.println("Le fichier local " + fichierLocal + " n'a pas pu être ouvert !");
        } catch (IOException e) {
            cr_rv = -1;
            System.err.println("Problème de réception de datagrammes ou d'écriture dans le fichier local " + fichierLocal + " !");
        }
        return cr_rv;
    }

    /**
     * Construit un datagramme TFTP pour la demande de réception d'un fichier sur le serveur.
     * @param fichierDistant Fichier du serveur à récupérer.
     * @return Requête en octets.
     */
    public byte[] demanderFichier(String fichierDistant) {
        String mode = "octet";
        int requeteTaille = 4 + fichierDistant.length() + mode.length();
        byte[] requete = new byte[requeteTaille];
        requete[0] = (byte) 0;
        requete[1] = RRQ;
        // On rajoute le nom du fichier à la requête.
        System.arraycopy(fichierDistant.getBytes(), 0, requete, 2, fichierDistant.length());
        requete[3 + fichierDistant.length()] = (byte) 0;
        System.arraycopy(mode.getBytes(), 0, requete, 3 + fichierDistant.length(), mode.length());
        requete[requeteTaille - 1] = (byte) 0;

        return requete;
    }
}

