package edu.njit.solarcar.electrical.motorLog;

import java.io.IOException;
import edu.njit.solarcar.electrical.motorLog.util.ExceptionAlert;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class MainWindow
{
	
	/*@FXML
	private LineChart<Number, Number> rpmChart;
	
	@FXML
	private NumberAxis rpmChartAxisT;
	
	@FXML
	private NumberAxis rpmChartAxisY;
	
	@FXML
	private LineChart<Number, Number> vBatChart;
	
	@FXML
	private NumberAxis vBatChartAxisT;
	
	@FXML
	private NumberAxis vBatChartAxisY;
	
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
	private Label vbatLabel;
	
	@FXML
	private Label logFileLabel;
	
	@FXML
	private Label logTimeLabel;*/
	
  private static final double GRAPH_TIME_SHOWN = 10.0;

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
  private Label throttleLabel;

  @FXML
  private Label vbatLabel;

  @FXML
  private Label totalVoltagelabel;

  @FXML
  private Label phaseAVoltageLabel;

  @FXML
  private Label phaseBVoltageLabel;

  @FXML
  private Label phaseCVoltageLabel;

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
  private LineChart<Number, Number> voltageChart;

  @FXML
  private NumberAxis voltageChartAxisT;

  @FXML
  private NumberAxis voltageChartAxisY;

  @FXML
  private CheckBox totalVoltageCheck;

  @FXML
  private CheckBox phaseAVoltageCheck;

  @FXML
  private CheckBox phaseBVoltageCheck;

  @FXML
  private CheckBox phaseCVoltageCheck;

  @FXML
  private CheckBox vBatCheck;

  @FXML
  private LineChart<Number, Number> throttleChart;

  @FXML
  private NumberAxis throttleChartAxisT;

  @FXML
  private NumberAxis throttleChartAxisY;

  @FXML
  private LineChart<Number, Number> rpmChart;

  @FXML
  private NumberAxis rpmChartAxisT;

  @FXML
  private NumberAxis rpmChartAxisY;

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
	
	private XYChart.Series<Number, Number> voltageSeriesTotal;
	private XYChart.Series<Number, Number> voltageSeriesA;
	private XYChart.Series<Number, Number> voltageSeriesB;
	private XYChart.Series<Number, Number> voltageSeriesC;
	private XYChart.Series<Number, Number> voltageSeriesBat;
	
	private XYChart.Series<Number, Number> rpmSeries;
	
	private XYChart.Series<Number, Number> throttleSeries;
	
	
	private double xRange;
	private int numSamplesShown;
	
	
	
	private void addToCurrentChart(
		double t, double iA, double iB, double iC, double iTotal
	) {
		
		// Set the x axis range
		if (t > xRange) {
			currentChartAxisT.setLowerBound(t - xRange);
			currentChartAxisT.setUpperBound(t - xRange);
		}
		
		// Update each series on the chart
		ObservableList<Data<Number, Number>> listA = currentSeriesA.getData();
		listA.add(new Data<Number, Number>(t, iA));
		if (listA.size() > numSamplesShown)
			listA.remove(0);
		
		ObservableList<Data<Number, Number>> listB = currentSeriesB.getData();
		listB.add(new Data<Number, Number>(t, iB));
		if (listB.size() > numSamplesShown)
			listB.remove(0);
		
		ObservableList<Data<Number, Number>> listC = currentSeriesC.getData();
		listC.add(new Data<Number, Number>(t, iC));
		if (listC.size() > numSamplesShown)
			listC.remove(0);
		
		ObservableList<Data<Number, Number>> listTotal = currentSeriesTotal
			.getData();
		listTotal.add(new Data<Number, Number>(t, iTotal));
		if (listTotal.size() > numSamplesShown)
			listTotal.remove(0);
		
		// Update the labels
		totalCurrentLabel.setText(String.valueOf(iTotal));
		phaseACurrentLabel.setText(String.valueOf(iA));
		phaseBCurrentLabel.setText(String.valueOf(iB));
		phaseCCurrentLabel.setText(String.valueOf(iC));
	}
	
	
	
	
	private void addToVoltageChart(double t, double vBat, double vA, double vB, double vC, double vTotal) {
		// Set the x axis range
		if (t > xRange) {
			voltageChartAxisT.setLowerBound(t - xRange);
			voltageChartAxisT.setUpperBound(t - xRange);
		}
		
		// update each series
		ObservableList<Data<Number, Number>> listBat = voltageSeriesBat.getData();
		listBat.add(new Data<Number, Number>(t, vBat));
		if (listBat.size() > numSamplesShown)
			listBat.remove(0);
		
		ObservableList<Data<Number, Number>> listA = voltageSeriesA.getData();
		listA.add(new Data<Number, Number>(t, vA));
		if (listA.size() > numSamplesShown)
			listA.remove(0);
		
		ObservableList<Data<Number, Number>> listB = voltageSeriesB.getData();
		listB.add(new Data<Number, Number>(t, vB));
		if (listB.size() > numSamplesShown)
			listB.remove(0);
		
		ObservableList<Data<Number, Number>> listC = voltageSeriesC.getData();
		listC.add(new Data<Number, Number>(t, vC));
		if (listC.size() > numSamplesShown)
			listC.remove(0);
		
		ObservableList<Data<Number, Number>> listTotal = voltageSeriesTotal
			.getData();
		listTotal.add(new Data<Number, Number>(t, vTotal));
		if (listTotal.size() > numSamplesShown)
			listTotal.remove(0);
		
		// Update the labels
		totalVoltagelabel.setText(String.valueOf(vTotal));
		phaseAVoltageLabel.setText(String.valueOf(vA));
		phaseBVoltageLabel.setText(String.valueOf(vB));
		phaseCVoltageLabel.setText(String.valueOf(vC));
		
		vbatLabel.setText(String.valueOf(vBat));
	}
	
	
	
	private void addToThrottleChart(double t, double throttle) {
		// Set the x axis range
		if (t > xRange) {
			throttleChartAxisT.setLowerBound(t - xRange);
			throttleChartAxisT.setUpperBound(t - xRange);
		}
		
		ObservableList<Data<Number, Number>> list = throttleSeries.getData();
		list.add(new Data<Number, Number>(t, throttle));
		if (list.size() > numSamplesShown)
			list.remove(0);
		
		throttleLabel.setText(String.valueOf(throttle));
	}
	
	
	
	
	
	private void addToRpmChart(double t, double rpm) {
		// Set the x axis range
		if (t > xRange) {
			rpmChartAxisT.setLowerBound(t - xRange);
			rpmChartAxisT.setUpperBound(t - xRange);
		}
		
		ObservableList<Data<Number, Number>> list = rpmSeries.getData();
		list.add(new Data<Number, Number>(t, rpm));
		if (list.size() > numSamplesShown)
			list.remove(0);
		
		rpmLabel.setText(String.valueOf(rpm));
	}
	
	
	
	/**
	 * Reads the state of the motor controller and logger,
	 * and updates the UI to reflect that data
	 */
	private void updateView() {
		ComController ctrl = AppController.getComController();
		
		// Set an appropriate port label
		String port = "Not Connected";
		if(ctrl.getBus() != null) {
			String s = ctrl.getBus().getAddress();
			if(!s.isEmpty()) {
				port = s;
			}
		}
		comPortLabel.setText(port);
		
		// Add the data
		if(ctrl.isPolling()) {
			
			double timePolling = 
				((double)(System.currentTimeMillis() - ctrl.getPollingStart()))
				/ 1000.0;
			
			LogData d = ctrl.getCurrData();
			
			addToCurrentChart(
				timePolling, 
				d.iA, 
				d.iB, 
				d.iC, 
				d.iTotal);
			
			addToVoltageChart(
				timePolling, 
				d.vbat,
				d.vA,
				d.vB,
				d.vC,
				d.vtotal);
			
			addToRpmChart(timePolling, d.rpm);
			addToThrottleChart(timePolling, d.throttle);
			
			// Also set the CAN id label here
			String canId = ctrl.getBus().getAddress();
			canIdLabel.setText(canId);
		} 
		
		// Set some informative constants if not connected to hint the user
		else
			setNoConnectionUI();
		
		
		if(ctrl.isLogging()) {
			String logFile = ctrl.logFile().getAbsolutePath();
			double timeLogging = 
				((double)(System.currentTimeMillis() - ctrl.getLogStart()))
				/ 1000.0;
			
			logFileLabel.setText(logFile);
			logTimeLabel.setText(String.format("%.01f", timeLogging));
		} else {
			logFileLabel.setText("Not Logging");
			logTimeLabel.setText("");
		}
	}




	/**
	 * Updates the UI to indicate that there is no connection
	 */
	private void setNoConnectionUI() {
		canIdLabel.setText("N/A");
		rpmLabel.setText("-");
		vbatLabel.setText("-");
		totalCurrentLabel.setText("-");
		phaseACurrentLabel.setText("-");
		phaseBCurrentLabel.setText("-");
		phaseBCurrentLabel.setText("-");
	}
	
	
	/**
	 * Updates teh number of samples shown on the graphs
	 * @param d
	 */
	private void updateNumSamples(ConfigData d) {
		numSamplesShown = (int) (d.samplingFreq * GRAPH_TIME_SHOWN);
	}
	
	
	
	
	@FXML
	void initialize() 
	{
		// initialize some stuff
		ConfigData d = AppController.readConfig();
		updateNumSamples(d);
		setNoConnectionUI();
		
		// Set up the UI updating task
		Timeline uiUpdater = new Timeline(
			new KeyFrame(Duration.millis(25), new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					updateView();
				}
				
			}));
		
		uiUpdater.setCycleCount(Timeline.INDEFINITE);
		uiUpdater.play();
	}
	
	
	
	
	@FXML
	void updateCurrentsShown(ActionEvent event) {
		ObservableList<Series<Number, Number>> lines = currentChart.getData();
		
		// Check each box, and if necessary swap out the actual series with an empty
		// dummy one to preserve order, for coloring purposes, or swap the actual
		// back in
		boolean showTotal = totalCurrentCheck.isSelected();
		if (showTotal && lines.get(0) != currentSeriesTotal)
			lines.set(0, currentSeriesTotal);
		else if (
			!showTotal && lines.get(0) == currentSeriesTotal
			)
			lines.set(0, new XYChart.Series<>());
		
		boolean showA = phaseACurrentCheck.isSelected();
		if (showA && lines.get(1) != currentSeriesA)
			lines.set(0, currentSeriesA);
		else if (
			!showA && lines.get(1) == currentSeriesA
			)
			lines.set(0, new XYChart.Series<>());
		
		boolean showB = phaseBCurrentCheck.isSelected();
		if (showB && lines.get(2) != currentSeriesB)
			lines.set(0, currentSeriesB);
		else if (
			!showB && lines.get(2) == currentSeriesB
			)
			lines.set(0, new XYChart.Series<>());
		
		boolean showC = phaseCCurrentCheck.isSelected();
		if (showC && lines.get(3) != currentSeriesC)
			lines.set(0, currentSeriesC);
		else if (
			!showC && lines.get(3) == currentSeriesC
			)
			lines.set(0, new XYChart.Series<>());
	}
	
	
	
	@FXML
	void updateVoltageShown(ActionEvent event) {
		ObservableList<Series<Number, Number>> lines = voltageChart.getData();
		
		// Check each box, and if necessary swap out the actual series with an empty
		// dummy one to preserve order, for coloring purposes, or swap the actual
		// back in
		boolean showTotal = totalVoltageCheck.isSelected();
		if (showTotal && lines.get(0) != voltageSeriesTotal)
			lines.set(0, voltageSeriesTotal);
		else if (!showTotal && lines.get(0) == voltageSeriesTotal)
			lines.set(0, new XYChart.Series<>());
		
		boolean showA = phaseAVoltageCheck.isSelected();
		if (showA && lines.get(1) != voltageSeriesA)
			lines.set(1, voltageSeriesA);
		else if (!showA && lines.get(1) == voltageSeriesA)
			lines.set(1, new XYChart.Series<>());
		
		boolean showB = phaseBVoltageCheck.isSelected();
		if (showB && lines.get(2) != voltageSeriesB)
			lines.set(2, voltageSeriesB);
		else if (!showB && lines.get(2) == voltageSeriesB)
			lines.set(2, new XYChart.Series<>());
		
		boolean showC = phaseCVoltageCheck.isSelected();
		if (showC && lines.get(3) != voltageSeriesC)
			lines.set(3, voltageSeriesC);
		else if (!showC && lines.get(3) == voltageSeriesC)
			lines.set(3, new XYChart.Series<>());
		
		boolean showBat = vBatCheck.isSelected();
		if (showBat && lines.get(3) != voltageSeriesBat)
			lines.set(4, voltageSeriesBat);
		else if (!showBat && lines.get(3) == voltageSeriesBat)
			lines.set(4, new XYChart.Series<>());
	}
	
	
	
	/**
	 * Clears all the data from each series of each chart
	 * @param event
	 */
	@FXML
	void clearCharts(ActionEvent event) {
		currentSeriesTotal.getData().clear();
		currentSeriesA.getData().clear();
		currentSeriesB.getData().clear();
		currentSeriesC.getData().clear();
		
		voltageSeriesTotal.getData().clear();
		voltageSeriesA.getData().clear();
		voltageSeriesB.getData().clear();
		voltageSeriesC.getData().clear();
		voltageSeriesBat.getData().clear();
		
		rpmSeries.getData().clear();
		throttleSeries.getData().clear();
	}
	
	
	
	/**
	 * Open the connection dialog and then attempt a connection
	 * 
	 * @param event
	 */
	@FXML
	void connectToController(ActionEvent event) {
		String port = CanConnectController.getController().promptSelectPort();
		if (port != null) {
			ConfigData d = AppController.readConfig();
			try {
				AppController.getComController().setConf(d);
				if(!AppController.getComController().startPolling(port)) {
					throw new IOException("Motor failed to respond");
				}
			}
			catch (IOException e) {
				e.printStackTrace();
				Alert a = new ExceptionAlert(e, "Unable to Connect to Motor");
				a.showAndWait();
			}
		}
	}
	
	
	
	@FXML
	void disconnectFromController(ActionEvent event) {
		AppController.getComController().stopPolling();
		setNoConnectionUI();
	}
	
	
	
	@FXML
	void editSettings(ActionEvent event) {
		ConfigData old = AppController.readConfig();
		ConfigData newData = ConfigController.getController().promptConfig(old);
		if(newData != null) {
			AppController.writeConfig(newData);
			updateNumSamples(newData);
		}
	}
	
	
	
	
	@FXML
	void startLog(ActionEvent event) {
		try {
			AppController.getComController().startLogging();
		}
		catch (IOException e) {
			e.printStackTrace();
			Alert a = new ExceptionAlert(e, "Unable to start logging");
			a.showAndWait();
		}
	}
	
	
	
	@FXML
	void stopLog(ActionEvent event) {
		AppController.getComController().stopLogging();
	}



	/**
	 * Called when a polling error has occured, and the polling loop has stopped
	 */
	public void pollingError(Exception e) {
		e.printStackTrace();
		Alert a = new ExceptionAlert(e, "Error Polling Device");
		a.showAndWait();
	}
	
}
