package trabajoFinal.cps;

import java.util.HashMap;
import java.util.Vector;

public class ManejadorDeMemoria {
	HashMap<Integer, TablaDePaginacion> hsmTablasDePaginacion;
	MarcoDePagina[] mdpMemoria;
	int iTamMemoria;
	MarcoDePagina[] mdpSwap;
	int iTamSwap;
	int iTamPagina;
	int iMarcosMemoria;
	int iMarcosSwap;

	/**
	 * Inicializa los arreglos de objetos MarcoDePagina de memoria y de swap.
	 *
	 * @param int iTamMemoria es el tamaño total en bytes de la memoria del simulador.
	 * @param int iTamMSap es el tamaño total en bytes del swap del simulador.
	 * @param int iTamPagina es el tamaño bytes de los marcos de pagina del simulador.
	 */
	public ManejadorDeMemoria(int iTamMemoria, int iTamSwap, int iTamPagina){
		this.iTamMemoria = iTamMemoria;
		this.iTamSwap = iTamSwap;
		this.iTamPagina = iTamPagina;
		iMarcosMemoria = iTamMemoria/iTamPagina;
		iMarcosSwap = iTamSwap/iTamPagina;

		mdpMemoria = new MarcoDePagina[iMarcosMemoria];
		mdpSwap = new MarcoDePagina[iMarcosSwap];
	}
	/**
	 * Carga el proceso a Memoria. Carga el proceso agregando una
	 * TablaDePaginacion a hsmTablasDePaginacion en la posicion iIDProceso.
	 *
	 * @param int iIDProceso es el ID del prcoceso a cargar.
	 * @param int iTam es el tamaño en bytes del programa a cargar.
	 *
	 * @return bool Regresa si el programa se pudo cargar o no.
	 */
	//TODO: Mejorar el return para poder saber la razón por la que no se pudo cargar el programa a memoria
	public boolean cargarProceso(int iIDProceso, int iTam){
		Vector <Pagina> vecPaginas;
		if(iTam > iTamMemoria){
			return false;
		}
		if(hsmTablasDePaginacion.containsKey(iIDProceso)){
			return false;
		}

		int iPaginasRequeridas = iTam/iTamPagina + ((iTam%iTamPagina != 0)?1:0);

		if(iPaginasRequeridas < memoriaVacia()){//No swapeo
			Vector<Pagina> vecPaginasNuevoProceso = new Vector<Pagina>();
			for(int i = 0; i < iPaginasRequeridas; i++){
				int iMarco = primerMarcoMemoriaVacio();
				vecPaginasNuevoProceso.add(new Pagina(iMarco * iTamPagina));
				mdpMemoria[iMarco].cargar(iIDProceso, i);
			}
			hsmTablasDePaginacion.put(iIDProceso, new TablaDePaginacion(iIDProceso, vecPaginasNuevoProceso));

		}else if(iPaginasRequeridas < (memoriaVacia() + swapVacio())){//Si hay swapeo
			int iPaginasASwapear = iPaginasRequeridas - memoriaVacia();

			for(int i = 0; i < iPaginasASwapear; i++){
				//Encontrar la pagina mas usada, estrategia de reemplazo MFU
				int maxiAcceso = 0;
				int maxiAccesoNum = 0;
				long maxiAccesoTimestamp = 0;
				for(int j = 0; j < iMarcosMemoria; j++){
					if(mdpMemoria[j].getiAcceso() > maxiAcceso){
						maxiAcceso = mdpMemoria[j].getiAcceso();
						maxiAccesoNum = j;
						maxiAccesoTimestamp = mdpMemoria[j].getiTimestamp();
					}else if(mdpMemoria[j].getiAcceso() == maxiAcceso && mdpMemoria[j].getiTimestamp() < maxiAccesoTimestamp){
						maxiAcceso = mdpMemoria[j].getiAcceso();
						maxiAccesoNum = j;
						maxiAccesoTimestamp = mdpMemoria[j].getiTimestamp();
					}
				}
				int iMarcoSwap = primerMarcoSwapVacio();
				TablaDePaginacion hsmTablaDeProccesoSwapeadoOut = hsmTablasDePaginacion.get(mdpMemoria[maxiAccesoNum].getiIDProceso());
				hsmTablaDeProccesoSwapeadoOut.swapOutPagina(maxiAccesoNum * iTamPagina, iMarcoSwap * iTamPagina);
				hsmTablasDePaginacion.replace(mdpMemoria[maxiAccesoNum].getiIDProceso(), hsmTablaDeProccesoSwapeadoOut);

				mdpMemoria[maxiAccesoNum].cargar(iIDProceso, i);
			}
		}else{//No hay espacio en memoria virtual para cargar el programa
			return false;
		}

		return true;
	}

	/**
	 * Contar la cantidad de marcos de página libres en memoria.
	 *
	 * @return int la cantidad de marcos de página libres en memoria.
	 */
	private int memoriaVacia(){
		int contadorMarcosLibres = 0;
		for(int i = 0; i < iMarcosMemoria; i++){
			if(mdpMemoria[i].getiIDProceso() == 0){
				contadorMarcosLibres++;
			}
		}
		return contadorMarcosLibres;
	}

	/**
	 * Contar la cantidad de marcos de página libres en swap.
	 *
	 * @return int la cantidad de marcos de página libres en swap.
	 */
	private int swapVacio(){
		int contadorMarcosLibres = 0;
		for(int i = 0; i < iMarcosSwap; i++){
			if(mdpSwap[i].getiIDProceso() == 0){
				contadorMarcosLibres++;
			}
		}
		return contadorMarcosLibres;
	}

	/**
	 * Buscar el primer marco de página vacío de la memoria.
	 *
	 * @return int el numero del primer marco de página vacío de la memoria.
	 */
	private int primerMarcoMemoriaVacio(){
		int i = 0;
		while(mdpMemoria[i].getiIDProceso() != 0){ //Ya se verificó que si había suficientes marcos disponibles
			i++;
		}
		return i;
	}

	/**
	 * Buscar el primer marco de página vacío de swap.
	 *
	 * @return int el numero del primer marco de página vacío de swap.
	 */
	private int primerMarcoSwapVacio(){
		int i = 0;
		while(mdpSwap[i].getiIDProceso() != 0){ //Ya se verificó que si había suficientes marcos disponibles
			i++;
		}
		return i;
	}

	/**
	 * Liberar Proceso
	 *
	 * Liberar un procceso de memoria.
	 *
	 * @param iIDProceso es el identificador <code>int</code> del proceso a liberar.
	 *
	 * @return CajaTemporal es un objeto para contener los dos vectores
	 * vecMemoria y vecSwap, que indican las paginas que se liberaron de
	 * memoria y swap respectivamente, y bEstabaCargado que indica si ese
	 * programa estaba cargado en primer lugar.
 **/
	public CajaTemporal liberarProceso( int iIDProceso ){
		CajaTemporal cajaTemporal;
		if(hsmTablasDePaginacion.containsKey(iIDProceso)){
			TablaDePaginacion liberarP = hsmTablasDePaginacion.get(iIDProceso);

			Vector<Integer> vecMemoria = liberarP.liberarMemoria();
			Vector<Integer> vecSwap = liberarP.liberarSwap();

			for (int iDirFisica : vecMemoria){
				mdpMemoria[iDirFisica / iTamPagina] = new MarcoDePagina();
			}
			for (int iDirFisica : vecSwap){
				mdpSwap[iDirFisica / iTamPagina] = new MarcoDePagina();
			}

			hsmTablasDePaginacion.remove(iIDProceso);

			cajaTemporal = new CajaTemporal(true, vecMemoria, vecSwap);
		}else{
			cajaTemporal = new CajaTemporal(false, null, null);
		}
		return cajaTemporal;
	}

	public class CajaTemporal{
		public Vector<Integer> vecMemoria;
		public Vector<Integer> vecSwap;
		public boolean bEstabaCargado;

		CajaTemporal(boolean bEstabaCargado, Vector<Integer> vecMemoria, Vector<Integer> vecSwap){
			if(bEstabaCargado){
				this.vecMemoria = vecMemoria;
				this.vecSwap = vecSwap;
				this.bEstabaCargado = true;
			}else{
				this.vecMemoria = vecMemoria;
				this.vecSwap = vecSwap;
				this.bEstabaCargado = false;
			}
		}
	}
}
