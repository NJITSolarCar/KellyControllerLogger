package edu.njit.solarcar.electrical.motorLog;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;

public class MainWindow
{
	
	@FXML
	private LineChart<Number, Number> rpmChart;
	
	@FXML
	private NumberAxis rpmChartAxisT;
	
	@FXML
	private NumberAxis rpmChartAxisY;
	
	@FXML
	private Label rpmLabel;
	
	@FXML
	private Label totalCurrentLabel;
	
	@FXML
	private Label phaseACurrentLabel;
	
	@FXML
	private Label phaseBCurrentLabel;
	
	@FXML
	private Label phaseCCurrentLabel;
	
	@FXML
	private LineChart<Number, Number> currentChart;
	
	@FXML
	private NumberAxis currentChartAxisT;
	
	@FXML
	private NumberAxis currentChartAxisY;
	
	@FXML
	private CheckBox totalCurrentCheck;
	
	@FXML
	private CheckBox phaseACurrentCheck;
	
	@FXML
	private CheckBox phaseBCurrentCheck;
	
	@FXML
	private CheckBox phaseCCurrentCheck;
	
	@FXML
	private Label comPortLabel;
	
	@FXML
	private Label canIdLabel;
	
	@FXML
	private Label logFileLabel;
	
	@FXML
	private Label logTimeLabel;
	
	// Other parameters
	private XYChart.Series<Number, Number> currentSeriesTotal;
	private XYChart.Series<Number, Number> currentSeriesA;
	private XYChart.Series<Number, Number> currentSeriesB;
	private XYChart.Series<Number, Number> currentSeriesC;
	
	private XYChart.Series<Number, Number> rpmSeries;
	
	private double xRange;
	private int numSamplesShown;
	
	
	private void addToCurrentChart(
		double t, int iA, int iB, int iC, int iTotal
	) {
		
		// Set the x axis range
		if(t > xRange) {
			currentChartAxisT.setLowerBound(t - xRange);
			currentChartAxisT.setUpperBound(t - xRange);
		}
		
		// Update each series on the chart
		ObservableList<Data<Number, Number>> listA = currentSeriesA.getData();
		listA.add(new Data<Number, Number>(t, iA));
		if(listA.size() > numSamplesShown) 
			listA.remove(0);
		
		ObservableList<Data<Number, Number>> listB = currentSeriesB.getData();
		listB.add(new Data<Number, Number>(t, iA));
		if(listB.size() > numSamplesShown) 
			listB.remove(0);
		
		ObservableList<Data<Number, Number>> listC = currentSeriesC.getData();
		listC.add(new Data<Number, Number>(t, iA));
		if(listC.size() > numSamplesShown) 
			listC.remove(0);
		
		ObservableList<Data<Number, Number>> listTotal = currentSeriesTotal.getData();
		listTotal.add(new Data<Number, Number>(t, iA));
		if(listTotal.size() > numSamplesShown) 
			listTotal.remove(0);
		
		
		// Update the labels
		totalCurrentLabel.setText(String.valueOf(iTotal));
		phaseACurrentLabel.setText(String.valueOf(iA));
		phaseBCurrentLabel.setText(String.valueOf(iB));
		phaseCCurrentLabel.setText(String.valueOf(iC));
	}
	
	
	
	private void addToRpmChart(double t, double rpm) {
		// Set the x axis range
		if(t > xRange) {
			rpmChartAxisT.setLowerBound(t - xRange);
			rpmChartAxisT.setUpperBound(t - xRange);
		}
		
		ObservableList<Data<Number, Number>> list = rpmSeries.getData();
		list.add(new Data<Number, Number>(t, rpm));
		if(list.size() > numSamplesShown) 
			list.remove(0);
	}
	
	

	
	@FXML
	void updateCurrentsShown(ActionEvent event) {
		ObservableList<Series<Number, Number>> lines = currentChart.getData();
		
		// Check each box, and if necessary swap out the actual series with an empty
		// dummy one to preserve order, for coloring purposes, or swap the actual back in
		boolean showTotal = totalCurrentCheck.isSelected();
		if(showTotal && lines.get(0) != currentSeriesTotal)
			lines.set(0, currentSeriesTotal);
		else if(!showTotal && lines.get(0) == currentSeriesTotal)
			lines.set(0, new XYChart.Series<>());
		
		boolean showA = phaseACurrentCheck.isSelected();
		if(showA && lines.get(1) != currentSeriesA)
			lines.set(0, currentSeriesA);
		else if(!showA && lines.get(1) == currentSeriesA)
			lines.set(0, new XYChart.Series<>());
		
		boolean showB = phaseBCurrentCheck.isSelected();
		if(showB && lines.get(2) != currentSeriesB)
			lines.set(0, currentSeriesB);
		else if(!showB && lines.get(2) == currentSeriesB)
			lines.set(0, new XYChart.Series<>());
		
		boolean showC = phaseCCurrentCheck.isSelected();
		if(showC && lines.get(3) != currentSeriesC)
			lines.set(0, currentSeriesC);
		else if(!showC && lines.get(3) == currentSeriesC)
			lines.set(0, new XYChart.Series<>());
	}
	
	
	
	@FXML
	void clearCharts(ActionEvent event) {
		
	}
	
	
	
	@FXML
	void connectToController(ActionEvent event) {
		
	}
	
	
	
	@FXML
	void disconnectFromController(ActionEvent event) {
		
	}
	
	
	
	@FXML
	void newLogFile(ActionEvent event) {
		
	}
	
	
	
	@FXML
	void startLog(ActionEvent event) {
		
	}
	
	
	
	@FXML
	void stopLog(ActionEvent event) {
		
	}

	
}
