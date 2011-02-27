/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package anonscanlations.downloader;

import java.util.*;
import java.lang.reflect.*;

/**
 *
 * @author Administrator
 */
public class YAMLContainer
{
    public HashMap<String, Object> exportVars()
    {
        HashMap<String, Object> map = new HashMap<String, Object>();
        Field[] fields = getClass().getFields();
        for(Field f : fields)
        {
            try
            {
                map.put(f.getName(), f.get(this));
            }
            catch(IllegalAccessException iae)
            {
                DownloaderUtils.error("couldn't get field: " + f, iae, false);
            }
        }

        return(map);
    }
    public void importVars(Map<String, Object> map)
    {
        Field[] fields = getClass().getFields();
        for(Field f : fields)
        {
            Object value = map.get(f.getName());
            if(value == null)
                continue;

            try
            {
                f.set(this, value);
            }
            catch(IllegalAccessException iae)
            {
                DownloaderUtils.error("couldn't set field: " + f, iae, false);
            }
        }
    }
}
