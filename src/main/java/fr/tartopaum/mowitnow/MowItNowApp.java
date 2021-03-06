package fr.tartopaum.mowitnow;

import java.io.PrintStream;
import java.io.Reader;
import java.text.MessageFormat;

import fr.tartopaum.mowitnow.exception.ExecutionException;
import fr.tartopaum.mowitnow.exception.HandlerException;
import fr.tartopaum.mowitnow.exception.ParseException;
import fr.tartopaum.mowitnow.model.Grid;
import fr.tartopaum.mowitnow.model.Order;
import fr.tartopaum.mowitnow.model.Mower;

/**
 * Application utilisant l'API mowitnow.
 * Lit un fichier et écrit dans le flux de sortie les situations finales des tondeuses.
 * @author Tartopaum
 */
public class MowItNowApp {

    private final MowItNowParser parser;
    private final OrderExecutor executor;

    public MowItNowApp(MowItNowParser parser, OrderExecutor executor) {
        super();
        this.parser = parser;
        this.executor = executor;
    }

    public void execute(final Reader in, final PrintStream out) throws ParseException, HandlerException {

        parser.parse(in, new MowItNowHandler() {

            private Grid grid;
            private Mower mower;

            @Override
            public void order(Order order) throws HandlerException {
                try {
                    mower = executor.execute(grid, mower, order);
                } catch (ExecutionException e) {
                    throw new HandlerException(e);
                }
            }

            @Override
            public void endMower() throws HandlerException {
                out.println(MowItNowApp.this.toString(mower));
            }

            @Override
            public void end() {

            }

            @Override
            public void beginMower(Mower mower) {
                this.mower = mower;
            }

            @Override
            public void begin(Grid grid) {
                this.grid = grid;
            }
        });
    }

    private String toString(Mower mower) throws HandlerException {
        String orientationString;
        switch (mower.getOrientation()) {
        case NORTH:
            orientationString = "N";
            break;
        case EAST:
            orientationString = "E";
            break;
        case WEST:
            orientationString = "W";
            break;
        case SOUTH:
            orientationString = "S";
            break;
        default:
            throw new HandlerException("Orientation non gérée : " + mower.getOrientation());
        }

        return MessageFormat.format("{0,number,#0} {1,number,#0} {2}",
                mower.getCoordinates().getX(),
                mower.getCoordinates().getY(),
                orientationString);
    }

}
