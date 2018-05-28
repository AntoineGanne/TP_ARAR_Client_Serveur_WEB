import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.Iterator;

public class Util {
    /*
    Socket socketTCP;
    InetAddress ipRecep; //adresseIP de la derniere personne a avoir envoyé un dp
    int portRecep; //port de la derniere personne a avoir envoyé un dp
    */

    protected final static int portServeur = 80;
    protected final static String ipServeur = "127.0.0.1";
    protected Socket connexion;
    protected InputStream in;
    protected OutputStream out;
    protected BufferedReader br;

    final static String CRLF = "\r\n";

    protected Util() {}

    /**
     * Permet d'intiialiser les flux d'entrée et de sortie de l'utilisateur.
     */
    public void initialiserStreams(){
        try {
            in = connexion.getInputStream();
            br = new BufferedReader(new InputStreamReader(in, "UTF-8"), 2048);
            out = connexion.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Permet de fermer la connexion avec un autre utilisateur.
     */
    protected void fermerConnexion(){
        try {
            connexion.close();
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return vrai si la connexion n'as pas encore été fermée
     */
    protected boolean connexionEstActive(){
            return !connexion.isClosed();
    }

    /**
     * Permet d'envoyer un message à un autre utilisateur.
     * @param request Message à envoyer.
     */
    public void send(String request) {
        if (!request.endsWith(CRLF)) request += CRLF;
        try {
            out.write(request.getBytes());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Permet d'envoyer un fichier texte à un autre utilisateur.
     * @param address Adresse du fichier à envoyer (depuis le poste de l'émetteur)
     * @throws IOException
     */
    public void fileToStream(String address) throws IOException {
        FileInputStream fis = null;
        BufferedReader br = null;
        try {
            fis = new FileInputStream(address);
            br = new BufferedReader(new InputStreamReader(fis, "UTF-8"), 2048);

            int c;
            while ((c = br.read()) != -1) {
                out.write(c);
            }
            out.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (br != null) br.close();
            if (fis != null) fis.close();
        }
    }

    /**
     * Permet de récupérer le fichier texte reçu dans le flux d'entrée du socket
     * et de le stocker dans le poste de l'utilisateur.
     * @param address Adresse vers laquelle stocker le fichier reçu.
     * @throws IOException
     */
    public void streamToFile(String address) throws IOException {
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

    /**
     * Permet d'envoyer une image à un autre utilisateur.
     * L'image reçue par le destinataire sera forcément sous format .jpg.
     * @param address Adresse de l'image à envoyer (depuis le poste de l'émetteur)
     */
    public void imageToStream(String address) {
        BufferedImage img;
        try {
            img = ImageIO.read(new File(address));
            ImageIO.write(img, "jpg", out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Permet de récupérer l'image reçue dans le flux d'entrée du socket
     * et de le stocker dans le poste de l'utilisateur.
     * @param address Adresse vers laquelle stocker l'image reçue.
     * @throws IOException
     */
    public void streamToImage(String address) throws IOException {
        ImageInputStream input = null;
        BufferedImage img;
        try {
            input = ImageIO.createImageInputStream(in);
            img = ImageIO.read(input);
            ImageIO.write(img, "jpg", new File(address));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) input.close();
        }
    }

/*

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
