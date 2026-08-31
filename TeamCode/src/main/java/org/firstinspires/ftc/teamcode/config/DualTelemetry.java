package org.firstinspires.ftc.teamcode.config;

import com.bylazar.telemetry.TelemetryManager;

import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.Telemetry;

@SuppressWarnings({"ConstantValue", "unused"})
public class DualTelemetry implements Telemetry {
    private final TelemetryManager.TelemetryWrapper panelsTelemetry;
    private final Telemetry telemetry;
    private boolean returnPanels = false;

    public DualTelemetry(Telemetry telemetry, TelemetryManager.TelemetryWrapper panelsTelemetry) {
        this.telemetry = telemetry;
        this.panelsTelemetry = panelsTelemetry;
    }

    public DualTelemetry(Telemetry telemetry, TelemetryManager.TelemetryWrapper panelsTelemetry, boolean returnPanels) {
        this(telemetry, panelsTelemetry);
        this.returnPanels = returnPanels;
    }

    public TelemetryManager.TelemetryWrapper getPanelsTelemetry() {
        return panelsTelemetry;
    }

    public Telemetry getTelemetry() {
        return telemetry;
    }

    public boolean getReturnPanels() {
        return returnPanels;
    }

    public DualTelemetry setReturnPanels(boolean returnPanels) {
        this.returnPanels = returnPanels;
        return this;
    }

    @Override
    public Item addData(String caption, String format, Object... args) {
        Item panels = panelsTelemetry.addData(caption, format, args);
        Item tele = telemetry.addData(caption, format, args);
        return returnPanels ? panels : tele;
    }

    @Override
    public Item addData(String caption, Object value) {
        Item panels = panelsTelemetry.addData(caption, value);
        Item tele =  telemetry.addData(caption, value);
        return returnPanels ? panels : tele;
    }

    @Override
    public <T> Item addData(String caption, Func<T> valueProducer) {
        Item panels = panelsTelemetry.addData(caption, valueProducer);
        Item tele = telemetry.addData(caption, valueProducer);
        return returnPanels ? panels : tele;
    }

    @Override
    public <T> Item addData(String caption, String format, Func<T> valueProducer) {
        Item panels = panelsTelemetry.addData(caption, format, valueProducer);
        Item tele = telemetry.addData(caption, format, valueProducer);
        return returnPanels ? panels : tele;
    }

    @Override
    public boolean removeItem(Item item) {
        boolean panels = panelsTelemetry.removeItem(item);
        boolean tele = telemetry.removeItem(item);
        return returnPanels ? panels : tele;
    }

    @Override
    public void clear() {
        panelsTelemetry.clear();
        telemetry.clear();
    }

    @Override
    public void clearAll() {
        panelsTelemetry.clearAll();
        telemetry.isAutoClear();
    }

    @Override
    public Object addAction(Runnable action) {
        Object panels = panelsTelemetry.addAction(action);
        Object tele = telemetry.addAction(action);
        return returnPanels ? panels : tele;
    }

    @Override
    public boolean removeAction(Object token) {
        boolean panels = panelsTelemetry.removeAction(token);
        boolean tele = telemetry.removeAction(token);
        return returnPanels ? panels : tele;
    }

    @Override
    public void speak(String text) {
        panelsTelemetry.speak(text);
        telemetry.speak(text);
    }

    @Override
    public void speak(String text, String languageCode, String countryCode) {
        panelsTelemetry.speak(text, languageCode, countryCode);
        telemetry.speak(text, languageCode, countryCode);
    }

    @Override
    public boolean update() {
        boolean panels = panelsTelemetry.update();
        boolean tele =  telemetry.update();
        return returnPanels ? panels : tele;
    }

    @Override
    public Line addLine() {
        Line panels = panelsTelemetry.addLine();
        Line tele = telemetry.addLine();
        return returnPanels ? panels : tele;
    }

    @Override
    public Line addLine(String lineCaption) {
        Line panels = panelsTelemetry.addLine(lineCaption);
        Line tele = telemetry.addLine(lineCaption);
        return returnPanels ? panels : tele;
    }

    @Override
    public boolean removeLine(Line line) {
        boolean panels = panelsTelemetry.removeLine(line);
        boolean tele = telemetry.removeLine(line);
        return returnPanels ? panels : tele;
    }

    @Override
    public boolean isAutoClear() {
        boolean panels = panelsTelemetry.isAutoClear();
        boolean tele = telemetry.isAutoClear();
        return returnPanels ? panels : tele;
    }

    @Override
    public void setAutoClear(boolean autoClear) {
        panelsTelemetry.setAutoClear(autoClear);
        telemetry.setAutoClear(autoClear);
    }

    @Override
    public int getMsTransmissionInterval() {
        int panels = panelsTelemetry.getMsTransmissionInterval();
        int tele = telemetry.getMsTransmissionInterval();
        return returnPanels ? panels : tele;
    }

    @Override
    public void setMsTransmissionInterval(int msTransmissionInterval) {
        panelsTelemetry.setMsTransmissionInterval(msTransmissionInterval);
        telemetry.setMsTransmissionInterval(msTransmissionInterval);
    }

    @Override
    public String getItemSeparator() {
        String panels = panelsTelemetry.getItemSeparator();
        String tele = telemetry.getItemSeparator();
        return returnPanels ? panels : tele;
    }

    @Override
    public void setItemSeparator(String itemSeparator) {
        panelsTelemetry.setItemSeparator(itemSeparator);
        telemetry.setItemSeparator(itemSeparator);
    }

    @Override
    public String getCaptionValueSeparator() {
        String panels = panelsTelemetry.getCaptionValueSeparator();
        String tele =  telemetry.getCaptionValueSeparator();
        return returnPanels ? panels : tele;
    }

    @Override
    public void setCaptionValueSeparator(String captionValueSeparator) {
        panelsTelemetry.setCaptionValueSeparator(captionValueSeparator);
        telemetry.setCaptionValueSeparator(captionValueSeparator);
    }

    @Override
    public void setDisplayFormat(DisplayFormat displayFormat) {
        panelsTelemetry.setDisplayFormat(displayFormat);
        telemetry.setDisplayFormat(displayFormat);
    }

    @Override
    public Log log() {
        Log panels = panelsTelemetry.log();
        Log tele = telemetry.log();
        return returnPanels ? panels : tele;
    }
}
