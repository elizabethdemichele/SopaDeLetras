/**
    * Lee el archivo para extraer el diccionario y el tablero.
    * @param archivo Archivo a leer
    * @throws IOException Si ocurre un error al leer el archivo
    */
private void leerArchivo(File archivo) throws IOException {
    dictionary = new HashSet<>();
    board = new char[4][4];
    matrizAdyacencia = new boolean[16][16]; // 4x4 tablero = 16 nodos
    
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
                dictionary.add(linea.toUpperCase());
            } else if (enTab && !linea.isEmpty()) {
                String[] letras = linea.split("\\s*,\\s*");
                for (int col = 0;  col < letras.length; col++) {
                    if (!letras[col].isEmpty()) {
                        board[Math.floorDiv(col, 4)][col % 4] = letras[col].charAt(0);
                    }
                }
            }
        }
    }
    
    // Construir la matriz de adyacencia para el grafo del tablero
    hacerMatriz();
}

/**
 * Construye la matriz de adyacencia para el grafo del tablero.
 * Cada celda está conectada con sus 8 vecinos (horizontal, vertical y diagonal).
 */
private void hacerMatriz() {
    for (int fila = 0; fila < 4; fila++) {
        for (int col = 0; col < 4; col++) {
            int nodo = fila * 4 + col;
            
            // Verificar los 8 vecinos posibles
            for (int r = Math.max(0, fila - 1); r <= Math.min(3, fila + 1); r++) {
                for (int c = Math.max(0, col - 1); c <= Math.min(3, col + 1); c++) {
                    if (r == fila && c == col) continue; // No hay autoconexión
                    
                    int vecino = r * 4 + c;
                    matrizAdyacencia[nodo][vecino] = true;
                    matrizAdyacencia[vecino][nodo] = true;
                }
            }
        }
    }
}
