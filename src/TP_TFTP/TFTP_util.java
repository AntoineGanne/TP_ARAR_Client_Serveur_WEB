package TP_TFTP;

import java.net.DatagramSocket;
import java.net.SocketException;

public abstract class TFTP_util {
    DatagramSocket ds;

    //OP CODES
    static final short RRQ= 01;
    static final short WRQ= 02;
    static final short DATA= 03;
    static final short ACK= 04;
    static final short ERROR= 05;

    static final int portTFTP=69;


     TFTP_util(){
        try {
            ds=new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
}
