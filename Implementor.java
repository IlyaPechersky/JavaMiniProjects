package lessons;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Implementor {
    public static void main(String[] args) throws IOException {
        String interfaceName = args[0];
        String currentDir = System.getProperty("user.dir");

        File newClass = new File(currentDir + "\\" + interfaceName + "Impl.java");
        newClass.createNewFile();

        BufferedWriter fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newClass)));

        String packageName = currentDir.substring(currentDir.indexOf("java") + 5);
        if (!packageName.isEmpty()) {
            fileWriter.write("package " + packageName + ";\n\n");
        }

        ZipInputStream javaLibArchive = new ZipInputStream(
                new FileInputStream("C:\\Users\\foodg\\.jdks.\\openjdk-17.0.2\\lib\\src.zip"));
        ZipEntry entry;
        while ((entry = javaLibArchive.getNextEntry()) != null) {
            System.out.println(entry.getName());
            if (entry.getName().contains("/" + interfaceName + ".java")) {
                String importPathWithSuffix = entry.getName().substring(entry.getName().indexOf("/") + 1);
                String properImport = String.join(".",
                        importPathWithSuffix.substring(0, importPathWithSuffix.length() - 5).split("/"));

                File cacheFile = new File(currentDir + "\\cached.txt");
                FileOutputStream outputStream = new FileOutputStream(cacheFile);
                try {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = javaLibArchive.read(buf)) > 0) {
                        outputStream.write(buf, 0, len);
                    }
                } finally {
                    outputStream.close();
                }

                fileWriter.write("import " + properImport + ";\n\n");
            }
        }

        fileWriter.write("public class " + interfaceName + "Impl implements " + interfaceName + " {\n");

        BufferedReader reader = new BufferedReader(new FileReader("cached.txt"));
        String cachedLine;
        while ((cachedLine = reader.readLine()) != null) {
            if (!cachedLine.contains("interface") && !cachedLine.contains("*") && cachedLine.contains("(")) {
                fileWriter.write("\t@Override\n" +
                        (cachedLine.contains("public") ? "\t" : "\tpublic") +
                        cachedLine.substring(0, (!cachedLine.contains(";") ? cachedLine.length() : cachedLine.indexOf(";"))).strip());
                cachedLine = reader.readLine();
                if (cachedLine == null) break;
                if (cachedLine.contains("throw")) {
                    fileWriter.write("\n\t" +
                            cachedLine.substring(0, (!cachedLine.contains(";") ? cachedLine.length() : cachedLine.indexOf(";"))).strip());
                }
                fileWriter.write(" {\n\t}\n");
            }
        }

        fileWriter.write("}\n");


        fileWriter.close();
    }
}
