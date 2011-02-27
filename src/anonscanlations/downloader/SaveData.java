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

    private ArrayList<Magazine> magazines;
    
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

    public void setMagazines(ArrayList<Magazine> myMagazines)
    {
        magazines = myMagazines;
    }
    public ArrayList<Magazine> getMagazines()
    {
        return(magazines);
    }

    public final void dumpYAML(String file) throws IOException
    {
        Yaml yaml = new Yaml(DownloadInfoServer.OPTIONS);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("date", date);

        for(Magazine mag : magazines)
        {
            Map<String, Object> magazineDump = mag.exportVars();
            for(Series series : mag.getSeries())
            {
                Map<String, Object> seriesDump = series.exportVars();
                for(Chapter chapter : series.getChapters())
                {
                    Map<String, Object> chapterDump = chapter.exportVars();
                    chapterDump.put("class", chapter.getClass().getName());
                    seriesDump.put(chapter.getTitle(), chapterDump);
                }
                seriesDump.put("class", series.getClass().getName());
                magazineDump.put(series.getOriginalTitle(), seriesDump);
            }
            magazineDump.put("class", mag.getClass().getName());
            data.put(mag.getOriginalTitle(), magazineDump);
        }

        String output = yaml.dump(data);

        OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
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
