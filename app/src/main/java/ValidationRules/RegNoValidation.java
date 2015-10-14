package ValidationRules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Home on 5/10/2015.
 */
public class RegNoValidation {
    // validating Registration Number
    public static boolean isValidRegNo(String regNo) {
        String regNoPatter = "[1-9]{0,1}[0-9]{0,4}";
        Pattern pattern = Pattern.compile(regNoPatter);
        Matcher matcher = pattern.matcher(regNo);
        return matcher.matches();
    }

    // validating Registration Number
    public static boolean isValidRegNoWithRequiredValidation(String regNo) {
        String regNoPatter = "[1-9][0-9]{0,4}";
        Pattern pattern = Pattern.compile(regNoPatter);
        Matcher matcher = pattern.matcher(regNo);
        return matcher.matches();
    }
}
