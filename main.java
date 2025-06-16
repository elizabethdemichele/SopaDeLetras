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