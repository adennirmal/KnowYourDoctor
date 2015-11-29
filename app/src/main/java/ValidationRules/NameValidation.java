package ValidationRules;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Name validation
public class NameValidation {
    public static boolean isValidName(String name) {
        String NAME_PATTERN = "^[A-Za-z .]+$";

        Pattern pattern = Pattern.compile(NAME_PATTERN);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }
}
