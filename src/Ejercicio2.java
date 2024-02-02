import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Ejercicio2 {
    /**
     * Pide dos paths por consola y ejecuta el metodo ejecute
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String sourceFile = asksForPath();
        String destFile = asksForPath();
        ejecute(sourceFile, destFile);
    }

    /**
     * ejecuta la compresion de un archivo o directorio sourceFile
     * y la mueve a destFile
     * @param sourceFile
     * @param destFile
     */
    public static void ejecute(String sourceFile, String destFile) {
        CompletableFuture<Object> completableFuture = CompletableFuture.supplyAsync(
                () -> {
                    FileOutputStream fos;
                    try {
                        fos = new FileOutputStream("dirCompressed.zip");
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                    ZipOutputStream zipOut = new ZipOutputStream(fos);
                    File fileToZip = new File(Path.of(sourceFile).toString());
                    try {
                        zipFile(fileToZip, fileToZip.getName(), zipOut);
                        zipOut.close();
                        fos.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                }
        ).whenComplete((object, throwable) -> {
            try {
                if (Files.exists(Path.of(destFile)))
                    Files.delete(Path.of(destFile));
                Files.move(Path.of("dirCompressed.zip"), Path.of(destFile));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ;
        });
        completableFuture.join();
    }

    /**
     * Pide un path por consola
     * @return
     */
    private static String asksForPath() {
        System.out.println("Introduce el Path: ");
        Scanner scanner = new Scanner(System.in);
        return Objects.requireNonNull(scanner.nextLine(), " ");
    }

    /**
     * Comprime un archivo
     * @param fileToZip
     * @param fileName
     * @param zipOut
     * @throws IOException
     */
    public static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }
}