package mayton.rdf;

import org.apache.commons.lang3.StringUtils;

public class RdfTools {


    public static String normalizeLiteral(String literal) {
        StringBuilder sb = new StringBuilder(literal.length());
        literal.chars().forEach(c -> {
            char ch = (char) c;
            if (ch == '(' || ch == ')' || ch == ' ') {
                sb.append('_');
            } else {
                sb.append(ch);
            }
        });
        return sb.toString();
    }

}
