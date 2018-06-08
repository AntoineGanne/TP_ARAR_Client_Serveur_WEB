package TP_TFTP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class TFTP_Send  extends TFTP_util{


    public static void main(String[] arg){
        //String contenuWRQ=WRQ+"1"+separateur+"octet"+separateur;
        //System.out.println(contenuWRQ.getBytes());

        try {
            InetAddress ipServeur =InetAddress.getByName("127.0.0.1");
            TFTP_Send t=new TFTP_Send();
            t.sendWRQ("1",ipServeur);
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
        try {
            dp = new DatagramPacket(contenuWRQ,contenuWRQ.length,adresseDistante,portTFTP);
            ds.send(dp);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void ecouteACK(){
        byte[] ack=new byte[512];
        DatagramPacket dp=new DatagramPacket(ack,ack.length);
        try {
            ds.receive(dp);
            System.out.println(Arrays.toString(dp.getData()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte opCode=ack[1];
        //ByteBuffer dst =null;
        //byte[] ={dst.get(0), dst.get(1)};
        if(opCode==ACK){

        }
    }

    public short SendFile(String nomFichierLocal,String adresseDistante){
        try {
            InetAddress ipServeur =InetAddress.getByName(adresseDistante);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


        return codesRetour.SUCCESS;
    }
}
