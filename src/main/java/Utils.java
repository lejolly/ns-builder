import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class Utils {

    public static String readFile(File file, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(file.toPath());
        return new String(encoded, encoding);
    }

    public static void writeFile(String file, String contents) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(contents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
