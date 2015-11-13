package org.saiko.ai.genetics.tsp;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;

public class StatisticsExportor implements ActionListener{

	public TSP tspParent;
	
	public StatisticsExportor(TSP parent){
		tspParent=parent;
	}
	
	
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		JFileChooser chooser = new JFileChooser();
		int returnVal=chooser.showSaveDialog(null);
		if(returnVal==JFileChooser.APPROVE_OPTION){
			File file =chooser.getSelectedFile();
			if(file.exists()){
				file.delete();
			}
			
			
			try {
				file.createNewFile();
				BufferedWriter statOutput = new BufferedWriter(new FileWriter(file));
				statOutput.write(tspParent.charger.statOutput.toString());
			//	statOutput.flush();
				statOutput.close();
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			
			
			
			
			
		}
		
		
		
		
	}

}
