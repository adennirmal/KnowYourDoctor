package pack.knowyourdoctor.AnimationControllers;

import android.animation.PropertyValuesHolder;

import pack.knowyourdoctor.Constants.Strings;

//Handle animations
public class Animator {
    //Handle rotation animation
    public static PropertyValuesHolder rotation(float... values) {
        return PropertyValuesHolder.ofFloat(Strings.ROTATION, values);
    }

    //Handle translation of object in x axis
    public static PropertyValuesHolder translationX(float... values) {
        return PropertyValuesHolder.ofFloat(Strings.TRANSLATION_X, values);
    }

    //Handle translation of object in y axis
    public static PropertyValuesHolder translationY(float... values) {
        return PropertyValuesHolder.ofFloat(Strings.TRANSLATION_Y, values);
    }
}
