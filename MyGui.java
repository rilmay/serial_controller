import javax.swing.*;

public class MyGui {
    static MyGui instance;

    public static MyGui getGUi(){
        return instance;
    }

    public JFrame jFrame = new JFrame();
    public JPanel jPanel = new JPanel();

    JButton connect;
    JButton disconnect;

    JButton submit;
    JButton open;

    JTextArea log;
    JTextField command;

    JComboBox<String> ports;

    JLabel logWindow;
    JLabel choosePort;

    JButton flush = new JButton();

    public static void main(String[] args) {
        instance = new MyGui();
        instance.initCcompotents();
    }

    void initCcompotents(){
        jFrame.setVisible(true);
        jFrame.setTitle("COM port control");
        jFrame.setBounds(750,250,700,600);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
        JScrollPane jScrollPane = new JScrollPane(log);
        jScrollPane.setBounds(370, 70, 250, 400);


        logWindow = new JLabel("Log:");
        logWindow.setBounds(370,40,30,25);

        choosePort = new JLabel("Choose existing port name");
        choosePort.setBounds(40, 40, 250, 25);

        flush = new JButton("flush");
        flush.setBounds(550,40,70,25);

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

}
