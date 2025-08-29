package said_edwincmd;

import java.io.File;

public class ManejoFiles {

    private File rutaActual;

    public ManejoFiles(String rutaInicial) {
        File f = new File(rutaInicial);
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
            System.out.println("Carpeta creada exitosamente: " + target.getPath());
            return true;
        } else {
            System.out.println("Error: no se pudo crear la carpeta.");
            return false;
        }
    }
}
