import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
    Socket connexionServ;
    InputStream inC;
    OutputStream outC;

    private static int portServeur=80;

    private static String ipServeur="127.0.0.1";

    public static void main(String[] args){
        Client c=new Client();
        c.connexion();
        c.initialiserStreams();
        c.envoyerServeur("bonjour  gentil serveur");
        c.fermerConnexion();
    }


    Client(){

    }

    private void connexion(){
        try {
            connexionServ =new Socket(ipServeur,portServeur);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void fermerConnexion(){
        try {
            connexionServ.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initialiserStreams(){
        try {
            inC=connexionServ.getInputStream();
            outC=connexionServ.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void envoyerServeur(String requete){
        try {
            outC.write(requete.getBytes());
            outC.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
