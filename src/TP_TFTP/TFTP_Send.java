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


    public static void main(String[] arg){
        //String contenuWRQ=WRQ+"1"+separateur+"octet"+separateur;
        //System.out.println(contenuWRQ.getBytes());
        Scanner sc = new Scanner(System.in);

        try {
            InetAddress ipServeur =InetAddress.getByName("127.0.0.1");
            TFTP_Send t=new TFTP_Send();
//            t.sendWRQ("t.txt",ipServeur);
////            t.ecouteACK(0);
            System.out.println("Veuillez entrer le fichier à envoyer");
            String f = sc.nextLine();
            t.sendFile(f,"127.0.0.1");
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
        short codeRetourData=send(adresseDistante, data);
        System.out.println("Envoi reussi du bloc Data "+numBloc);



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
        ecouteACK(0);

        String adresseFichierLocal=dossierFichiers+nomFichierLocal;
        FileInputStream fis = null;
        BufferedReader brFis = null;
        File file=null;

        try {
            file=new File(adresseFichierLocal);
            FileInputStream in=new FileInputStream(adresseFichierLocal);
            byte[] fileBytes=in.readAllBytes();
            long tailleFichier=fileBytes.length;
            long nbBlocs=tailleFichier/tailleMaxBloc;
            fis = new FileInputStream(file);
            brFis = new BufferedReader(new InputStreamReader(fis, "UTF-8"), 2048);

            byte c;
            boolean dernierBlocAtteint=false;
            int numBloc=1;
//            for(int b=1;b<nbBlocs;b++){
            while(!dernierBlocAtteint){
                int nbBytes=512;
                byte[] fileData=new byte[tailleMaxBloc];
               // c=in.read(buffer);

                for(int i=0;i<512;i++){
//                    c= (byte) brFis.read();
                    c= (byte) brFis.read();
                    if(c==-1) {
                        nbBytes=i;
                        System.out.println("la lecture du fichier a atteint le dernier bloc "+numBloc);
                        dernierBlocAtteint=true;
                        //sendData((byte)numBloc,buffer,buffer.length,ipServeur);
                        break;
                    }
                    else{
                        //fileData[i]=(byte)c;
                        fileData[i]=c;
                    }

                }
//                System.arraycopy(buffer, i, fileBytes, 0, tailleMaxBloc);

                sendData((byte)numBloc,fileData,nbBytes,ipServeur);

                short codeRetourACK=ecouteACK(numBloc);

                numBloc++;
            }
//
//
//            for(int b=1;b<nbBlocs;b++) {
//                in.read(buffer );
//                sendData((byte) numBloc, fileData,tailleMaxBloc , ipServeur);
//                short codeRetourACK = ecouteACK(numBloc);


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
