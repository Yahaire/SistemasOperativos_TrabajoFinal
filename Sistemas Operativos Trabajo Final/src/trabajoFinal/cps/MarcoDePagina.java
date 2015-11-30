package trabajoFinal.cps;
//java.lang.Object;
//java.util.Date;
//java.sql.Timestamp;

public class MarcoDePagina {
  int iIDProceso;
  int iAcceso;
  long iTimestamp;
  int iNumPagTabla;



	public MarcoDePagina(){
		iIDProceso = 0;
		iAcceso = 0;
		iTimestamp = 0;
		iNumPagTabla = 0;
	}

	public void acceso(){
		iAcceso++;
	}

	public void Cargar(int iIDp, int iNumP){
	//  Timestamp time = new Timestamp();

		iIDProceso = iIDp;
		iAcceso = 0;
	//  iTimestamp = time.getime();
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
