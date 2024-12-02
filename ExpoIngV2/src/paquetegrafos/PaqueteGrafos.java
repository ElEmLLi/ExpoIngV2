/** Creado el 1/12/2024 por:
 * Pedro Enrique Mendoza Garcia
 * Mayra Gonzales Martinez
 * Bruno Arturo Goñi Flores
 * Ultima Modificacion: 1/12/2024
 */

//----------------------------------------------------------------------------------------------------------------------

package paquetegrafos;

//----------------------------------------------------------------------------------------------------------------------

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

//----------------------------------------------------------------------------------------------------------------------

public class PaqueteGrafos extends Application {

    //------------------------------------------------------------------------------------------------------------------

    public void start(Stage stage) {
        Pane pane = new Pane();
        Scene scene = new Scene(pane, 1000, 800);

        Grafo g = new Grafo(pane);

        // Ruta al archivo de texto que contiene la matriz
        String rutaArchivo = "C:\\Users\\GNU\\IdeaProjects\\ExpoIngV2\\src\\paquetegrafos\\Grafo.txt";

        // Leer la matriz desde el archivo
        int[][] matriz = leerMatrizDesdeArchivo(rutaArchivo);

        // Nombres de los nodos
        String[] nombres = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};

        // Crear el grafo desde la matriz
        crearGrafoDesdeMatrizArchivo(g, matriz, nombres, pane);

        // Calcular la ruta crítica
        List<Arco> rutaCritica = g.CalcularRutaCritica();

        // Posicionar los nodos jerárquicamente
        posicionarNodosJerarquicos(g, pane);

        // Dibujar las aristas, destacando la ruta crítica
        dibujarAristas(g, pane, rutaCritica);

        stage.setTitle("Grafo con Ruta Crítica");
        stage.setScene(scene);
        stage.show();
    }

    //------------------------------------------------------------------------------------------------------------------

    private int[][] leerMatrizDesdeArchivo(String rutaArchivo) {
        int[][] matriz = null;

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            List<String[]> lineas = new ArrayList<>();
            String linea;

            while ((linea = br.readLine()) != null) {
                lineas.add(linea.split(","));
            }

            int filas = lineas.size();
            int columnas = lineas.get(0).length;
            matriz = new int[filas][columnas];

            for (int i = 0; i < filas; i++) {
                for (int j = 0; j < columnas; j++) {
                    matriz[i][j] = Integer.parseInt(lineas.get(i)[j].trim());
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error al convertir datos a enteros: " + e.getMessage());
        }

        return matriz;
    }

    //------------------------------------------------------------------------------------------------------------------

    private void crearGrafoDesdeMatrizArchivo(Grafo g, int[][] matriz, String[] nombres, Pane pane) {
        // Agregar nodos al grafo
        for (String nombre : nombres) {
            g.AgregarVertice(nombre);
        }

        // Agregar aristas al grafo
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                if (matriz[i][j] > 0) {
                    g.AgregarArista(nombres[i], nombres[j], matriz[i][j], pane);
                }
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    private void posicionarNodosJerarquicos(Grafo g, Pane pane) {
        pane.getChildren().clear();

        int nivelInicialX = 100;
        int espaciadoX = 150;
        int espaciadoY = 100;
        int alturaTotal = 800;

        Map<Object, Integer> gradosEntrada = new HashMap<>();
        Map<Object, NodoGrafo> nodosMap = new HashMap<>();
        Queue<NodoGrafo> cola = new LinkedList<>();
        Map<Integer, List<NodoGrafo>> niveles = new HashMap<>();
        int nivelActual = 0;

        NodoGrafo temp = g.primero;
        while (temp != null) {
            gradosEntrada.put(temp.dato, 0);
            nodosMap.put(temp.dato, temp);
            temp = temp.siguiente;
        }

        temp = g.primero;
        while (temp != null) {
            Arco arco = temp.lista.primero;
            while (arco != null) {
                gradosEntrada.put(arco.destino, gradosEntrada.get(arco.destino) + 1);
                arco = arco.siguiente;
            }
            temp = temp.siguiente;
        }

        for (Map.Entry<Object, Integer> entry : gradosEntrada.entrySet()) {
            if (entry.getValue() == 0) {
                cola.add(nodosMap.get(entry.getKey()));
            }
        }

        while (!cola.isEmpty()) {
            int size = cola.size();
            niveles.putIfAbsent(nivelActual, new ArrayList<>());

            for (int i = 0; i < size; i++) {
                NodoGrafo actual = cola.poll();
                niveles.get(nivelActual).add(actual);

                Arco arco = actual.lista.primero;
                while (arco != null) {
                    Object destino = arco.destino;
                    gradosEntrada.put(destino, gradosEntrada.get(destino) - 1);
                    if (gradosEntrada.get(destino) == 0) {
                        cola.add(nodosMap.get(destino));
                    }
                    arco = arco.siguiente;
                }
            }
            nivelActual++;
        }

        for (Map.Entry<Integer, List<NodoGrafo>> entry : niveles.entrySet()) {
            int nivel = entry.getKey();
            List<NodoGrafo> nodosEnNivel = entry.getValue();

            int yInicial = (alturaTotal - ((nodosEnNivel.size() - 1) * espaciadoY)) / 2;

            for (int i = 0; i < nodosEnNivel.size(); i++) {
                NodoGrafo nodo = nodosEnNivel.get(i);
                nodo.x = nivelInicialX + nivel * espaciadoX;
                nodo.y = yInicial + i * espaciadoY;
                animarNodo(pane, nodo.x, nodo.y, nodo.dato.toString());
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    private void reordenarElementos(Pane pane) {
        List<javafx.scene.Node> nodos = new ArrayList<>();
        List<javafx.scene.Node> aristas = new ArrayList<>();

        // Separar nodos y aristas
        for (javafx.scene.Node node : pane.getChildren()) {
            if (node instanceof javafx.scene.shape.Circle || node instanceof javafx.scene.text.Text) {
                nodos.add(node); // Agregar nodos y etiquetas
            } else {
                aristas.add(node); // Agregar líneas o curvas
            }
        }

        // Limpiar el pane y reorganizar
        pane.getChildren().clear();
        pane.getChildren().addAll(aristas); // Agregar primero las líneas
        pane.getChildren().addAll(nodos);  // Agregar luego los nodos y etiquetas
    }

    //------------------------------------------------------------------------------------------------------------------

    private void dibujarAristas(Grafo g, Pane pane, List<Arco> rutaCritica) {
        NodoGrafo temp = g.primero;
        while (temp != null) {
            Arco arco = temp.lista.primero;
            while (arco != null) {
                NodoGrafo nodoDestino = g.obtenerNodo(arco.destino);
                if (nodoDestino != null) {
                    boolean esRutaCritica = rutaCritica.contains(arco);
                    // Dibuja la arista antes de los nodos
                    animarArista(pane, temp.x, temp.y, nodoDestino.x, nodoDestino.y, arco.peso, esRutaCritica);
                }
                arco = arco.siguiente;
            }
            temp = temp.siguiente;
        }

        // Reorganizar para que las líneas estén detrás de los nodos
        reordenarElementos(pane);
    }

    //------------------------------------------------------------------------------------------------------------------

    public static void animarArista(Pane pane, double startX, double startY, double endX, double endY, float peso, boolean esRutaCritica) {
        // Crear la línea
        javafx.scene.shape.Line linea = new javafx.scene.shape.Line();
        linea.setStartX(startX);
        linea.setStartY(startY);
        linea.setEndX(endX);
        linea.setEndY(endY);

        // Configurar el estilo de la línea
        if (esRutaCritica) {
            linea.setStroke(javafx.scene.paint.Color.RED);
            linea.setStrokeWidth(5); // Más grueso para la ruta crítica
        } else {
            linea.setStroke(javafx.scene.paint.Color.web("#041366"));
            linea.setStrokeWidth(2); // Grosor estándar para las demás aristas
        }

        // Crear el texto del peso
        double midX = (startX + endX) / 2;
        double midY = (startY + endY) / 2;
        javafx.scene.text.Text pesoTexto = new javafx.scene.text.Text(midX, midY - 10, String.valueOf(peso));

        // Estilo del texto del peso
        if (esRutaCritica) {
            pesoTexto.setFill(javafx.scene.paint.Color.RED); // Rojo para ruta crítica
            pesoTexto.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;"); // Tamaño más grande y en negrita
        } else {
            pesoTexto.setFill(javafx.scene.paint.Color.web("#041366")); // Negro para otras rutas
            pesoTexto.setStyle("-fx-font-size: 14px;"); // Tamaño más grande y en negrita
        }

        // Agregar la línea y el texto al pane
        pane.getChildren().addAll(linea, pesoTexto);
    }

    //------------------------------------------------------------------------------------------------------------------

    public static void animarNodo(Pane pane, double x, double y, String etiqueta) {
        // Crear el nodo como un círculo
        javafx.scene.shape.Circle nodo = new javafx.scene.shape.Circle(x, y, 20);
        nodo.setFill(javafx.scene.paint.Color.web("#bffd5e")); // color de relleno del nodo
        nodo.setStroke(javafx.scene.paint.Color.BLACK); // Color de circunferencia
        nodo.setStrokeWidth(3); //Grosor de la Circunferencia

        // Crear el texto del nodo
        javafx.scene.text.Text text = new javafx.scene.text.Text(x - 5, y + 5, etiqueta);
        text.setFill(javafx.scene.paint.Color.BLACK);
        text.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;"); // Tamaño de fuente más grande y en negrita

        // Agregar el nodo y el texto al pane
        pane.getChildren().addAll(nodo, text);
    }

    //------------------------------------------------------------------------------------------------------------------

    public static void main(String[] args) {
        launch();
    }
}

//----------------------------------------------------------------------------------------------------------------------