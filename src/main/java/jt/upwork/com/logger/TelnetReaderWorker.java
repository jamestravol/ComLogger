package jt.upwork.com.logger;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author jamestravol
 */
public class TelnetReaderWorker extends SwingWorker<Integer, String> {

    private final String port;
    private final int baud;
    private final ConnectionPanel connectionPanel;
    private final Runnable completeCallback;
    private final DateTimeFormatter dateFormatter;
    private final DateTimeFormatter timeFormatter;
    private volatile SerialPort serialPort;
    private volatile List<String[]> dataList = new LinkedList<>();
    private volatile String currentValue = "";

    private ReentrantLock lock = new ReentrantLock();

    public TelnetReaderWorker(String port, int baud, ConnectionPanel connectionPanel, Runnable completeCallback) {
        this.port = port;
        this.baud = baud;
        this.connectionPanel = connectionPanel;
        dateFormatter = DateTimeFormatter.ofPattern(Config.INSTANCE.getProperty("app.date.format"));
        timeFormatter = DateTimeFormatter.ofPattern(Config.INSTANCE.getProperty("app.time.format"));
        this.completeCallback = completeCallback;
    }

    @Override
    protected Integer doInBackground() {
        try {
            return doInBackgroundInternal();
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        } finally {
            connectionPanel.setDisconnected();
            connectionPanel.cancel();
            SwingUtilities.invokeLater(completeCallback);
        }
    }

    private Integer doInBackgroundInternal() throws Exception {

        serialPort = new SerialPort(port);

        serialPort.openPort();
        serialPort.setParams(baud, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        serialPort.addEventListener(this::read, SerialPort.MASK_RXCHAR);

        connectionPanel.setConnected();

        System.out.println("The COM is listening:" + port + ":" + baud);

        while (!isCancelled()) {
            Thread.sleep(1000);
        }

        return 0;
    }

    private synchronized void read(SerialPortEvent event) {
        System.out.println("RXCHAR:" + event.isRXCHAR());
        System.out.println("BREAK:" + event.isBREAK());
        System.out.println("CTS:" + event.isCTS());
        System.out.println("RXFLAG:" + event.isRXFLAG());
        if (event.isRXCHAR() && event.getEventValue() > 0) {
            try {
                final String text = serialPort.readString(event.getEventValue());
                System.out.println("Got text: " + text);
                currentValue += text;
                if (currentValue.contains("\n")) {
                    final String[] split = currentValue.split("[\n\r]{1,2}");
                    if (currentValue.endsWith("\n")) {
                        for (String s : split) {
                            if (!s.isEmpty()) {
                                store(s);
                            }
                        }
                        currentValue = "";
                    } else {
                        for (int i = 0; i < split.length - 1; i++) {
                            String s = split[i];
                            if (!s.isEmpty()) {
                                store(s);
                            }
                        }
                        currentValue = split[split.length - 1];
                    }
                }
            } catch (SerialPortException e) {
                cancel();
            }
        }
    }

    private void store(String text) {

        System.out.println("Got the new line: " + text);

        final boolean appendDate = Boolean.parseBoolean(Config.INSTANCE.getProperty("app.append.date"));
        final int columnLimit = Integer.parseInt(Config.INSTANCE.getProperty("app.column.limit"));
        final String[] split = text.split(Config.INSTANCE.getProperty("app.separator.regex").trim());

        System.out.println("Splitted: " + Arrays.toString(split));

        LinkedList<String> vals = new LinkedList<>();

        final LocalDateTime localDate = LocalDateTime.now();

        if (appendDate) {
            vals.add(dateFormatter.format(localDate));
            vals.add(timeFormatter.format(localDate));
        }

        for (int i = 0; i < split.length && i < columnLimit; i++) {
            vals.add(split[i].trim());
        }

        lock.lock();
        try {
            dataList.add(vals.toArray(new String[0]));
        } finally {
            lock.unlock();
        }
    }

    public List<String[]> getDataList() {
        return dataList;
    }

    public void clearDataList() {
        dataList = new LinkedList<>();
    }

    public void cancel() {
        cancel(true);
        try {
            serialPort.closePort();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    public ReentrantLock getLock() {
        return lock;
    }
}
