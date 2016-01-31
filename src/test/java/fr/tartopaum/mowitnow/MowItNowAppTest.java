package fr.tartopaum.mowitnow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MowItNowAppTest {

    private MowItNowApp app;

    private MowItNowParser parser;
    private OrderExecutor executor;

    @Before
    public void init() {
        parser = new MowItNowParserImpl();
        executor = new OrderExecutorImpl();

        app = new MowItNowApp(parser, executor);
    }

    @Test
    public void testParseOk() throws Exception {
        String input = "5 5\n"
                + "1 2 N\n"
                + "GAGAGAGAA\n"
                + "3 3 E\n"
                + "AADAADADDA";

    // exécution
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Reader reader = new InputStreamReader(new ByteArrayInputStream(input.getBytes("UTF-8")), "UTF-8");
                PrintStream out = new PrintStream(baos, false, "UTF-8")) {
            app.execute(reader, out);
        }

    // vérifications
        String result = new String(baos.toByteArray(), "UTF-8");
        Assert.assertEquals(
                  "1 3 N" + System.lineSeparator()
                + "5 1 E" + System.lineSeparator(),
                result);
    }

}
