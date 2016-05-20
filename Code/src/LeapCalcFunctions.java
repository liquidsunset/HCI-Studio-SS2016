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

}
