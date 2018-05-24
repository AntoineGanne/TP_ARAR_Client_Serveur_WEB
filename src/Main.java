import java.io.*;

public class Main {

    public static void readFromFile(String address, OutputStream out) {
        try {
            InputStream in = new FileInputStream(address);
            BufferedReader br = new BufferedReader(new InputStreamReader(in), 2048);

            String line;
            int offset = 0;
            while ((line = br.readLine()) != null) {
                offset += line.length();
                System.out.println(line);
                out.write(line.getBytes("UTF-8"), offset, line.length());
            }
            br.close();
            in.close();
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //OutputStream out
    public static void writeInFile(InputStream in, OutputStream out) {
        try {
            int length;
            byte[] buffer = new byte[2048];
            while ((length = in.read(buffer)) > 0) {
                System.out.println((char)length);
                out.write(buffer, 0, length);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Serveur.lectureFichier("src/Fichier/test.txt");
    }
}