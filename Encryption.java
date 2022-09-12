import BasicIO.ASCIIOutputFile;
import BasicIO.BinaryOutputFile;

import java.io.Serial;
import java.io.Serializable;

public class Encryption implements Serializable {

    private static final long SerialVersionUID = 100000l;

    private BinaryOutputFile file;
    String[] lines;
    String key;

    public Encryption(String[] text, String key){
        lines = text;
        this.key = key;
    }

    public void writeToFile(String path){
        file = new BinaryOutputFile(path + ".bin");
        file.writeObject(this);
    }
}
