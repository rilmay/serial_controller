import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Map;

public class MyGui {
    static MyGui instance;

    public static MyGui getGUi(){
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

    int currentFrameHight = 600;

    public static void main(String[] args) {
        instance = new MyGui();
        instance.initComponents();
        instance.assignListeners();
    }

    private void initComponents(){
        jFrame.setVisible(true);
        jFrame.setTitle("COM port control");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setFrameSize(currentFrameHight);

        jFrame.add(jPanel);

        jPanel.setLayout(null);

        connect = new JButton("connect");
        connect.setBounds(140,70,100,25);

        disconnect = new JButton("disconnect");
        disconnect.setBounds(245, 70, 100, 25);

        String[] example = {"com1", "com3", "com4"};
        ports = new JComboBox<>(example);
        ports.setBounds(40, 70, 95, 25);


        command = new JTextField(10);
        command.setBounds(40, 110, 115, 25);

        submit = new JButton("submit");
        submit.setBounds(160, 110, 100, 25);

        open = new JButton("open...");
        open.setBounds(265, 110, 80, 25);

        log = new JTextArea(5,25);
        log.setLineWrap(true);
        log.setEnabled(false);
        JScrollPane jScrollPane = new JScrollPane(log);
        jScrollPane.setBounds(370, 70, 250, 400);


        logWindow = new JLabel("Log:");
        logWindow.setBounds(370,40,30,25);

        choosePort = new JLabel("Choose existing port name");
        choosePort.setBounds(40, 40, 250, 25);

        flush = new JButton("flush");
        flush.setBounds(550,40,70,25);

        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File( "." ));
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
        jPanel.revalidate();

        jPanel.add(connect);
        jPanel.revalidate();

        jPanel.add(disconnect);
        jPanel.revalidate();

        jPanel.add(ports);
        jPanel.revalidate();

        jPanel.add(command);
        jPanel.revalidate();

        jPanel.add(submit);

        jPanel.revalidate();
        jPanel.add(open);

        jPanel.revalidate();

        jPanel.repaint();
    }

    private void assignListeners(){
        open.addActionListener(e -> {
            int result = chooser.showOpenDialog(jPanel);
            if(result == 0){
                configFile = chooser.getSelectedFile();
                config = JsonParser.parse(configFile);
                addCustomCommands();
            }
        });
        flush.addActionListener(e ->
            log.setText("")
        );

        submit.addActionListener(e -> {
            writeLog(command.getText());
            command.setText("");
        }
        );

    }

    private void addCustomCommands(){
        if(config == null) {
            return;
        }
        int offset = 130;

        int expectedHight = config.keySet().size() * 30 + offset + 10;

        if( expectedHight > currentFrameHight){
            setFrameSize(expectedHight + 10);
        }

        for(Map.Entry<String, String> entry: config.entrySet()){
            String name = entry.getKey();
            JButton jButton = new JButton(name);
            int yCoordinate = offset + 30;
            int width = name.length() * 15;
            offset = yCoordinate;
            jButton.setBounds(40, yCoordinate, width, 25);
            jButton.addActionListener(this::performCommandAction);
            jPanel.add(jButton);
        }

        jPanel.revalidate();
        jPanel.repaint();
    }

    private void performCommandAction(ActionEvent e){
        JButton button = (JButton) e.getSource();
        String buttonName = button.getText();
        String value = config.get(buttonName);
        writeLog(value);
    }

    void writeLog(String text){
        if(log.getText().length() == 0){
            log.append(text);
        }else {
            log.append("\n" + text);
        }
    }

    void setFrameSize(int height){
        jFrame.setBounds(750,250,700,height);
    }
}
