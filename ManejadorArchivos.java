package sopaletras;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.util.*;

/**
 * Maneja las operaciones de lectura y escritura de archivos
 */
public class ManejadorArchivos {
    private String archivoActual;
    
    /**
     * Cargar un archivo dado por el usuario
     * @param parent: archivo a cargar
     * @return FileData
     */
    public FileData cargarArchivo(JFrame parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de texto", "txt"));
        
        int result = fileChooser.showOpenDialog(parent);
        if (result == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            archivoActual = archivo.getAbsolutePath();
            
            try {
                return leerArchivo(archivo);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, "Error al leer el archivo: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }
    
    /**
     * Leer los datos de un archivo
     * @param archivo
     * @return FileData
     * @throws IOException 
     */
    private FileData leerArchivo(File archivo) throws IOException {
        Set<String> diccionario = new HashSet<>();
        char[][] tablero = new char[4][4];
        
        boolean enDic = false;
        boolean enTab = false;
        
        try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                linea = linea.trim();
                
                switch (linea) {
                    case "dic":
                        enDic = true;
                        continue;
                    case "/dic":
                        enDic = false;
                        continue;
                    case "tab":
                        enTab = true;
                        continue;
                    case "/tab":
                        enTab = false;
                        continue;
                    default:
                        break;
                }
                
                if (enDic && !linea.isEmpty()) {
                    diccionario.add(linea.toUpperCase());
                } else if (enTab && !linea.isEmpty()) {
                    String[] letras = linea.split("\\s*,\\s*");
                    for (int col = 0;  col < letras.length; col++) {
                        if (!letras[col].isEmpty()) {
                            tablero[Math.floorDiv(col, 4)][col % 4] = letras[col].charAt(0);
                        }
                    }
                }
            }
        }
        return new FileData(diccionario, tablero);
    }
    
    /**
     * Guardar el diccionario con las palabras encontradas
     * @param diccionario
     * @return bool
     */
    public boolean guardarDiccionario(Set<String> diccionario) {
        if (archivoActual == null || diccionario == null) {
            return false;
        }
        try {
            List<String> lineas = new ArrayList<>();
            boolean enDic = false;
            
            try (BufferedReader reader = new BufferedReader(new FileReader(archivoActual))) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    linea = linea.trim();

                    if (linea.equals("dic")) {
                        lineas.add(linea);
                        diccionario.forEach(lineas::add);
                        
                        while ((linea = reader.readLine()) != null && !linea.trim().equals("/dic")) {}
                        if (linea != null) {
                            lineas.add(linea.trim());
                        }
                        enDic = false;
                    } else if (!enDic) {
                        lineas.add(linea);
                    }
                }
            }
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoActual))) {
                for (String linea : lineas) {
                    writer.write(linea);
                    writer.newLine();
                }
            }
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    public static record FileData(Set<String> diccionario, char[][] tablero) {}
}
