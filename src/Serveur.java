import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Serveur {
    private ServerSocket socServ;
    private Socket connexionClient;

    private static int portServeur=80;

    public static void main(String[] args){
        Serveur s=new Serveur();
        s.connexion();
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

}
