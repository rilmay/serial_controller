import util.HexBinUtil;
import util.JsonParser;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Map;

public class MyGui {
    private static MyGui instance;
    private boolean isConnected = false;

    public static MyGui getGUi() {
        return instance;
    }

    public JFrame jFrame = new JFrame();
    public JPanel jPanel = new JPanel();

    File configFile;
    Map<String, String> config;

    JButton connect;
    JButton disconnect;

    JButton submit;
    JButton open;

    JTextArea log;
    JTextField command;

    JComboBox<String> ports;

    JLabel logWindow;
    JLabel choosePort;

    JButton flush;

    JFileChooser chooser;

    int currentFrameHeight = 600;

    MyCommunicator communicator;

    public static void main(String[] args) {
        instance = new MyGui();
        instance.communicator = new MyCommunicator(instance);
        instance.initComponents();
        instance.assignListeners();
    }

    private void initComponents() {
        jFrame.setVisible(true);
        jFrame.setTitle("SPI control panel");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setFrameSize(currentFrameHeight);

        jFrame.add(jPanel);

        jPanel.setLayout(null);

        connect = new JButton("connect");
        connect.setBounds(140, 70, 100, 25);

        disconnect = new JButton("disconnect");
        disconnect.setBounds(245, 70, 100, 25);

        ports = new JComboBox<>(communicator.getPorts());
        ports.setBounds(40, 70, 95, 25);


        command = new JTextField(10);
        command.setBounds(40, 110, 115, 25);

        submit = new JButton("submit");
        submit.setBounds(160, 110, 100, 25);

        open = new JButton("open...");
        open.setBounds(265, 110, 80, 25);

        log = new JTextArea(5, 25);
        log.setLineWrap(true);
        log.setEnabled(false);
        JScrollPane jScrollPane = new JScrollPane(log);
        jScrollPane.setBounds(370, 70, 250, 400);


        logWindow = new JLabel("Log:");
        logWindow.setBounds(370, 40, 30, 25);

        choosePort = new JLabel("Choose existing port name");
        choosePort.setBounds(40, 40, 250, 25);

        flush = new JButton("flush");
        flush.setBounds(550, 40, 70, 25);

        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.getName().endsWith(".json") || f.isDirectory();
            }

            @Override
            public String getDescription() {
                return "json files";
            }
        });

        jPanel.add(flush);

        jPanel.add(choosePort);

        jPanel.add(logWindow);

        jPanel.add(jScrollPane);

        jPanel.add(connect);

        jPanel.add(disconnect);

        jPanel.add(ports);

        jPanel.add(command);

        jPanel.add(submit);

        jPanel.add(open);

        jPanel.revalidate();

        jPanel.repaint();

        setConnected(false);
    }

    private void setConnected(boolean isConnected){
        this.isConnected = isConnected;
        if(this.isConnected){
            ports.setEnabled(false);
            connect.setEnabled(false);
            disconnect.setEnabled(true);
            submit.setEnabled(false);
            command.setEnabled(false);
        }else{
            ports.setEnabled(true);
            connect.setEnabled(true);
            disconnect.setEnabled(false);
            submit.setEnabled(false);
            command.setEnabled(false);
        }
    }

    private void assignListeners() {
        open.addActionListener(e -> {
            int result = chooser.showOpenDialog(jPanel);
            if (result == 0) {
                configFile = chooser.getSelectedFile();
                config = JsonParser.parse(configFile);
                addCustomCommands();
                open.setEnabled(false);
            }
        });
        flush.addActionListener(e ->
                log.setText("")
        );

        submit.addActionListener(e -> {
                    submitPerformed(command.getText());
                    command.setText("");
                }
        );
        connect.addActionListener(e -> connectPerformed());
        disconnect.addActionListener(e -> disconnectPerformed());

    }

    private void addCustomCommands() {
        if (config == null) {
            return;
        }
        int offset = 150;

        int expectedHeight = config.keySet().size() * 30 + offset + 60;

        if (expectedHeight > currentFrameHeight) {
            setFrameSize(expectedHeight);
        }

        for (Map.Entry<String, String> entry : config.entrySet()) {
            String name = entry.getKey();
            if(name.length() > 17){
                name = name.substring(0, 16) + "...";
            }
            JButton jButton = new JButton(name);
            int yCoordinate = offset;
            offset += 30;
            int width = name.length() * 15;
            jButton.setBounds(40, yCoordinate, width, 25);
            jButton.addActionListener(this::performCommandAction);
            jPanel.add(jButton);
        }

        jPanel.revalidate();
        jPanel.repaint();
    }

    private void performCommandAction(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        String buttonName = button.getText();
        String value = config.get(buttonName);
        submitPerformed(value);
    }

    void writeLog(String text) {
        log.append((log.getText().length() == 0) ? text : "\n" + text);
    }

    private void setFrameSize(int height) {
        jFrame.setBounds(750, 250, 700, height);
    }


    private void connectPerformed() {
        try{
            communicator.connect();
            if (communicator.initIOStream()) {
                communicator.initListener();
                writeLog("Success");
                setConnected(true);
            }
        }catch (Exception e){
            writeLog("Oops, something wrong just happened, message: " + e.getMessage());
        }
    }

    private void disconnectPerformed() {
        communicator.disconnect();
        setConnected(false);
    }

    private void submitPerformed(String data) {
        try {
            if (isConnected) {
                byte[] b = HexBinUtil.hexStringToByteArray(data);
                String out = HexBinUtil.stringFromByteArray(b);
                writeLog("WRITE: " + out);
                communicator.writeData(b);
            } else {
                writeLog("You are not connected to any port, please choose one");
            }
        }catch (NumberFormatException e){
            writeLog("Oops, something with your byte input, message: " + e.getMessage());
        }
        catch (Exception e){
            writeLog("Oops, something wrong just happened, message: " + e.getMessage());
        }
    }
}
