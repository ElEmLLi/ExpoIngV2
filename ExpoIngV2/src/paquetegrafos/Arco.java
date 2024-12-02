package paquetegrafos;

//----------------------------------------------------------------------------------------------------------------------

public class Arco {
    Object origen;
    Object destino;
    float peso;
    Arco siguiente;

    //------------------------------------------------------------------------------------------------------------------

    public Arco(Object origen, Object destino, float peso) {
        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
        this.siguiente = null;
    }
}

//----------------------------------------------------------------------------------------------------------------------

