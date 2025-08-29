package said_edwincmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ManejoFiles {

    private File rutaActual;

    public ManejoFiles(String rutaInicial) {
        this.rutaActual = new File(rutaInicial);
    }

    public void cd(File nuevaRuta) {
        this.rutaActual = nuevaRuta;
    }

    public boolean mkdir(String nombre) {   
        if (nombre == null || nombre.trim().isEmpty()) {
            System.out.println("Error: nombre no válido.");
            return false;
        }

        File target = new File(rutaActual, nombre);

        if (target.exists()) {
            System.out.println("Error: un archivo/directorio ya existe " + target.getPath());
            return false;
        }

        if (target.mkdirs()) {
            System.out.println("Directorio creado exitosamente: " + target.getPath());
            return true;
        } else {
            System.out.println("Error: no se pudo crear el directorio.");
            return false;
        }
    }

    public boolean mfile(String nombre) throws IOException {
        if (nombre == null || nombre.trim().isEmpty()) {
            System.out.println("Error: nombre no valido.");
            return false;
        }

        File target = new File(rutaActual, nombre);

        if (target.exists()) {
            System.out.println("Error: un archivo/directorio ya existe " + target.getPath());
            return false;
        }

        if (target.createNewFile()) {
            System.out.println("Archivo creado exitosamente: " + target.getPath());
            return true;
        } else {
            System.out.println("Error: no se pudo crear el archivo.");
            return false;
        }
    }

    public boolean rm(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            System.out.println("Error: nombre no valido.");
            return false;
        }

        File target = new File(rutaActual, nombre);

        if (!target.exists()) {
            System.out.println("Error: el archivo/folder no existe " + target.getPath());
            return false;
        }

        if (target.delete()) {
            System.out.println("Archivo/directorio eliminado exitosamente: " + target.getPath());
            return true;
        } else {
            System.out.println("Error: no se pudo eliminar el archivo/directorio.");
            return false;
        }
    }

    public void mostrarDir(String nombre) {
        File target = new File(rutaActual, nombre);

        if (!target.exists()) {
            System.out.println("\nError: directorio no existe.");
            return;
        }
        if (!target.isDirectory()) {
            System.out.println("\nError: no es un directorio: " + target.getPath());
            return;
        }

        System.out.println("\nDirectorio de: " + target.getAbsolutePath());
        dir(target);
    }

    private void dir(File dir) {
        File children[] = dir.listFiles();

        if (children == null) {
            System.out.println("Directorio no posee archivos.");
            return;
        }

        int cantArchivos = 0;
        int cantDir = 0;
        long bytesArchivos = 0;

        System.out.printf("%-19s  %-6s  %12s  %-30s%n",
                "Última Modificacion", "Tipo", "Tamano", "Nombre");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (File child : children) {
            if (child.isHidden()) {
                continue;
            }

            String fecha = sdf.format(new Date(child.lastModified()));
            String tipo = child.isDirectory() ? "<DIR>" : "FILE";

            String tamStr;
            if (child.isDirectory()) {
                tamStr = "";
                cantDir++;
            } else {
                long sizeBytes = child.length();
                long sizeKB = (sizeBytes + 1024) / 1024;
                tamStr = sizeKB + " KB";
                cantArchivos++;
                bytesArchivos += sizeBytes;
            }

            System.out.printf("%-19s  %-6s  %12s  %-30s%n",
                    fecha, tipo, tamStr, child.getName());
        }

        long totalKB = (bytesArchivos + 1024) / 1024;
        long bytesLibres = dir.getFreeSpace();

        System.out.printf("%n%d Archivo(s) %d KB%n", cantArchivos, totalKB);
        System.out.printf("%d Directorio(s)%n", cantDir);
        System.out.printf("%d Bytes libres %n", bytesLibres);
    }

    public boolean wr(String nombre, String texto) {
        if (nombre == null || nombre.trim().isEmpty()) {
            System.out.println("Error: nombre no válido.");
            return false;
        }

        File target = new File(rutaActual, nombre);

        if (!target.exists() || target.isDirectory()) {
            System.out.println("Error: seleccione un archivo valido.");
            return false;
        }

        try (FileWriter fw = new FileWriter(target, false)) {
            fw.write(texto);
            System.out.println("Texto escrito en archivo: " + target.getPath());
            return true;
        } catch (IOException e) {
            System.out.println("Error al escribir archivo: " + e.getMessage());
            return false;
        }
    }

    public String rd(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            System.out.println("Error: nombre no valido.");
            return null;
        }

        File target = new File(rutaActual, nombre);

        if (!target.exists() || target.isDirectory()) {
            System.out.println("Error: seleccione un archivo valido.");
            return null;
        }

        StringBuilder contenido = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(target))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                contenido.append(linea).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Error al leer archivo: " + e.getMessage());
            return null;
        }

        return contenido.toString();
    }

}
