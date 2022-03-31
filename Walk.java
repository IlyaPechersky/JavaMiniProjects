package lessons;

import java.io.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Walk {
    private static final String DEFAULT_HASH_SUM = "0000000000000000000000000000000000000000";

    public static void main(String[] args) throws FileNotFoundException, NoSuchAlgorithmException {
        String inputPath = args[0];
        String outputPath = args[1];

        Scanner scan = new Scanner(new File(System.getProperty("user.dir") + "\\" + inputPath));
        PrintWriter writer = new PrintWriter(System.getProperty("user.dir") + "\\" + outputPath);

        MessageDigest digest = MessageDigest.getInstance("SHA-1");

        while (scan.hasNextLine()) {
            String filePath = String.join("\\", scan.nextLine().split("/"));

            String fullPath = System.getProperty("user.dir") + "\\" + filePath;

            try {
                File currentDir = new File(fullPath);
                FileInputStream fileInputStream = new FileInputStream(currentDir);

                DigestInputStream digestInputStream = new DigestInputStream(fileInputStream, digest);
                byte[] bytes = new byte[1024];
                while (digestInputStream.read(bytes) > 0) ;

                byte[] resultByteArray = digest.digest();

                writer.println(String.format("%s %s",
                        bytesToHexString(resultByteArray),
                        filePath));
            } catch (FileNotFoundException exception) {
                writer.println(String.format("%s %s",
                        DEFAULT_HASH_SUM,
                        filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        scan.close();
        writer.close();
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            int value = b & 0xFF;
            if (value < 16) builder.append("0");
            builder.append(Integer.toHexString(value));
        }
        return builder.toString();
    }
}
