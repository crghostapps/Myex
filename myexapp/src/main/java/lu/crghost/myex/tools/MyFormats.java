package lu.crghost.myex.tools;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;


/**
 * Formatings and conversions in function of current locale
 * Created by CR on 18/05/2015.
 */
public class MyFormats {

    public static final int DECIMALS_LOCATIONS = 6;
    public static final int DECIMALS_ALTITUDE  = 1;

    /**
     * Convert String to BigDecimal
     * @param s
     * @return
     */
    public static BigDecimal parseDecimal(String s) {
        return parseDecimal(s,0);
    }

    /**
     * Convert String to BigDecimal
     * @param s
     * @param decimals
     * @return
     */
    public static BigDecimal parseDecimal(String s, int decimals) {
        BigDecimal big = BigDecimal.ZERO;
        if (s!=null && s.length() > 0) {
            try {
                NumberFormat nf = NumberFormat.getNumberInstance();
                nf.setMaximumFractionDigits(decimals);
                nf.setMinimumFractionDigits(decimals);
                nf.setRoundingMode(RoundingMode.HALF_UP);
                Number n = nf.parse(s);
                big = new BigDecimal(n.doubleValue());
            } catch (ParseException e) {
            }
        }
        return big;
    }

    /**
     * Format BigDecimal to String
     * @param big
     * @return
     */
    public static String formatDecimal(BigDecimal big) {
        return formatDecimal(big,0);
    }

    /**
     * Format BigDecimal to String
     * @param big
     * @param decimals
     * @return
     */
    public static String formatDecimal(BigDecimal big, int decimals) {
        String s = "";
        if (big!=null && big.compareTo(BigDecimal.ZERO) != 0 ) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            nf.setMaximumFractionDigits(decimals);
            nf.setMinimumFractionDigits(decimals);
            nf.setRoundingMode(RoundingMode.HALF_UP);
            s = nf.format(big.doubleValue());
        }
        return s;
    }

}
