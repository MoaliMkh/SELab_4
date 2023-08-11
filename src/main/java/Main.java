import java.io.FileNotFoundException;

import Parser.ParserFacade;

public class Main {
    public static void main(String[] args) {
        ParserFacade parserFacade = new Parser.ParserFacade();
        try {
            parserFacade.startParse();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
