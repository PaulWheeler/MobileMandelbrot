/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mandelbrot;

import javax.microedition.lcdui.*;
//import com.nokia.mid.ui.DeviceControl.*;
import java.io.*;
import javax.microedition.io.*;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.Connector;
import javax.microedition.media.MediaException;
import java.lang.Double;

/**
 *
 * @author Owner
 */
public class PlotEngine extends Canvas
{

    private double screenResX; //X Width of screen
    private double screenResY; //Y Hight of screen
    private double fractalPixelX;//The space a pixel takes up in the fractal scale
    private double fractalPixelY;// The space a pixel takes up in the fractal scale
    private double fracPointX; // The X point on the fractal scale
    private double fracPointY;// The Y point of the fractal scale
    private double fractalWidth;
    private double fractalHeight;
    private double centerFractalPositionX;
    private double centerFractalPositionY;
    private double startPositionX = 2;
    private double startPositionY = 2;
    private int itters = 50;// Fractal itterations
    private int breakoutItteration;
    private int red = 0;
    private int green = 0;
    private int blue = 0;
    private int[] redArray;
    private int[] greenArray;
    private int[] blueArray;
    private int pallet;
    private Image buffer;
    private String fileName;
    private String fullPath;
    private String path;
    private boolean screenToggle;
    private boolean kindaBusy = false;
    public boolean exitThread = false;
    Graphics gc;
    private MathEngine bob;
    private String palletSelector;
    private FileConnection fc1 = null;
    private DataOutputStream out = null;
    private DataInputStream in = null;

    public PlotEngine(int itt, double startX, double startY,
            double fWidth, String selectPallet, boolean fullScreen)
    {
        //com.nokia.mid.ui.DeviceControl.setLights(0,99);
        screenToggle = fullScreen;
        setScreenToggle(fullScreen);
        palletSelector = selectPallet;
        startPositionX = startX;
        startPositionY = startY;
        fractalWidth = fWidth;
        itters = itt;
        setScreenSize();
        //Initialise the colour arrays
        redArray = new int[8];
        greenArray = new int[8];
        blueArray = new int[8];
        for (int n = 0; n < 8; n++)
        {
            redArray[n] = (n * 32);
            greenArray[n] = (n * 32);
            blueArray[n] = (n * 32);
        }
    }

    public static boolean isOdd(int i)
    {
        return (i & 1) != 0;
    }

    public void checkExitThread()
    {
    }

    public void paintToZebraBuffer()
    {
        fractalPixelX = fractalWidth / screenResX; // Each pixel represents fractalPixelX on the fractal scale
        fractalPixelY = fractalHeight / screenResY; // Each pixel represents fractalPixelY on the fractal scale
        for (int x = 0; x <= screenResX; x = x + 1)
        {
            fracPointX = ((fractalPixelX * x) + getStartPositionX());

            for (int y = 0; y <= screenResY; y = y + 1)
            {
                if (exitThread == true)
                {
                    //setKindaBusy(false);
                    x = Utils.doubleToInt(screenResX) + 1;
                    y = Utils.doubleToInt(screenResY) + 1;
                } else
                {
                    fracPointY = (fractalPixelY * -y + getStartPositionY());
                    bob = new MathEngine(fracPointX, fracPointY, itters);
                    if (isOdd(bob.getBreakoutIteration()))
                    {
                        gc.setColor(0, 0, 0);
                    } else
                    {
                        gc.setColor(255, 255, 255);
                    }

                    gc.drawRect(Utils.doubleToInt(x), Utils.doubleToInt(y), 1, 1);
                }
            }
            repaint();
            serviceRepaints();
        }
        centerFractalPositionX = (fractalWidth / 2) + getStartPositionX();
        centerFractalPositionY = (fractalHeight / 2) + getStartPositionY();
        System.out.println("Zebra Plot engine has completed");
        exitThread = false;
        kindaBusy= false;
    }

    public void paintToColourBuffer()
    {
        //setFullScreenMode(true);
        fractalPixelX = fractalWidth / screenResX; // Each pixel represents fractalPixelX on the fractal scale
        fractalPixelY = fractalHeight / screenResY; // Each pixel represents fractalPixelY on the fractal scale
        for (int x = 0; x <= screenResX; x = x + 1)
        {
            fracPointX = ((fractalPixelX * x) + getStartPositionX());

            for (int y = 0; y <= screenResY; y = y + 1)
            {
                if (exitThread == true)
                {
                    x = Utils.doubleToInt(screenResX) + 1;
                    
                    y = Utils.doubleToInt(screenResY) + 1;
                }else{
                fracPointY = (fractalPixelY * -y + getStartPositionY());
                bob = new MathEngine(fracPointX, fracPointY, itters);
                breakoutItteration = bob.getBreakoutIteration();

                if (breakoutItteration == -1)
                {
                    gc.setColor(0, 0, 0);
                } else
                {
                    pallet = Utils.doubleToInt(breakoutItteration / 8) % 6;
                    //Red to yellow
                    if (pallet == 0)
                    {
                        red = 255;
                        blue = 0;
                        green = greenArray[breakoutItteration % 8];
                    }
                    //Yellow to green
                    if (pallet == 1)
                    {
                        green = 255;
                        blue = 0;
                        red = 255 - (redArray[breakoutItteration % 8]);
                    }
                    //Green to turquoise
                    if (pallet == 2)
                    {
                        green = 255;
                        red = 0;
                        blue = blueArray[breakoutItteration % 8];
                    }
                    //Turquoise to blue
                    if (pallet == 3)
                    {
                        blue = 255;
                        red = 0;
                        green = 255 - (redArray[breakoutItteration % 8]);
                    }
                    //Blue to purple
                    if (pallet == 4)
                    {
                        blue = 255;
                        green = 0;
                        red = redArray[breakoutItteration % 8];
                    }
                    //Purple to red
                    if (pallet == 5)
                    {
                        red = 255;
                        green = 0;
                        blue = 255 - (blueArray[breakoutItteration % 8]);
                    }
                    gc.setColor(red, green, blue);
                }

                gc.drawRect(Utils.doubleToInt(x), Utils.doubleToInt(y), 1, 1);
            }}
            repaint();
            serviceRepaints();
        }
        centerFractalPositionX = (fractalWidth / 2) + getStartPositionX();
        centerFractalPositionY = (fractalHeight / 2) + getStartPositionY();
        System.out.println("Colour Plot engine has completed");
        exitThread = false;
        kindaBusy= false;
    }

    public void paintToRedBuffer()
    {
        //setFullScreenMode(true);
        fractalPixelX = fractalWidth / screenResX; // Each pixel represents fractalPixelX on the fractal scale
        fractalPixelY = fractalHeight / screenResY; // Each pixel represents fractalPixelY on the fractal scale
        for (int x = 0; x <= screenResX; x = x + 1)
        {
            fracPointX = ((fractalPixelX * x) + getStartPositionX());

            for (int y = 0; y <= screenResY; y = y + 1)
            {
                if (exitThread == true)
                {
                    x = Utils.doubleToInt(screenResX) + 1;
                    y = Utils.doubleToInt(screenResY) + 1;
                }else{
                fracPointY = (fractalPixelY * -y + getStartPositionY());
                bob = new MathEngine(fracPointX, fracPointY, itters);
                breakoutItteration = bob.getBreakoutIteration();

                if (breakoutItteration == -1)
                {
                    gc.setColor(0, 0, 0);
                } else
                {
                    green = 0;
                    blue = 0;
                    red = (breakoutItteration % 32 * 8);
                    //System.out.println("BreakoutItteration % 256 = "+breakoutItteration % 256);
                    gc.setColor(red, green, blue);
                }

                gc.drawRect(Utils.doubleToInt(x), Utils.doubleToInt(y), 1, 1);
            }}
            repaint();
            serviceRepaints();
        }
        centerFractalPositionX = (fractalWidth / 2) + getStartPositionX();
        centerFractalPositionY = (fractalHeight / 2) + getStartPositionY();
        System.out.println("Red Plot engine has completed");
        exitThread = false;
        kindaBusy= false;
    }

    public void paintToPastelBuffer()
    {
        //setFullScreenMode(true);
        fractalPixelX = fractalWidth / screenResX; // Each pixel represents fractalPixelX on the fractal scale
        fractalPixelY = fractalHeight / screenResY; // Each pixel represents fractalPixelY on the fractal scale
        for (int x = 0; x <= screenResX; x = x + 1)
        {
            fracPointX = ((fractalPixelX * x) + getStartPositionX());

            for (int y = 0; y <= screenResY; y = y + 1)
            {
                if (exitThread == true)
                {
                    x = Utils.doubleToInt(screenResX) + 1;
                    
                    y = Utils.doubleToInt(screenResY) + 1;
                }else{
                fracPointY = (fractalPixelY * -y + getStartPositionY());
                bob = new MathEngine(fracPointX, fracPointY, itters);
                breakoutItteration = bob.getBreakoutIteration();

                if (breakoutItteration == -1)
                {
                    gc.setColor(0, 0, 0);
                } else
                {
                    red = (breakoutItteration % 16 * 16);
                    green = (breakoutItteration % 32 * 8);
                    blue = (breakoutItteration % 64 * 4);
                    //System.out.println("BreakoutItteration % 256 = "+breakoutItteration % 256);
                    gc.setColor(red, green, blue);
                }

                gc.drawRect(Utils.doubleToInt(x), Utils.doubleToInt(y), 1, 1);
            }}
            repaint();
            serviceRepaints();
        }
        centerFractalPositionX = (fractalWidth / 2) + getStartPositionX();
        centerFractalPositionY = (fractalHeight / 2) + getStartPositionY();
        System.out.println("Greyscale Plot engine has completed");
        exitThread = false;
        kindaBusy= false;
    }

    public void paintToBenoitBuffer()
    {
        //setFullScreenMode(true);
        fractalPixelX = fractalWidth / screenResX; // Each pixel represents fractalPixelX on the fractal scale
        fractalPixelY = fractalHeight / screenResY; // Each pixel represents fractalPixelY on the fractal scale

        for (int x = 0; x <= screenResX; x = x + 1)
        {
            fracPointX = ((fractalPixelX * x) + getStartPositionX());

            for (int y = 0; y <= screenResY; y = y + 1)
            {
                if (exitThread == true)
                {
                    x = Utils.doubleToInt(screenResX) + 1;
              
                    y = Utils.doubleToInt(screenResY) + 1;
                }else{
                fracPointY = (fractalPixelY * -y + getStartPositionY());
                bob = new MathEngine(fracPointX, fracPointY, itters);
                breakoutItteration = bob.getBreakoutIteration();

                if (breakoutItteration == -1)
                {
                    gc.setColor(0, 0, 0);
                } else
                {
                    red = 255;
                    green = 255;
                    blue = 255;
                    //System.out.println("BreakoutItteration % 256 = "+breakoutItteration % 256);
                    gc.setColor(red, green, blue);
                }

                gc.drawRect(Utils.doubleToInt(x), Utils.doubleToInt(y), 1, 1);
            }}

            repaint();
            serviceRepaints();

        }
        centerFractalPositionX = (fractalWidth / 2) + getStartPositionX();
        centerFractalPositionY = (fractalHeight / 2) + getStartPositionY();
        //kindaBusy=false;
        System.out.println("Benoit Plot engine has completed");
        exitThread = false;
        kindaBusy= false;
    }

    public void paintToStripeBuffer()
    {
        //setFullScreenMode(true);
        fractalPixelX = fractalWidth / screenResX; // Each pixel represents fractalPixelX on the fractal scale
        fractalPixelY = fractalHeight / screenResY; // Each pixel represents fractalPixelY on the fractal scale
        for (int x = 0; x <= screenResX; x = x + 1)
        {
            fracPointX = ((fractalPixelX * x) + getStartPositionX());

            for (int y = 0; y <= screenResY; y = y + 1)
            {
                if (exitThread == true)
                {
                    x = Utils.doubleToInt(screenResX) + 1;

                    y = Utils.doubleToInt(screenResY) + 1;
                }else{
                fracPointY = (fractalPixelY * -y + getStartPositionY());
                bob = new MathEngine(fracPointX, fracPointY, itters);
                breakoutItteration = bob.getBreakoutIteration();

                if (breakoutItteration == -1)
                {
                    gc.setColor(0, 0, 0);
                } else
                {
                    if (isOdd(bob.getBreakoutIteration()))
                    {
                        pallet = Utils.doubleToInt(breakoutItteration / 8) % 6; // was 8
                        //Red to yellow
                        if (pallet == 3)
                        {
                            red = 255;
                            blue = 128;
                            green = greenArray[breakoutItteration % 8];
                        }
                        //Yellow to green
                        if (pallet == 4)
                        {
                            green = 255;
                            blue = 128;
                            red = 255 - (redArray[breakoutItteration % 8]);
                        }
                        //Green to turquoise
                        if (pallet == 5)
                        {
                            green = 255;
                            red = 128;
                            blue = blueArray[breakoutItteration % 8];
                        }
                        //Turquoise to blue
                        if (pallet == 0)
                        {
                            blue = 255;
                            red = 128;
                            green = 255 - (redArray[breakoutItteration % 8]);
                        }
                        //Blue to purple
                        if (pallet == 1)
                        {
                            blue = 255;
                            green = 128;
                            red = redArray[breakoutItteration % 8];
                        }
                        //Purple to red
                        if (pallet == 2)
                        {
                            red = 255;
                            green = 128;
                            blue = 255 - (blueArray[breakoutItteration % 8]);
                        }
                    } else
                    {
                        pallet = Utils.doubleToInt(breakoutItteration / 8) % 6;
                        //Red to yellow
                        if (pallet == 3)
                        {
                            red = 255;
                            blue = 0;
                            green = greenArray[breakoutItteration % 8];
                        }
                        //Yellow to green
                        if (pallet == 4)
                        {
                            green = 255;
                            blue = 0;
                            red = 255 - (redArray[breakoutItteration % 8]);
                        }
                        //Green to turquoise
                        if (pallet == 5)
                        {
                            green = 255;
                            red = 0;
                            blue = blueArray[breakoutItteration % 8];
                        }
                        //Turquoise to blue
                        if (pallet == 0)
                        {
                            blue = 255;
                            red = 0;
                            green = 255 - (redArray[breakoutItteration % 8]);
                        }
                        //Blue to purple
                        if (pallet == 1)
                        {
                            blue = 255;
                            green = 0;
                            red = redArray[breakoutItteration % 8];
                        }
                        //Purple to red
                        if (pallet == 2)
                        {
                            red = 255;
                            green = 0;
                            blue = 255 - (blueArray[breakoutItteration % 8]);
                        }
                    }
                    //System.out.println("BreakoutItteration % 256 = "+breakoutItteration % 256);
                    gc.setColor(red, green, blue);
                }

                gc.drawRect(Utils.doubleToInt(x), Utils.doubleToInt(y), 1, 1);
            }}
            repaint();
            serviceRepaints();
        }
        centerFractalPositionX = (fractalWidth / 2) + getStartPositionX();
        centerFractalPositionY = (fractalHeight / 2) + getStartPositionY();
        System.out.println("Strip Plot engine has completed");
        exitThread = false;
        kindaBusy= false;
    }

    protected void paint(Graphics g)
    {

        g.drawImage(buffer, 0, 0, 0);
        //kindaBusy = false;
    }

    public void setIters(int newItteration)
    {
        itters = newItteration;
    }

    public int getIters()
    {
        return itters;
    }

    public int getScreenHeight()
    {
        return getHeight();
    }

    public void setBufferSize()
    {
        buffer.createImage(getScreenHeight(), getScreenWidth());
    }

    public int getScreenWidth()
    {
        return getWidth();
    }

    public double getCenterFracPositionX()
    {
        return (fractalWidth / 2) + startPositionX;
    }

    public void setCenterFracPositionX(double posX)
    {
        centerFractalPositionX = posX;
    }

    public double getCenterFracPositionY()
    {
        return (fractalHeight / 2) - startPositionY;
    }

    public void setCenterFracPositionY(double posY)
    {
        centerFractalPositionY = posY;
    }

    public double getStartPositionX()
    {
        return startPositionX;
    }

    public void setStartPositionX(double spx)
    {
        startPositionX = spx;
    }

    public void setCenterPositionY(double centerY)
    {
        startPositionY = centerY;
    }

    public double getStartPositionY()
    {
        return startPositionY;
    }

    public void setStartPositionY(double spy)
    {
        startPositionY = spy;
    }

    public double getFractalWidth()
    {
        return fractalWidth;
    }

    public void setFractalWidth(double sfw)
    {
        fractalWidth = sfw;
    }

    public double getFractalHeight()
    {
        return fractalHeight;
    }

    public void setFractalHeight(double fh)
    {
        fractalHeight = fh;
    }

    public void changeIterations(int itterations)
    {
        repaint();
        itters = itterations;
    }

    public void setPallet(String plt)
    {
        palletSelector = plt;
    }

    public String getPallet()
    {
        return palletSelector;
    }

    public void setScreenToggle(boolean state)
    {
        if (state == true)
        {
            setFullScreenMode(true);
        } else
        {
            setFullScreenMode(false);

        }
        setScreenSize();
        buffer = Image.createImage(getScreenWidth(), getScreenHeight());
        gc = buffer.getGraphics();
        //choosePallet();
        //repaint();
    }

    public void setScreenSize()
    {
        screenResY = getHeight();
        screenResX = getWidth();
        fractalHeight = (fractalWidth / screenResX) * screenResY;
        fractalPixelX = fractalWidth / getWidth(); // Each pixel represents fractalPixelX on the fractal scale
        fractalPixelY = fractalHeight / getWidth(); // Each pixel represents fractalPixelY on the fractal scale
    }

    public String getFullPath()
    {
        return fullPath;
    }

    public boolean getKindaBusy()
    {
        return kindaBusy;
    }

    public void setKindaBusy(boolean state)
    {
        kindaBusy = state;

    }

    public boolean getExitThread()
    {
        return exitThread;
    }

    public void setExitThread(boolean state)
    {
        exitThread = state;
        //kindaBusy = !state;
    }

    public void choosePallet()
    {
        if (palletSelector == null ? "zebra" == null : palletSelector.equals("zebra"))
        {
            paintToZebraBuffer();

        }
        if (palletSelector == null ? "colour" == null : palletSelector.equals("colour"))
        {
            paintToColourBuffer();

        }
        if (palletSelector == null ? "redMood" == null : palletSelector.equals("redMood"))
        {
            System.out.println("RedMood from PlotEngine");
            paintToRedBuffer();

        }
        if (palletSelector == null ? "pastel" == null : palletSelector.equals("pastel"))
        {
            System.out.println("Pastel from PlotEngine");
            paintToPastelBuffer();

        }
        if (palletSelector == null ? "benoit" == null : palletSelector.equals("benoit"))
        {
            System.out.println("Benoit from PlotEngine");
            paintToBenoitBuffer();


        }
        if (palletSelector == null ? "stripe" == null : palletSelector.equals("stripe"))
        {
            System.out.println("Stripe from PlotEngine");
            paintToStripeBuffer();
        }
    }

    private void zebraPallet()
    {
        if (isOdd(bob.getBreakoutIteration()))
        {
            gc.setColor(0, 0, 0);
        } else
        {
            gc.setColor(255, 255, 255);
        }
    }

    /**
     * Saves image captured by camera.
     */
    public void captureAndSaveImage()
    {
        try
        {
            byte[] data = null;
            //Image image = Image.createImage(data, 0, length);
            ImageItem imageItem = new ImageItem(null, buffer, 0, null);
            //mForm.append(imageItem);
            // mForm.setTitle("Done.");

            fc1 = (FileConnection) Connector.open("file:///root1/mandelbrot.PNG");

            out = new DataOutputStream(fc1.openOutputStream());
            out.write(data);
        } catch (IOException ioe)
        {
            StringItem stringItem = new StringItem(null, ioe.toString());
        }
        try
        {
            if (in != null)
            {
                in.close();
            }
            if (fc1 != null)
            {
                fc1.close();
            }
        } catch (IOException ioe)
        {
        }

    }
    //byte[] content = get_Byte_Array(img);

    public byte[] getByteArray(Image img)
    {

        int[] imgRgbData = new int[img.getWidth() * img.getHeight()];
        byte[] imageData = null;
        try
        {
            img.getRGB(imgRgbData, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());
        } catch (Exception e)
        {
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        try
        {
            for (int i = 0; i < imgRgbData.length; i++)
            {
                dos.writeInt(imgRgbData[i]);
            }

            imageData = baos.toByteArray();
            baos.close();
            dos.close();
        } catch (Exception e)
        {
        }
        return imageData;
    }

    public void serializePicture()
            throws IOException
    {
// e:/picture and c:/picture are good for SonyEricsson. Not good on Nokia
        fileName = "mand0.png";
        path = "file:///e:/picture/";
        //it is never guarantied that the properties will exists, better check it!
        FileConnection dire = (FileConnection) Connector.open(path);
        if (dire.exists() == false)
        {
            path = "file:///c:/picture/";
            dire = (FileConnection) Connector.open(path);
            if (dire.exists() == false)
            {
                path = System.getProperty("fileconn.dir.memorycard");
                if (path == null)
                {
                    path = System.getProperty("fileconn.dir.photos");
                    if (path == null)
                    {
                        throw new IOException("Can't find a place to save your picture");
                    }
                }
            }
        }
        System.out.println("Path is not null");
        //String fullPath = null;


        if (path.endsWith("/"))
        {

            fullPath = path + fileName;

        } else
        {
            fullPath = path + '/' + fileName;
        }
        System.out.println("Fullpath = " + fullPath);
        FileConnection con = (FileConnection) Connector.open(fullPath, Connector.READ_WRITE);
        int fileNum = 0;
        int fileIndex;
        if (!con.exists())
        {
            System.out.println("con.exists() = " + con.exists());
            con.create();
            System.out.println("Con state now = " + con.exists());
        } else
        {
            while (con.exists() == true)
            {
                fileNum++;
                //fileIndex = fullPath.indexOf("mand");
                fileName = fullPath.substring(0, fullPath.indexOf("mand"));
                fullPath = fileName + "mand" + fileNum + ".png";
                System.out.println("File = " + fullPath);
                con = (FileConnection) Connector.open(fullPath, Connector.READ_WRITE);
            }
            con.create();
            System.out.println("con file already exists");
        }
        System.out.println("File conection bit done");
        System.out.println("DataOutputStream about to do");
        DataOutputStream dos = new DataOutputStream(con.openDataOutputStream());
        System.out.println("DataOutputStream done");
        byte[] pngData = PNGEncoder.toPNG(buffer, false);
        dos.write(pngData);
        System.out.println("Written to the buffer now");
        dos.flush();
        dos.close();
        con.close();
        System.out.println("Loose ends closed. End of method");
    }

    private int calculateMandelbrotIterations(float x, float y, int MAX_ITERATIONS)
    {

        float xx = 0.0f;

        float yy = 0.0f;

        int iter = 0;

        while (xx * xx + yy * yy <= 4.0f && iter < MAX_ITERATIONS)
        {

            float temp = xx * xx - yy * yy + x;

            yy = 2.0f * xx * yy + y;

            xx = temp;

            iter++;

        }

        return iter;
    }

    protected void keyPressed(int key)
    {
        int action = getGameAction(key);
        boolean gotAction = true, gotKey = true;

        switch (action)
        {
            case RIGHT:
                if (kindaBusy == false)
                {
                    kindaBusy = true;
                    new RightKeyThread().start();
                } else
                {
                    System.out.println("BUSY");
                }

                break;
            case LEFT:
                if (kindaBusy == false)
                {
                    kindaBusy = true;
                    new LeftKeyThread().start();
                } else
                {
                    System.out.println("BUSY");
                }

                break;
            case DOWN:
                if (kindaBusy == false)
                {
                    kindaBusy = true;
                    new DownKeyThread().start();
                } else
                {
                    System.out.println("BUSY");
                }

                break;
            case UP:
                if (kindaBusy == false)
                {
                    kindaBusy = true;
                    new UpKeyThread().start();
                } else
                {
                    System.out.println("BUSY");
                }

                break;
            case FIRE:
            default:
                gotAction = false;
        }


        if (!gotAction)
        {
            switch (key)
            {
                case KEY_NUM1:
                    if (kindaBusy == false)
                    {
                        kindaBusy = true;
                        new ZoomOutThread().start();
                    } else
                    {
                        System.out.println("BUSY");
                    }


                    break;
                case KEY_NUM3:
                    if (kindaBusy == false)
                    {
                        kindaBusy = true;
                        new ZoomInThread().start();
                    } else
                    {
                        System.out.println("BUSY");
                    }



                    break;
                case KEY_NUM0:
                    if (kindaBusy == false)
                    {
                        kindaBusy = true;
                        new ReplotThread().start();
                    } else
                    {
                        System.out.println("BUSY");
                    }


                    break;
                case KEY_NUM9:

                    System.out.println("getItters = " + this.getIters());
                    System.out.println("getStartPositionX = " + this.getStartPositionX());
                    System.out.println("getStartPositionY = " + this.getStartPositionY());
                    System.out.println("getFactalWidth = " + this.getFractalWidth());
                    System.out.println("getFractalHeight = " + this.getFractalHeight());
                    System.out.println("getPallet = " + this.getPallet());
                    break;
                default:
                    gotKey = false;
            }
        }
    }

    class RightKeyThread extends Thread
    {

        public void run()
        {
            System.out.println("Key thread experiment");
            centerFractalPositionX = centerFractalPositionX + (fractalWidth / 3);
            startPositionX = centerFractalPositionX - (fractalWidth / 2);
            choosePallet();
            repaint();
            MobileMandelbrot.display.flashBacklight(1200);
            MobileMandelbrot.display.vibrate(100);
            System.out.println("\nRight Button pressed");
            kindaBusy = false;
        }
    }

    class LeftKeyThread extends Thread
    {

        public void run()
        {
            System.out.println("Key thread experiment");
            centerFractalPositionX = centerFractalPositionX - (fractalWidth / 3);
            startPositionX = centerFractalPositionX - (fractalWidth / 2);
            choosePallet();
            repaint();
            MobileMandelbrot.display.flashBacklight(1200);
            MobileMandelbrot.display.vibrate(100);
            System.out.println("RIGHT");
            kindaBusy = false;
        }
    }

    class UpKeyThread extends Thread
    {

        public void run()
        {
            System.out.println("Key thread experiment");
            centerFractalPositionY = centerFractalPositionY + (fractalHeight / 3);
            startPositionY = centerFractalPositionY - (fractalHeight / 2);
            choosePallet();
            repaint();
            MobileMandelbrot.display.flashBacklight(1200);
            MobileMandelbrot.display.vibrate(100);
            System.out.println("DOWN");
            kindaBusy = false;
        }
    }

    class DownKeyThread extends Thread
    {

        public void run()
        {
            System.out.println("Key thread experiment");
            centerFractalPositionY = centerFractalPositionY - (fractalHeight / 3);
            startPositionY = centerFractalPositionY - (fractalHeight / 2);
            choosePallet();
            repaint();
            MobileMandelbrot.display.flashBacklight(1200);
            MobileMandelbrot.display.vibrate(100);
            kindaBusy = false;
        }
    }

    class ZoomOutThread extends Thread
    {

        public void run()
        {
            System.out.println("Key thread Zoom Out\n");
            System.out.println("Number 1 pressed");
            fractalWidth = fractalWidth * 2;
            fractalHeight = fractalHeight * 2;
            startPositionX = startPositionX - (fractalWidth / 4);
            startPositionY = startPositionY + (fractalHeight / 4);
            choosePallet();
            repaint();
            MobileMandelbrot.display.flashBacklight(1200);
            MobileMandelbrot.display.vibrate(100);
            kindaBusy = false;
        }
    }

    class ZoomInThread extends Thread
    {

        public void run()
        {
            System.out.println("KeyThread Zoom In\n");
            System.out.println("Number 3 pressed");
            startPositionX = startPositionX + (fractalWidth / 4);// was 4
            startPositionY = startPositionY - (fractalHeight / 4);// was 4
            fractalWidth = fractalWidth / 2; // was 2
            fractalHeight = fractalHeight / 2; // was 2
            choosePallet();
            repaint();
            MobileMandelbrot.display.flashBacklight(1200);
            MobileMandelbrot.display.vibrate(100);
            kindaBusy = false;
        }
    }

    class ReplotThread extends Thread
    {

        public void run()
        {
            kindaBusy = true;
            System.out.println("Key thread Replot\n");
            System.out.println("0 pressed; REPLOT");
            choosePallet();
            repaint();
            MobileMandelbrot.display.flashBacklight(1200);
            MobileMandelbrot.display.vibrate(100);
            exitThread = false;
            kindaBusy = false;
        }
    }
}
