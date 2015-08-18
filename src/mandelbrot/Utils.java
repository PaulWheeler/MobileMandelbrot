/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mandelbrot;

/**
 *
 * @author Owner
 */
public class Utils
{

    public static int doubleToInt(double value)
    {
        Double dValue = new Double(value);
        String doubleStr = dValue.toString();
        int index = doubleStr.indexOf(".");
        String intStr = doubleStr.substring(0, index);

        return Integer.parseInt(intStr);
    }

 public static boolean IsDivisable(int i, int divider)
 {
 return i%divider == 0;
 }

}
