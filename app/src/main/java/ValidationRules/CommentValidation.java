package ValidationRules;

import android.content.Context;

/**
 * Created by Aden on 9/8/2015.
 */
public class CommentValidation {

    static String[] SPLIT_COMMENT;

    public static String[] checkComment(String comment, int wordsArrayId, int ignoreWordsArrayId, Context context) {
        SPLIT_COMMENT = comment.split("\\s+"); //Split comment from spaces
        String[] wordsFromArray = context.getResources().getStringArray(wordsArrayId);
        String[] wordsToIgnore = context.getResources().getStringArray(ignoreWordsArrayId);
        int words;
        String[] result = {"0", ""};
        for (words = 0; words < SPLIT_COMMENT.length; words++) {
            String word = SPLIT_COMMENT[words].toLowerCase();

            int i;
            for (i = 0; i < wordsFromArray.length; i++) {
                int j;
                boolean ignore = false;
                for (j = 0; j < wordsToIgnore.length; j++) {
                    if (word.equalsIgnoreCase(wordsToIgnore[j])) {
                        ignore = true;
                    }
                }
                if (ignore) {
                    break; //Go to next word if the word should be ignored
                }
                if (word.contains(wordsFromArray[i])) {
                    SPLIT_COMMENT[words] = "";
                    result[0] = String.valueOf(-1);
                }
            }
        }
        //Generate the new sentence
        String newSentence;
        String prefix = "";
        StringBuilder sb = new StringBuilder();
        int i;
        for (i = 0; i < SPLIT_COMMENT.length; i++) {
            sb.append(prefix);
            prefix = " ";
            sb.append(SPLIT_COMMENT[i]);
            ////newSentence += splitComment[i].toString() + " ";
        }
        newSentence = sb.toString();
        result[1] = newSentence;

        return result;
    }
}
