import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

public class Ejercicio3 {
    public static void main(String[] args) throws InterruptedException {

        final String[] URLS = {
                "https://refactorizando.com/uso-de-completablefuture-en-java/",
                "https://refactorizando.com/uso-de-completablefuture-en-java/",
                "https://refactorizando.com/uso-de-completablefuture-en-java/",
                "https://refactorizando.com/uso-de-completablefuture-en-java/",
                "https://refactorizando.com/uso-de-completablefuture-en-java/",
                "https://refactorizando.com/uso-de-completablefuture-en-java/",
                "https://refactorizando.com/uso-de-completablefuture-en-java/",
                "https://refactorizando.com/uso-de-completablefuture-en-java/"
        };

        Ejercicio1 ejercicio1 = new Ejercicio1();
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(
                () -> {
                    int i = 1;
                    for (String path : URLS) {
                        ejercicio1.path = Path.of("src", "test", "pagina" + i + ".html");
                        ejercicio1.descargaURL(path);
                        i++;
                    }
                }
        ).whenComplete(
                (aVoid, throwable) -> {
                    Ejercicio2.ejecute("src\\test", "test.zip");
                }
        );
        completableFuture.join();
    }
}
