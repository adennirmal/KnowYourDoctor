package pack.knowyourdoctor.Validators;

import android.content.Context;

import pack.knowyourdoctor.R;

/**
 * Created by Aden on 9/8/2015.
 */
public class CommentValidation {

    static String[] splitComment;
    public static String[] checkComment (String comment, int wordsArrayId, Context context)
    {
        splitComment = comment.split("\\s+");
        String[] wordsFromArray = context.getResources().getStringArray(wordsArrayId);
        int words;
        String[] result = {"0",""};
        for (words = 0; words < splitComment.length; words++)
        {
            String word = splitComment[words].toLowerCase();
            int i;
            for (i = 0; i < wordsFromArray.length; i++) {
                if (word.contains(wordsFromArray[i])) {
                    splitComment[words] = "";
                    result[0] = String.valueOf(-1);
                }
            }
        }
        int i;
        String newSentence = "";
        for (i = 0; i < splitComment.length; i++) {
            newSentence += splitComment[i].toString() + " ";
        }
        result[1] = newSentence;

        return result;
    }
}
