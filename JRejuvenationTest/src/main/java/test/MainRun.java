package test;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jrejuvenation.api.Rejuvenation;

/**
 *
 * @author Reza Azizi (azizi.ra@gmail.com)
 * @version 1.0.1
 */
public class MainRun implements Rejuvenation{

    int i=0;
    
    @Override
    public void OnStart(Map map) {
              
        Integer index = (Integer)map.get("index");
        if(index == null){
            map.put("index", new Integer(i));
        }else{
            
            i = index.intValue();
                        
        }
    }

    @Override
    public void RunStep(Map map) {
        try {
            
            i++;
            
            Thread.sleep(1000); // wait a second for slow presentation
            System.out.println("test i=" + i);
            
            
        } catch (InterruptedException ex) {
            Logger.getLogger(MainRun.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public void OnStop(Map map) {
        map.put("index", new Integer(i));
    }
    
}
