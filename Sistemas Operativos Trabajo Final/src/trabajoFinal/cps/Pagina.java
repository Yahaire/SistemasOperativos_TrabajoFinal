package trabajoFinal.cps;

public class Pagina {
	int iDirFisica; // Dirección de memoria física donde se encuentra la página
	boolean bSwap;		// Si esta página se encuentra en memoria Swap
	int iDirSwap;	// Si bSwap está activo, dirección de Swap en la que se encuentra la página.
	boolean bModificado;	// Indica si ha habido alguna modificación en la página.
	
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
		this.bModificado = false;
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
		this.bModificado = true;
	}
	
	/*
	 * Get DirFisica
	 * 
	 * M�todo get de iDirFisica.
	 * 
	 * @return la direcci�n <code>int</code> en memoria en donde est� guardada la p�gina si bSwap es false
	 */
	public int getDirFisica() {
		return this.iDirFisica;
	}
	
	/*
	 * Get Swap
	 * 
	 * M�todo get de bSwap
	 * 
	 * @return <code>boolean</code> de si la p�gina se encuentra actualmente en memoria swap
	 */
	public boolean getbSwap() {
		return this.bSwap;
	}
	
	/*
	 * Get DirSwap
	 * 
	 * M�todo get de iDirSwap
	 * 
	 * @return la direcci�n <code>int</code> en memoria en donde est� guardada la p�gina si bSwap es false
	 */
	public int iDirSwap() {
		return this.iDirSwap;
	}
	
	/*
	 * Get Modificado
	 * 
	 * M�todo get de bModificado
	 * 
	 * @return <code>boolean</code> de si la p�gina ha sido modificada
	 */
	public boolean getModificado() {
		return this.bModificado;
	}
}
