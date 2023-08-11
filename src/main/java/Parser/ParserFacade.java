package Parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ParserFacade {
    private final Parser parser;

    public ParserFacade() {
        this.parser = new Parser();
    }

    public void startParse() throws FileNotFoundException {
        this.parser.startParse(new Scanner(new File("src/main/resources/code")));
    }
}
