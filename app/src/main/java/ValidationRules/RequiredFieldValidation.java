package ValidationRules;


/**
 * Created by Home on 5/10/2015.
 */
public class RequiredFieldValidation {
    public static boolean isEmpty(String text) {
        return text.compareTo("") == 0;
    }

}
