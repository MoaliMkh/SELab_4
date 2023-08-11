import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import Parser.ParserFacade;

public class Main {
    public static void main(String[] args) {
        ParserFacade parserFacade = new Parser.ParserFacade();
        try {
            parserFacade.startParse(new Scanner(new File("src/main/resources/code")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
