package trabajoFinal.cps;


public class MarcoDePagina {
	int iIDProceso;
	int iAcceso;
	long iTimestamp;
	int iNumPagTabla;

	/**
	 * Marco De Página
	 *
	 * Constructor de la clase
	 */
	public MarcoDePagina(){
    iIDProceso = -1;
		iAcceso = -1;
		iTimestamp = -1;
		iNumPagTabla = -1;
	}

	/**
	 * Marco de página (2)
	 *
	 * Constructor parametrizado de la clase
	 * @param iIDProceso es el identificador <code>int</code> del proceso que estará en el marco
	 */
	public MarcoDePagina(int iIDProceso){
		this.iIDProceso = iIDProceso;
		iAcceso = -1;
		iTimestamp = -1;
		iNumPagTabla = -1;
	}

	/**
	 * Acceso
	 *
	 * Contabiliza la cantidad de accesos al marco de página (a utilizar para técnicas de reemplazo)
	 */
	public void acceso(){
		iAcceso++;
	}

	/**
	 * Cargar
	 *
	 * Carga un marco de página con un proceso en específico.
	 *
	 * @param iIDp es el identificador <code>int</code> del proceso
	 * @param iNumP es el número de página <code>int</code> dentro de la tabla de paginación del proceso
	 */
	public void cargar(int iIDp, int iNumP){
		iIDProceso = iIDp;
		iAcceso = 0;
		iTimestamp = System.currentTimeMillis();
		iNumPagTabla = iNumP;
	}

	public int getiIDProceso(){
		return iIDProceso;
	}

	public int getiAcceso(){
		return iAcceso;
	}

	public long getiTimestamp(){
		return iTimestamp;
	}

	public int getiNumPagTabla(){
		return iNumPagTabla;
	}
}
