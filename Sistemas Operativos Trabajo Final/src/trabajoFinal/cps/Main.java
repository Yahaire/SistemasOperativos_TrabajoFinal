/**
 * Trabajo Final de Sistemas Operativos
 * 
 * Autores:
 *    - (A01151984) Jorge Pérez
 *    - (A01280388) Melissa Garza
 *    - (A01280252) Adrián Martínez
 *    - (A01136467) Yahaire Salazar
 * 
 * Fecha: 30 de noviembre de 2015
 * 
 * El programa lee archivos .txt para simular la función de un Manejador de Memoria de un 
 * Sistema Operativo, utilizando el algoritmo de reemplazo MFU (Most Frequently Used).
 * 
 * El programa también muestra logs en consola de las acciones realizadas.
 */

package trabajoFinal.cps;

import static java.lang.System.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import java.util.zip.Inflater;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;


import trabajoFinal.cps.*;

public class Main {

	public static void main(String args[]) {
		String sRespuesta;	// Almacena la respuesta de la consola
		Scanner scaEntrada = new Scanner(System.in);	// Lee input de la consola
		BufferedReader bfrArchivoEntrada = null;	// Enlace con el archivo
		
		Archivo arcEntrada;
		
		do {
			for (int iK = 0; iK < 5; iK++) {	//"Limpiar" pantalla
				System.out.println();
			}
			
			arcEntrada = new Archivo();
			
			// Abrir archivo
			System.out.print("Nombre del archivo: ");
			sRespuesta = scaEntrada.nextLine();
			
			if (sRespuesta.contains(".txt")) {	// Aceptar solo archivos .txt
				try {
					bfrArchivoEntrada = new BufferedReader( new FileReader(sRespuesta));
				
					String sEntrada;	// Guarda la linea completa del archivo
					int iContLinea = 0;	// Cuenta la línea de datos que se está leyendo
					
					while ( (sEntrada = bfrArchivoEntrada.readLine()) != null ) {
						iContLinea++;
						
						String sError = arcEntrada.AgregarInstruccion(sEntrada, iContLinea);
						
						if ( sError.equals("") ) {
							// Trabajar con string
						}
						else {
							System.out.print(sError);
						}
					}
					System.out.println();
					bfrArchivoEntrada.close();
					
					// Procesar instrucciones						
					if (arcEntrada.vecInstrucciones.size() > 0) {	// Conseguir la primera instrucción
						int iContInstruccion = 0;
						Archivo.Instruccion insActual;
						
						insActual = arcEntrada.vecInstrucciones.get(0);
					
						// Recorrer todas las instrucciones disponibles o hasta que se indique se finaliza el programa
						while (iContInstruccion < arcEntrada.vecInstrucciones.size()
								&& !(insActual.sTipo.equals("E")) 
								) {
							ManejadorDeMemoria mdmSistemaOperativo = new ManejadorDeMemoria( 2048, 4096, 8 );
							HashMap<Integer, Long> hsmTurnarounds = new HashMap<Integer, Long>();	// Contendrá los tiempos de turnaround de cada uno de los procesos (�ltima vez que el proceso se carg�)
							HashMap<Integer, Integer> hsmPageFaults = new HashMap<Integer, Integer>();	// Cuenta la cantidad de page faults que ha hecho cada proceso
							int iCantSwapInsHechos = 0;		// Almacena la cantidad de swap ins hechos por el manejador de memoria
							int iCantSwapOutsHechos = 0;	// Almacena la cantidad de swap outs hechos en el manejador de memoria
							
							// Continuar hasta que se indique fin de sección de instrucciones
							while ( !(insActual.sTipo.equals("F")) ) {
								System.out.println( insActual.getInstruccion() );	// Mostrar instrucciones diferentes de F y E
								
								if ( insActual.sTipo.equals("P") ) {	// Cargar proceso
									System.out.println("Asignar " + insActual.iValor1 + " bytes al proceso " + insActual.iValor2);
									
									ManejadorDeMemoria.ProcesoCargado cajaTemporal = mdmSistemaOperativo.cargarProceso(insActual.iValor2, insActual.iValor1);

									switch (cajaTemporal.iError) {
										case 0:	// Se cargá el programa correctamente
											hsmTurnarounds.put(insActual.iValor2, System.currentTimeMillis());	// Iniciar contador de turnaround del proceso
											hsmPageFaults.put(insActual.iValor2, cajaTemporal.iPagefaults);
											
											if (cajaTemporal.bSwapOut) { // Mostrar outputs si hubo swaps
												for (ManejadorDeMemoria.InfoSwap infSwap: cajaTemporal.vecInfoSwap) {
													iCantSwapOutsHechos++;	// Cada swap out se cuenta
													
													System.out.print("Pagina " + infSwap.iPagina 
															+ " del proceso " + infSwap.iIDProceso
															+ " swappeada al marco " + infSwap.iMarcoSwap
															+ " del area de swapping"
															+ "\n");
												}
											}
											
											System.out.print("Se asignaron los siguientes marcos de página al proceso " + insActual.iValor2 + ": ");
											for (Integer iMarcoAsignado: cajaTemporal.vecMarcosMemoriaAsignados) {
												System.out.print( iMarcoAsignado + ", ");
											}
											System.out.println();
											break;
										case 1:	// Error. El proceso es más grande que la memoria 
											System.out.println("No fue posible cargar el proceso. El proceso que se intenta cargar es mas grande que la memoria.");
											break;
										case 2:	// Error. El proceso ya está cargado
											System.out.println("No fue posible cargar el proceso. Ya hay un proceso con el mismo ID.");
											break;
										case 3:	// Error. Ya no hay espacio suficiente para cargar el programa (Swap llena)
											System.out.println("No fue posible cargar el proceso. No queda memoria suficiente en el sistema para cargar mas procesos.");
											break;
										default:
											System.out.println("Error inesperado al cargar el proceso.");
									}
								}
								
								else if ( insActual.sTipo.equals("A") ) {	// Accesar a proceso para leer o modificar
									System.out.print("Obtener la dirección real correspondiente a la dirección virtual " + insActual.iValor1
														+ " del proceso " + insActual.iValor2);
									if (insActual.iValor3 > 0) { // se busca modificar la línea
										System.out.print(" y modificar dicha direccion");
									}
									System.out.println(".");
									
									boolean bModificar = insActual.iValor3 == 1;
									ManejadorDeMemoria.MarcoAccesado cajaTemporal = mdmSistemaOperativo.accesarProceso(insActual.iValor2, insActual.iValor1, bModificar);
									
									switch (cajaTemporal.iError) {
										case 0:	// Se accesó el programa correctamente
											if ( cajaTemporal.bPagefault ) { // Se tuvo que cargar página a memoria
												hsmPageFaults.put(insActual.iValor2, 1 + hsmPageFaults.get(insActual.iValor2));	// Aumentar contador de pageFaults
												iCantSwapInsHechos++;
												
												if ( cajaTemporal.bSwapOut ) {	// Fue necersario el hacer un swap para cargar la página a memoria
													iCantSwapOutsHechos++;
													System.out.println(
															"Pagina " + cajaTemporal.iPaginaSwapOut
															+ " del proceso " + cajaTemporal.iIDProcesoSwapOut
															+ " swappeada al marco " + cajaTemporal.iMarcoDeSwapParaSwapOut
															);
												}
												
												System.out.print(	// Decir dónde quedó cargado el proceso
														"Se localizo la pagina " + cajaTemporal.iPagina
														+ " del proceso " + cajaTemporal.iIDProcesoSwapIn
														+ " que estaba en la posicion " + cajaTemporal.iMarcoDeSwapParaSwapIn
														+ " de swapping y se cargo al marco " + cajaTemporal.iDirDeMemoria);
												
												if ( insActual.iValor3 == 1) {
													System.out.print(" modificada");
												}
												
												System.out.println(".");
											}
											if (insActual.iValor3 == 1) {
												System.out.println(
														"Página " + cajaTemporal.iPagina
														+ " del proceso " + insActual.iValor2
														+ " modificada.");
											}
											
											System.out.println(
													"Direccion virtual: " + insActual.iValor1
													+ "\tDireccion real: " + cajaTemporal.iDirDeMemoria);
											break;
										case 1:	// Error. El programa no está cargado en memoria 
											System.out.println("El proceso indicado no existe.");
											break;
										case 2:	// Error. 
											System.out.println("El proceso no cuenta con acceso a la dirección de memoria especificada.");
											break;
										default:
											System.out.println("Error inesperado al accesar el proceso.");
									}
									
								}
								else if ( insActual.sTipo.equals("L") ) {	// Liberar proceso de memoria
									ManejadorDeMemoria.MarcosLiberados cajaTemporal = mdmSistemaOperativo.liberarProceso(insActual.iValor1);
									
									if (cajaTemporal.bEstabaCargado) {	// Asegurarse que el proceso estaba cargado en memoria
										hsmTurnarounds.put(insActual.iValor1, System.currentTimeMillis() - hsmTurnarounds.get(insActual.iValor1));	// Calcular turnaround time
										
										// Mostrar resultados de librerar Memoria
										if ( cajaTemporal.vecMemoria.size() > 0) {
											System.out.print("Se liberan los marcos de página de memoria real: ");
											for (Integer iVal: cajaTemporal.vecMemoria) {
												System.out.print(iVal + ", ");
											}
											System.out.println();
										}
										
										// Mostrar resultados de liberar Swap
										if ( cajaTemporal.vecSwap.size() > 0) {
											System.out.print("Se liberan las posiciones del área de swapping: ");
											for (Integer iVal: cajaTemporal.vecSwap) {
												System.out.print(iVal + ", ");
											}
											System.out.println();
										}
									}
									else {	// Error. El proceso no existía en memoria.
										System.out.println("El proceso que se intentó liberar no se encontraba en memoria.");
									}
								}
								System.out.println();
								
								iContInstruccion++;
								insActual = arcEntrada.vecInstrucciones.get(iContInstruccion);
							}
							System.out.println();
							
							// Calcular turnaround times restantes
							Vector<Integer> veciIDProcesosALiberar = mdmSistemaOperativo.getTodosProcesos();
							for (Integer iIDProceso: veciIDProcesosALiberar) {
								hsmTurnarounds.put(iIDProceso, System.currentTimeMillis() - hsmTurnarounds.get(iIDProceso));
							}
							
							// Calcular turnaround time promedio
							double dTurnaroundPromedio = 0;
							for (Integer iIDProceso: hsmTurnarounds.keySet()) {
								dTurnaroundPromedio += hsmTurnarounds.get(iIDProceso);
							}
							dTurnaroundPromedio /= (double) hsmTurnarounds.size();
							
							// Reporte de salida ("F")
							System.out.println( insActual.getInstruccion() );	// Mostrar instrucción F
							System.out.println("Fin.");
													
							for (Integer iIDProceso: hsmTurnarounds.keySet()) {	// Turnaround por proceso
								System.out.println( 
										"Turnaround del proceso " + iIDProceso
										+ ": " + hsmTurnarounds.get(iIDProceso) 
										+ "ms.");
							}
							System.out.println("Turnaround promedio: " + dTurnaroundPromedio + "ms.");
							System.out.println();
							for (Integer iIDProceso: hsmPageFaults.keySet()) {	// Turnaround por proceso
								System.out.println( 
										"Pagefaults de proceso " + iIDProceso
										+ ": " + hsmPageFaults.get(iIDProceso) );
							}
							System.out.println();
							
							System.out.println("SwapIns realizados: " + iCantSwapInsHechos);
							System.out.println("SwapOuts realizados: " + iCantSwapOutsHechos);
							
							System.out.println();
							System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
							
							iContInstruccion++;
							insActual = arcEntrada.vecInstrucciones.get(iContInstruccion);
						}
						iContInstruccion++;
						
						// Mostrar salidas finales
						System.out.println(insActual.getInstruccion());	// Mostrar la instrucción se espera sea E
						
						if (iContInstruccion < arcEntrada.vecInstrucciones.size()) { // Se terminó el programa con un E y quedaban instrucciones despu�s 
							System.out.println("Fin del programa. Se detectaron instrucciones después de E, pero no se ejecutaron.");
						}
						else if (insActual.sTipo.equals("E")) {
							System.out.println("Fin del programa.");
						}
						else {	// El programa terminó de leer las instrucciones, pero no había E
							System.out.println("Fin del programa. No se encontró la instrucción E para terminar su programa.");
						}
					}
					else {	// Error. No hay instrucciones
						System.out.println("El archivo provisto no contiene instrucciones.");
					}
					
				} catch (FileNotFoundException e) { // Manejar errores al abrir el archivo
					System.out.println("No se ha podido encontrar el archivo indicado\n");
				} catch (IOException e) {	// Falta manejar
					System.out.println("Error inesperado IOException.\n");
				} finally {
					if (bfrArchivoEntrada != null) {
						try {	
							bfrArchivoEntrada.close();	
						} catch (IOException e) {	
							System.out.println("Ocurrió un error inesperado al intentar cerrar el documento.");	
						}
					}
				}
			}
			else {	//%a//Denegar archivos que no sean .txt
				System.out.println("El archivo debe ser un .txt para poder utilizarlo.\n");	
			}
			
			System.out.print("Desea correr el programa nuevamente?(s/n) ");
			sRespuesta = scaEntrada.nextLine();
		} while (sRespuesta.contains("s") || sRespuesta.contains("S"));
																										
	}

}
