package said_edwincmd;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat; 
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class visCmd extends JFrame {

    private final JTextArea area = new JTextArea();
    private int inicioEntrada;
    private File currentPath;
    private ManejoFiles fileManager;

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

        String userDir = System.getProperty("user.dir");
        currentPath = new File(userDir);
        fileManager = new ManejoFiles(userDir);

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
        String comandoCompleto = texto.substring(inicioEntrada).trim();
        imprimir("\n");

        if (comandoCompleto.isEmpty()) {
            prompt();
            return;
        }

        String[] partesComando = comandoCompleto.split("\\s+", 2);
        String comando = partesComando[0].toLowerCase();
        String argumento = partesComando.length > 1 ? partesComando[1] : "";

        try {
            switch (comando) {
                case "exit":
                    System.exit(0);
                    break;
                case "cls":
                    area.setText("");
                    break;
                case "echo":
                    imprimir(argumento + "\n");
                    break;
                case "date":
                    imprimir("The current date is: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + "\n");
                    break;
                case "time":
                    imprimir("The current time is: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")) + "\n");
                    break;
                case "cd":
                    cambiarDirectorio(argumento);
                    break;
                case "dir":
                    imprimir(listarDirectorio());
                    break;
                case "mkdir":
                    if (fileManager.mkdir(argumento)) {
                        imprimir("Directorio creado.\n");
                    } else {
                        imprimir("No se pudo crear el directorio. Puede que ya exista o el nombre sea invalido.\n");
                    }
                    break;
                case "mfile":
                     if (fileManager.mfile(argumento)) {
                        imprimir("Archivo creado.\n");
                    } else {
                        imprimir("No se pudo crear el archivo. Puede que ya exista o el nombre sea invalido.\n");
                    }
                    break;
                case "rm":
                    if (fileManager.rm(argumento)) {
                        imprimir("Archivo/directorio eliminado.\n");
                    } else {
                        imprimir("No se pudo eliminar. Verifique que el archivo/directorio exista y que no esté en uso.\n");
                    }
                    break;
                case "help":
                    imprimir("Comandos disponibles:\n");
                    imprimir("  CLS              Limpia la pantalla.\n");
                    imprimir("  DATE             Muestra la fecha actual.\n");
                    imprimir("  TIME             Muestra la hora actual.\n");
                    imprimir("  DIR              Muestra la lista de archivos y directorios.\n");
                    imprimir("  ECHO <msg>       Muestra un mensaje.\n");
                    imprimir("  CD <dir>         Cambia de directorio (usa '..' para retroceder).\n");
                    imprimir("  MKDIR <dir>      Crea un directorio.\n");
                    imprimir("  MFILE <file>     Crea un archivo vacío.\n");
                    imprimir("  RM <file/dir>    Elimina un archivo o directorio.\n");
                    imprimir("  EXIT             Cierra la consola.\n");
                    break;
                default:
                    imprimir("'" + comando + "' is not recognized as an internal or external command,\noperable program or batch file.\n");
                    break;
            }
        } catch (IOException e) {
            imprimir("Error de I/O: " + e.getMessage() + "\n");
        }

        prompt();
    }
    
    private String listarDirectorio() {
        File[] files = currentPath.listFiles();
        StringBuilder sb = new StringBuilder();
        sb.append(" Directory of ").append(currentPath.getAbsolutePath()).append("\n\n");
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy  hh:mm a");

        if (files != null) {
            for (File file : files) {
                sb.append(sdf.format(file.lastModified()));
                if (file.isDirectory()) {
                    sb.append("    <DIR>          ");
                } else {
                    sb.append(String.format("    %,15d ", file.length()));
                }
                sb.append(file.getName()).append("\n");
            }
        }
        return sb.toString();
    }

    private void cambiarDirectorio(String ruta) {
        if (ruta.isEmpty()) {
            imprimir(currentPath.getAbsolutePath() + "\n");
            return;
        }

        File nuevaRuta;
        if (ruta.equals("..")) {
            nuevaRuta = currentPath.getParentFile();
        } else {
            nuevaRuta = new File(ruta);
            if (!nuevaRuta.isAbsolute()) {
                nuevaRuta = new File(currentPath, ruta);
            }
        }

        if (nuevaRuta != null && nuevaRuta.exists() && nuevaRuta.isDirectory()) {
            currentPath = nuevaRuta;
            fileManager.cd(currentPath);
        } else {
            imprimir("The system cannot find the path specified.\n");
        }
    }

    private void imprimir(String s) {
        area.append(s);
        area.setCaretPosition(area.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new visCmd().setVisible(true));
    }
}