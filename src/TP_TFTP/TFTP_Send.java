package TP_TFTP;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class TFTP_Send  extends TFTP_util{
    static String dossierFichiers="fichiers_Pumpkin/";


    public static void main(String[] arg){
        //String contenuWRQ=WRQ+"1"+separateur+"octet"+separateur;
        //System.out.println(contenuWRQ.getBytes());

        try {
            InetAddress ipServeur =InetAddress.getByName("127.0.0.1");
            TFTP_Send t=new TFTP_Send();
            t.sendWRQ("t.txt",ipServeur);
            t.ecouteACK(0);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    static final byte separateur=0;

    public TFTP_Send(){
        super();
    }

    public void writeBytesOfString(String s){
        byte[] bytesContenu=s.getBytes();
        System.out.println("byte[] du string");
        for(byte b:bytesContenu){
            System.out.print(b);
            System.out.print(" ");
        }
    }

    public int sendData(short numBloc, byte[] fileData,InetAddress adresseDistante){
        int tailleContenu=4+fileData.length;
        byte[] data=new byte[tailleContenu];
        data[0]=separateur;
        data[1]=DATA;

        data[2]=separateur;
        data[3]=(byte)numBloc;

        System.arraycopy(fileData,0,data,2,fileData.length);

        DatagramPacket dp;
        short codeRetourData=send(adresseDistante, data);
        System.out.println("Envoi reussi du bloc Data "+numBloc);


        short codeRetourACK=ecouteACK(numBloc);

        return codesRetour.SUCCESS;
    }

    private short send(InetAddress adresseDistante, byte[] data) {
        DatagramPacket dp;
        try {
            dp = new DatagramPacket(data,data.length,adresseDistante,portTFTP);
            ds.send(dp);

        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.out.println("Ip du serveur introuvable");
            return codesRetour.LOCAL_ERROR;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("erreut, le datagramme n'a pas pu etre envoyé");

            return codesRetour.TRANSFERT_ERROR;
        }
        return codesRetour.SUCCESS;
    }


    public void sendWRQ(String nomFichier,InetAddress adresseDistante){
        //String contenuWRQ=separateur+WRQ+nomFichier+(byte)0+"octet"+(byte)0;
        //writeBytesOfString(contenuWRQ);

        String mode="octet";
        int tailleContenuWRQ=1+1+nomFichier.length()+1+mode.length()+1;
        byte[] contenuWRQ =new byte[tailleContenuWRQ];
        contenuWRQ[0]=separateur;
        contenuWRQ[1]=WRQ;
        // copie des bytes de nomFichier dans contenuWRQ
        System.arraycopy(nomFichier.getBytes(),0,contenuWRQ,2,nomFichier.length());
        contenuWRQ[2+nomFichier.length()]=separateur;
        System.arraycopy(mode.getBytes(),0,contenuWRQ,3+nomFichier.length(),mode.length());
        contenuWRQ[tailleContenuWRQ-1]=separateur;

       DatagramPacket dp;
        send(adresseDistante, contenuWRQ);

    }

    public short ecouteACK(int  numBlocAttendu){
        byte[] ack=new byte[4];
        DatagramPacket dp=new DatagramPacket(ack,ack.length);
        try {
            ds.receive(dp);
            System.out.println("recoit dans ACK:"+Arrays.toString(dp.getData()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte opCode=ack[1];
        byte numBloc = ack[3];
        if(opCode==ACK && numBloc==numBlocAttendu){
            return codesRetour.SUCCESS;
        }else{
            System.out.println("erreur ecoute ACK, num bloc attendu="+numBlocAttendu);
            return codesRetour.TRANSFERT_ERROR;
        }
    }

    public short SendFile(String nomFichierLocal,String adresseDistante){
        InetAddress ipServeur;
        try {
            ipServeur =InetAddress.getByName(adresseDistante);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return codesRetour.LOCAL_ERROR;
        }


        sendWRQ(nomFichierLocal,ipServeur);
        ecouteACK(0);

        String adresseFichierLocal=dossierFichiers+nomFichierLocal;
        FileInputStream fis = null;
        BufferedReader brFis = null;
        byte[] fileData=new byte[512];
        try {
            fis = new FileInputStream(adresseFichierLocal);
            brFis = new BufferedReader(new InputStreamReader(fis, "UTF-8"), 2048);

            int c;

            for(int i=0;i<512;i++){
                c=brFis.read();
                if(c==-1) break;
                else{
                    fileData[i]=(byte)c;
                }
            }

            sendData((short)1,fileData,ipServeur);

            if (brFis != null) brFis.close();
            if (fis != null) fis.close();
        } catch (FileNotFoundException e) {
            System.out.println("Fichier introuvable.");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("fin de l'envoi de fichier a Pumpkin");
        return codesRetour.SUCCESS;
    }
}
