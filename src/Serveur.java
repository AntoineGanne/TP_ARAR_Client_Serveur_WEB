import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.lang.String;
import java.lang.Byte;

public class Serveur extends Util{

    public List<Byte> lectureFichier(String adressseFichier){
        List<Byte>  input =new ArrayList<Byte>();
        int i,b;
        try{
            FileInputStream f= new FileInputStream(adressseFichier);
            b=f.read();
            for (i = 0; b!=-1; i++) {

                input.add((byte)b);
                b=f.read();
            }


        }
        catch(IOException ex){
            System.out.println(ex);
        }

        System.out.println();
    }
}
