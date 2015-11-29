package trabajoFinal.cps;

import java.util.Iterator;
import java.util.Vector;

public class TablaDePaginacion {
    //Guarda la dirección en memoria de la página [n] del vector.
    Vector <Pagina> vecPaginas;
    // ID del proceso al cual pertenece la tabla de paginación.
    int iIDProceso;

    /**
     * Inicializa iIDProceso, y el vector de Páginas.
     *
     * @param iIDProceso es el ID del prcoceso a inicializar.
     * @param iNumPaginas es la cantidad de paginas del proceso.
     */
    public void TablaDePaginacion(int iIDProceso, int iNumPaginas) {
        this.iIDProceso = iIDProceso;
        vecPaginas.setSize(iNumPaginas);
    }

    /**
     * Método utilizado para obtener las direcciones de memoria que se deben
     * liberar
     *
     * @return Un vector de enteros que representa las direcciones
     * en memoria a liberar.
     */
    public Vector<Integer> liberarMemoria() {

        Vector<Integer> vecLiberar = new Vector<>();

        for (Pagina p : vecPaginas) {
            vecLiberar.addElement(p.getDirFisica);
        }

        return vecLiberar;
    }

    /**
     * Método utilizado para obtener las direcciones de swap que se deben
     * liberar.
     *
     * @return Un vector de enteros que representa las direcciones
     * en Swap a liberar.
     */
    public Vector<Integer> liberarSwap() {

        Vector<Integer> vecLiberar = new Vector<>();

        for (Pagina p : vecPaginas) {
            vecLiberar.addElement(p.iDirSwap);
        }

        return vecLiberar;
    }
}

