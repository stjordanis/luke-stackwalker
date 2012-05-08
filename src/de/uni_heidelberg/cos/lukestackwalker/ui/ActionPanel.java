/*
 * This file is part of Luke Stackwalker.
 * https://github.com/bhoeckendorf/luke-stackwalker
 * 
 * Copyright 2012 Burkhard Hoeckendorf <b.hoeckendorf at web dot de>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.uni_heidelberg.cos.lukestackwalker.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import de.uni_heidelberg.cos.lukestackwalker.DataSetTreeModel;


public class ActionPanel extends JPanel implements ActionListener {
	
	private DataSetTreeModel dataSetTreeModel;
	private JButton
		updateDataFileHierarchyButton,
		checkDataSetConsistencyButton,
		startButton;
	private JComboBox actionsComboBox;
	private final String[] actions = {"Move to folder structure"};
	private static JTextField targetDirEdit = new JTextField();
	
	
	public ActionPanel(DataSetTreeModel dataSetTreeModel) {
		this.dataSetTreeModel = dataSetTreeModel;
		initUi();
		updateDataFileHierarchyButton.addActionListener(this);
		DefaultComboBoxModel actionsComboBoxModel = new DefaultComboBoxModel();
		for (String action : actions)
			actionsComboBoxModel.addElement(action);
		actionsComboBox.setModel(actionsComboBoxModel);
	}
	
	
	private void initUi() {
		setLayout(new MigLayout("", "[grow]", "[][][][][]"));
		
		updateDataFileHierarchyButton = new JButton("Update data file hierarchy");
		add(updateDataFileHierarchyButton, "cell 0 0,grow");
		
		checkDataSetConsistencyButton = new JButton("Check data file hierarchy consistency");
		add(checkDataSetConsistencyButton, "cell 0 1,grow");
		
		actionsComboBox = new JComboBox();
		add(actionsComboBox, "cell 0 2,grow");
		
		JLabel targetDirLabel = new JLabel("Target folder");
		add(targetDirLabel, "cell 0 3");
		add(targetDirEdit, "cell 0 3,grow");
		
		startButton = new JButton("Start");
		add(startButton, "cell 0 4,grow");
	}

	
	public static String getTargetDir() {
		return targetDirEdit.getText().trim();
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JButton source = (JButton)e.getSource();
		if (source == updateDataFileHierarchyButton)
			dataSetTreeModel.update();
		else if (source == startButton)
			dataSetTreeModel.move();
	}

}
