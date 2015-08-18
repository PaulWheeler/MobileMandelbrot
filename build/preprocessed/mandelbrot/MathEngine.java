/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mandelbrot;

/**
 *
 * @author Owner
 */
public class MathEngine {

    private double zX;
    private double zY;
    private double zTempX;
    private double zTempY;
    private double cX;
    private double cY;
    private double abVal;
    private int itterations;
    private int breakoutItteration = 1;

    MathEngine(double c1, double c2, int itt) {
        //Initialise the variables
        cX = c1;
        cY = c2;
        zX = cX;
        zY = cY;
        itterations = itt;

        abVal = (zX*zX) + (zY*zY);
        //Start of the loop
        while (breakoutItteration < itterations && (abVal < 4)) {
            breakoutItteration++;

            //Finding Zsquared
            zTempX = (zX*zX) - (zY*zY);
            zTempY = (2 * zX * zY);
            syncZ();

            //Add C to Z Squared
            zTempX = zX + cX;
            zTempY = zY + cY;
            syncZ();

            //Test absolute value again
            abVal = (zX*zX) + (zY*zY);
        }
// This bit is to let the plottter know to colour a black pixel
        if(breakoutItteration == itterations)
        {
            breakoutItteration = -1;
        }
    }

    private void syncZ() {
        zX = zTempX;
        zY = zTempY;
    }

    public double getAbsoluteValue(){
        return abVal;
    }

    public int getBreakoutIteration(){
        return breakoutItteration;
    }
}


