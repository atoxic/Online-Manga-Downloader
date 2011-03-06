/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader;

import java.util.*;
import java.lang.reflect.*;

/**
 *
 * @author /a/non
 */
public class YAMLable
{
    public final HashMap<String, Object> exportVars()
    {
        return(exportVars(getClass()));
    }

    public final HashMap<String, Object> exportVars(Class c)
    {
        HashMap<String, Object> map = new HashMap<String, Object>();
        Field[] fields = c.getDeclaredFields();
        for(Field f : fields)
        {
            int mods = f.getModifiers();
            if(Modifier.isFinal(mods) || Modifier.isStatic(mods) || Modifier.isTransient(mods))
                continue;
            try
            {
                f.setAccessible(true);
                map.put(f.getName(), f.get(this));
            }
            catch(IllegalAccessException iae)
            {
                DownloaderUtils.error("couldn't get field: " + f, iae, false);
            }
        }

        Class superclass = c.getSuperclass();
        if(!superclass.equals(YAMLable.class))
        {
            map.putAll(exportVars(superclass));
        }

        return(map);
    }

    public final void importVars(Map<String, Object> map)
    {
        importVars(map, getClass());
    }
    public final void importVars(Map<String, Object> map, Class c)
    {
        Field[] fields = c.getDeclaredFields();
        for(Field f : fields)
        {
            f.setAccessible(true);
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
        Class superclass = c.getSuperclass();
        if(!superclass.equals(YAMLable.class))
        {
            importVars(map, superclass);
        }
    }
}
