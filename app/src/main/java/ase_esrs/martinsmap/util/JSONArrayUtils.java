package ase_esrs.martinsmap.util;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Utility methods for house price data within JSONArrays
 * @author Dan and Mark
 */
public interface JSONArrayUtils {

    /**
     * Returns the smallest house price within the JSONArray
     * @param array The JSONArray of house price data
     * @return The smallest house price
     * @throws JSONException
     * @author Mark
     */
    public static int getMinPrice(JSONArray array) throws JSONException {
        int min = array.getJSONObject(0).getInt("price");
        for (int i = 1; i < array.length(); i++) {
            int currprice = array.getJSONObject(i).getInt("price");
            if (currprice < min) min = currprice;
        }
        return min;
    }

    /**
     * Returns the largest house price within the JSONArray
     * @param array The JSONArray of house price data
     * @return The largest house price
     * @throws JSONException
     * @author Mark
     */
    public static int getMaxPrice(JSONArray array) throws JSONException {
        int max = array.getJSONObject(0).getInt("price");
        for (int i = 1; i < array.length(); i++) {
            int currprice = array.getJSONObject(i).getInt("price");
            if (currprice > max) max = currprice;
        }
        return max;
    }

    /**
     * Returns the average of the house prices within the JSONArray
     * @param array The JSONArray of house price data
     * @return The average house price
     * @throws JSONException
     * @author Mark
     */
    public static int getAveragePrice(JSONArray array) throws JSONException {
        long total = 0;
        for (int i = 0; i < array.length(); i++) {
            total += array.getJSONObject(i).getInt("price");
        }
        long avg = total / array.length();
        return (int) avg;
    }

    public static String getCrimeNameFromCategory(String category) {
        switch (category) {
            case "all-crime":
                return "All crime";
            case "anti-social-behaviour":
                return "Anti-social behaviour";
            case "bicycle-theft":
                return "Bicycle theft";
            case "burglary":
                return "Burglary";
            case "criminal-damage-arson":
                return "Criminal damage & arson";
            case "drugs":
                return "Drugs";
            case "other-theft":
                return "Other theft";
            case "possession-of-weapons":
                return "Possession of weapons";
            case "public-order":
                return "Public order";
            case "robbery":
                return "Robbery";
            case "shoplifting":
                return "Shoplifting";
            case "theft-from-the-person":
                return "Theft from the person";
            case "vehicle-crime":
                return "Vehicle crime";
            case "violent-crime":
                return "Violence and sexual offences";
            case "other-crime":
                return "Other crime";
            default:
                return "";
        }
    }
}
