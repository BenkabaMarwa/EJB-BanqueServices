package socketsClientSereverTP;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ServerGUI extends JFrame {
    private JButton statutButton, envoyerButton, envoyerFichierButton, vocalButton;
    private JTextField messageField;
    private JTextArea zoneMessages;
    private JTextArea clientZoneMessages;
    private JComboBox<String> protocoleCombo;

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DatagramSocket udpSocket;
    private InetAddress udpClientAddress;
    private int udpClientPort;
    private PrintWriter out;
    private BufferedReader in;
    private String currentProtocol = "TCP";

    private Thread tcpThread, udpThread;
    private boolean udpListening = false;
    private boolean vocalActive = true;
    private boolean fileMode = false;

    public ServerGUI() {
        setTitle("Serveur");
        setSize(900, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel hautPanel = new JPanel(new GridLayout(2, 1));

        JPanel topLine = new JPanel(new FlowLayout(FlowLayout.LEFT));
        protocoleCombo = new JComboBox<>(new String[]{"TCP", "UDP"});
        topLine.add(new JLabel("Types de sockets :"));
        topLine.add(protocoleCombo);

        JPanel secondLine = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statutButton = new JButton("Serveur inactif");
        vocalButton = new JButton("Vocal ON");
        vocalButton.setBackground(Color.GREEN);
        secondLine.add(statutButton);
        secondLine.add(vocalButton);

        hautPanel.add(topLine);
        hautPanel.add(secondLine);
        add(hautPanel, BorderLayout.NORTH);

        zoneMessages = new JTextArea();
        zoneMessages.setEditable(false);
        JScrollPane serveurScroll = new JScrollPane(zoneMessages);
        serveurScroll.setBorder(BorderFactory.createTitledBorder("Messages Serveur"));

        clientZoneMessages = new JTextArea();
        clientZoneMessages.setEditable(false);
        JScrollPane clientScroll = new JScrollPane(clientZoneMessages);
        clientScroll.setBorder(BorderFactory.createTitledBorder("Notification Client"));
        clientZoneMessages.setBackground(new Color(245, 245, 255));
        clientZoneMessages.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, serveurScroll, clientScroll);
        splitPane.setDividerLocation(450);
        add(splitPane, BorderLayout.CENTER);

        JPanel basPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        setPlaceholder();

        envoyerButton = new JButton("Envoyer Message");
        envoyerFichierButton = new JButton("Choisissez un élément");

        JPanel rightPanel = new JPanel(new GridLayout(2, 1));
        rightPanel.add(envoyerFichierButton);
        rightPanel.add(envoyerButton);

        basPanel.add(rightPanel, BorderLayout.EAST);
        basPanel.add(messageField, BorderLayout.CENTER);
        add(basPanel, BorderLayout.SOUTH);

        // Listeners
        statutButton.addActionListener(e -> lancerServeur());
        envoyerButton.addActionListener(e -> envoyerMessage());
        envoyerFichierButton.addActionListener(e -> choisirFichier());
        vocalButton.addActionListener(e -> toggleVocal());

        addMouseListeners();

        setVisible(true);
    }

    private void toggleVocal() {
        vocalActive = !vocalActive;
        vocalButton.setText(vocalActive ? "Vocal ON" : "Vocal OFF");
        vocalButton.setBackground(vocalActive ? Color.GREEN : Color.RED);
    }

    private void addMouseListeners() {
        MouseAdapter vocalMouse = new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (vocalActive) speakButtonText(((JButton) e.getSource()).getText());
            }
        };
        statutButton.addMouseListener(vocalMouse);
        envoyerButton.addMouseListener(vocalMouse);
        envoyerFichierButton.addMouseListener(vocalMouse);
        vocalButton.addMouseListener(vocalMouse);
    }

    private void speakButtonText(String buttonText) {
        try {
            String command = "espeak \"" + buttonText + "\"";
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setPlaceholder() {
        messageField.setForeground(Color.GRAY);
        messageField.setText("Cliquez ici pour entrer du texte");
        messageField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (messageField.getText().equals("Cliquez ici pour entrer du texte")) {
                    messageField.setText("");
                    messageField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (messageField.getText().isEmpty()) {
                    messageField.setForeground(Color.GRAY);
                    messageField.setText("Cliquez ici pour entrer du texte");
                }
            }
        });
    }

    private void lancerServeur() {
        try {
            currentProtocol = protocoleCombo.getSelectedItem().toString();
            if (currentProtocol.equals("TCP")) {
                serverSocket = new ServerSocket(12345);
                zoneMessages.append("Serveur TCP en attente de connexion...\n");
                tcpThread = new Thread(() -> {
                    try {
                        clientSocket = serverSocket.accept();
                        int response = JOptionPane.showConfirmDialog(this, "Un client veut se connecter. Accepter ?", "Demande de connexion", JOptionPane.YES_NO_OPTION);
                        if (response == JOptionPane.YES_OPTION) {
                            statutButton.setText("Serveur en exécution");
                            zoneMessages.append("Client TCP accepté.\n");
                            out = new PrintWriter(clientSocket.getOutputStream(), true);
                            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                            String ligne;
                            while ((ligne = in.readLine()) != null) {
                                zoneMessages.append("Client : " + ligne + "\n");
                                clientZoneMessages.append("Client : " + ligne + "\n");
                            }
                        } else {
                            zoneMessages.append("Connexion refusée.\n");
                            clientSocket.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                tcpThread.start();
            } else {
                udpSocket = new DatagramSocket(12346);
                udpListening = true;
                statutButton.setText("Serveur en exécution");
                zoneMessages.append("Serveur UDP en écoute...\n");
                udpThread = new Thread(() -> {
                    byte[] buffer = new byte[65535];
                    while (udpListening) {
                        try {
                            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                            udpSocket.receive(packet);
                            udpClientAddress = packet.getAddress();
                            udpClientPort = packet.getPort();
                            String message = new String(packet.getData(), 0, packet.getLength());
                            zoneMessages.append("Client UDP : " + message + "\n");
                            clientZoneMessages.append("Client : " + message + "\n");
                        } catch (IOException e) {
                            if (udpListening) e.printStackTrace();
                        }
                    }
                });
                udpThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void envoyerMessage() {
        if (fileMode) return;
        String message = messageField.getText();
        if (message.isEmpty() || message.equals("Cliquez ici pour entrer du texte")) return;

        try {
            if (currentProtocol.equals("TCP")) {
                if (out != null) {
                    out.println(message);
                    zoneMessages.append("Moi : " + message + "\n");
                    clientZoneMessages.append("Serveur : " + message + "\n");
                }
            } else {
                if (udpClientAddress != null) {
                    byte[] data = message.getBytes();
                    DatagramPacket packet = new DatagramPacket(data, data.length, udpClientAddress, udpClientPort);
                    udpSocket.send(packet);
                    zoneMessages.append("Moi : " + message + "\n");
                    clientZoneMessages.append("Serveur : " + message + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        resetPlaceholder();
    }

    private void choisirFichier() {
        JFileChooser chooser = new JFileChooser();
        int retour = chooser.showOpenDialog(this);
        if (retour == JFileChooser.APPROVE_OPTION) {
            File fichier = chooser.getSelectedFile();
            messageField.setText("ENABLED");
            messageField.setForeground(Color.GRAY);
            messageField.setEditable(false);
            fileMode = true;

            new Thread(() -> {
                try {
                    if (fichier.getName().endsWith(".jpg") || fichier.getName().endsWith(".png") || fichier.getName().endsWith(".jpeg")) {
                        envoyerImage(fichier);
                    } else {
                        if (currentProtocol.equals("TCP")) {
                            if (clientSocket != null && !clientSocket.isClosed()) {
                                OutputStream os = clientSocket.getOutputStream();
                                FileInputStream fis = new FileInputStream(fichier);
                                BufferedOutputStream bos = new BufferedOutputStream(os);
                                out.println("FICHIER:" + fichier.getName());

                                byte[] buffer = new byte[4096];
                                int count;
                                while ((count = fis.read(buffer)) > 0) {
                                    bos.write(buffer, 0, count);
                                }
                                bos.flush();
                                fis.close();
                                zoneMessages.append("Fichier envoyé (TCP): " + fichier.getName() + "\n");
                                clientZoneMessages.append("Serveur a envoyé le fichier : " + fichier.getName() + "\n");
                            }
                        } else {
                            if (udpClientAddress != null) {
                                FileInputStream fis = new FileInputStream(fichier);
                                byte[] buffer = new byte[4096];
                                int count;
                                while ((count = fis.read(buffer)) > 0) {
                                    DatagramPacket packet = new DatagramPacket(buffer, count, udpClientAddress, udpClientPort);
                                    udpSocket.send(packet);
                                }
                                zoneMessages.append("Fichier envoyé (UDP): " + fichier.getName() + "\n");
                                clientZoneMessages.append("Serveur a envoyé le fichier : " + fichier.getName() + "\n");
                                fis.close();
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fileMode = false;
                resetPlaceholder();
            }).start();
        }
    }

    private void envoyerImage(File imageFile) {
        try {
            if (currentProtocol.equals("TCP") && clientSocket != null && !clientSocket.isClosed()) {
                OutputStream os = clientSocket.getOutputStream();
                BufferedOutputStream bos = new BufferedOutputStream(os);
                FileInputStream fis = new FileInputStream(imageFile);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }
                bos.flush();
                fis.close();
                zoneMessages.append("Image envoyée (TCP): " + imageFile.getName() + "\n");
                clientZoneMessages.append("Serveur a envoyé l'image : " + imageFile.getName() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetPlaceholder() {
        messageField.setForeground(Color.GRAY);
        messageField.setText("Cliquez ici pour entrer du texte");
        messageField.setEditable(true);
    }

    public static void main(String[] args) {
        new ServerGUI();
    }
}