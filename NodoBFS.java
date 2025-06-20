package sopaletras;


/**
 * Clase auxiliar para representar nodos en la búsqueda BFS
 */
public class NodoBFS {
    public int fila, col;
    public int indice;
    
    public NodoBFS(int fila, int col, int indice, NodoBFS padre) {
        this.fila = fila;
        this.col = col;
        this.indice = indice;
    }
}
