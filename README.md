# Sopa de Letras
- Proyecto 1 de Estructuras de Datos
- Elizabeth De Michele
## Descripción general
Este es un programa que busca palabras en una sopa de letras. Puede buscar todas las palabras en un archivo dado, o buscar una palabra específica. 
Funciona a través de una interfaz gráfica (GUI). El código fuente se estructura en distintos módulos, cada uno con un respectivo archivo.

1. `SopaLetras.java`: archivo principal el cual se debe correr para mostrar la aplicación.
2. `BuscadorPalabras.java`: se encarga de toda la lógica de buscar una palabra en la sopa. Usa dos métodos: _breadth-first search_ (BFS) o
   _depth-first search_ (DFS).
3. `ManejadorArchivos.java`: se encarga de toda la carga, lectura y guardado de archivos.
4. `ManejadorGUI.java`: maneja toda la interfaz gráfica a través de la cuargar se cargan/guardan archivos y se indica el método de búsqueda y las palabras a buscar.
5. `NodoBFS.java`: clase que se usa en la búsqueda BFS.
