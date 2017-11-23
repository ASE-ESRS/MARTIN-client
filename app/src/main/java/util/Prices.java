package util;

/**
 * Created by danny on 23/11/2017.
 */

public class Prices {

    public static double priceIntensity(int price) {
        return priceIntensity(price, 400000, 125000);
    }

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

}
