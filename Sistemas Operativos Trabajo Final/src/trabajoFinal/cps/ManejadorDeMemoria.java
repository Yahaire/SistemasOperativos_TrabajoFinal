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


	public ManejadorDeMemoria(int iTamMemoria, int iTamSwap, int iTamPagina){
		this.iTamMemoria = iTamMemoria;
		this.iTamSwap = iTamSwap;
		this.iTamPagina = iTamPagina;
		iMarcosMemoria = iTamMemoria/iTamPagina;
		iMarcosSwap = iTamSwap/iTamPagina;

		mdpMemoria = new MarcoDePagina[iMarcosMemoria];
		mdpSwap = new MarcoDePagina[iMarcosSwap];
	}

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

	private int memoriaVacia(){
		int contadorMarcosLibres = 0;
		for(int i = 0; i < iMarcosMemoria; i++){
			if(mdpMemoria[i].getiIDProceso() == 0){
				contadorMarcosLibres++;
			}
		}
		return contadorMarcosLibres;
	}
	private int swapVacio(){
		int contadorMarcosLibres = 0;
		for(int i = 0; i < iMarcosSwap; i++){
			if(mdpSwap[i].getiIDProceso() == 0){
				contadorMarcosLibres++;
			}
		}
		return contadorMarcosLibres;
	}
	private int primerMarcoMemoriaVacio(){
		int i = 0;
		while(mdpMemoria[i].getiIDProceso() != 0){ //Ya se verificó que si había suficientes marcos disponibles
			i++;
		}
		return i;
	}
	private int primerMarcoSwapVacio(){
		int i = 0;
		while(mdpSwap[i].getiIDProceso() != 0){ //Ya se verificó que si había suficientes marcos disponibles
			i++;
		}
		return i;
	}
}
