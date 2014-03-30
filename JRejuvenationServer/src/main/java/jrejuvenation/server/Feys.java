package jrejuvenation.server;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Reza Azizi (azizi.ra@gmail.com)
 * @version 1.0.1
 */
public class Feys {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        int flag = 0;
        do {
            
            Feys ff = new Feys();
            flag = ff.startSystem();
        } while (flag == 0);
    }

    public int startSystem() {

        try {


            BaseNet b = new BaseNet();
            b.start();


        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
            Runtime.getRuntime().runFinalization();
            Runtime.getRuntime().gc();
            return 0;
        } catch (Throwable ex) {
            System.out.println(ex.getMessage());
            Runtime.getRuntime().runFinalization();
            Runtime.getRuntime().gc();
            return 0;
        }

        return 0;
    }
}
