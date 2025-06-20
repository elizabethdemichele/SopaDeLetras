package sopaletras;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Clase que contiene la lógica para buscar palabras en el tablero usando DFS o BFS
 */
public class BuscadorPalabras {
    private char[][] tablero;

    public BuscadorPalabras(char[][] tablero) {
        this.tablero = tablero;
    } 
    
    /**
     * Método para buscar una palabra dado un método (BFS o DFS)
     * @param palabra: palabra a buscar
     * @param usarBFS: indica el método a usar
     * @return bool
     */
    public boolean buscarPalabra(String palabra, boolean usarBFS) {
        if (palabra == null || palabra.isEmpty())
            return false;
            
        char primeraLetra = palabra.charAt(0);
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (tablero[row][col] == primeraLetra) {
                    if (usarBFS) {
                        if (busquedaBFS(palabra, row, col)) {
                            return true;
                        }
                    } else {
                        boolean[][] visitados = new boolean[4][4];
                        if (busquedaDFS(palabra, row, col, 0, visitados)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Método para búsqueda en profundidad de un grafo (DFS)
     * @param palabra: es la palabra específica a buscar
     * @param fila: fila del tablero en la que buscar
     * @param col: columna del tablero en la que buscar
     * @param index: índice del nodo actual
     * @param visitados: nodos que han sido visitados
     * @return true si se encontró la palabra, false de lo contrario
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
     * Búsqueda en anchura (BFS) de una palabra específica
     * @param palabra: palabra a buscar
     * @param filaInicial: fila donde comenzar la búsqueda
     * @param columnaInicial: columna donde empezar la búsqueda
     * @return bool
     */
    private boolean busquedaBFS(String palabra, int filaInicial, int columnaInicial) {
        Queue<NodoBFS> cola = new LinkedList<>();
        cola.add(new NodoBFS(filaInicial, columnaInicial, 0, null));

        while (!cola.isEmpty()) {
            NodoBFS nodoActual = cola.poll();

            if (nodoActual.indice == palabra.length() - 1) {
                return true;
            }

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
    
    /**
     * Método para buscar todas las palabras del diccionario dado
     * @param diccionario: Set string contiene las palabras a buscar
     * @param usarBFS: bool indica qué método usar
     * @return Set string de palabras encontradas
     */
    public Set<String> buscarTodasLasPalabras(Set<String> diccionario, boolean usarBFS) {
        Set<String> palabrasEncontradas = new HashSet<>();
        for (String palabra : diccionario) {
            if (buscarPalabra(palabra, usarBFS)) {
                palabrasEncontradas.add(palabra);
            }
        }
        return palabrasEncontradas;
    }
}
