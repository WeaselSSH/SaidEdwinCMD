package said_edwincmd;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class visCmd extends JFrame {

    private final JTextArea area;
    private int inicioEntrada;
    private File rutaActual;
    private ManejoFiles gestorArchivos;

    public visCmd() {
        super("Símbolo del sistema");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);

        area = new JTextArea();
        area.setEditable(true);
        area.setBackground(Color.BLACK);
        area.setForeground(Color.WHITE);
        area.setCaretColor(Color.WHITE);
        area.setFont(new Font("Consolas", Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        add(new JScrollPane(area));

        PrintStream printStream = new PrintStream(new CustomOutputStream(area));
        System.setOut(printStream);
        System.setErr(printStream);

        area.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    ejecutarComando();
                }
                if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    if (area.getCaretPosition() <= inicioEntrada) {
                        e.consume();
                    }
                }
            }
        });

        String dirUsuario = System.getProperty("user.dir");
        rutaActual = new File(dirUsuario);
        gestorArchivos = new ManejoFiles(dirUsuario);
        
        promptInicial();
    }
    
    private void promptInicial() {
        System.out.print("Microsoft Windows [Versión 10.0.22621.521]\n");
        System.out.print("(c) Microsoft Corporation. Todos los derechos reservados.\n\n");
        System.out.print("Si ocupas ayuda usa el comando 'help'.\n\n");
        prompt();
    }
    
    private void prompt() {
        System.out.print(rutaActual.getAbsolutePath() + ">");
        inicioEntrada = area.getDocument().getLength();
    }

    private void ejecutarComando() {
        String textoCompleto = area.getText();
        String textoComando = textoCompleto.substring(inicioEntrada).trim();
        System.out.print("\n");

        if (textoComando.isEmpty()) {
            prompt();
            return;
        }

        String[] partes = textoComando.split("\\s+", 2);
        String comando = partes[0].toLowerCase();
        String argumento = partes.length > 1 ? partes[1] : "";

        try {
            switch (comando) {
                case "exit": case "salir":
                    System.exit(0);
                    break;
                case "cls": case "limpiar":
                    area.setText("");
                    prompt();
                    return;
                case "echo":
                    System.out.println(argumento);
                    break;
                case "date": case "fecha":
                    System.out.println("La fecha actual es: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    break;
                case "time": case "hora":
                    System.out.println("La hora actual es: " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                    break;
                case "cd":
                    cambiarDirectorio(argumento);
                    break;
                case "dir":
                    gestorArchivos.mostrarDir(argumento);
                    break;
                case "mkdir":
                    gestorArchivos.mkdir(argumento);
                    break;
                case "mfile":
                    gestorArchivos.mfile(argumento);
                    break;
                case "rm":
                    gestorArchivos.rm(argumento);
                    break;
                case "wr":
                    String[] argsWr = argumento.split("\\s+", 2);
                    if (argsWr.length < 2) {
                        System.out.println("Uso incorrecto. Sintaxis: wr <archivo> <texto para escribir>");
                    } else {
                        gestorArchivos.wr(argsWr[0], argsWr[1]);
                    }
                    break;
                case "rd":
                    String contenido = gestorArchivos.rd(argumento);
                    if (contenido != null) {
                        System.out.print(contenido);
                    }
                    break;
                case "help": case "ayuda":
                    imprimirAyuda();
                    break;
                default:
                    System.out.println("'" + comando + "' no se reconoce como un comando interno o externo,\nprograma o archivo por lotes ejecutable.");
                    break;
            }
        } catch (IOException e) {
            System.out.println("Error de Entrada/Salida: " + e.getMessage());
        }

        prompt();
    }

    private void cambiarDirectorio(String ruta) {
        if (ruta.isEmpty()) {
            System.out.println(rutaActual.getAbsolutePath());
            return;
        }
        File nuevaRuta;
        if (ruta.equals("..")) {
            nuevaRuta = rutaActual.getParentFile();
        } else {
            nuevaRuta = new File(ruta);
            if (!nuevaRuta.isAbsolute()) {
                nuevaRuta = new File(rutaActual, ruta);
            }
        }
        if (nuevaRuta != null && nuevaRuta.exists() && nuevaRuta.isDirectory()) {
            rutaActual = nuevaRuta;
            gestorArchivos.cd(rutaActual);
        } else {
            System.out.println("El sistema no puede encontrar la ruta especificada.");
        }
    }

    private void imprimirAyuda() {
        System.out.println("Comandos disponibles:");
        System.out.println("  CLS, LIMPIAR         Limpia la pantalla.");
        System.out.println("  DATE, FECHA          Muestra la fecha actual.");
        System.out.println("  TIME, HORA           Muestra la hora actual.");
        System.out.println("  DIR [directorio]     Muestra la lista de archivos y subdirectorios.");
        System.out.println("  ECHO <mensaje>       Muestra un mensaje en pantalla.");
        System.out.println("  CD <directorio>      Cambia de directorio (usa '..' para retroceder).");
        System.out.println("  MKDIR <directorio>   Crea un directorio.");
        System.out.println("  MFILE <archivo>      Crea un archivo vacío.");
        System.out.println("  RM <archivo/dir>     Elimina un archivo o directorio.");
        System.out.println("  WR <archivo> <texto> Escribe o sobrescribe texto en un archivo existente.");
        System.out.println("  RD <archivo>         Muestra el contenido de un archivo de texto.");
        System.out.println("  EXIT, SALIR          Cierra la consola.");
    }

    public static class CustomOutputStream extends OutputStream {
        private JTextArea textArea;
        public CustomOutputStream(JTextArea textArea) { this.textArea = textArea; }
        @Override
        public void write(int b) throws IOException {
            textArea.append(String.valueOf((char) b));
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }
}