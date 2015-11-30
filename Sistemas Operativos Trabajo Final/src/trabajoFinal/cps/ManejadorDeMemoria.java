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
				int marcoSwapOut = mfu();

				int iMarcoSwap = primerMarcoSwapVacio();
				TablaDePaginacion hsmTablaDeProccesoSwapeadoOut = hsmTablasDePaginacion.get(mdpMemoria[marcoSwapOut].getiIDProceso());
				hsmTablaDeProccesoSwapeadoOut.swapOutPagina(marcoSwapOut * iTamPagina, iMarcoSwap * iTamPagina);
				hsmTablasDePaginacion.replace(mdpMemoria[marcoSwapOut].getiIDProceso(), hsmTablaDeProccesoSwapeadoOut);

				mdpSwap[iMarcoSwap] = mdpMemoria[marcoSwapOut];
				mdpMemoria[marcoSwapOut].cargar(iIDProceso, i);
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
			if(mdpMemoria[i].getiIDProceso() == -1){
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
			if(mdpSwap[i].getiIDProceso() == -1){
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
		while(mdpMemoria[i].getiIDProceso() != -1){ //Ya se verificó que si había suficientes marcos disponibles
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
		while(mdpSwap[i].getiIDProceso() != -1){ //Ya se verificó que si había suficientes marcos disponibles
			i++;
		}
		return i;
	}

	private int mfu(){
		int maxiAcceso = -1;
		int maxiAccesoNum = -1;
		long maxiAccesoTimestamp = -1;
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
		return maxiAccesoNum;
	}

	/**
	 * Accesar Proceso
	 *
	 * Traduce la dirección del proceso iIDProceso en
	 * hsmTablasDePaginacion y llama al método accesar de los
	 * MarcoDePagina que fueron accesados.
	 *
	 * @param iIDProceso es el identificador <code>int</code> del proceso
	 * que accesa a su memoria liberar.
	 *
	 * @param iPagina es el numero <code>int</code> de página que se quiere accesar
	 *
	 * @param bModificar es un <code>boolean</code> que indica si se va a
	 * modificar la página accesada
	 *
	 * //TODO: Corregir el return
	 * @return <code>int</code> con el estatus del acceso.
	 * 	0 -> acceso exitoso.
	 * 	1 -> el programa no está cargado.
	 * 	2 -> esa página no pertenece a ese procceso.
	 */
	MarcoAccesado accesarProceso(int iIDProceso, int iPagina, boolean bModificar){
		if(!hsmTablasDePaginacion.containsKey(iIDProceso)){// el procceso no se encuentra cargado
			MarcoAccesado marcoAccesado = new MarcoAccesado();
			marcoAccesado.iError = 1;
			return marcoAccesado;
		}

		Vector <Pagina> vecPaginas = hsmTablasDePaginacion.get(iIDProceso).getVecPaginas();

		if(iPagina > vecPaginas.size()){//esa página no pertenece a ese procceso
			MarcoAccesado marcoAccesado = new MarcoAccesado();
			marcoAccesado.iError = 2;
			return marcoAccesado;
		}

		Pagina paginaAccesada = vecPaginas.get(iPagina);
		if(!paginaAccesada.getbSwap()){// la Pagina está en memoria, no hay swaping
			int iMarco = paginaAccesada.getDirFisica() / iTamPagina;
			mdpMemoria[iMarco].acceso();

			MarcoAccesado marcoAccesado = new MarcoAccesado();
			marcoAccesado.iMarcoDeMemoria = iMarco;
			return marcoAccesado;

			//TODO: Cambiarla si fue modificada
		}else{// la Pagina está en swap, hay que hacer swapIn
			if(memoriaVacia() > 1){// si hay espacio en memoria para hacer el swapIn
				int iMarco = primerMarcoMemoriaVacio();
				mdpMemoria[iMarco] = new MarcoDePagina(iIDProceso);

				TablaDePaginacion tablaDePaginacion = hsmTablasDePaginacion.get(iIDProceso);
				tablaDePaginacion.swapInPagina(paginaAccesada.getiDirSwap(), iMarco * 8);
				hsmTablasDePaginacion.replace(iIDProceso, tablaDePaginacion);

				MarcoAccesado marcoAccesado = new MarcoAccesado();
				marcoAccesado.iMarcoDeMemoria = iMarco;
				marcoAccesado.iMarcoDeSwapParaSwapIn = paginaAccesada.getiDirSwap() / iTamPagina;
				marcoAccesado.iIDProcesoSwapIn = iIDProceso;
				return marcoAccesado;
			}else{// no hay espacio en memoria para hacer el swapIn, hay que hacer swapOut primero
				MarcoAccesado marcoAccesado = new MarcoAccesado();

				int iMarcoMemoria = mfu();
				marcoAccesado.iMarcoDeMemoria = iMarcoMemoria;

				int iMarcoSwap = primerMarcoSwapVacio();

				//Swap Out
				TablaDePaginacion hsmTablaDeProccesoSwapeadoOut = hsmTablasDePaginacion.get(mdpMemoria[iMarcoMemoria].getiIDProceso());
				marcoAccesado.iPaginaSwapOut = mdpMemoria[iMarcoMemoria].getiNumPagTabla();
				hsmTablaDeProccesoSwapeadoOut.swapOutPagina(iMarcoMemoria * iTamPagina, iMarcoSwap * iTamPagina);
				hsmTablasDePaginacion.replace(mdpMemoria[iMarcoMemoria].getiIDProceso(), hsmTablaDeProccesoSwapeadoOut);

				mdpSwap[iMarcoSwap] = mdpMemoria[marcoAccesado.iPaginaSwapOut];

				//Swap In
				marcoAccesado.iMarcoDeSwapParaSwapIn = paginaAccesada.getiDirSwap() / iTamPagina;
				mdpMemoria[iMarcoMemoria] = mdpSwap[marcoAccesado.iMarcoDeSwapParaSwapIn];

				TablaDePaginacion tablaDePaginacion = hsmTablasDePaginacion.get(iIDProceso);
				tablaDePaginacion.swapInPagina(paginaAccesada.getiDirSwap(), iMarcoMemoria * iTamPagina);
				hsmTablasDePaginacion.replace(iIDProceso, tablaDePaginacion);

				marcoAccesado.iIDProcesoSwapIn = iIDProceso;
				return marcoAccesado;
			}
		}
	}


	/**
	 * Liberar Proceso
	 *
	 * Liberar un procceso de memoria.
	 *
	 * @param iIDProceso es el identificador <code>int</code> del proceso a liberar.
	 *
	 * @return <code>CajaTemporal</code> es un objeto para contener los dos vectores
	 * vecMemoria y vecSwap, que indican las paginas que se liberaron de
	 * memoria y swap respectivamente, y bEstabaCargado que indica si ese
	 * programa estaba cargado en primer lugar.
 **/
	public MarcosLiberados liberarProceso( int iIDProceso ){
		MarcosLiberados marcosLiberados;
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

			marcosLiberados = new MarcosLiberados(true, vecMemoria, vecSwap);
		}else{
			marcosLiberados = new MarcosLiberados(false, null, null);
		}
		return marcosLiberados;
	}

	public class MarcosLiberados{
		public Vector<Integer> vecMemoria;
		public Vector<Integer> vecSwap;
		public boolean bEstabaCargado;

		MarcosLiberados(boolean bEstabaCargado, Vector<Integer> vecMemoria, Vector<Integer> vecSwap){
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
	public class MarcoAccesado{
		public int iError;
		public boolean bPagefault;
		public boolean bSwapOut;
		public int iIDProcesoSwapOut;
		public int iIDProcesoSwapIn;
		public int iMarcoDeMemoria;
		public int iMarcoDeSwapParaSwapIn;
		public int iMarcoDeSwapParaSwapOut;
		public int iPagina;
		public int iPaginaSwapOut;

		MarcoAccesado(){
			iError = 0;
		}
	}
}
