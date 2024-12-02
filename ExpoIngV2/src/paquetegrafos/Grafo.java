package paquetegrafos;

//----------------------------------------------------------------------------------------------------------------------

import javafx.scene.layout.Pane;
import java.util.*;

//----------------------------------------------------------------------------------------------------------------------

public class Grafo {
    NodoGrafo primero;
    NodoGrafo ultimo;
    Pane pane;

    //------------------------------------------------------------------------------------------------------------------

    public Grafo(Pane pane) {
        this.pane = pane;
        primero = null;
        ultimo = null;
    }

    //------------------------------------------------------------------------------------------------------------------

    public boolean GrafoVacio() {
        return primero == null;
    }

    //------------------------------------------------------------------------------------------------------------------

    public void AgregarVertice(Object dato) {
        if (!ExisteVertice(dato)) {
            NodoGrafo nuevo = new NodoGrafo(dato);
            if (GrafoVacio()) {
                primero = nuevo;
                ultimo = nuevo;
            } else {
                ultimo.siguiente = nuevo;
                ultimo = nuevo;
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public void AgregarArista(Object origen, Object destino, float peso, Pane pane) {
        if (ExisteVertice(origen) && ExisteVertice(destino)) {
            NodoGrafo temp = primero;
            while (temp != null) {
                if (temp.dato.equals(origen)) {
                    temp.lista.NuevaAdyancencia(origen, destino, peso);
                    NodoGrafo nodoDestino = obtenerNodo(destino);
                    if (nodoDestino != null) {
                        PaqueteGrafos.animarArista(pane, temp.x, temp.y, nodoDestino.x, nodoDestino.y, peso, false);
                    }
                    break;
                }
                temp = temp.siguiente;
            }
        } else {
            System.out.println("Error: Nodo origen o destino no existe.");
        }
    }

    //------------------------------------------------------------------------------------------------------------------

    public boolean ExisteVertice(Object dato) {
        NodoGrafo temp = primero;
        while (temp != null) {
            if (temp.dato.equals(dato)) {
                return true;
            }
            temp = temp.siguiente;
        }
        return false;
    }

    //------------------------------------------------------------------------------------------------------------------

    public NodoGrafo obtenerNodo(Object dato) {
        NodoGrafo temp = primero;
        while (temp != null) {
            if (temp.dato.equals(dato)) {
                return temp;
            }
            temp = temp.siguiente;
        }
        return null;
    }

    //------------------------------------------------------------------------------------------------------------------

    public List<Arco> CalcularRutaCritica() {
        Map<Object, Integer> gradosEntrada = new HashMap<>();
        Map<Object, NodoGrafo> nodos = new HashMap<>();
        List<Arco> rutaCritica = new ArrayList<>();

        NodoGrafo temp = primero;
        while (temp != null) {
            gradosEntrada.put(temp.dato, 0);
            nodos.put(temp.dato, temp);
            temp = temp.siguiente;
        }

        temp = primero;
        while (temp != null) {
            Arco arco = temp.lista.primero;
            while (arco != null) {
                gradosEntrada.put(arco.destino, gradosEntrada.get(arco.destino) + 1);
                arco = arco.siguiente;
            }
            temp = temp.siguiente;
        }

        Queue<NodoGrafo> cola = new LinkedList<>();
        Stack<NodoGrafo> pila = new Stack<>();

        for (Map.Entry<Object, Integer> entry : gradosEntrada.entrySet()) {
            if (entry.getValue() == 0) {
                cola.add(nodos.get(entry.getKey()));
            }
        }

        while (!cola.isEmpty()) {
            NodoGrafo actual = cola.poll();
            pila.push(actual);

            Arco arco = actual.lista.primero;
            while (arco != null) {
                NodoGrafo destino = nodos.get(arco.destino);
                destino.inicioTemprano = Math.max(destino.inicioTemprano, actual.inicioTemprano + (int) arco.peso);
                gradosEntrada.put(arco.destino, gradosEntrada.get(arco.destino) - 1);
                if (gradosEntrada.get(arco.destino) == 0) {
                    cola.add(destino);
                }
                arco = arco.siguiente;
            }
        }

        NodoGrafo nodoFinal = pila.peek();
        nodoFinal.inicioTardio = nodoFinal.inicioTemprano;

        while (!pila.isEmpty()) {
            NodoGrafo actual = pila.pop();

            Arco arco = actual.lista.primero;
            while (arco != null) {
                NodoGrafo destino = nodos.get(arco.destino);
                actual.inicioTardio = Math.min(actual.inicioTardio, destino.inicioTardio - (int) arco.peso);
                arco = arco.siguiente;
            }
            actual.CalcularHolgura();
        }

        temp = primero;
        while (temp != null) {
            if (temp.holgura == 0) {
                Arco arco = temp.lista.primero;
                while (arco != null) {
                    NodoGrafo destino = nodos.get(arco.destino);
                    if (destino.holgura == 0) {
                        rutaCritica.add(arco);
                    }
                    arco = arco.siguiente;
                }
            }
            temp = temp.siguiente;
        }

        return rutaCritica;
    }
}

//----------------------------------------------------------------------------------------------------------------------