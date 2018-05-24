
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.lang.String;
import java.lang.Byte;
import java.net.*;


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

    static public List<Byte> lectureFichier(String adressseFichier){
        List<Byte>  input =new ArrayList<Byte>();
        int i,b;
        try{
            FileInputStream f= new FileInputStream(adressseFichier);
            b=f.read();
            for (i = 0; b!=-1; i++) {

                input.add((byte)b);
                b=f.read();
            }
            f.close();

        }
        catch(IOException ex){
            System.out.println(ex);
        }
        /*
        String file_string = "";

        for( i = 0; i < input.size(); i++)
        {
            file_string += (char)input.get(i).intValue();
        }


        System.out.println(file_string);
        */
        return input;
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
