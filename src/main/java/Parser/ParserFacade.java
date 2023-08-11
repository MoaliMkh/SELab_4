package Parser;

import java.util.Scanner;

public class ParserFacade {
    private final Parser parser;

    public ParserFacade() {
        this.parser = new Parser();
    }

    public void startParse(Scanner scanner) {
        this.parser.startParse(scanner);
    }
}
