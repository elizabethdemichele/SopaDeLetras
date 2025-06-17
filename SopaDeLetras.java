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
        panelTablero.removeAll();
        
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
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
    * Llama a la aplicación.
    */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SopaDeLetras app = new SopaDeLetras();
            app.setVisible(true);
        });
    }
}
