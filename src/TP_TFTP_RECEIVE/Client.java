package TP_TFTP_RECEIVE;

import java.io.*;
import java.net.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {

    private static final byte RRQ = 1;
    private static final byte DATA = 3;
    private static final byte ACK = 4;
    private static final byte ERROR = 5;

    private static final String serverPumpkin = "192.168.43.94";
    private static int portPumpkin = 69;
    private DatagramSocket ds;
    private DatagramPacket dp; // DTG reçu du serveur.
    private static final int sizePackets = 516;

    public static void main(String[] arg) {
        Client c = new Client();
        int cr_rv = c.receiveFile("src/Fichier/Client/JojoGood.gif", "src/Fichier/Serveur/JojoGood.gif", serverPumpkin);
        System.out.println("cr_rv : " + cr_rv);
    }

    public Client() {
        try {
            ds = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

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

    // Returne true si le fichier indiqué n'existe pas, false s'il existe ou que le repértoire du fichier n'existe pas.
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

    // Renvoie 0 si l'envoi du datagramme s'est bien passé, 1 s'il y a une erreur côté serveur, -1 s'il y a une erreur côté client.
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

    // Renvoie 0 si le transfert s'est bien passé, 1 s'il y a une erreur côté serveur ou -1 s'il y a une erreur côté client.
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

    // Il crée une requête RRQ pour le fichier donné en argument, le mode est octet.
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

