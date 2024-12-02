package paquetegrafos;

//----------------------------------------------------------------------------------------------------------------------

public class ListaAdyacencia {
    Arco primero;
    Arco ultimo;

    //------------------------------------------------------------------------------------------------------------------

    public ListaAdyacencia() {
        primero = null;
        ultimo = null;
    }

    //------------------------------------------------------------------------------------------------------------------

    public void NuevaAdyancencia(Object origen, Object destino, float peso) {
        Arco nuevo = new Arco(origen, destino, peso);
        if (primero == null) {
            primero = nuevo;
        } else {
            Arco temp = primero;
            while (temp.siguiente != null) {
                temp = temp.siguiente;
            }
            temp.siguiente = nuevo;
        }
    }
}

//----------------------------------------------------------------------------------------------------------------------