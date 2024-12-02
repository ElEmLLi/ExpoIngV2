package paquetegrafos;

//----------------------------------------------------------------------------------------------------------------------

public class NodoGrafo {
    Object dato;
    ListaAdyacencia lista;
    NodoGrafo siguiente;
    int inicioTemprano;
    int inicioTardio;
    int holgura;
    double x, y;

    //------------------------------------------------------------------------------------------------------------------

    public NodoGrafo(Object x) {
        dato = x;
        lista = new ListaAdyacencia();
        siguiente = null;
        inicioTemprano = 0;
        inicioTardio = Integer.MAX_VALUE;
        holgura = 0;
        this.x = 0;
        this.y = 0;
    }

    //------------------------------------------------------------------------------------------------------------------

    public void CalcularHolgura() {
        holgura = inicioTardio - inicioTemprano;
    }
}

//----------------------------------------------------------------------------------------------------------------------