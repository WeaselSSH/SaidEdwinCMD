package said_edwincmd;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class visCmd extends JFrame {

    private final JTextArea area = new JTextArea();
    private int inicioEntrada;
    private File currentPath;

    public visCmd() {
        super("Command Prompt");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        area.setBackground(Color.BLACK);
        area.setForeground(Color.WHITE);
        area.setCaretColor(Color.WHITE);
        area.setFont(new Font("Consolas", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(area);
        add(scroll);

        area.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    ejecutarLinea();
                }
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    if (area.getCaretPosition() <= inicioEntrada) {
                        e.consume();
                    }
                }
            }
        });
        currentPath = new File(System.getProperty("user.dir"));

        imprimir("Microsoft Windows [Version 10.0.22621.521]\n");
        imprimir("(c) Microsoft Corporation. All rights reserved.\n\n");
        prompt();
    }

    private void prompt() {
        imprimir(currentPath.getAbsolutePath() + ">");
        inicioEntrada = area.getDocument().getLength();
    }

    private void ejecutarLinea() {
        String texto = area.getText();
        String comando = texto.substring(inicioEntrada).trim();
        imprimir("\n");

        if (comando.isEmpty()) {
        } else if (comando.equalsIgnoreCase("exit")) {
            System.exit(0);
        } else if (comando.equalsIgnoreCase("cls")) {
            area.setText("");
        } else if (comando.startsWith("echo ")) {
            imprimir(comando.substring(5) + "\n");
        } 
        else if (comando.startsWith("cd ")) {
            String nuevaRutaStr = comando.substring(3).trim();
            File nuevaRuta;

            if (nuevaRutaStr.equals("..")) {
                nuevaRuta = currentPath.getParentFile();
            } else {
                nuevaRuta = new File(nuevaRutaStr);
                if (!nuevaRuta.isAbsolute()) {
                    nuevaRuta = new File(currentPath, nuevaRutaStr);
                }
            }

            if (nuevaRuta != null && nuevaRuta.exists() && nuevaRuta.isDirectory()) {
                currentPath = nuevaRuta;
            } else {
                imprimir("The system cannot find the path specified.\n");
            }
        }
        else if (comando.equalsIgnoreCase("help")) {
            imprimir("Comandos simples: cls, echo, exit, help, cd\n");
        } else {
            imprimir("'" + comando + "' is not recognized as an internal or external command,\noperable program or batch file.\n");
        }

        prompt();
    }

    private void imprimir(String s) {
        area.append(s);
        area.setCaretPosition(area.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new visCmd().setVisible(true));
    }
}