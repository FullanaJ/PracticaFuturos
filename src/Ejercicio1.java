import javax.net.ssl.SSLHandshakeException;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class Ejercicio1 {

    Path path = Path.of("src/pagina.html");
    /**
     * Pide una URL por consola
     * @return
     */
    private String asksForURL() {
        System.out.println("Introduce la URL: ");
        Scanner scanner = new Scanner(System.in);
        return Objects.requireNonNull(scanner.nextLine()," ");
    }

    /**
     * Descarga una URL y la guarda en un archivo
     * @param url
     */
    public void descargaURL(String url){

        CompletableFuture<HttpResponse<String>> completableFuture = CompletableFuture.supplyAsync(
                () ->{
                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .GET() // GET is default
                            .build();
                    try {
                        return client.send(request, HttpResponse.BodyHandlers.ofString());
                    }catch (SSLHandshakeException e) {
                        throw new RuntimeException(e);
                    }
                    catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
        ).thenApply(
                (response) -> {
                    System.out.println("Status code: " + response.statusCode());
                    System.out.println("Headers: " + response.headers());
                    return response;
                }
        ).thenApply(
                (response) -> {
                    if (!Files.exists(path)) {
                        try {
                            Files.createFile(path);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    try {
                        Files.writeString(path, response.body());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                }

        );
        completableFuture.join();
    }

    /**
     * Ejecuta la descarga de una URL y la muestra por consola
     * @param args
     */
    public static void main(String[] args) {
       Ejercicio1 ejercicio1 = new Ejercicio1();
       ejercicio1.descargaURL(ejercicio1.asksForURL());
        try {
            BufferedReader reader = new BufferedReader(Files.newBufferedReader(ejercicio1.path));
            reader.lines().forEach(System.out::println);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
