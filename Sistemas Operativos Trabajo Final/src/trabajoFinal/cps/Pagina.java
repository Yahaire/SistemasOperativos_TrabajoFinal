package trabajoFinal.cps;

public class Pagina {
	int iDirFisica; // Direcci√≥n de memoria f√≠sica donde se encuentra la p√°gina
	boolean bSwap;		// Si esta p√°gina se encuentra en memoria Swap
	int iDirSwap;	// Si bSwap est√° activo, direcci√≥n de Swap en la que se encuentra la p√°gina.
	boolean bModificado;	// Indica si ha habido alguna modificaci√≥n en la p√°gina.
	
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
		this.bModificado = false;
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
		this.bModificado = true;
	}
	
	/*
	 * Get DirFisica
	 * 
	 * MÈtodo get de iDirFisica.
	 * 
	 * @return la direcciÛn <code>int</code> en memoria en donde est· guardada la p·gina si bSwap es false
	 */
	public int getDirFisica() {
		return this.iDirFisica;
	}
	
	/*
	 * Get Swap
	 * 
	 * MÈtodo get de bSwap
	 * 
	 * @return <code>boolean</code> de si la p·gina se encuentra actualmente en memoria swap
	 */
	public boolean getbSwap() {
		return this.bSwap;
	}
	
	/*
	 * Get DirSwap
	 * 
	 * MÈtodo get de iDirSwap
	 * 
	 * @return la direcciÛn <code>int</code> en memoria en donde est· guardada la p·gina si bSwap es false
	 */
	public int iDirSwap() {
		return this.iDirSwap;
	}
	
	/*
	 * Get Modificado
	 * 
	 * MÈtodo get de bModificado
	 * 
	 * @return <code>boolean</code> de si la p·gina ha sido modificada
	 */
	public boolean getModificado() {
		return this.bModificado;
	}
}
