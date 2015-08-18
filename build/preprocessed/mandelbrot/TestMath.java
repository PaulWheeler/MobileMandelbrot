/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mandelbrot;

/**
 *
 * @author paulwheeler
 */
public class TestMath {

   private int calculateMandelbrotIterations(float x, float y, int MAX_ITERATIONS) {

    float xx = 0.0f;

    float yy = 0.0f;

    int iter = 0;

    while (xx * xx + yy * yy <= 4.0f && iter<MAX_ITERATIONS) {

        float temp = xx*xx - yy*yy + x;

        yy = 2.0f*xx*yy + y;

        xx = temp;

        iter ++;

    }

    return iter;
}


}
