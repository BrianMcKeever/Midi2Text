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


public class Midi2Text implements ActionListener {
    private JFrame window;
    private JButton convert;

    public static void main(String[] args) {
        new Midi2Text();
    }


    public Midi2Text() {
        window = new JFrame("Midi2Text");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(400, 220);
        JPanel bg = new JPanel(new GridLayout(6, 1));
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
        if (e.getSource() == convert) {
            convert.setEnabled(false);
            convert();
            convert.setEnabled(true);
            //System.exit(0);
        }
    }

    private void convert() {
        Score s = new Score();
        Read.midi(s);
        int size = s.getSize();
        if(size == 0) { //if the user hit cancel
            return;
        }
        // open text file
        try {

            String fileName = saveData();
            if(fileName == null) return;
            FileWriter textFile = new FileWriter(fileName);
            String title = s.getTitle();
            textFile.write("#Title:\n");
            textFile.write(title + "\n");

            int timeSignatureNumerator = s.getNumerator();
            int timeSignatureDenominator = s.getDenominator();
            textFile.write("#Time Signature:\n");
            textFile.write(timeSignatureNumerator + "/" + timeSignatureDenominator + "\n");

            double bpm = s.getTempo();
            textFile.write("#Beats per Minute:\n");
            textFile.write(bpm + "\n");

            int keyQuality = s.getKeyQuality();
            textFile.write("#Key Quality:\n");
            if(keyQuality == 1) {
                textFile.write("Minor\n");
            } else {
                textFile.write("Major\n");
            }

            int keySignature = s.getKeySignature();
            textFile.write("#Key Signature. If it's positive, it's the number sharps otherwise the number flats:\n");
            textFile.write(keySignature + "\n");

            double longestRhythmValue = s.getLongestRhythmValue();
            textFile.write("#Longest Rhythm Value:\n");
            textFile.write(longestRhythmValue + "\n");

            double shortestRhythmValue = s.getShortestRhythmValue();
            textFile.write("#Shortest Rhythm Value:\n");
            textFile.write(shortestRhythmValue + "\n");

            textFile.write("#Start Time - Pitch - Duration - Dynamic - Pitch Name\n");
            // read note data and convert
            // get data values

            Enumeration enum1 = s.getPartList().elements();
            while (enum1.hasMoreElements()) {
                Part part = (Part) enum1.nextElement();
                textFile.write("#Part:\n");
                textFile.write(part.getTitle() + "\n");
                textFile.write("#Instrument:\n");
                textFile.write(Integer.toString(part.getInstrument()) + "\n");
                part.getTitle();
                Enumeration enum2 = part.getPhraseList().elements();
                while (enum2.hasMoreElements()) {
                    Phrase phrase = (Phrase) enum2.nextElement();
                    textFile.write("#New Phrase:\n");
                    double startTime = phrase.getStartTime();
                    Enumeration enum3 = phrase.getNoteList().elements();
                    while (enum3.hasMoreElements()) {
                        Note note = (Note) enum3.nextElement();
                        // start time
                        textFile.write(Double.toString(startTime) + "\t");
                        // pitch
                        textFile.write(Integer.toString(note.getPitch()) + "\t");
                        // duration
                        textFile.write(Double.toString(note.getDuration()) + "\t");
                        // velocity
                        textFile.write(Integer.toString(note.getDynamic()) + "\t");

                        //string representation of note
                        if(note.isRest()) {
                            textFile.write("Rest\n");
                        } else {
                            textFile.write(note.getNote() + "\n");
                        }
                        startTime += note.getDuration();
                    }
                }
            }
            textFile.close();
        } catch (IOException e) {
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
        fd.setVisible(true);
        if(fd.getFile() == null) return null; //if the user hit cancel, bail

        String fileName = fd.getFile();
        if (fileName != null) {
            fileName = fd.getDirectory() + fileName;
        }
        return fileName;
    }

}
