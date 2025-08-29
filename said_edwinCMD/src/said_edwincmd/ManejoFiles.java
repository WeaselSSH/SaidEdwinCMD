package said_edwincmd;

import java.io.File;
import java.io.IOException;

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
            System.out.println("Error: nombre no válido.");
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
            System.out.println("Error: nombre no válido.");
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
    
    public void dir() {
        
    }
}
