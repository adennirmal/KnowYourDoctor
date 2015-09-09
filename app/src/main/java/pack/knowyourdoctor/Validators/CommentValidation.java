package pack.knowyourdoctor.Validators;

import android.content.Context;

import pack.knowyourdoctor.R;

/**
 * Created by Aden on 9/8/2015.
 */
public class CommentValidation {

    static String[] splitComment;
    public static String[] checkComment (String comment, int wordsArrayId, int ignoreWordsArrayId, Context context)
    {
        splitComment = comment.split("\\s+"); //Split comment from spaces
        String[] wordsFromArray = context.getResources().getStringArray(wordsArrayId);
        String[] wordsToIgnore = context.getResources().getStringArray(ignoreWordsArrayId);
        int words;
        String[] result = {"0",""};
        for (words = 0; words < splitComment.length; words++)
        {
            String word = splitComment[words].toLowerCase();

            int i;
            for (i = 0; i < wordsFromArray.length; i++) {
                int j;
                boolean ignore = false;
                for (j = 0; j < wordsToIgnore.length; j++)
                {
                    if (word.equalsIgnoreCase(wordsToIgnore[j]))
                    {
                        ignore = true;
                    }
                }
                if (ignore)
                {
                    break; //Go to next word if the word should be ignored
                }
                if (word.contains(wordsFromArray[i])) {
                    splitComment[words] = "";
                    result[0] = String.valueOf(-1);
                }
            }
        }
        //Generate the new sentence
        String newSentence;
        String prefix = "";
        StringBuilder sb = new StringBuilder();
        int i;
        for (i = 0; i < splitComment.length; i++) {
            sb.append(prefix);
            prefix = " ";
            sb.append(splitComment[i]);
            //newSentence += splitComment[i].toString() + " ";
        }
        newSentence = sb.toString();
        result[1] = newSentence;

        return result;
    }
}