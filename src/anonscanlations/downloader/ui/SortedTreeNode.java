/*
 * Coded by /a/non, for /a/non
 */

package anonscanlations.downloader.ui;

import anonscanlations.downloader.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import javax.imageio.*;

import java.io.*;
import java.util.*;
import java.net.*;

/**
 *
 * @author /a/non
 */
public class SortedTreeNode extends DefaultMutableTreeNode
{
    public SortedTreeNode(Object obj)
    {
        super(obj);
    }

    @Override
    public void add(MutableTreeNode node)
    {
        insert(node, getIndexIfInserted(node));
    }

    public int getIndexIfInserted(MutableTreeNode node)
    {
        DefaultMutableTreeNode n = (DefaultMutableTreeNode)node;
        Enumeration e = children();
        int i;
        for(i = 0; e.hasMoreElements(); i++)
        {
            DefaultMutableTreeNode each = (DefaultMutableTreeNode)e.nextElement();
            if((n.getUserObject().toString()).compareTo(each.getUserObject().toString()) <= 0)
                break;
        }
        return(i);
    }
}
