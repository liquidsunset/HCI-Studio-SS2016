import com.leapmotion.leap.Vector;

import java.math.BigDecimal;

/**
 * Created by liquidsunset on 18.05.16.
 */
public final class LeapCalcFunctions {

    public static double calcAngelBetweenVectorsInDegrees(Vector v1, Vector v2) {
        BigDecimal angle = new BigDecimal(Math.toDegrees(v1.angleTo(v2)));
        return angle.setScale(LeapFXConstant.DOUBLE_ROUND_DECIMAL, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static int getRectangleFromAngel(double angelToXAxis) {
        double[] angels;
        switch (LeapFXConstant.COUNT_ELEMENTS){
            case 3:
                angels = LeapFXConstant.angelForThreeElements;
                break;
            case 4:
                angels = LeapFXConstant.angelForFourElements;
                break;
            case 5:
                angels = LeapFXConstant.angelForFiveElements;
                break;
            default:
                return -1;
        }
        for (int i = 0; i < LeapFXConstant.COUNT_ELEMENTS; i++) {
            if (isBetween(angels[i], angels[i + 1], angelToXAxis)){
                return i;
            }
        }
        return -1;
    }

    private static boolean isBetween(double fromValue, double toValue, double value) {
        return toValue > fromValue ? value > fromValue && value <= toValue : value >= toValue && value < fromValue;
    }

}
