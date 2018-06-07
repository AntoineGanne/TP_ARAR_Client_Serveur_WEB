package TP_TFTP_RECEIVE;

import java.io.*;
import java.net.*;

public class Client {

    private static final String serverPumpkin = "127.0.0.1";
    private static final int portPumpkin = 69;
    private DatagramSocket ds;
    private DatagramPacket dp;
    private static final int sizePackets = 516;

    public Client() {
        try {
            ds = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void send(byte[] request) {
        try {
            InetAddress ip = InetAddress.getByName(serverPumpkin);
            dp = new DatagramPacket(request, request.length, ip, portPumpkin);
            ds.send(dp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveFile() {
        byte[] buffer;
        byte[] entete = new byte[2];
        int compteur = 1;
        while (leftDP()) {
            System.out.println("Paquet TFTP n°" + compteur);
            compteur++;

            buffer = new byte[sizePackets];
            dp = new DatagramPacket(buffer, buffer.length);
            try {
                ds.receive(dp);

                entete[0] = buffer[0]; // OPCODE
                entete[1] = buffer[1]; // ERROR CODE
                //if (entete[1] == DATA) {
                //}

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean leftDP() {
        return !(dp.getLength() < sizePackets);
    }

    public void writeInFile(byte[] datas, String localFile) {
        try {
            OutputStream out = new FileOutputStream(localFile);
            out.write(datas);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
