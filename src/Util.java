import java.io.*;
import java.net.*;

public class Util {
    Socket socketTCP;
    InetAddress ipRecep; //adresseIP de la derniere personne a avoir envoyé un dp
    int portRecep; //port de la derniere personne a avoir envoyé un dp

    static int PORT_SERVER=1500;


    protected Util(){

    }

    public void fileToStream(String address, OutputStream out) throws IOException {
        InputStream in = null;
        BufferedReader br = null;
        try {
            in = new FileInputStream(address);
            br = new BufferedReader(new InputStreamReader(in, "UTF-8"), 2048);

            int c;
            while ((c = br.read()) != -1) {
                out.write(c);
            }
            br.close();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) br.close();
            if (in != null) in.close();
        }

    }

    public void streamToFile(InputStream in, String address) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(address);

            byte[] buffer = new byte[2048];
            int length;
            while ((length = in.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        } catch(IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) fos.close();
        }
    }

/*
    protected void envoyer(String data,String ip, int port){
        try {
            InetAddress a =InetAddress.getByName(ip);
            envoyer(data,a,port);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    protected void envoyer(String data,InetAddress ip, int port){
        DatagramPacket dp;
        try {
            dp = new DatagramPacket(data.getBytes(),data.length(),ip,port);
            ds.send(dp);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    protected int ecouterPort(byte[] data){
        DatagramPacket dp=ecouter(data);
        return dp.getPort();
    }

    protected DatagramPacket ecouter(byte[] data){
        return ecouter(data,false);
    }

    protected DatagramPacket ecouter(byte[] data,boolean printData){
        DatagramPacket dp=new DatagramPacket(data,data.length);
        try {
            ds.receive(dp);
            if(printData){
                String str = new String(data, StandardCharsets.UTF_8);
                str=str.substring(0,dp.getLength());
                System.out.println("data:"+str);
            }
            ipRecep=dp.getAddress();
            portRecep=dp.getPort();
            return dp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dp;
    }

    protected void repondre(String data){
        envoyer(data,ipRecep,portRecep);
    }

    public static List<Integer> scan(int dep,int fin){
        ArrayList<Integer> res=new ArrayList<>();
        DatagramSocket ds;
        for(int i=dep;i<=fin;i++){
            try {
                ds=new DatagramSocket(i);
                res.add(i);
                ds.close();
            } catch (SocketException e) {
//                System.out.println(i);
            }
        }
//        System.out.println("\n Fin des ports occupés. \n  ports libres:");
        return res;
    }

    protected int getPortEcoute(){
        return ds.getLocalPort();
    }

*/

}
