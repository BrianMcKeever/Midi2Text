//////////////////////////////////////////////
//                Midi2Text
//
// Converts a MIDi file to a tab-delimeted
// text file. useful for importing into
// a spreadsheet for statistical analysis.
//
// (c) 2005 Andrew R. Brown
// 
// This application is built using the jMusic
// library,m and hence this code is distibuted
// under the GPL license (see below).
//
//////////////////////////////////////////////

/*
This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or any
later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

*/

import jm.JMC;
import jm.music.data.*;
import jm.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Enumeration;


public class Midi2Text implements ActionListener{
    private JFrame window;
    private JButton convert;

    public static void main(String[] args) { 
	new Midi2Text();
    }
    

    public Midi2Text() {
	window = new JFrame("Midi2Text");
	window.setSize(400, 220);
	JPanel bg = new JPanel(new GridLayout(6,1));
	// add instructions
	JLabel text0 = new JLabel("MIDI file to Text File converter: by Andrew R. Brown", 0);
	bg.add(text0);
	JLabel text1 = new JLabel("1. Click the 'Convert' button", 0);
	bg.add(text1);
	JLabel text2 = new JLabel("2. Choose a MIDI file", 0);
	bg.add(text2);
	JLabel text3 = new JLabel("3. Name and save the text file", 0);
	bg.add(text3);
	// add button
	JPanel btnPanel = new JPanel();
	convert = new JButton("Convert");
	convert.addActionListener(this);
	btnPanel.add(convert);
	bg.add(btnPanel);
	// pad
	bg.add(new JPanel());
	// show
	window.getContentPane().add(bg);
	window.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
	if(e.getSource() == convert) {
	    convert.setEnabled(false);
	    convert();
	    convert.setEnabled(true);
	    //System.exit(0);
	}
    }

    private void convert() {
	Score s = new Score();
	Read.midi(s);
	// open text file
	try {
	    FileWriter textFile = new FileWriter(saveData());
	    textFile.write("Start Time" + "\t" + "Pitch" + "\t" + 
			   "Duration" + "\t" + "Dynamic" + "\n");
	    // read note data and convert
	    // get data values
	    Enumeration enum1 = s.getPartList().elements();
	    while(enum1.hasMoreElements()){
		Part part = (Part) enum1.nextElement();
		Enumeration enum2 = part.getPhraseList().elements();
		while(enum2.hasMoreElements()){
		    Phrase phrase = (Phrase) enum2.nextElement();
		    double startTime = phrase.getStartTime(); 
		    Enumeration enum3 = phrase.getNoteList().elements();
		    while(enum3.hasMoreElements()){
			Note note = (Note) enum3.nextElement();
			if (note.getPitch() != JMC.REST) {
			    // start time
			    textFile.write(Double.toString(startTime) + "\t");
			    // pitch
			    textFile.write(Integer.toString(note.getPitch()) + "\t");
			    // duration
			    textFile.write(Double.toString(note.getDuration()) + "\t");
			    // velocity
			    textFile.write(Integer.toString(note.getDynamic()) + "\n");
			}
			startTime += note.getDuration();
		    }
		}
	    }
	    textFile.close();
	}catch(IOException e){
	    System.err.println(e);
	}    
    }

 /** 
    * Save the histogram data to a tab delimited text file
    * with a file name to be specified by a dialog box.
    */
    public String saveData() {
        FileDialog fd = new FileDialog(new Frame(), 
            "Save data as a text file named...", FileDialog.SAVE);
        fd.show();
        String fileName = fd.getFile();
        if (fileName != null) {
            fileName = fd.getDirectory() + fileName;
        }
	return fileName;
    }

}
