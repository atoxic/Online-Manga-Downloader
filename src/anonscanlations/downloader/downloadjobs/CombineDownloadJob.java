package anonscanlations.downloader.downloadjobs;

import java.io.*;
import java.awt.image.*;
import javax.imageio.*;
import java.awt.*;

/**
 *
 * @author Administrator
 */
public class CombineDownloadJob extends DownloadJob
{
    private File f;
    // image dimensions for final image and piece images
    private int w, h, gridW, gridH;
    protected ImageDownloadJob grid[][];
    public CombineDownloadJob(String _desc, File _file, int _width, int _height, int _gridW, int _gridH)
    {
        super(_desc);
        
        f = _file;
        w = _width;
        h = _height;
        gridW = _gridW;
        gridH = _gridH;
        grid = null;
    }
    
    public void run() throws Exception
    {
        if(grid == null)
            throw new IllegalArgumentException("Grid is null");
        
        BufferedImage complete = new BufferedImage(w, h,
                                                    BufferedImage.TYPE_INT_RGB);

        Graphics2D g = complete.createGraphics();
        for(int x = 0; x < grid.length; x++)
        {
            for(int y = 0; y < grid[x].length; y++)
            {
                if(grid[x][y].getImage() != null)
                    g.drawImage(grid[x][y].getImage(), x * gridW, y * gridH, null);
            }
        }
        ImageIO.write(complete, "JPEG", f);
    }
}
