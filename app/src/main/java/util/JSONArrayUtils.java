package util;

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
}
