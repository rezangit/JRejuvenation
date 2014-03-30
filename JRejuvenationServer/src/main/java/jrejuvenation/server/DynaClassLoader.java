package jrejuvenation.server;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Reza Azizi (azizi.ra@gmail.com)
 * @version 1.0.1
 */
public class DynaClassLoader {

    String path = System.getProperty("user.dir", "n/a");

    Object instance;
    Method mOnStart;
    Method mOnStop;
    Method mOnRunStep;

    public void loader() {

        Class classToLoad;
        URLClassLoader child;

        path += File.separator + "deploy";

        File file = new File(path);
        String[] list = file.list();

        if (list.length < 1) {

            System.out.println("Plugin not found, Add service plugin in server/deploy folder.");

        }

        try {

            URL[] classes = {new File(list[0]).toURI().toURL()};

            child = new URLClassLoader(classes);

            classToLoad = Class.forName("test.MainRun", true, child);
            instance = classToLoad.newInstance();

            mOnStart = classToLoad.getDeclaredMethod("OnStart", Map.class);
            mOnStop = classToLoad.getDeclaredMethod("OnStop", Map.class);
            mOnRunStep = classToLoad.getDeclaredMethod("RunStep", Map.class);

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void OnStart(Map dataMap) {
        try {

            mOnStart.invoke(instance, dataMap);

        } catch (IllegalAccessException ex) {
            Logger.getLogger(DynaClassLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(DynaClassLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(DynaClassLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void OnStop(Map dataMap) {
        try {

            mOnStop.invoke(instance, dataMap);

        } catch (IllegalAccessException ex) {
            Logger.getLogger(DynaClassLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(DynaClassLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(DynaClassLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void RunStep(Map dataMap) {
        try {

            mOnRunStep.invoke(instance, dataMap);

        } catch (IllegalAccessException ex) {
            Logger.getLogger(DynaClassLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(DynaClassLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(DynaClassLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
