package trabajoFinal.cps;

public class Pagina {
	int iDirFisica; // Direcci√≥n de memoria f√≠sica donde se encuentra la p√°gina
	boolean bSwap;		// Si esta p√°gina se encuentra en memoria Swap
	int iDirSwap;	// Si bSwap est√° activo, direcci√≥n de Swap en la que se encuentra la p√°gina.
	boolean bModificacion;	// Indica si ha habido alguna modificaci√≥n en la p√°gina.
	
	/*
	 * Pagina
	 * 
	 * Constructor parametrizado de la clase.
	 * 
	 * @param iDirMemoria es la direcciÛn <code>int</code> de memoria en donde se indicar· estar· cargada la p·gina
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
	 * Cambia bSwap a true y guarda la direcciÛn de Swap nueva
	 * 
	 * @param iDirSwap es la direcciÛn <code>int</code> de Swap en donde se indicar· est· guardada la p·gina
	 */
	public void swapOut( int iDirSwap ) {
		this.bSwap = true;
		this.iDirSwap = iDirSwap;
	}
	
	/*
	 * Swap In
	 * 
	 * Cambia bSwap a false y guarda la direcciÛn de Memoria nueva.
	 * 
	 * @param iDirMem es la direcciÛn <code>int</code> de Memoria en donde se indicar· est· guardada la p·gina
	 */
	public void swapIn( int iDirMem ) {
		this.bSwap = false;
		this.iDirFisica = iDirMem;
	}
	
	/*
	 * Modificar
	 * 
	 * Activa el indicador de que esta p·gina ha sido modificada.
	 */
	public void modificar() {
		this.bModificacion = true;
	}
}
