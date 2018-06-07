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


    public void sendWRQ(String nomFichierLocal,InetAddress adresseDistante){
        String contenuWRQ=separateur+WRQ+nomFichierLocal+(byte)0+"octet"+(byte)0;
        writeBytesOfString(contenuWRQ);



       DatagramPacket dp;
        try {
            dp = new DatagramPacket(contenuWRQ.getBytes(),contenuWRQ.length(),adresseDistante,portTFTP);
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
