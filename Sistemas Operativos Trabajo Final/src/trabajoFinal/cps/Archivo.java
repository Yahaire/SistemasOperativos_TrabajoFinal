package trabajoFinal.cps;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Archivo {
	private String sNombre;			// nombre del archivo
	public Vector<Double> vecX;		//%m// Posición en X de los puntos
	public Vector<Double> vecY;		//%m// Posición en Y de los puntos
	public Vector<Instruccion> vecInstrucciones;
	public double dXk;				//%m// Guarda el dato Xk del archivo (primer dato a leer)				
	
	/*
	 * Archivo
	 * 
	 * Constructor default
	 */
	public Archivo() {
		sNombre = "";	
		vecX = new Vector<Double>();	//%m
		vecY = new Vector<Double>();	//%m
		vecInstrucciones = new Vector<Instruccion>();
		dXk = -1;					//%m
	}
																	
	/*
	 * Archivo (2)
	 * 
	 * Constructor con nombre
	 * 
	 * @param sNombre es el nombre <code>String</code> del archivo
	 */
	public Archivo( String sNombre ) {
		this.sNombre = sNombre;
		vecX = new Vector<Double>();	//%m
		vecY = new Vector<Double>();	//%m
		vecInstrucciones = new Vector<Instruccion>();
		dXk = -1;				 	//%m
	}

	/*
	 * setNombre
	 * 
	 * Set de sNombre
	 * 
	 * @param sNombre es el nombre <code>String</code> del archivo
	 */
	public void setNombre( String sNombre ) {
		this.sNombre = sNombre;
	}

	/*
	 * getNombre
	 * 
	 * Get de sNombre
	 * 
	 * @return el nombre <code>String</code> del archivo
	 */
	public String getNombre() {
		return this.sNombre;
	}
	
	/*
	 * Agregar Instruccion
	 * 
	 * Añade una instrucción nueva.
	 * Regresa un string vacío ("") si se tuvo éxito, y el mensaje de error, si no.
	 * 
	 * @param sInstruccion es el <code>String</code> de la instrucción a agregar
	 * @param iLinea es el número <code>int</code> de línea que se está intentando agregar (para mensajes de error)
	 * 
	 * @return el mensaje <code>String</code> del error encontrado, si es que hay
	 */
	public String AgregarInstruccion( String sInstruccion, int iLinea ) {	//%m		
		String sMensajeError = "No se ha podido agregar la línea " + iLinea + ". ";
		boolean bError = false;
		
		if ( !sInstruccion.trim().equals("") ) {	// Revisar que la instrucción no sea una línea vacía
			StringTokenizer tokEntrada = new StringTokenizer(sInstruccion, " ");
			
			String sDefinidor = tokEntrada.nextToken().trim();
			
			int[] iValores = new int[3];	// Valores a usar para guardar como objeto Instruccion
			
			iValores[0] = -1;
			iValores[1] = -1;
			iValores[2] = -1;
			
			try {
				for (int iK = 0; iK < tokEntrada.countTokens() - 1; iK++) {
					iValores[iK] = Integer.parseInt( tokEntrada.nextToken().trim() );
				}
			} catch (NumberFormatException e) { // Manejar errores al leer datos
				bError = true;
				sMensajeError = sMensajeError + "Alguno de los datos de la instruccion se encuentra mal escrito.\n";
			}
			
			if ( !bError ) {
				Instruccion insAAgregar = new Instruccion(sDefinidor, iValores[0], iValores[1], iValores[2]);
				
				if ( insAAgregar.esInstruccionValida() ) {
					this.vecInstrucciones.addElement(insAAgregar);
				}
				else {
					bError = true;
					sMensajeError = sMensajeError + "El primer caracter de la instrucción no es correcto.\n";
				}
			}
		}
		else {
			bError = true;
			sMensajeError = sMensajeError + "La linea parece estar vacia\n";
		}
		
		if ( bError ) {
			return sMensajeError;
		}
		else {
			return "";
		}
	}
	
	class Instruccion {
		protected String sTipo;	// Tipo de instrucción
		int iValor1;	// Valor 1 (en caso de necesitarse para la instrucción)
		int iValor2;	// Valor 2 (en caso de necesitarse para la instrucción)
		int iValor3;	// Valor 3 (en caso de necesitarse para la instrucción)
		int iCantValDisp;	// Cantidad de valores disponibles. Depende del tipo de instrucción.
		boolean bInstruccionValida;	// Indica si la instrucción es válida o no
		
		/*
		 * Instruccion
		 * 
		 * Constructor parametrizado de la subclase. Se necesitan valor para todos los parámetros. En caso 
		 * de no necesitarse, se ignorará el parámetro (depende del tipo).
		 * 
		 * @sTipo es el tipo en <code>String</code> de la instrucción. Se usará para saber cómo manejar los otros valores
		 * @iVal1 es el valor <code>int</code> del primer dato (en caso de ser necesario)
		 * @iVal2 es el valor <code>int</code> del segundo dato (en caso de ser necesario)
		 * @iVal3 es el valor <code>int</code> del tercer dato (en caso de ser necesario)
		 */
		public Instruccion( String sTipo, int iVal1, int iVal2, int iVal3 ) {
			
			this.bInstruccionValida = true;
			
			this.iValor1 = iVal1;
			this.iValor2 = iVal2;
			this.iValor3 = iVal3;
			
			// Verificar que la isntrucción sea válida y designar cuántos valores se pueden usar
			if (sTipo.equals("P")) {
				this.iCantValDisp = 2;
			}
			else if (sTipo.equals("A")) {
				this.iCantValDisp = 3;
			}
			else if (sTipo.equals("F")) {
				this.iCantValDisp = 0;
			}
			else if (sTipo.equals("L")) {
				this.iCantValDisp = 1;
			}
			else if (sTipo.equals("E")) {
				this.iCantValDisp = 0;
			}
			else {	// Instrucción no válida
				bInstruccionValida = false;
				this.iCantValDisp = -1;
			}
			
			// Asignar tipo
			if ( bInstruccionValida ) {
				this.sTipo = sTipo;
			}
			else {
				this.sTipo = "Instruccion Invalida";
			}
		}
		
		/*
		 * Es Instrucción Válida
		 * 
		 * Regresa si la instrucción es válida
		 */
		public boolean esInstruccionValida() {
			return bInstruccionValida;
		}
		
		public int getValor1() {
			if (iCantValDisp > 0) {
				return this.iValor1;
			}
			else {
				return -1;
			}
		}
		
		public int getValor2() {
			if (iCantValDisp > 1) {
				return this.iValor2;
			}
			else {
				return -1;
			}
		}
		
		public int getValor3() {
			if (iCantValDisp > 2) {
				return this.iValor3;
			}
			else {
				return -1;
			}
		}
	}
		
}

