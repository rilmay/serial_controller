import gnu.io.*;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TooManyListenersException;

public class MyCommunicator implements SerialPortEventListener{
    MyGui gui;

    private CommPortIdentifier selectedPortIdentifier = null;
    private HashMap<String, CommPortIdentifier> portMap = new HashMap<>();
    private Enumeration ports = null;
    private SerialPort serialPort = null;
    final static int TIMEOUT = 2000;
    private static int BAUD_RATE = 9600;
    private InputStream input = null;
    private OutputStream output = null;




    public MyCommunicator(MyGui gui) {
        this.gui = gui;
    }

    public String[] getPorts() {
        ports = CommPortIdentifier.getPortIdentifiers();

        while (ports.hasMoreElements()) {
            CommPortIdentifier curPort = (CommPortIdentifier) ports.nextElement();

            if (curPort.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                portMap.put(curPort.getName(), curPort);
            }
        }
        return portMap.keySet().toArray(new String[0]);
    }

    public void connect()
    {
        String selectedPort = (String)gui.ports.getSelectedItem();
        selectedPortIdentifier = portMap.get(selectedPort);

        CommPort commPort = null;

        try
        {
            commPort = selectedPortIdentifier.open("UART controller", TIMEOUT);
            serialPort = (SerialPort)commPort;
            serialPort.setSerialPortParams(BAUD_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            gui.writeLog(selectedPort + " opened successfully.");
            gui.log.setForeground(Color.black);
        }
        catch (PortInUseException e)
        {
            gui.writeLog(selectedPort + " is in use. (" + e.toString() + ")");
            gui.log.setForeground(Color.RED);
        }
        catch (Exception e)
        {
            gui.writeLog("Failed to open " + selectedPort + "(" + e.toString() + ")");
            gui.log.setForeground(Color.RED);
        }
    }

    public void disconnect()
    {
        try
        {

            serialPort.removeEventListener();
            serialPort.close();
            input.close();
            output.close();

            gui.writeLog("Disconnected.");
            gui.log.setForeground(Color.black);
        }
        catch (Exception e)
        {
            gui.writeLog("Failed to close " + serialPort.getName() + "(" + e.toString() + ")");
            gui.log.setForeground(Color.RED);
        }
    }

    public void initListener()
    {
        try
        {
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        }
        catch (TooManyListenersException e)
        {
            gui.writeLog("Too many listeners. (" + e.toString() + ")");
            gui.log.setForeground(Color.RED);
        }
    }

    public boolean initIOStream()
    {
        boolean successful = false;

        try {
            //
            input = serialPort.getInputStream();
            output = serialPort.getOutputStream();

            successful = true;
            return successful;
        }
        catch (IOException e) {
            gui.writeLog("I/O Streams failed to open. (" + e.toString() + ")");
            gui.log.setForeground(Color.RED);
            return successful;
        }
    }

    public boolean writeData(byte[] bytes){
        boolean successfull = false;
        try
        {
            gui.writeLog("WRITING: " + HexBinUtil.stringFromByteArray(bytes));
            output.write(bytes);
            output.flush();
            successfull = true;
        }
        catch (Exception e)
        {
            gui.writeLog("Failed to write data. (" + e.toString() + ")");
            gui.log.setForeground(Color.RED);
        }

        return successfull;
    }

    public boolean writeData(byte b){
        return writeData(HexBinUtil.byteArray(b));
    }

    public boolean writeTestData(){
        byte b = 0;
        return writeData(b);
    }


    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE)
        {
            try
            {
                byte singleData = (byte)input.read();
                gui.writeLog("READING: " + HexBinUtil.stringFromByteArray(singleData));
            }
            catch (Exception e)
            {
                gui.writeLog("Failed to read data. (" + e.toString() + ")");
                gui.log.setForeground(Color.RED);
            }
        }
    }
}
