


 import org.jfree.chart.ChartFactory;
 import org.jfree.chart.ChartPanel;
 import org.jfree.chart.JFreeChart;
 import org.jfree.chart.plot.PlotOrientation;
 import org.jfree.data.xy.XYSeries;
 import org.jfree.data.xy.XYSeriesCollection;
 import org.jfree.ui.ApplicationFrame;
 import org.jfree.ui.RefineryUtilities;


public class Grapher{
	private Graph graph;
	public Grapher(final String title, int num_series, String[] series_titles, String x_axis, String y_axis){
		graph = new Graph(title, num_series, series_titles, x_axis, y_axis);
		graph.pack();
		RefineryUtilities.centerFrameOnScreen(graph);
		graph.setVisible(true);
	}
	
	public void addPoint(int series_num, double time, double position){
		graph.addPoint(series_num, time, position);
	}
	
	
	
	/********************************************************************
	* Internal class
	*********************************************************************/
	private static class Graph extends ApplicationFrame{
		XYSeries[] series;
		
		public Graph(final String title, int num_series, String[] series_titles, String x_axis, String y_axis){
			super(title);
			XYSeriesCollection data = new XYSeriesCollection();
			
			series = new XYSeries[num_series];
			for (int i=0; i<num_series; i++){
				series[i] = new XYSeries(series_titles[i]);
				data.addSeries(series[i]);
			}
			
			
			final JFreeChart chart = ChartFactory.createXYLineChart(
				title,
				x_axis, 
				y_axis, 
				data,
				PlotOrientation.VERTICAL,
				true,
				true,
				false
			);

			final ChartPanel chartPanel = new ChartPanel(chart);
			chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
			setContentPane(chartPanel);
		}
		
		public void addPoint(int series_num, double time, double position){
			series[series_num].add(time, position);
		}
		
	}
	
}