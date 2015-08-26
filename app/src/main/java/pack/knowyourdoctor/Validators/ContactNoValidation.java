package pack.knowyourdoctor.Validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Home on 5/10/2015.
 */
public class ContactNoValidation {
    // validating Registration Number
    public static boolean isValidContactNo(String contactNo) {
        String contactNoPattern = "[0-9]{3}-[0-9]{7}";

        Pattern pattern = Pattern.compile(contactNoPattern);
        Matcher matcher = pattern.matcher(contactNo);
        return matcher.matches();
    }
}
