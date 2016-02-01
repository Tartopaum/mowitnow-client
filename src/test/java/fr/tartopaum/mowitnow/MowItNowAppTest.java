package fr.tartopaum.mowitnow;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.tartopaum.mowitnow.model.Coordinates;
import fr.tartopaum.mowitnow.model.Grid;
import fr.tartopaum.mowitnow.model.Order;
import fr.tartopaum.mowitnow.model.Orientation;
import fr.tartopaum.mowitnow.model.Mower;

public class MowItNowAppTest {

    /** Context JMock. */
    private Mockery context = new Mockery();

    /** L'application à tester. */
    private MowItNowApp app;

    /** Dépendance : parser. */
    private MowItNowParser parser;

    /** Dépendance : executor. */
    private OrderExecutor executor;

    @Before
    public void init() {
        parser = context.mock(MowItNowParser.class);
        executor = context.mock(OrderExecutor.class);

        app = new MowItNowApp(parser, executor);
    }

    /** Matcher permettant d'extraire une valeur passée en paramètre lors de l'appel d'une méthode d'un mock. */
    public static class ExtractionMatcher<T> extends BaseMatcher<T> {

        private T value;

        @Override
        @SuppressWarnings("unchecked")
        public boolean matches(Object item) {
            value = (T) item;
            return true;
        }

        @Override
        public void describeTo(Description description) {
        }

        public T getValue() {
            return value;
        }

    }

    /** Test d'intégration sur le jeu de test de l'énoncé. */
    @Test
    public void testOk() throws Exception {
        String input = "5 5\n"
                + "1 2 N\n"
                + "G\n"
                + "3 3 E\n"
                + "A";

        final ExtractionMatcher<MowItNowHandler> handlerExtractor = new ExtractionMatcher<>();

        final Grid grid = new Grid(6, 6);
        final Mower mower1 = new Mower(new Coordinates(1, 2), Orientation.NORTH);
        final Mower mower1End = new Mower(new Coordinates(1, 2), Orientation.WEST);
        final Mower mower2 = new Mower(new Coordinates(3, 3), Orientation.EAST);
        final Mower mower2End = new Mower(new Coordinates(4, 3), Orientation.EAST);

    // exécution
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Reader reader = new InputStreamReader(new ByteArrayInputStream(input.getBytes("UTF-8")), "UTF-8");
                PrintStream out = new PrintStream(baos, false, "UTF-8")) {

            context.checking(new Expectations() {
                {
                    oneOf(parser).parse(with(reader), with(handlerExtractor));

                    oneOf(executor).execute(grid, mower1, Order.TURN_LEFT);
                    will(returnValue(mower1End));

                    oneOf(executor).execute(grid, mower2, Order.GO_FORWARD);
                    will(returnValue(mower2End));
                }
            });

            app.execute(reader, out);

            // simulation des ordres envoyés par le parseur
            MowItNowHandler handler = handlerExtractor.getValue();
            handler.begin(grid);

            handler.beginMower(mower1);
            handler.order(Order.TURN_LEFT);
            handler.endMower();

            handler.beginMower(mower2);
            handler.order(Order.GO_FORWARD);
            handler.endMower();

            handler.end();
        }

    // vérifications
        String result = new String(baos.toByteArray(), "UTF-8");
        Assert.assertEquals(
                  "1 2 W" + System.lineSeparator()
                + "4 3 E" + System.lineSeparator(),
                result);
    }

}
