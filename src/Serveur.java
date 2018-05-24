import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Serveur {
    private ServerSocket socServ;
    private Socket connexionClient;


    InputStream inS;
    OutputStream outS;

    private static int portServeur=80;

    public static void main(String[] args){
        Serveur s=new Serveur();
        s.connexion();
        s.initialiserStreams();
        s.ecouterClient();


        s.fermerConnexion();
    }

    public Serveur(){

    }

    private void connexion(){
        try {
            socServ =new ServerSocket(portServeur);
            connexionClient = socServ.accept();
            System.out.println("connexion acceptée"+ connexionClient.getInetAddress()+ " port: "+ connexionClient.getPort());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fermerConnexion(){
        try {
            socServ.close();
            connexionClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initialiserStreams(){
        try {
            inS=connexionClient.getInputStream();
            outS=connexionClient.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String ecouterClient(){
        try {
            char s=(char)inS.read();
            while(s!=-1){
               System.out.print(s);
               s=(char)inS.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "yo yo yo";
    }

}
