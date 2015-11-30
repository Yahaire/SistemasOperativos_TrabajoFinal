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
				int iDirMemoria = primerMarcoMemoriaVacio();
				vecPaginasNuevoProceso.add(new Pagina(iDirMemoria));
			}
			hsmTablasDePaginacion.put(iIDProceso, new TablaDePaginacion(iIDProceso, vecPaginasNuevoProceso));

		}else if(iPaginasRequeridas < (memoriaVacia() + swapVacio())){//Si hay swapeo



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
}
