package Scanner;

import ErrorHandler.ErrorHandler;
import Scanner.token.Token;
import Scanner.type.Type;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class lexicalAnalyzer {
    private final Matcher matcher;

    public lexicalAnalyzer(java.util.Scanner sc) {
        StringBuilder input = new StringBuilder();
        while (sc.hasNext()) {
            input.append(sc.nextLine());
        }
        StringBuilder tokenPattern = new StringBuilder();
        for (Type tokenType : Type.values())
            tokenPattern.append(String.format("|(?<%s>%s)", tokenType.name(), tokenType.pattern));
        Pattern expression = Pattern.compile(tokenPattern.substring(1));
        matcher = expression.matcher(input.toString());
    }

    public Token getNextToken() {
        while (matcher.find()) {
            try {
                for (Type t : Type.values()) {
                    if (matcher.group(t.name()) != null) {
                        if (matcher.group(Type.COMMENT.name()) != null) {
                            break;
                        }
                        if (matcher.group(Type.ErrorID.name()) != null) {
                            throw new Exception();
                        }
                        return new Token(t, matcher.group(t.name()));
                    }
                }
            } catch (Exception e) {
                ErrorHandler.printError("The id must start with character");
            }
        }
        return new Token(Type.EOF, "$");
    }
}
