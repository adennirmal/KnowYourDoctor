package ValidationRules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

//NIC Validation
public class NICValidation {
    // validating Registration Number
    public static boolean isValidNIC(String nicNo) {
        String nicNumPattern = "^\\d{9}(X|V)$";

        Pattern pattern = Pattern.compile(nicNumPattern);
        Matcher matcher = pattern.matcher(nicNo);
        return matcher.matches();
    }
}
