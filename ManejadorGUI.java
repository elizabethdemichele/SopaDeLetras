package sopaletras;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

/**
 * Maneja la interfaz gráfica de usuario de la aplicación
 */
public class ManejadorGUI extends JFrame {
    private JTextArea salida;
    private JTextField palabraParaBuscar;
    private JButton cargarArchivo, buscarTodas, buscarPalabra, guardarDic;
    private JComboBox<String> metodoBusqueda;
    private JPanel panelTablero;
    private JLabel tiempo;
    private char[][] tablero;
    private Set<String> diccionario;
    private ManejadorArchivos manejadorArchivos;
    private BuscadorPalabras buscador;
    
    public ManejadorGUI() {
        super("Buscador de Palabras en Tablero");
        manejadorArchivos = new ManejadorArchivos();
        configurarInterfaz();
    }
    
    /**
     * Método para mostrar la interfaz gráfica del usuario (GUI)
     */
    public void mostrarInterfaz() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Método para llamar las funciones de configuración de la GUI
     */
    private void configurarInterfaz() {
        iniciarComponentes();
        distribuirComponentes();
        configurarListeners();
    }

    /**
     * Método para inicializar los componentes de la interfaz
     */
    private void iniciarComponentes() {
        salida = new JTextArea();
        salida.setEditable(false);
        salida.setFont(new Font("Monospaced", Font.PLAIN, 12));
        palabraParaBuscar = new JTextField(20);
        
        cargarArchivo = new JButton("Cargar Archivo");
        buscarTodas = new JButton("Buscar Todas las Palabras");
        buscarPalabra = new JButton("Buscar Palabra Específica");
        guardarDic = new JButton("Guardar Diccionario");
        metodoBusqueda = new JComboBox<>(new String[]{"DFS", "BFS"});
        
        panelTablero = new JPanel(new GridLayout(4, 4));
        panelTablero.setPreferredSize(new Dimension(300, 300));
        panelTablero.setBorder(BorderFactory.createTitledBorder("Tablero"));
        
        tiempo = new JLabel("Tiempo: 0 ms");
        tiempo.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    /**
     * Configurar el layout de la interfaz gráfica
     */
    private void distribuirComponentes() {
        JPanel panelSuperior = new JPanel();
        panelSuperior.add(cargarArchivo);
        panelSuperior.add(new JLabel("Método de búsqueda:"));
        panelSuperior.add(metodoBusqueda);
        panelSuperior.add(buscarTodas);
        
        JPanel middlePanel = new JPanel(new BorderLayout());
        middlePanel.add(panelTablero, BorderLayout.WEST);
        JScrollPane scrollPane = new JScrollPane(salida);
        middlePanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel panelInferior = new JPanel();
        panelInferior.add(new JLabel("Palabra a buscar:"));
        panelInferior.add(palabraParaBuscar);
        panelInferior.add(buscarPalabra);
        panelInferior.add(guardarDic);
        panelInferior.add(tiempo);
        
        setLayout(new BorderLayout());
        add(panelSuperior, BorderLayout.NORTH);
        add(middlePanel, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    /**
     * Hacer que los botones o áreas de texto escuchen el input del usuario
     */
    private void configurarListeners() {
        cargarArchivo.addActionListener(e -> cargarArchivo());
        buscarTodas.addActionListener(e -> buscarTodasLasPalabras());
        buscarPalabra.addActionListener(e -> buscarPalabraEspecifica());
        guardarDic.addActionListener(e -> guardarDiccionario());
    }
    
    /**
     * Cargar el archivo dado por el usuario
     */
    private void cargarArchivo() {
        ManejadorArchivos.FileData datos = manejadorArchivos.cargarArchivo(this);
        if (datos != null) {
            this.tablero = datos.tablero();
            this.diccionario = datos.diccionario();
            mostrarTablero();
            buscador = new BuscadorPalabras(tablero);
            salida.setText("Diccionario cargado con " + diccionario.size() + " palabras.\n");
            salida.append("Tablero cargado correctamente.\n");
        }
    }

    /**
     * Método para mostrar la sopa de letras dada
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
     * Método para buscar todas las palabras del diccionario dado.
     */
    private void buscarTodasLasPalabras() {
        if (tablero == null || diccionario == null || buscador == null) {
            mostrarError("Primero cargue un archivo con el tablero y diccionario.");
            return;
        }
        
        String metodo = (String) metodoBusqueda.getSelectedItem();
        salida.append("\nBuscando todas las palabras usando " + metodo + "...\n");
        
        long tiempoInicial = System.currentTimeMillis();
        Set<String> palabrasEncontradas = buscador.buscarTodasLasPalabras(diccionario, metodo.equals("BFS"));
        long duracion = System.currentTimeMillis() - tiempoInicial;
        
        tiempo.setText("Tiempo: " + duracion + " ms");
        salida.append("Palabras encontradas (" + palabrasEncontradas.size() + "):\n");
        palabrasEncontradas.forEach(palabra -> salida.append(palabra + "\n"));
        salida.append("Tiempo total: " + duracion + " ms\n");
    }
    
    /**
     * Método para buscar una palabra específica dada por el usuario
     */
    private void buscarPalabraEspecifica() {
        // Revisar que el tablero y diccionario no sean nulos
        if (tablero == null || diccionario == null || buscador == null) {
            mostrarError("Primero cargue un archivo con el tablero y diccionario.");
            return;
        }
        // Obtener palabra a buscar y verificar que no sea vacía
        String palabra = palabraParaBuscar.getText().trim().toUpperCase();
        if (palabra.isEmpty()) {
            mostrarError("Ingrese una palabra para buscar.");
            return;
        }
        // Método a usar
        String metodo = (String) metodoBusqueda.getSelectedItem();
        salida.append("\nBuscando la palabra '" + palabra + "' usando " + metodo + "...\n");
        // Tracking del tiempo
        long tiempoInicial = System.currentTimeMillis();
        boolean encontrada = buscador.buscarPalabra(palabra, metodo.equals("BFS"));
        long duracion = System.currentTimeMillis() - tiempoInicial;
        tiempo.setText("Tiempo: " + duracion + " ms");
        // Si se encontró, imprimirlo en la salida
        if (encontrada) {
            salida.append("La palabra '" + palabra + "' fue encontrada en el tablero.\n");
            if (!diccionario.contains(palabra)) {
                diccionario.add(palabra);
                salida.append("La palabra '" + palabra + "' ha sido agregada al diccionario.\n");
            }
            // Mostrar árbol de recorrido BFS si se usó ese método
            if (metodo.equals("BFS"))
                mostrarArbol(palabra);
        }
        // De lo contrario, imprimir un mensaje avisando esto
        else {
            salida.append("La palabra '" + palabra + "' NO fue encontrada en el tablero.\n");
        }
        salida.append("Tiempo de búsqueda: " + duracion + " ms\n");
    }
    
    /**
     * Añadir al diccionario con las palabras nuevas que se han encontrado
     * y guardar en el archivo original
     */
    private void guardarDiccionario() {
        if (manejadorArchivos.guardarDiccionario(diccionario)) {
            salida.append("\nDiccionario guardado correctamente en el archivo.\n");
        } else {
            mostrarError("No hay diccionario cargado o no se ha seleccionado un archivo.");
        }
    }
    
    /**
     * Mostrar un mensaje si ocurre algún error en el programa
     * @param mensaje: mensaje que enseñar en la interfaz
     */
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
    * Muestra una representación visual del árbol de recorrido BFS
    * en la búsqueda de una palabra.
    * @param palabra: palabra para la cual mostrar su árbol de búsqueda
    */
   private void mostrarArbol(String palabra) {
       // Verificar que la palabra no sea vacía
       if (palabra == null || palabra.isEmpty())
           return;
       // Configuración inicial del sistema GraphStream
       System.setProperty("org.graphstream.ui", "swing");
       System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
       // Crear el grafo principal que contendrá todos los subárboles
       Graph graph = new MultiGraph("BFS Tree");
       graph.setAttribute("ui.stylesheet", 
           "node {" +
           "   fill-color: #4682B4;" +
           "   size: 40px;" +
           "   text-alignment: center;" +
           "   text-size: 14px;" +
           "   text-style: bold;" +
           "   text-color: white;" +
           "}" +
           "edge {" +
           "   fill-color: #777;" +
           "   size: 2px;" +
           "}");
       char primeraLetra = palabra.charAt(0);
       boolean encontrado = false;
       // Iterar por el tablero para encontrar todas las posiciones iniciales posibles
       for (int row = 0; row < 4; row++) {
           for (int col = 0; col < 4; col++) {
               if (tablero[row][col] == primeraLetra) {
                   encontrado = true;
                   // Configurar la estructura para BFS
                   Queue<NodoBFS> cola = new LinkedList<>();
                   Set<String> visitados = new HashSet<>();
                   // Crear nodo raíz con su etiqueta
                   String idRaiz = String.format("%d-%d-%d,%d", row, col, row, col);
                   Node raiz = graph.addNode(idRaiz);
                   raiz.setAttribute("ui.label", String.format("(%d,%d)\n%c", row, col, primeraLetra));
                   cola.add(new NodoBFS(row, col, 0, null));
                   visitados.add(idRaiz);
                   // Mapa para mantener relación padre-hijo
                   Map<String, String> padreDe = new HashMap<>();
                   // Iterar hasta que la cola esté vacía
                   while (!cola.isEmpty()) {
                       NodoBFS nodoActual = cola.poll();
                       // Terminar si llegamos al final de la palabra
                       if (nodoActual.indice == palabra.length() - 1) {
                           break;
                       }
                       // De lo contrario, avanzar a la siguiente letra
                       char nextChar = palabra.charAt(nodoActual.indice + 1);
                       // Explorar todos los vecinos posibles
                       for (int r = Math.max(0, nodoActual.fila - 1); r <= Math.min(3, nodoActual.fila + 1); r++) {
                           for (int c = Math.max(0, nodoActual.col - 1); c <= Math.min(3, nodoActual.col + 1); c++) {
                               if (r == nodoActual.fila && c == nodoActual.col)
                                   continue;
                               // Revisar si el vecino corresponde a la siguiente letra
                               if (tablero[r][c] == nextChar) {
                                   String idNodo = String.format("%d-%d-%d,%d", row, col, r, c);
                                   // Revisar si no se ha visitado el nodo
                                   if (!visitados.contains(idNodo)) {
                                       Node nodo = graph.addNode(idNodo);
                                       nodo.setAttribute("ui.label", String.format("(%d,%d)\n%c", r, c, nextChar));
                                       padreDe.put(idNodo, String.format("%d-%d-%d,%d", row, col, nodoActual.fila, nodoActual.col));
                                       cola.add(new NodoBFS(r, c, nodoActual.indice + 1, nodoActual));
                                       visitados.add(idNodo);
                                   }
                               }
                           }
                       }
                   }
                   // Crear las aristas basadas en las relaciones padre-hijo
                   for (Map.Entry<String, String> entry : padreDe.entrySet()) {
                       String hijo = entry.getKey();
                       String padre = entry.getValue();
                       graph.addEdge(padre + "-" + hijo, padre, hijo, true);
                   }
               }
           }
       }
       if (!encontrado) {
           JOptionPane.showMessageDialog(this, "La primera letra de la palabra no se encuentra en el tablero.", 
               "Error", JOptionPane.ERROR_MESSAGE);
           return;
       }
       // Insertar el grafo en un cuadro JDialog
       JDialog dialogoGrafico = new JDialog(this, "Árbol de Recorrido BFS - Palabra: " + palabra, true);
       dialogoGrafico.setSize(800, 800);
       // Usar un diseño de caja vertical para organizar los componentes
       JPanel panelPrincipal = new JPanel(new BorderLayout());
       // Usar el método display() del grafo para crear el visualizador
       Viewer viewer = graph.display();
       ViewPanel viewPanel = (ViewPanel) viewer.getDefaultView();
       // Botón de cierre de la visualización
       JButton botonCierre = new JButton("Cerrar Visualización");
       botonCierre.addActionListener(e -> dialogoGrafico.dispose());
       panelPrincipal.add(viewPanel, BorderLayout.CENTER);
       panelPrincipal.add(botonCierre, BorderLayout.SOUTH);
       dialogoGrafico.add(panelPrincipal);
       dialogoGrafico.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
       dialogoGrafico.setLocationRelativeTo(this);
       dialogoGrafico.setVisible(true);
   }
    
}
