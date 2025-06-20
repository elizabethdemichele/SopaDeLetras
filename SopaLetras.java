package sopaletras;

import javax.swing.SwingUtilities;

/**
 * Clase principal que inicia la aplicación de Sopa de Letras
 */
public class SopaLetras {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Llamar la interfaz gráfica
            ManejadorGUI app = new ManejadorGUI();
            app.mostrarInterfaz();
        });
    }
}