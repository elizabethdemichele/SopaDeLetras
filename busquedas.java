/**
 * Búsqueda en profundidad (DFS) para encontrar una palabra.
 */
private boolean dfsSearch(String palabra, int fila, int col, int indice, boolean[][] visitados) {
    if (indice == palabra.length()) {
        return true;
    }
    
    if (fila < 0 || fila >= 4 || col < 0 || col >= 4 || visitados[fila][col] || 
        tablero[fila][col] != palabra.charAt(indice)) {
        return false;
    }
    
    visitados[fila][col] = true;
    
    // Explorar los 8 vecinos posibles
    for (int r = fila - 1; r <= fila + 1; r++) {
        for (int c = col - 1; c <= col + 1; c++) {
            if (r == fila && c == col) continue;
            if (dfsSearch(palabra, r, c, indice + 1, visitados)) {
                return true;
            }
        }
    }
    
    visitados[fila][col] = false;
    return false;
}


/**
 * Clase auxiliar para representar nodos en la búsqueda BFS.
 */
private static class NodoBFS {
    int fila, col;
    int indice;
    NodoBFS parent;
    
    NodoBFS(int fila, int col, int indice, NodoBFS parent) {
        this.fila = fila;
        this.col = col;
        this.indice = indice;
        this.parent = parent;
    }
}


/**
 * Búsqueda en anchura (BFS) para encontrar una palabra.
 */
private boolean bfsSearch(String palabra, int filaInicial, int columnaInicial) {
    Queue<NodoBFS> queue = new LinkedList<>();
    queue.add(new NodoBFS(filaInicial, columnaInicial, 0, null));

    while (!queue.isEmpty()) {
        NodoBFS nodoActual = queue.poll();

        if (nodoActual.indice == palabra.length() - 1) {
            return true;
        }

        // Explorar los 8 vecinos posibles
        for (int r = Math.max(0, nodoActual.fila - 1); r <= Math.min(3, nodoActual.fila + 1); r++) {
            for (int c = Math.max(0, nodoActual.col - 1); c <= Math.min(3, nodoActual.col + 1); c++) {
                if (r == nodoActual.fila && c == nodoActual.col) continue;

                if (tablero[r][c] == palabra.charAt(nodoActual.indice + 1)) {
                    queue.add(new NodoBFS(r, c, nodoActual.indice + 1, nodoActual));
                }
            }
        }
    }

    return false;
}
