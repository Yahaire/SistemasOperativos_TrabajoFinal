package trabajoFinal.cps;

import java.util.Iterator;
import java.util.Vector;

public class TablaDePaginacion {
	//Guarda la dirección en memoria de la página [n] del vector.
	private Vector <Pagina> vecPaginas;
	// ID del proceso al cual pertenece la tabla de paginación.
	private int iIDProceso;

	/**
	 * Inicializa iIDProceso, y el vector de Páginas.
	 *
	 * @param iIDProceso es el ID del prcoceso a inicializar.
	 * @param vecPaginas es el vector que guarda la dirección en memoria de la página [n] del vector.
	 */
	public TablaDePaginacion(int iIDProceso, Vector<Pagina> vecPaginas) {
		this.iIDProceso = iIDProceso;
		this.vecPaginas = vecPaginas;
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

		for (Pagina p : getVecPaginas()) {
			vecLiberar.addElement(p.getDirFisica());
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

		for (Pagina p : getVecPaginas()) {
			vecLiberar.addElement(p.iDirSwap);
		}

		return vecLiberar;
	}

		/**
		 * Llama a la función swapOut() de la Pagina que se encuentra en iDirMem
		 *
		 * @param iDirMem Es la dirección en Memoria de la pagina a la que se hara swapOut.
		 * @param iDirSwap Es la dirección de Swap donde se pondrá la página.
		 */
		public void swapOutPagina (int iDirMem, int iDirSwap) {

			for (Pagina p : getVecPaginas()) {
				if (p.getDirFisica() == iDirMem) {
					p.swapOut(iDirSwap);
					break;
				}
			}
		}

		/**
		 * Llama a la función swapIn() de la Pagina que se encuentra en iDirSwap
		 *
		 * @param iDirSwap Es la dirección de Swap de donde se saca la página.
		 * @param iDirMem Es la dirección en Memoria donde se pondrá la página.
		 */
		public void swapInPagina (int iDirSwap, int iDirMem) {

			for (Pagina p : getVecPaginas()) {
				if (p.getiDirSwap() == iDirSwap) {
					p.swapIn(iDirMem);
					break;
				}
			}
		}


		/**
	 * Get VecPaginas
	 *
	 * Método get de vecPaginas
	 *
	 * @return El vector <code>Pagina</code> que contiene la dirección
		 * de las paginas en las que esta cargado el proceso.
	 */
		public Vector <Pagina> getVecPaginas() {
			return vecPaginas;
		}

		/**
		 * Set VecPaginas
		 *
		 * Método set de vecPaginas
		 *
		 * @param vecPaginas El vector de paginas que se recibirá.
		 */
		public void setVecPaginas(Vector <Pagina> vecPaginas) {
			this.vecPaginas = vecPaginas;
		}

		/**
	 * Get iIDProceso
	 *
	 * Método get de iIDProceso
	 *
	 * @return El <code>int</code> que contiene el ID del proceso.
	 */
		public int getiIDProceso() {
			return iIDProceso;
		}

		/**
		 * Set iIDProceso
		 *
		 * Método set de iIDProceso
		 *
		 * @param iIDProceso El ID que se le asignará al proceso.
		 */
		public void setiIDProceso(int iIDProceso) {
			this.iIDProceso = iIDProceso;
		}

	/**
	 * Get Página por Direccion
	 *
	 * Obtener el numero de página del proceso que corresponde a la direccion.
	 *
	 * @param iDirFisica es el <code>int</code> de la dirección de la que se busca la página.
	 *
	 * @return el indice de la pagina
	 */
	public int getPaginaPorDir(int iDirFisica){
		for(int i = 0; i < vecPaginas.size(); i++){
			if (vecPaginas.get(i).getDirFisica() == iDirFisica){
				return i;
			}
		}
		return -1;
	}
}

