package ValidationRules;


//Required field validation
public class RequiredFieldValidation {
    public static boolean isEmpty(String text) {
        return text.compareTo("") == 0;
    }

}
