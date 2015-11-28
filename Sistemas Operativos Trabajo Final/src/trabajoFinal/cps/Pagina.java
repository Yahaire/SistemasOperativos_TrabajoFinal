package trabajoFinal.cps;

public class Pagina {
	int iDirFisica; // Dirección de memoria física donde se encuentra la página
	boolean bSwap;		// Si esta página se encuentra en memoria Swap
	int iDirSwap;	// Si bSwap está activo, dirección de Swap en la que se encuentra la página.
	boolean bModificacion;	// Indica si ha habido alguna modificación en la página.
	
	/*
	 * Pagina
	 * 
	 * Constructor parametrizado de la clase.
	 * 
	 * @param iDirMemoria es la direcci�n <code>int</code> de memoria en donde se indicar� estar� cargada la p�gina
	 */
	public Pagina( int iDirMemoria ) {
		this.iDirFisica = iDirMemoria;
		this.bSwap = false;
		this.iDirSwap = -1;
		this.bModificacion = false;
	}
	
	/*
	 * Swap Out
	 * 
	 * Cambia bSwap a true y guarda la direcci�n de Swap nueva
	 * 
	 * @param iDirSwap es la direcci�n <code>int</code> de Swap en donde se indicar� est� guardada la p�gina
	 */
	public void swapOut( int iDirSwap ) {
		this.bSwap = true;
		this.iDirSwap = iDirSwap;
	}
	
	/*
	 * Swap In
	 * 
	 * Cambia bSwap a false y guarda la direcci�n de Memoria nueva.
	 * 
	 * @param iDirMem es la direcci�n <code>int</code> de Memoria en donde se indicar� est� guardada la p�gina
	 */
	public void swapIn( int iDirMem ) {
		this.bSwap = false;
		this.iDirFisica = iDirMem;
	}
	
	/*
	 * Modificar
	 * 
	 * Activa el indicador de que esta p�gina ha sido modificada.
	 */
	public void modificar() {
		this.bModificacion = true;
	}
}
