package sopadeletras;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Aplicación para buscar palabras en una sopa de letras usando DFS o BFS.
 * Implementa un grafo no dirigido para representar las letras y sus conexiones
 */
public class SopaDeLetras extends JFrame {
    private JTextArea salida;
    private JTextField palabraParaBuscar;
    private JButton cargarArchivo, buscarTodas, buscarPalabra, guardarDic;
    private JComboBox<String> metodoBusqueda;
    private JPanel panelTablero;
    private JLabel tiempo;
    private char[][] tablero;
    private Set<String> diccionario;
    private String archivoActual;
    
    
    public SopaDeLetras() {
        // Iniciar la GUI
        super("Buscador de Palabras en Tablero");
        iniciarGUI();
        mostrarComponentes();
        escucharInput();
        
        // Método de cierre y tamaño de la GUI
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }
    
    /**
     * Inicializa la interfaz gráfica
     */
    private void iniciarGUI() {
        // Añadir áreas de input y output de texto
        salida = new JTextArea();
        salida.setEditable(false);
        salida.setFont(new Font("Monospaced", Font.PLAIN, 12));
        palabraParaBuscar = new JTextField(20);
        
        // Añadir botones para distintas acciones
        cargarArchivo = new JButton("Cargar Archivo");
        buscarTodas = new JButton("Buscar Todas las Palabras");
        buscarPalabra = new JButton("Buscar Palabra Específica");
        guardarDic = new JButton("Guardar Diccionario");
        metodoBusqueda = new JComboBox<>(new String[]{"DFS", "BFS"});
        
        // Añadir tablero con la sopa de letras
        panelTablero = new JPanel(new GridLayout(4, 4));
        panelTablero.setPreferredSize(new Dimension(300, 300));
        panelTablero.setBorder(BorderFactory.createTitledBorder("Tablero"));
        
        // Variable de tiempo de ejecución de operaciones
        tiempo = new JLabel("Tiempo: 0 ms");
        tiempo.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    /**
     * Distribuye los componentes de la GUI en panele
     */
    private void mostrarComponentes() {
        // Panel superior para cargar archivos y buscar palabras del diccionario
        JPanel panelSuperior = new JPanel();
        panelSuperior.add(cargarArchivo);
        panelSuperior.add(new JLabel("Método de búsqueda:"));
        panelSuperior.add(metodoBusqueda);
        panelSuperior.add(buscarTodas);
        
        // Panel medio muestra la sopa de letras y resultados de operaciones
        JPanel middlePanel = new JPanel(new BorderLayout());
        middlePanel.add(panelTablero, BorderLayout.WEST);
        JScrollPane scrollPane = new JScrollPane(salida);
        middlePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel inferior para la búsqueda de palabras específicas
        JPanel panelInferior = new JPanel();
        panelInferior.add(new JLabel("Palabra a buscar:"));
        panelInferior.add(palabraParaBuscar);
        panelInferior.add(buscarPalabra);
        panelInferior.add(guardarDic);
        panelInferior.add(tiempo);
        // Añadir los paneles a la GUI
        setLayout(new BorderLayout());
        add(panelSuperior, BorderLayout.NORTH);
        add(middlePanel, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    /**
     * Convierte a los botones y áreas de texto anteriores en áreas de input
     */
    private void escucharInput() {
        cargarArchivo.addActionListener(e -> cargarArchivoFun());
        buscarTodas.addActionListener(e -> buscarTodasFun());
        buscarPalabra.addActionListener(e -> buscarPalabraFun());
        guardarDic.addActionListener(e -> guardarDicFun());
    }
    
    /**
     * Carga un archivo de texto con el diccionario y el tablero.
     */
    private void cargarArchivoFun() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de texto", "txt"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();
            archivoActual = archivo.getAbsolutePath();
            
            try {
                leerArchivo(archivo);
                mostrarTablero();
                salida.setText("Diccionario cargado con " + diccionario.size() + " palabras.\n");
                salida.append("Tablero cargado correctamente.\n");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al leer el archivo: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Lee el archivo para extraer el diccionario y el tablero.
     * @param archivo Archivo a leer
     * @throws IOException Si ocurre un error al leer el archivo
     */
    private void leerArchivo(File archivo) throws IOException {
        diccionario = new HashSet<>();
        tablero = new char[4][4];
        
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
    }
    
    /**
     * Muestra el tablero en el panel correspondiente.
     */
    private void mostrarTablero() {
        // Primero asegurar que el tablero esté vacío
        panelTablero.removeAll();
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                // Mostrar valor en la posición del tablero guardado
                JLabel label = new JLabel(String.valueOf(tablero[row][col]), SwingConstants.CENTER);
                label.setFont(new Font("Arial", Font.BOLD, 24));
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                panelTablero.add(label);
            }
        }
        panelTablero.revalidate();
        panelTablero.repaint();
    }
    
    /**
     * Busca todas las palabras del diccionario en el tablero.
     */
    private void buscarTodasFun() {
        // Verificar que tengamos un tablero y un diccionario
        if (tablero == null || diccionario == null) {
            JOptionPane.showMessageDialog(this, "Primero cargue un archivo con el tablero y diccionario.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Obtener el método a emplearse en la búsqueda
        String metodo = (String) metodoBusqueda.getSelectedItem();
        salida.append("\nBuscando todas las palabras usando " + metodo + "...\n");
        // Iniciar un set con las palabras encontradas y guardar el tiempo inicial
        Set<String> palabrasEncontradas = new HashSet<>();
        long tiempoInicial = System.currentTimeMillis();
        // Iterar por cada palabra en el diccionario y buscarla
        for (String palabra : diccionario) {
            if (buscarPalabra(palabra, metodo.equals("BFS"))) {
                palabrasEncontradas.add(palabra);
            }
        }
        // Guardar la duración de la búsqueda
        long tiempoFinal = System.currentTimeMillis();
        long duracion = tiempoFinal - tiempoInicial;
        tiempo.setText("Tiempo: " + duracion + " ms");
        // Imprimir el resultado de la búsqueda
        salida.append("Palabras encontradas (" + palabrasEncontradas.size() + "):\n");
        for (String palabra : palabrasEncontradas) {
            salida.append(palabra + "\n");
        }
        // Mostrar la duración de la búsqueda
        salida.append("Tiempo total: " + duracion + " ms\n");
    }
    
    /**
     * Busca una palabra específica en el tablero.
     */
    private void buscarPalabraFun() {
        // Verificar que se tenga un tablero y un diccionario
        if (tablero == null || diccionario == null) {
            JOptionPane.showMessageDialog(this, "Primero cargue un archivo con el tablero y diccionario.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Verificar que la palabra a buscar no sea vacía
        String palabra = palabraParaBuscar.getText().trim().toUpperCase();
        if (palabra.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese una palabra para buscar.", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Obtener el método de búsqueda
        String metodo = (String) metodoBusqueda.getSelectedItem();
        salida.append("\nBuscando la palabra '" + palabra + "' usando " + metodo + "...\n");
        // Buscar la palabra y guardar la duración de la búsqueda
        long tiempoInicial = System.currentTimeMillis();
        boolean encontrada = buscarPalabra(palabra, metodo.equals("BFS"));
        long tiempoFinal = System.currentTimeMillis();
        long duracion = tiempoFinal - tiempoInicial;
        tiempo.setText("Tiempo: " + duracion + " ms");
        // Imprimir los resultados de la búsqueda
        if (encontrada) {
            salida.append("La palabra '" + palabra + "' fue encontrada en el tablero.\n");
            if (!diccionario.contains(palabra)) {
                diccionario.add(palabra);
                salida.append("La palabra '" + palabra + "' ha sido agregada al diccionario.\n");
            }
            // Mostrar árbol de recorrido BFS si se usó ese método
            if (metodo.equals("BFS")) {
                mostrarArbol(palabra);
            }
        } else {
            salida.append("La palabra '" + palabra + "' NO fue encontrada en el tablero.\n");
        }
        salida.append("Tiempo de búsqueda: " + duracion + " ms\n");
    }
    
    /**
     * Busca una palabra en el tablero usando DFS o BFS.
     * @param palabra Palabra a buscar
     * @param usarBFS True para usar BFS, false para DFS
     * @return True si la palabra fue encontrada
     */
    private boolean buscarPalabra(String palabra, boolean usarBFS) {
        // Verificar que la palabra no sea vacía
        if (palabra == null || palabra.isEmpty())
            return false;
        char primeraLetra = palabra.charAt(0);
        // Encontrar letra que coincida con la primera letra de la palabra
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (tablero[row][col] == primeraLetra) {
                    // Usar método BFS si ese es el indicado
                    if (usarBFS) {
                        if (busquedaBFS(palabra, row, col)) {
                            return true;
                        }
                    } else {
                        // Usar método DFS de lo contrario
                        boolean[][] visitados = new boolean[4][4];
                        if (busquedaDFS(palabra, row, col, 0, visitados)) {
                            return true;
                        }
                    }
                }
            }
        }
        // Retornar falso si no se pudo encontrar la palabra
        return false;
    }
    
    /**
    * Búsqueda en profundidad (DFS) para encontrar una palabra.
    */
   private boolean busquedaDFS(String palabra, int fila, int col, int index, boolean[][] visitados) {
       if (index == palabra.length()) {
           return true;
       }
       if (fila < 0 || fila >= 4 || col < 0 || col >= 4 || visitados[fila][col] || 
           tablero[fila][col] != palabra.charAt(index)) {
           return false;
       }
       visitados[fila][col] = true;
       // Explorar los 8 vecinos posibles
       for (int r = fila - 1; r <= fila + 1; r++) {
           for (int c = col - 1; c <= col + 1; c++) {
               if (r == fila && c == col) continue;
               if (busquedaDFS(palabra, r, c, index + 1, visitados)) {
                   return true;
               }
           }
       }
       visitados[fila][col] = false;
       return false;
   }

    /**
    * Búsqueda en anchura (BFS) para encontrar una palabra.
    */
   private boolean busquedaBFS(String palabra, int filaInicial, int columnaInicial) {
       Queue<NodoBFS> cola = new LinkedList<>();
       cola.add(new NodoBFS(filaInicial, columnaInicial, 0, null));

       while (!cola.isEmpty()) {
           NodoBFS nodoActual = cola.poll();

           if (nodoActual.indice == palabra.length() - 1) {
               return true;
           }

           // Explorar los 8 vecinos posibles
           for (int r = Math.max(0, nodoActual.fila - 1); r <= Math.min(3, nodoActual.fila + 1); r++) {
               for (int c = Math.max(0, nodoActual.col - 1); c <= Math.min(3, nodoActual.col + 1); c++) {
                   if (r == nodoActual.fila && c == nodoActual.col) continue;

                   if (tablero[r][c] == palabra.charAt(nodoActual.indice + 1)) {
                       cola.add(new NodoBFS(r, c, nodoActual.indice + 1, nodoActual));
                   }
               }
           }
       }

       return false;
   }
}
