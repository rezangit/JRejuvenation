package jrejuvenation.api;

import java.util.Map;

/**
 *
 * @author Reza Azizi (azizi.ra@gmail.com)
 * @version 1.0.1
 */
public interface Rejuvenation {
    
    public void OnStart(Map map);
    public void RunStep(Map map);
    public void OnStop(Map map);
    
    
}
