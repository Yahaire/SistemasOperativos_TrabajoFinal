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
	 * @param iDirMemoria es la dirección <code>int</code> de memoria en donde se indicará estará cargada la página
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
	 * Cambia bSwap a true y guarda la dirección de Swap nueva
	 *
	 * @param iDirSwap es la dirección <code>int</code> de Swap en donde se indicará está guardada la página
	 */
	public void swapOut( int iDirSwap ) {
		this.bSwap = true;
		this.iDirSwap = iDirSwap;
	}

	/*
	 * Swap In
	 *
	 * Cambia bSwap a false y guarda la dirección de Memoria nueva.
	 *
	 * @param iDirMem es la dirección <code>int</code> de Memoria en donde se indicará está guardada la página
	 */
	public void swapIn( int iDirMem ) {
		this.bSwap = false;
		this.iDirFisica = iDirMem;
	}

	/*
	 * Modificar
	 *
	 * Activa el indicador de que esta página ha sido modificada.
	 */
	public void modificar() {
		this.bModificado = true;
	}

	/*
	 * Get DirFisica
	 *
	 * Método get de iDirFisica.
	 *
	 * @return la dirección <code>int</code> en memoria en donde está guardada la página si bSwap es false
	 */
	public int getDirFisica() {
		return this.iDirFisica;
	}

	/*
	 * Get Swap
	 *
	 * Método get de bSwap
	 *
	 * @return <code>boolean</code> de si la página se encuentra actualmente en memoria swap
	 */
	public boolean getbSwap() {
		return this.bSwap;
	}

	/*
	 * Get DirSwap
	 *
	 * Método get de iDirSwap
	 *
	 * @return la dirección <code>int</code> en memoria en donde está guardada la página si bSwap es false
	 */
	public int iDirSwap() {
		return this.iDirSwap;
	}

	/*
	 * Get Modificado
	 *
	 * Método get de bModificado
	 *
	 * @return <code>boolean</code> de si la página ha sido modificada
	 */
	public boolean getModificado() {
		return this.bModificado;
	}
}
