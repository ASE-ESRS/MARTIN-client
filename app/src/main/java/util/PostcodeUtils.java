package util;

import java.util.regex.Pattern;

/**
 * PostcodeUtils provides utilities for working with Postcodes within the application.
 * @author Daniel Arthur
 */
public interface PostcodeUtils {

    /**
     * Determines whether a given postcode is a valid UK postcode
     * @param postcode The postcode to be validated
     * @return True if the postcode is a valid UK postcode, false otherwise.
     */
    public static boolean isValidPostcode(String postcode) {
        String regex = "^[a-zA-Z]{1,2}[0-9Rr][0-9a-zA-Z]?\\s?[0-9][abdABD-hjlnpHJLNP-uwUW-zZ]{2}$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(postcode).matches();
    }

}
