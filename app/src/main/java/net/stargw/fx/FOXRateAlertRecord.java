package net.stargw.fx;

import java.io.Serializable;

/**
 * Created by swatts on 01/04/18.
 */

public class FOXRateAlertRecord implements Serializable {
    String code1;
    String code2;
    boolean active = true;  // place holder for future use
    float oldValue;
    float value;
}
