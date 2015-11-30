package trabajoFinal.cps;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import java.util.Scanner;

public class Archivo {
	private String sNombre;			// nombre del archivo
	public Vector<Double> vecX;		//%m// Posición en X de los puntos
	public Vector<Double> vecY;		//%m// Posición en Y de los puntos
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
	 * Agregar Punto
	 *
	 * Añade un elemento a vecX y vecY si se considera como un punto nuevo
	 * (Los valores no se encuentran dentro de los vectores, ya). Regresa verdadero
	 * si el punto era nuevo y se agregó al objeto.
	 *
	 * @param fX es el valor <code>double</code> en X del punto a agregar
	 * @param fY es el valor <code>double</code> en Y del punto a agregar
	 *
	 * @return verdadero si se agregó el punto a los vectores
	 */
	public boolean AgregarPunto( double dX, double dY ) {	//%m
		boolean bUnico = true;

		for (int iK = 0; iK < vecX.size(); iK++) {	// Encontrar si el punto ya existe dentro de la lista
			if (vecX.elementAt(iK) == dX && vecY.elementAt(iK) == dY) {	//%m
				bUnico = false;
			}
		}

		if ( bUnico ) {	// Añadir punto si es nuevo
			vecX.addElement(dX);	//%m
			vecY.addElement(dY);	//%m
		}

		return bUnico;
	}
}

