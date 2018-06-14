package TP_TFTP;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Scanner;

public class TFTP_Send  extends TFTP_util{
    static String dossierFichiers="fichiersClient/";
    static int portPumpkin;
    static int tailleMaxBloc=512;
    static String ipServerString="192.168.43.93"; //"127.0.0.1";


    public static void main(String[] arg){
        //String contenuWRQ=WRQ+"1"+separateur+"octet"+separateur;
        //System.out.println(contenuWRQ.getBytes());
        Scanner sc = new Scanner(System.in);

        try {
            InetAddress ipServeur =InetAddress.getByName(ipServerString);
            TFTP_Send t=new TFTP_Send();
//            t.sendWRQ("t.txt",ipServeur);
////            t.ecouteACK(0);

            System.out.println("Veuillez entrer le nom du fichier à envoyer");
            String f = sc.nextLine();
            String file = dossierFichiers+f;
            File fichier=new File(file);
            if (!fichier.exists()) {
                System.out.println("Ce fichier n'existe pas");
            } else t.sendFile(f,ipServerString);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

    static final byte separateur=0;

    public TFTP_Send(){
        super();
        portPumpkin=super.portTFTP;
    }

    public void writeBytesOfString(String s){
        byte[] bytesContenu=s.getBytes();
        System.out.println("byte[] du string");
        for(byte b:bytesContenu){
            System.out.print(b);
            System.out.print(" ");
        }
    }

    public int sendData(short numBloc, byte[] fileData, int nbBytes, InetAddress adresseDistante){
        int tailleContenu=4+nbBytes;
        byte[] data=new byte[tailleContenu];
        data[0]=separateur;
        data[1]=DATA;

        data[2]=separateur;
        data[3]=(byte)numBloc;

        System.arraycopy(fileData,0,data,4,nbBytes);

        DatagramPacket dp;
        short cr_em=send(adresseDistante, data);
        if(cr_em != 0){
            if(cr_em == -1) return codesRetour.LOCAL_ERROR;
            if (cr_em == 1) return codesRetour.TRANSFERT_ERROR;
        }
        System.out.println("Envoi reussi du bloc Data " + numBloc);
        return codesRetour.SUCCESS;
    }

    private short send(InetAddress adresseDistante, byte[] data) {
        DatagramPacket dp;
        try {
            dp = new DatagramPacket(data,data.length,adresseDistante,portPumpkin);
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
            System.out.println("recoit dans ACK:"+Arrays.toString(dp.getData())+"\n");
            portPumpkin=dp.getPort();
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

    public short sendFile(String nomFichierLocal,String adresseDistante)  {
        InetAddress ipServeur;
        try {
            ipServeur =InetAddress.getByName(adresseDistante);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return codesRetour.LOCAL_ERROR;
        }


        sendWRQ(nomFichierLocal,ipServeur);
        int codeRetourAck=ecouteACK(0);
        if(codeRetourAck==codesRetour.TRANSFERT_ERROR){
            System.out.println("Write Request refusée ou echouée");
            return codesRetour.TRANSFERT_ERROR;
        }

        String adresseFichierLocal=dossierFichiers+nomFichierLocal;
        FileInputStream fis = null;
        BufferedReader brFis = null;
        File file=null;

        try {
            file=new File(adresseFichierLocal);
            FileInputStream in=new FileInputStream(adresseFichierLocal);
            byte byteTemp;
            int intTemp;
            boolean dernierBlocAtteint=false;
            int numBloc=1;
//            for(int b=1;b<nbBlocs;b++){
            while(!dernierBlocAtteint){
                int nbBytes=512;
                byte[] fileData=new byte[tailleMaxBloc];

                for(int i=0;i<512;i++){
//                    byteTemp= (byte) brFis.read();
//                    intTemp= brFis.read();
                    intTemp=in.read();
                    if(intTemp==-1) {
                        nbBytes=i;
                        System.out.println("la lecture du fichier a atteint le dernier bloc "+numBloc);
                        dernierBlocAtteint=true;
                        break;
                    }
                    else{
                        fileData[i]=(byte)intTemp;
                    }
                }
                sendData((byte)numBloc,fileData,nbBytes,ipServeur);

                short codeRetourACK=ecouteACK(numBloc);
                if(codeRetourACK==codesRetour.TRANSFERT_ERROR){
                    throw new Exception("erreur ACK");
                }

                numBloc++;
            }

        } catch (FileNotFoundException e) {
            System.out.println("Fichier introuvable.");
            return codesRetour.LOCAL_ERROR;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return codesRetour.LOCAL_ERROR;
        } catch (IOException e) {
            e.printStackTrace();
            return codesRetour.LOCAL_ERROR;
        } catch (Exception e) {
            e.printStackTrace();
            return codesRetour.TRANSFERT_ERROR;
        }

        System.out.println("fin de l'envoi de fichier a Pumpkin");
        return codesRetour.SUCCESS;
    }
}
