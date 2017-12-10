package util;

import java.util.regex.Pattern;

/**
 * Created by danny on 09/12/2017.
 */

public class PostcodeUtils {

    public static boolean isValidPostcode(String postcode) {
        String regex = "^[A-Z]{1,2}[0-9R][0-9A-Z]? [0-9][ABD-HJLNP-UW-Z]{2}$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(postcode).matches();
    }

}
