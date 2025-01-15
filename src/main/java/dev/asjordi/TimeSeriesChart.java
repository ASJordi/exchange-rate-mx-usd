package dev.asjordi;

import dev.asjordi.model.Bmx;
import dev.asjordi.model.Dato;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.IOException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.HorizontalAlignment;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.svg.SVGGraphics2D;
import org.jfree.svg.SVGUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.*;

public class TimeSeriesChart {

    private static final Logger LOGGER = LoggerConfig.getLogger();
    private Bmx bmx;

    public TimeSeriesChart(Bmx bmx) {
        this.bmx = bmx;
    }

    public void generateChart() {
        LOGGER.log(Level.INFO, () -> "Starting chart generation");
        JFreeChart chart = createChart(createDataset());

        SVGGraphics2D svgGraphics2D = new SVGGraphics2D(1920, 1080);
        svgGraphics2D.setRenderingHint(JFreeChart.KEY_SUPPRESS_SHADOW_GENERATION, true);

        Rectangle rectangle = new Rectangle(0, 0, 1920, 1080);
        chart.draw(svgGraphics2D, rectangle);

        Path path = Paths.get("chart.svg");

        try {
            SVGUtils.writeToSVG(path.toFile(), svgGraphics2D.getSVGElement());
            LOGGER.log(Level.INFO, () -> "Chart generated successfully");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Error writing SVG file");
            throw new RuntimeException(e);
        }
    }

    private XYDataset createDataset() {
        TimeSeries timeSeries = new TimeSeries("Tasa de cambio (MXN/USD)");
        List<Dato> datos = bmx.getSeries().get(0).getDatos();

        datos.forEach(dato -> {
            var day = new Day(dato.getFecha().getDayOfMonth(), dato.getFecha().getMonthValue(), dato.getFecha().getYear());
            var value = Double.parseDouble(dato.getDato());
            timeSeries.add(day, value);
        });

        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(timeSeries);

        return dataset;
    }

    private JFreeChart createChart(XYDataset dataset) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Tipo de cambio Pesos por dólar E.U.A.",
                null, "Pesos por Dólar", dataset);

        String fontName = "Palatino";
        chart.getTitle().setFont(new Font(fontName, Font.BOLD, 28));
        chart.addSubtitle(new TextTitle(
                "Source: https://www.banxico.org.mx/SieAPIRest/service/v1",
                new Font(fontName, Font.PLAIN, 24)));
        chart.addSubtitle(new TextTitle(
                "Serie: SF43718",
                new Font(fontName, Font.PLAIN, 22)));

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setDomainPannable(true);
        plot.setRangePannable(false);
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        plot.getDomainAxis().setLowerMargin(0.0);
        plot.getDomainAxis().setLabelFont(new Font(fontName, Font.BOLD, 18));
        plot.getDomainAxis().setTickLabelFont(new Font(fontName, Font.PLAIN, 16));
        plot.getRangeAxis().setLabelFont(new Font(fontName, Font.BOLD, 18));
        plot.getRangeAxis().setTickLabelFont(new Font(fontName, Font.PLAIN, 16));
        plot.setRangeGridlinePaint(Color.GRAY);
        plot.setDomainGridlinePaint(Color.GRAY);
        chart.getLegend().setItemFont(new Font(fontName, Font.PLAIN, 18));
        chart.getLegend().setFrame(BlockBorder.NONE);
        chart.getLegend().setHorizontalAlignment(HorizontalAlignment.CENTER);
        XYItemRenderer r = plot.getRenderer();

        if (r instanceof XYLineAndShapeRenderer renderer) {
            renderer.setDefaultShapesVisible(false);
            renderer.setDrawSeriesLineAsPath(true);
            renderer.setAutoPopulateSeriesStroke(false);
            renderer.setDefaultStroke(new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL), false);
            renderer.setSeriesPaint(0, Color.RED);
            renderer.setSeriesPaint(1, new Color(24, 123, 58));
            renderer.setSeriesPaint(2, new Color(149, 201, 136));
            renderer.setSeriesPaint(3, new Color(1, 62, 29));
            renderer.setSeriesPaint(4, new Color(81, 176, 86));
            renderer.setSeriesPaint(5, new Color(0, 55, 122));
            renderer.setSeriesPaint(6, new Color(0, 92, 165));
        }

        return chart;
    }
}
