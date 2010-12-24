/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader;

import java.io.*;
import java.util.*;

import org.yaml.snakeyaml.*;

/**
 *
 * @author /a/non
 */
public class SaveData implements Serializable
{
    private Date date;

    private TreeMap<String, Magazine> magazines;
    
    public SaveData()
    {
        date = null;
        magazines = null;
    }

    public Date getDate(){ return(date); }
    public void setDate(Date myDate){ date = myDate; }

    public void resetDate()
    {
        date = new Date();
    }

    public void setMagazines(TreeMap<String, Magazine> myMagazines)
    {
        magazines = myMagazines;
    }
    public TreeMap<String, Magazine> getMagazines()
    {
        return(magazines);
    }

    public final void dumpYAML(String file) throws IOException
    {
        Yaml yaml = new Yaml();

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("date", date);

        for(Map.Entry<String, Magazine> magEntry : magazines.entrySet())
        {
            Map<String, Object> magazineDump = magEntry.getValue().dump();
            for(Series series : magEntry.getValue().getSeries())
            {
                Map<String, Object> seriesDump = series.dump();
                for(Chapter chapter : series.getChapters())
                {
                    Map<String, Object> chapterDump = chapter.dump();
                    chapterDump.put("class", chapter.getClass().getName());
                    seriesDump.put(chapter.getTitle(), chapterDump);
                }
                seriesDump.put("class", series.getClass().getName());
                magazineDump.put(series.getOriginalTitle(), seriesDump);
            }
            magazineDump.put("class", magEntry.getValue().getClass().getName());
            data.put(magEntry.getValue().getOriginalTitle(), magazineDump);
        }

        String output = yaml.dump(data);

        FileWriter out = new FileWriter(file);
        out.write(output);
        out.close();
    }

    public final void serializeToFile(String file) throws IOException
    {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));

        out.writeObject(this);

        out.close();
    }
}
