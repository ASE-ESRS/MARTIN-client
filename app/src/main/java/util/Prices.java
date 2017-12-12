package util;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * The Prices class provides static methods for gauging the intensity of a price up against different bounds.
 * @author Daniel Arthur
 */
public interface Prices {

    /**
     * Returns a price intensity based on UK house prices (max 400,000, min 125,000)
     * @param price The price to be assessed
     * @return A double between 0.0 and 1.0 inclusive that is the intensity of price
     */
    public static double priceIntensity(int price) {
        return priceIntensity(price, 400000, 125000);
    }

    /**
     * Returns a price intensity based on two bounds
     * @param price The price to be assessed
     * @param max_price The upper bound to assess the price against
     * @param min_price The lower bound to assess the price against
     * @return A double between 0.0 and 1.0 inclusive that is the intensity of price
     */
    public static double priceIntensity(int price, int max_price, int min_price) {
        if(price >= max_price) {
            return 1.0;
        } else if(price <= min_price) {
            return 0.0;
        } else {
            double diff = max_price - min_price;
            return (price - min_price) / diff;
        }
    }

    public static String priceToString(int price) {
        return NumberFormat.getCurrencyInstance(Locale.UK).format(price);
    }

}
