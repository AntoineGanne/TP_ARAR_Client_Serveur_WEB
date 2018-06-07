package TP_TFTP_RECEIVE;

import java.io.*;
import java.net.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {

    private  static final byte RRQ = 1;
    private  static final byte DATA = 3;
    private  static final byte ACK = 4;
    private  static final byte ERROR = 5;

    private static final String serverPumpkin = "127.0.0.1";
    private static int portPumpkin = 69;
    private DatagramSocket ds;
    private DatagramPacket dp; // DTG reçu du serveur.
    private static final int sizePackets = 516;

    public static void main (String[] arg){
        Client c = new Client();
        c.receiveFile("src/Fichier/Client/testPump.html", "src/Fichier/Serveur/testPump.html");
    }

    public Client() {
        try {
            ds = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void receiveFile(String fichierLocal, String fichierDistant) {
        byte[] request = demanderFichier(fichierDistant);
        send(request);
        int compteur = receive(fichierLocal);
        if (compteur > 0) {
            int length = (compteur - 1) * 512 + dp.getLength() - 4;
            System.out.println("Fichier " + fichierDistant + " reçu avec succès !");
            System.out.println("Poids du fichier distant : " + (new File(fichierDistant)).length() + " octets.");
            System.out.println("Poids du fichier local : " + length + " octets.");
        }
    }

    public void send(byte[] request) {
        try {
            InetAddress ip = InetAddress.getByName(serverPumpkin);
            DatagramPacket dp = new DatagramPacket(request, request.length, ip, portPumpkin);
            ds.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Renvoie 0 en cas d'erreur, sinon le nombre de paquets nécessaires pour la réception du fichier.
    public int receive(String fichierLocal) {
        byte[] buffer;
        byte[] datas = new byte[512];
        int compteur = 0;
        boolean end = false;
        try {
            OutputStream out = new FileOutputStream(fichierLocal);
            while (!end) {
                compteur++;
                System.out.println("Paquet TFTP n°" + compteur);

                buffer = new byte[sizePackets];
                dp = new DatagramPacket(buffer, buffer.length);
                ds.receive(dp);
                // On récupère le nouveau port de Pumpkin.
                portPumpkin = dp.getPort();
                byte[] opCode = {buffer[0], buffer[1]}; // OPCODE & ERROR CODE
                if (opCode[1] == DATA) {
                    // On récupère la partie "datas" du paquet TFTP datas.
                    System.arraycopy(buffer, 4, datas, 0, buffer.length - 4);
                    // On les écrit dans le fichier local.
                    out.write(datas);
                    // On envoie le paquet ACK.
                    byte[] ack = {0, ACK, buffer[2], buffer[3]};
                    send(ack);
                    // On vérifie si le DTG reçu est le dernier ou non.
                    end = (dp.getLength() < sizePackets);
                }
            }
            out.close();
        } catch (IOException e) {
            compteur = 0;
            e.printStackTrace();
        }
        return compteur;
    }

    // Il crée une requête RRQ pour le fichier donné en argument, le mode est octet.
    public byte[] demanderFichier(String fichierDistant){
        String mode = "octet";
        int requeteTaille = 4 + fichierDistant.length() + mode.length();
        byte[] requete = new byte[requeteTaille];
        requete[0] = (byte)0;
        requete[1] = RRQ;
        // On rajoute le nom du fichier à la requête.
        System.arraycopy(fichierDistant.getBytes(), 0, requete, 2, fichierDistant.length());
        requete[3 + fichierDistant.length()] = (byte)0;
        System.arraycopy(mode.getBytes(),0,requete,3+fichierDistant.length(),mode.length());
        requete[requeteTaille - 1] = (byte)0;
        return requete;
    }
}

