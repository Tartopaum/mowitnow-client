package fr.tartopaum.mowitnow;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Exemple d'utilisation de l'API mowitnow.
 * Se base sur le fichier dont le nom est passé en premier argument de la ligne de commandes.
 * @author Tartopaum
 */
public class Client {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            throw new Exception("Il faut spécifier le nom du fichier à charger");
        }

        final MowItNowParser parser = new MowItNowParserImpl();
        final OrderExecutor executor = new OrderExecutorImpl();
        final MowItNowApp app = new MowItNowApp(parser, executor);

        try (Reader reader = new InputStreamReader(new FileInputStream(args[0]), "UTF-8")) {
            app.execute(reader, System.out);
        }
    }

}
