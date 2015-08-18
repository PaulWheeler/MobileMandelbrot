/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mandelbrot;

/**
 * @author Owner
 */
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;
import java.io.*;
// Sony ericsson and Nokia enhancement
//import com.nokia.mid.ui.*;

public class MobileMandelbrot extends MIDlet implements CommandListener
{

    /**
     * A class for keeping the back light active on MIDP devices
     */
    class LightThread extends Thread
    {

        public void run()
        {

            System.out.println("I'm trying to keep the backlight on");
            while (true)
            {
                // Use one or the other lines of code
                //none specific device code
                display.flashBacklight(10);
                //Sony ericsson and Nokia code
                //com.nokia.mid.ui.DeviceControl.setLights(0,100);
                try
                {
                    Thread.sleep(6000);
                } catch (InterruptedException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * This class initiates file saving in a separate thread from the main class
     */
    class FileOps extends Thread
    {

        public void run()
        {
            int i = 0;
            try
            {
                plotter.serializePicture();
            } catch (IOException ex)
            {
                ex.printStackTrace();
                Alert fileOperations = new Alert("Mobile Mandelbrot", "I'm sorry, "
                        + "for some reason i couldn't save the picture to your phone"
                        , null, AlertType.INFO);
                fileOperations.setTimeout(5000);
                display.setCurrent(fileOperations);
                i = 1;
            }
            if (i == 0)
            {
                Alert fileOperations = new Alert("Mobile Mandelbrot",
                        "Your picture has been saved to\n" + plotter.getFullPath(),
                        null, AlertType.INFO);
                fileOperations.setTimeout(5000);
                display.setCurrent(fileOperations);
            }
        }
    }
    public static Display display;
    private PlotEngine plotter;
    private RecordStore rs = null;
    private RecordEnumeration re;
    private int recordNO;
    private int index = 0;
    private String state = new String("On");
    private Command exitCommand = new Command("Exit", Command.EXIT, 0); // The exit command
    private Command palletOkCommand = new Command("Ok", Command.OK, 2); // The ok command
    private Command backCommand = new Command("Back", Command.BACK, 10);
    private Command iterationMenuCommand = new Command("Iterations", Command.ITEM, 5);
    private Command okItCommand = new Command("Ok", Command.OK, 1);
    private Command aboutCommand = new Command("About", Command.ITEM, 9);
    private Command helpCommand = new Command("Help", Command.ITEM, 8);
    private Command sellectPalletCommand = new Command("Select pallet", Command.ITEM, 4);
    //private Command recordManagementCommand = new Command("Load / Save", Command.ITEM, 5);
    private Command saveRecordCommand = new Command("Save position", Command.ITEM, 3);
    private Command loadRecordCommand = new Command("Load position", Command.ITEM, 2);
    private Command okRecordCommand = new Command("Ok", Command.ITEM, 1);
    private Command infoCommand = new Command("Fractal info", Command.ITEM, 7);
    private Command ScreenSizeCommand = new Command("Screen size ", Command.ITEM, 6);
    private Command ScreenSizeOkCommand = new Command("Ok",Command.OK,1);
    private Command saveImageCommand = new Command("Snapshot", Command.ITEM, 1);
    private TextField textField;
    private TextBox textBox;
    private Form form;
    private String selectedPallet;
    //private int itterations = 50;
    private int newIterations = 0;
    private List paletteList = new List("Select one", List.EXCLUSIVE);
    private List recordList = new List("Select one", List.EXCLUSIVE);
    private List screenList = new List("Select one", List.EXCLUSIVE);
    private Image logo;
    private boolean fullScreen = false;
    private Thread replot;

    public MobileMandelbrot()
    {
        paletteList.append("Rainbowlicious", null);
        paletteList.append("Zebra Zedebara", null);
        paletteList.append("Red or Dead", null);
        paletteList.append("Pastal Funkiness", null);
        paletteList.append("Benoits first view", null);
        paletteList.append("Stripe Tastic", null);
        screenList.append("Standard Screen", null);
        screenList.append("Full screen", null);
        display = Display.getDisplay(this);
        //Create a RMS
        try
        {
            rs = RecordStore.openRecordStore("myRecord", true);
            rs.addRecord(null, 0, 0);
            rs.closeRecordStore();
        } catch (Exception e)
        {
            System.out.println(e);
        }

        plotter = new PlotEngine(50, -2, 2, 4, "colour", false);

    }

    public void startApp()
    {
        Thread keepPhoneActive = new LightThread();
        keepPhoneActive.setPriority(10);
        keepPhoneActive.start();

        plotter.setCommandListener(this);
        plotter.addCommand(exitCommand);
        plotter.addCommand(iterationMenuCommand);
        plotter.addCommand(sellectPalletCommand);
        plotter.addCommand(aboutCommand);
        plotter.addCommand(helpCommand);
        plotter.addCommand(loadRecordCommand);
        plotter.addCommand(saveRecordCommand);
        plotter.addCommand(infoCommand);
        plotter.addCommand(ScreenSizeCommand);
        plotter.addCommand(saveImageCommand);
        display.setCurrent(plotter);
        //plotter.choosePallet();
        // display.flashBacklight(1200);
        //replot = plotter.new ReplotThread();
        if (plotter.getKindaBusy() == false)
        {
            plotter.setKindaBusy(true);
            plotter.new ReplotThread().start();
        }
    }

    public void pauseApp()
    {
    }

    public void destroyApp(boolean b)
    {
    }

    public void commandAction(Command c, Displayable d)
    {
        if (c == exitCommand)
        {
            destroyApp(false);
            notifyDestroyed();
        }
        if (c == palletOkCommand)
        {
            plotter.setExitThread(true);
            while (plotter.getKindaBusy() == true)
            {
                System.out.println("Still busy. Waiting.......");
            }
            System.out.println("pallet number " + paletteList.getSelectedIndex());
            if (paletteList.getSelectedIndex() == 0)
            {
                plotter.setExitThread(false);
                selectedPallet = "colour";
                System.out.println("Colour from main!");
                plotter.setPallet("colour");
                display.setCurrent(plotter);

                plotter.new ReplotThread().start();

            }
            if (paletteList.getSelectedIndex() == 1)
            {
                plotter.setExitThread(false);
                selectedPallet = "zebra";
                System.out.println("Zebra from menu in main");
                plotter.setPallet("zebra");
                display.setCurrent(plotter);
                plotter.new ReplotThread().start();
            }
            if (paletteList.getSelectedIndex() == 2)
            {
                plotter.setExitThread(false);
                selectedPallet = "redMood";
                System.out.println("Red mood from menu in main");
                plotter.setPallet("redMood");
                display.setCurrent(plotter);
                plotter.new ReplotThread().start();
            }
            if (paletteList.getSelectedIndex() == 3)
            {
                plotter.setExitThread(false);
                selectedPallet = "greyScale";
                System.out.println("Pastel from main");
                plotter.setPallet("pastel");
                display.setCurrent(plotter);
                plotter.new ReplotThread().start();
            }
            if (paletteList.getSelectedIndex() == 4)
            {
                plotter.setExitThread(false);
                selectedPallet = "benoit";
                System.out.println("benoit from main");
                plotter.setPallet("benoit");
                display.setCurrent(plotter);
                plotter.new ReplotThread().start();
            }
            if (paletteList.getSelectedIndex() == 5)
            {
                plotter.setExitThread(false);
                selectedPallet = "stripe";
                System.out.println("Stripe from main");
                plotter.setPallet("stripe");
                display.setCurrent(plotter);
                plotter.new ReplotThread().start();
            }

            //plotter.new ReplotThread().start();



        }

        if (c == loadRecordCommand)
        {
            System.out.println("Load record");
            try
            {
                rs = RecordStore.openRecordStore("myRecord", false);
                byte bytesFromRecordStore[] = rs.getRecord(1);
                String s = new String(bytesFromRecordStore);
                String a = new String();
                String b = new String();
                String e = new String();
                String f = new String();
                String h = new String();
                String g = new String();
                System.out.println(s);
                rs.closeRecordStore();
                int firstChar = 0;
                int lastChar = 0;

                lastChar = s.indexOf(":", lastChar);
                a = s.substring(firstChar, lastChar);
                firstChar = lastChar + 1;

                lastChar = s.indexOf(":", firstChar);
                b = s.substring(firstChar, lastChar);
                firstChar = lastChar + 1;


                lastChar = s.indexOf(":", firstChar);
                e = s.substring(firstChar, lastChar);
                firstChar = lastChar + 1;


                lastChar = s.indexOf(":", firstChar);
                f = s.substring(firstChar, lastChar);
                firstChar = lastChar + 1;


                lastChar = s.indexOf(":", firstChar);
                h = s.substring(firstChar, lastChar);
                firstChar = lastChar + 1;

                lastChar = s.indexOf(":", firstChar);
                g = s.substring(firstChar, lastChar);
                //firstChar = lastChar+1;


                System.out.println("String a = " + a);
                System.out.println("String b = " + b);
                System.out.println("String e = " + e);
                System.out.println("String f = " + f);
                System.out.println("String h = " + h);
                System.out.println("String g = " + g);

                //plotter = new PlotEngine(Integer.parseInt(a),
                //      Double.parseDouble(b),Double.parseDouble(e),
                //    Double.parseDouble(f),g);

                plotter.setExitThread(true);

                plotter.setIters(Integer.parseInt(a));
                plotter.setStartPositionX(Double.parseDouble(b));
                plotter.setStartPositionY(Double.parseDouble(e));
                plotter.setFractalWidth(Double.parseDouble(f));
                plotter.setFractalHeight(Double.parseDouble(h));
                plotter.setPallet(g);
                display.setCurrent(plotter);

 
                plotter.new ReplotThread().start();
            } catch (Exception e)
            {
                System.out.println(e);
            }
        }

        if (c == saveRecordCommand)
        {
            System.out.println("Saving initiated");
            try
            {

                rs = RecordStore.openRecordStore("myRecord", false);
                //index++;
                byte b[] = (plotter.getIters()
                        + ":" + plotter.getStartPositionX()
                        + ":" + plotter.getStartPositionY()
                        + ":" + plotter.getFractalWidth()
                        + ":" + plotter.getFractalHeight()
                        + ":" + plotter.getPallet()
                        + ":").getBytes();
                //Adding record to record store
                rs.setRecord(1, b, 0, b.length);
                rs.closeRecordStore();
                System.out.println("Record added");
                display.setCurrent(plotter);

                Alert saveConf = new Alert("Mobile Mandelbrot", "Fractal detail and position has been saved", null, AlertType.INFO);
                saveConf.setTimeout(5000);
                display.setCurrent(saveConf);
                System.out.println("saveConf selected");
            } catch (Exception e)
            {
                System.out.println(e);
            }
        }
        if (c == iterationMenuCommand)
        {
            System.out.println("Iterations activated");
            textField = new TextField("Number of iterations to be calculated",
                    "" + plotter.getIters(), 5, TextField.NUMERIC);
            form = new Form("Iterations");
            form.addCommand(backCommand);
            form.addCommand(okItCommand);
            form.append(textField);
            form.setCommandListener(this);
            display.setCurrent(form);

        }
        if (c == okItCommand)
        {
            if(plotter.getKindaBusy()==true)
            {
            plotter.setExitThread(true);
            while (plotter.getKindaBusy() == true)
            {
                System.out.println("Still busy. Waiting.......");
            }
            }
            display.setCurrent(plotter);
            String numberString;
            numberString = textField.getString();
            newIterations = Integer.parseInt(numberString);
            plotter.changeIterations(newIterations);
            plotter.new ReplotThread().start();

        }
        if (c == backCommand)
        {
            display.setCurrent(plotter);

            System.out.println("Back command Activated");
        }
        if (c == aboutCommand)
        {
            Alert about = new Alert("Mobile Mandelbrot", "Programmed by Paul Wheeler"
                    + "\n Version 2.0\n Copyright 2010"
                    + "\n Rest in peace Benoit Mandelbrot", null, AlertType.INFO);
            about.setTimeout(5000);
            display.setCurrent(about);
            System.out.println("About command selected");
        }
        if (c == helpCommand)
        {
            System.out.println("Help command selected\n");
            TextField t = new TextField("Help Section",
                    "To zoom in: press 3"
                    + "\nTo zoom out: press 1"
                    + "\n Use directional keys to scroll around image."
                    + "\n Change the iterations to reaveal more detail", 500, TextField.UNEDITABLE);
            form = new Form("Help");
            form.addCommand(backCommand);
            form.append(t);
            form.setCommandListener(this);
            display.setCurrent(form);

        }
        if (c == sellectPalletCommand)
        {
            //paletteList = new Form("PalletSelection");
            paletteList.addCommand(backCommand);
            paletteList.addCommand(palletOkCommand);
            paletteList.setCommandListener(this);
            display.setCurrent(paletteList);

        }
        if (c == infoCommand)
        {
            System.out.println("Info command selected\n");
            TextField t = new TextField("Fractal Info",
                    "x position = " + plotter.getCenterFracPositionX()
                    + "\ny position = " + plotter.getCenterFracPositionY()
                    + "\nMagnification = " + (4 / plotter.getFractalWidth())
                    + "X", 500, TextField.UNEDITABLE);
            form = new Form("Mobile Mandelbrot");
            form.addCommand(backCommand);
            form.append(t);
            form.setCommandListener(this);
            display.setCurrent(form);
        }



        if (c == ScreenSizeCommand)
        {

        //paletteList = new Form("PalletSelection");
            screenList.addCommand(backCommand);
            screenList.addCommand(ScreenSizeOkCommand);
            screenList.setCommandListener(this);
            display.setCurrent(screenList);
            System.out.println("Screen Select command selected");
            
        }
        if (c == ScreenSizeOkCommand)
        {
            plotter.setExitThread(true);
            while (plotter.getKindaBusy() == true)
            {
                System.out.println("Still busy. Waiting.......");
            }
            if (screenList.getSelectedIndex() == 1)
            {
                plotter.setExitThread(false);
                plotter.setScreenToggle(true);
                fullScreen = true;
                state = "off";
                System.out.println("Fullscreen activated" + "\nState = " + state);
                plotter.new ReplotThread().start();
                display.setCurrent(plotter);
            }

            if (screenList.getSelectedIndex() == 0)
            {
                plotter.setExitThread(false);
                plotter.setScreenToggle(false);
                fullScreen = false;
                state = "on";
                System.out.println("Fullscreen De-activatd" + "\nState = " + state);
                plotter.setExitThread(false);
                plotter.new ReplotThread().start();
                display.setCurrent(plotter);
            }
        }
        if (c == saveImageCommand)
        {
            new FileOps().start();
            System.out.println("Save image menu item selected");
        }

    }
}
