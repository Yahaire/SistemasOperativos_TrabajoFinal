package trabajoFinal.cps;

import java.util.HashMap;
import java.util.Vector;
import java.util.Set;
import java.util.Arrays;

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
	 * @param iTamMemoria es el tamaño total en bytes de la memoria del simulador.
	 * @param iTamSwap es el tamaño total en bytes del swap del simulador.
	 * @param iTamPagina es el tamaño bytes de los marcos de pagina del simulador.
	 */
	public ManejadorDeMemoria(int iTamMemoria, int iTamSwap, int iTamPagina){
		this.iTamMemoria = iTamMemoria;
		this.iTamSwap = iTamSwap;
		this.iTamPagina = iTamPagina;
		iMarcosMemoria = iTamMemoria/iTamPagina;
		iMarcosSwap = iTamSwap/iTamPagina;

		hsmTablasDePaginacion = new HashMap<Integer, TablaDePaginacion>();

		mdpMemoria = new MarcoDePagina[iMarcosMemoria];
		for (int iK = 0; iK < iTamMemoria/iTamPagina; iK++) {
			mdpMemoria[iK] = new MarcoDePagina();
		}

		mdpSwap = new MarcoDePagina[iMarcosSwap];
		for (int iK = 0; iK < iTamSwap/iTamPagina; iK++) {
			mdpSwap[iK] = new MarcoDePagina();
		}
	}
	/**
	 * Carga el proceso a Memoria. Carga el proceso agregando una
	 * TablaDePaginacion a hsmTablasDePaginacion en la posicion iIDProceso.
	 *
	 * @param iIDProceso es el ID del prcoceso a cargar.
	 * @param iTam es el tamaño en bytes del programa a cargar.
	 *
	 * TODO: Corregir return
	 * @return Regresa si el programa se pudo cargar o no.
	 */
	public ProcesoCargado cargarProceso(int iIDProceso, int iTam){
		Vector <Pagina> vecPaginas;
		if(iTam > iTamMemoria){
			return new ProcesoCargado(1);
		}
		if(hsmTablasDePaginacion.containsKey(iIDProceso)){
			return new ProcesoCargado(2);
		}

		int iPaginasRequeridas = iTam/iTamPagina + ((iTam%iTamPagina != 0)?1:0);

		//System.out.println("Paginas requeridas: " + iPaginasRequeridas); //DEBUG

		if(iPaginasRequeridas < memoriaVacia()){//No swapeo
			Vector<Pagina> vecPaginasNuevoProceso = new Vector<Pagina>();
			Vector<Integer> vecMarcosMemoriaAsignados = new Vector<Integer>();
			for(int i = 0; i < iPaginasRequeridas; i++){
				int iMarco = primerMarcoMemoriaVacio();
				vecPaginasNuevoProceso.add(new Pagina(iMarco * iTamPagina));
				vecMarcosMemoriaAsignados.add(iMarco);
				mdpMemoria[iMarco].cargar(iIDProceso, i);
			}
			hsmTablasDePaginacion.put(iIDProceso, new TablaDePaginacion(iIDProceso, vecPaginasNuevoProceso));

			return new ProcesoCargado(iIDProceso, iPaginasRequeridas, vecMarcosMemoriaAsignados);
		}else if(iPaginasRequeridas < (memoriaVacia() + swapVacio())){//Si hay swapeo
			int iPaginasASwapear = iPaginasRequeridas - memoriaVacia();

			//System.out.println("PAginas a swapeoutear: " + iPaginasASwapear); //DEBUG

			Vector<InfoSwap> vecInfoSwap = new Vector<InfoSwap>();

			//Swap Out
			for(int i = 0; i < iPaginasASwapear; i++){
				int marcoSwapOut = mfu();

				int iMarcoSwap = primerMarcoSwapVacio();
				TablaDePaginacion tablaDeProcesoSwapeadoOut = hsmTablasDePaginacion.get(mdpMemoria[marcoSwapOut].getiIDProceso());

				int iPaginaSwap = mdpMemoria[marcoSwapOut].getiNumPagTabla();
				tablaDeProcesoSwapeadoOut.swapOutPagina(marcoSwapOut * iTamPagina, iMarcoSwap * iTamPagina);
				hsmTablasDePaginacion.replace(mdpMemoria[marcoSwapOut].getiIDProceso(), tablaDeProcesoSwapeadoOut);

				mdpSwap[iMarcoSwap] = mdpMemoria[marcoSwapOut];
				mdpMemoria[marcoSwapOut] = new MarcoDePagina();

				vecInfoSwap.add(new InfoSwap(mdpSwap[iMarcoSwap].getiIDProceso(), marcoSwapOut, iMarcoSwap, iPaginaSwap));
			}

			//System.out.println("Memoria vaclia despues de swap out: " + memoriaVacia()); //DEBUG

			//Cargar
			Vector<Pagina> vecPaginasNuevoProceso = new Vector<Pagina>();
			Vector<Integer> vecMarcosMemoriaAsignados = new Vector<Integer>();
			for(int i = 0; i < iPaginasRequeridas; i++){
				int iMarco = primerMarcoMemoriaVacio();
				vecPaginasNuevoProceso.add(new Pagina(iMarco * iTamPagina));
				vecMarcosMemoriaAsignados.add(iMarco);
				mdpMemoria[iMarco].cargar(iIDProceso, i);
			}
			hsmTablasDePaginacion.put(iIDProceso, new TablaDePaginacion(iIDProceso, vecPaginasNuevoProceso));

			return new ProcesoCargado(iIDProceso, iPaginasRequeridas, vecMarcosMemoriaAsignados, vecInfoSwap);
		}else{//No hay espacio en memoria virtual para cargar el programa
			return new ProcesoCargado(3);
		}
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

	/**
	 * Most Frequently Used
	 *
	 * Enontrar el indice del marco de pagina mas frecuentemente usado
	 *
	 * @return el <code>int</code> indice del marco de página mas frecuentemente usado
	 */
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
	 * @param iDirVirtual es el numero <code>int</code> de la memoria virtual a accesar
	 *
	 * @param bModificar es un <code>boolean</code> que indica si se va a
	 * modificar la página accesada
	 *
	 * //TODO: Corregir el return
	 * @return <code>int</code> con el estatus del acceso.
	 * 	0 -> acceso exitoso.
	 * 	1 -> el programa no está cargado.
	 * 	2 -> esa página no pertenece a ese proceso.
	 */
	public MarcoAccesado accesarProceso(int iIDProceso, int iDirVirtual, boolean bModificar){
		if(!hsmTablasDePaginacion.containsKey(iIDProceso)){// el proceso no se encuentra cargado
			MarcoAccesado marcoAccesado = new MarcoAccesado();
			marcoAccesado.iError = 1;
			return marcoAccesado;
		}

		//System.out.println("pid: " + iIDProceso); //DEBUG
		//System.out.println("dir: " + iDirVirtual); //DEBUG

		Vector <Pagina> vecPaginas = hsmTablasDePaginacion.get(iIDProceso).getVecPaginas();

		int iOffset = iDirVirtual%iTamPagina;
		int iPagina = iDirVirtual/iTamPagina;

		//System.out.println("iPagina: " + iPagina); //DEBUG

		if(iPagina > vecPaginas.size()){//esa página no pertenece a ese proceso
			MarcoAccesado marcoAccesado = new MarcoAccesado();
			marcoAccesado.iError = 2;
			return marcoAccesado;
		}

		Pagina paginaAccesada = vecPaginas.get(iPagina);
		if(!paginaAccesada.getbSwap()){// la Pagina está en memoria, no hay swaping
			//System.out.println("la Pagina está en memoria, no hay swaping"); //DEBUG

			int iMarco = paginaAccesada.getDirFisica() / iTamPagina;
			mdpMemoria[iMarco].acceso();

			MarcoAccesado marcoAccesado = new MarcoAccesado();
			marcoAccesado.iDirDeMemoria = iMarco * iTamPagina + iOffset;
			marcoAccesado.bPagefault = false;
			TablaDePaginacion tablaDePaginacion = hsmTablasDePaginacion.get(mdpMemoria[iMarco].getiIDProceso());
			//System.out.println("iDirMemoria: " + marcoAccesado.iDirDeMemoria); //DEBUG
			marcoAccesado.iPagina =  tablaDePaginacion.getPaginaPorDir(iMarco * iTamPagina);

			return marcoAccesado;

		}else{// la Pagina está en swap, hay que hacer swapIn
		//System.out.println("la Pagina está en swap, hay que hacer swapIn"); //DEBUG
			if(memoriaVacia() > 1){// si hay espacio en memoria para hacer el swapIn
				int iMarco = primerMarcoMemoriaVacio();
				mdpMemoria[iMarco] = new MarcoDePagina(iIDProceso);

				TablaDePaginacion tablaDePaginacion = hsmTablasDePaginacion.get(iIDProceso);
				tablaDePaginacion.swapInPagina(paginaAccesada.getiDirSwap(), iMarco * iTamPagina);
				hsmTablasDePaginacion.replace(iIDProceso, tablaDePaginacion);

				MarcoAccesado marcoAccesado = new MarcoAccesado();
				marcoAccesado.iDirDeMemoria = iMarco * iTamPagina + iOffset;
				marcoAccesado.iMarcoDeSwapParaSwapIn = paginaAccesada.getiDirSwap() / iTamPagina;
				marcoAccesado.iIDProcesoSwapIn = iIDProceso;
				marcoAccesado.bPagefault = true;
				return marcoAccesado;
			}else{// no hay espacio en memoria para hacer el swapIn, hay que hacer swapOut primero
				MarcoAccesado marcoAccesado = new MarcoAccesado();

				int iMarcoMemoria = mfu();
				marcoAccesado.iDirDeMemoria = iMarcoMemoria * iTamPagina + iOffset;

				int iMarcoSwap = primerMarcoSwapVacio();

				//Swap Out
				TablaDePaginacion hsmTablaDeProcesoSwapeadoOut = hsmTablasDePaginacion.get(mdpMemoria[iMarcoMemoria].getiIDProceso());
				marcoAccesado.iPaginaSwapOut = mdpMemoria[iMarcoMemoria].getiNumPagTabla();
				hsmTablaDeProcesoSwapeadoOut.swapOutPagina(iMarcoMemoria * iTamPagina, iMarcoSwap * iTamPagina);
				hsmTablasDePaginacion.replace(mdpMemoria[iMarcoMemoria].getiIDProceso(), hsmTablaDeProcesoSwapeadoOut);
				marcoAccesado.iIDProcesoSwapOut = mdpMemoria[iMarcoMemoria].getiIDProceso();
				marcoAccesado.iMarcoDeSwapParaSwapOut = iMarcoSwap;

				mdpSwap[iMarcoSwap] = mdpMemoria[marcoAccesado.iPaginaSwapOut];

				//Swap In
				marcoAccesado.iMarcoDeSwapParaSwapIn = paginaAccesada.getiDirSwap() / iTamPagina;
				mdpMemoria[iMarcoMemoria] = mdpSwap[marcoAccesado.iMarcoDeSwapParaSwapIn];

				TablaDePaginacion tablaDePaginacion = hsmTablasDePaginacion.get(iIDProceso);
				tablaDePaginacion.swapInPagina(paginaAccesada.getiDirSwap(), iMarcoMemoria * iTamPagina);
				hsmTablasDePaginacion.replace(iIDProceso, tablaDePaginacion);
				marcoAccesado.bPagefault = true;
				marcoAccesado.bSwapOut = true;

				marcoAccesado.iIDProcesoSwapIn = iIDProceso;
				return marcoAccesado;
			}
		}
	}


	/**
	 * Liberar Proceso
	 *
	 * Liberar un proceso de memoria.
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

			Vector<Integer> vecMemoriaDir = liberarP.liberarMemoria();
			Vector<Integer> vecSwapDir = liberarP.liberarSwap();

			Vector<Integer> vecMemoria = new Vector<Integer>();
			Vector<Integer> vecSwap = new Vector<Integer>();


			for (int iDirFisica : vecMemoriaDir){
				vecMemoria.add(iDirFisica / iTamPagina);
				mdpMemoria[iDirFisica / iTamPagina] = new MarcoDePagina();
			}
			for (int iDirFisica : vecSwapDir){
				vecSwap.add(iDirFisica / iTamPagina);
				mdpSwap[iDirFisica / iTamPagina] = new MarcoDePagina();
			}

			hsmTablasDePaginacion.remove(iIDProceso);

			marcosLiberados = new MarcosLiberados(true, vecMemoria, vecSwap);
		}else{
			marcosLiberados = new MarcosLiberados(false, null, null);
		}
		return marcosLiberados;
	}

	public Vector<Integer> getTodosProcesos(){
		Set setIDProcesos = hsmTablasDePaginacion.keySet();

		return new Vector(Arrays.asList(setIDProcesos.toArray()));
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
		public int iDirDeMemoria;
		public int iMarcoDeSwapParaSwapIn;
		public int iMarcoDeSwapParaSwapOut;
		public int iPagina;
		public int iPaginaSwapOut;

		MarcoAccesado(){
			iError = 0;
		}
	}
	public class InfoSwap{
		public int iIDProceso;
		public int iMarcoDeMemoria;
		public int iMarcoSwap;
		public int iPagina;

		/**
		 * Constructor parametrizado de IndoSwap
		 *
		 * @param iIDProceso es el <code>int</code> del identificador del proceso
		 */
		InfoSwap(int iIDProceso, int iMarcoDeMemoria, int iMarcoSwap, int iPagina){
			this.iIDProceso = iIDProceso;
			this.iMarcoDeMemoria = iMarcoDeMemoria;
			this.iMarcoSwap = iMarcoSwap;
			this.iPagina = iPagina;
		}
	}
	public class ProcesoCargado{
		public int iError;
		public int iIDProceso;
		public Vector<Integer> vecMarcosMemoriaAsignados;
		public Vector<InfoSwap> vecInfoSwap;
		public int iPagefaults;
		public boolean bSwapOut;

		ProcesoCargado(int iError){
			this.iError = iError;
		}
		ProcesoCargado(int iIDProceso, int iPagefaults, Vector<Integer> vecMarcosMemoriaAsignados){
			this.iError = 0;
			this.iIDProceso = iIDProceso;
			this.iPagefaults = iPagefaults;
			this.vecMarcosMemoriaAsignados = vecMarcosMemoriaAsignados;
			bSwapOut = false;
		}
		ProcesoCargado(int iIDProceso, int iPagefaults, Vector<Integer> vecMarcosMemoriaAsignados, Vector<InfoSwap> vecInfoSwap){
			this.iError = 0;
			this.iIDProceso = iIDProceso;
			this.iPagefaults = iPagefaults;
			this.vecMarcosMemoriaAsignados = vecMarcosMemoriaAsignados;
			this.vecInfoSwap = vecInfoSwap;
			bSwapOut = true;
		}
	}
}
