package TP_TFTP_RECEIVE;


import javax.xml.crypto.Data;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

//Inspiré de https://javapapers.com/java/java-tftp-client/
public class Client {
    DatagramSocket ds;
    private static final int portTFTP=69;
    private static final int SUCCESS=0;
    private static final int LOCAL_ERROR=-1;
    private static final int TRANSFERT_ERROR=1;
    private  static final byte RRQ=1;
    private  static final byte DATA=2;
    private  static final byte ACK=3;
    private  static final byte ERROR=3;

    public static void main (String[] arg){
        byte[] rq=faireDemandeFichier("Coucou.txt");
        try {
            String lisible = new String(rq, "UTF-8");
            System.out.println(lisible);
            rq
        }
        catch(IOException e){
            System.out.println("BUg");
        }
    }

    public short receiveFile(String nomFichierLocal,String adresseDistante){
        return 0;
    }

    //Il crée une requète RRQ pour le fichier donné en argument, le mode est octet
    static public byte[] faireDemandeFichier(String nomFichier){
        String mode="octet";
        int requeteTaille=4+nomFichier.length()+mode.length();

        byte[] requete=new byte[requeteTaille];

        requete[0]=(byte)0;
        requete[1]=RRQ;
         //On rajoute le nom du fichier à la requête
        System.arraycopy(nomFichier.getBytes(),0,requete,2,nomFichier.length());

        requete[3+nomFichier.length()]=(byte)0;


        System.arraycopy(mode.getBytes(),0,requete,3+nomFichier.length(),mode.length());

        requete[requeteTaille-1]=(byte)0;
        return requete;


    }

    //NbPaquet représente le code du paquet qui a été reçu
    private void  envoitAck(byte[] nbPaquet){
        byte[] bienRecu={0,ACK,nbPaquet[0],nbPaquet[1]};

        DatagramPacket retour= new DatagramPacket(bienRecu,bienRecu.length,inetAddress,)
        try{
            data
        }
    }
}