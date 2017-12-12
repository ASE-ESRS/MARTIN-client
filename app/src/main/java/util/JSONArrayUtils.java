package util;

import org.json.JSONArray;
import org.json.JSONException;

public interface JSONArrayUtils {

    public static int getMinPrice(JSONArray array) throws JSONException {
        int min = array.getJSONObject(0).getInt("price");
        for (int i = 1; i < array.length(); i++) {
            int currprice = array.getJSONObject(i).getInt("price");
            if (currprice < min) min = currprice;
        }
        return min;
    }

    public static int getMaxPrice(JSONArray array) throws JSONException {
        int max = array.getJSONObject(0).getInt("price");
        for (int i = 1; i < array.length(); i++) {
            int currprice = array.getJSONObject(i).getInt("price");
            if (currprice > max) max = currprice;
        }
        return max;
    }

    public static int getAveragePrice(JSONArray array) throws JSONException {
        long total = 0;
        for (int i = 0; i < array.length(); i++) {
            total += array.getJSONObject(i).getInt("price");
        }
        long avg = total / array.length();
        return (int) avg;
    }
}
