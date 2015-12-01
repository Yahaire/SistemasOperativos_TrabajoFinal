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
			
			if (sRespuesta.contains(".txt")) {	//%a// Aceptar solo archivos .txt
				try {
					bfrArchivoEntrada = new BufferedReader( new FileReader(sRespuesta));
				
					String sEntrada;	// Guarda la linea completa del archivo
					int iContLinea = 0;	// Cuenta la l�nea de datos que se est� leyendo
					
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
					bfrArchivoEntrada.close();
					
					// Procesar instrucciones						
					if (arcEntrada.vecInstrucciones.size() > 0) {	// Conseguir la primera instrucci�n
						int iContInstruccion = 0;
						Archivo.Instruccion insActual;
						
						insActual = arcEntrada.vecInstrucciones.get(0);
					
						// Recorrer todas las instrucciones disponibles o hasta que se indique se finaliza el programa
						while (iContInstruccion < arcEntrada.vecInstrucciones.size()
								&& !(insActual.sTipo.equals("E")) 
								) {
							ManejadorDeMemoria mdmSistemaOperativo = new ManejadorDeMemoria( 2048, 4096, 8 );
							HashMap<Integer, Long> hsmTurnarounds = new HashMap<Integer, Long>();	// Contendr� los tiempos de turnaround de cada uno de los procesos (�ltima vez que el proceso se carg�)
							
							
							// Continuar hasta que se indique fin de secci�n de instrucciones
							while ( !(insActual.sTipo.equals("F")) ) {
								System.out.println( insActual.getInstruccion() );	// Mostrar instrucciones diferentes de F y E
								
								if ( insActual.sTipo.equals("P") ) {	// Cargar proceso
																	System.out.println(insActual.getValor2());
									System.out.println("Asignar " + insActual.iValor1 + " bytes al proceso " + insActual.iValor2);
									
									ManejadorDeMemoria.ProccesoCargado cajaTemporal = mdmSistemaOperativo.cargarProceso(insActual.iValor1, insActual.iValor2);
									
									switch (cajaTemporal.iError) {
										case 0:	// Se carg� el programa correctamente
											hsmTurnarounds.put(insActual.iValor1, System.currentTimeMillis());	// Iniciar contador de turnaround del proceso
											
											for (ManejadorDeMemoria.InfoSwap infSwap: cajaTemporal.vecInfoSwap) {	// Mostrar outputs si hubo swaps
												System.out.print("Pagina " + infSwap.iPagina 
														+ " del proceso " + infSwap.iIDProceso
														+ " swappeada al marco " + infSwap.iMarcoSwap
														+ " del area de swapping");
											}
											
											System.out.print("Se asignaron los siguientes marcos de pagina al proceso " + insActual.iValor1 + ": ");
											for (Integer iMarcoAsignado: cajaTemporal.vecMarcosMemoriaAsignados) {
												System.out.print( iMarcoAsignado + ", ");
											}
											break;
										case 1:	// Error. El proceso es m�s grande que la memoria 
											System.out.println("No fue posible cargar el proceso. El proceso que se intenta cargar es mas grande que la memoria.");
											break;
										case 2:	// Error. El proceso ya est� cargado
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
									System.out.print("Obtener la direcci�n real correspondiente a la direcci�n virtual " + insActual.iValor1
														+ "del proceso " + insActual.iValor2);
									if (insActual.iValor3 > 0) { // se busca modificar la l�nea
										System.out.print(" y modificar dicha direccion");
									}
									System.out.println(".");
									
									boolean bModificar = insActual.iValor3 == 1;
									ManejadorDeMemoria.MarcoAccesado cajaTemporal = mdmSistemaOperativo.accesarProceso(insActual.iValor1, insActual.iValor2, bModificar);
									
									switch (cajaTemporal.iError) {
										case 0:	// Se acces� el programa correctamente
											if ( cajaTemporal.bPagefault ) { // Se tuvo que cargar p�gina a memoria
												if ( cajaTemporal.bSwapOut ) {	// Fue necersario el hacer un swap para cargar la p�gina a memoria
													System.out.println(
															"Pagina " + cajaTemporal.iPaginaSwapOut
															+ " del proceso " + cajaTemporal.iIDProcesoSwapOut
															+ " swappeada al marco " + cajaTemporal.iMarcoDeSwapParaSwapOut
															);
												}
												
												System.out.print(	// Decir d�nde qued� cargado el proceso
														"Se localizo la pagina " + cajaTemporal.iPagina
														+ "del proceso " + cajaTemporal.iIDProcesoSwapIn
														+ " que estaba en la posicion " + cajaTemporal.iMarcoDeSwapParaSwapIn
														+ " de swapping y se cargo al marco " + cajaTemporal.iMarcoDeMemoria);
												
												if ( insActual.iValor3 == 1) {
													System.out.print(" modificada");
												}
												
												System.out.println(".");
											}
											System.out.println(
													"Direccion virtual: " + insActual.iValor1
													+ "\tDireccion real: " + cajaTemporal.iMarcoDeMemoria);
											break;
										case 1:	// Error. El proceso es m�s grande que la memoria 
											System.out.println("No fue posible cargar el proceso. El proceso que se intenta cargar es mas grande que la memoria.");
											break;
										case 2:	// Error. El proceso ya est� cargado
											System.out.println("No fue posible cargar el proceso. Ya hay un proceso con el mismo ID.");
											break;
										case 3:	// Error. Ya no hay espacio suficiente para cargar el programa (Swap llena)
											System.out.println("No fue posible cargar el proceso. No queda memoria suficiente en el sistema para cargar mas procesos.");
											break;
										default:
											System.out.println("Error inesperado al cargar el proceso.");
									}
									
								}
								else if ( insActual.sTipo.equals("L") ) {	// Liberar proceso de memoria
									ManejadorDeMemoria.MarcosLiberados cajaTemporal = mdmSistemaOperativo.liberarProceso(insActual.iValor1);
									
									if (cajaTemporal.bEstabaCargado) {	// Asegurarse que el proceso estaba cargado en memoria
										hsmTurnarounds.put(insActual.iValor1, System.currentTimeMillis() - hsmTurnarounds.get(insActual.iValor1));	// Calcular turnaround time
										
										// Mostrar resultados de librerar Memoria
										System.out.print("Se liberan los marcos de p�gina de memoria real: ");
										for (Integer iVal: cajaTemporal.vecMemoria) {
											System.out.print(iVal + ", ");
										}
										System.out.println();
										
										// Mostrar resultados de liberar Swap
										System.out.print("Se liberan las posiciones del �rea de swapping: ");
										for (Integer iVal: cajaTemporal.vecSwap) {
											System.out.print(iVal + ", ");
										}
										System.out.println();
									}
									else {	// Error. El proceso no exist�a en memoria.
										System.out.println("El proceso que se intent� liberar no se encontraba en memoria.");
									}
								}
								
								iContInstruccion++;
								insActual = arcEntrada.vecInstrucciones.get(iContInstruccion);
							}
							
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
							System.out.println( insActual.getInstruccion() );	// Mostrar instrucci�n F
							System.out.println("Fin.");
							for (Integer iIDProceso: veciIDProcesosALiberar) {	// Turnaround por proceso
								System.out.println( 
										"Turnaround del proceso " + iIDProceso
										+ ": " + hsmTurnarounds.get(iIDProceso) );
							}
							System.out.println("Turnaround promedio: " + dTurnaroundPromedio);
							//TURNAROUND POR PROCESO, TURNAROUND PROMEDIO, CANT PAGE FAULTS, CANT SWAP IN, CANT SWAP OUT
						}
						
						// Mostrar salidas finales
						System.out.println(insActual.getInstruccion());	// Mostrar la instrucci�n se espera sea E
						
						if (iContInstruccion < arcEntrada.vecInstrucciones.size()) { // Se termin� el programa con un E y quedaban instrucciones despu�s 
							System.out.println("Fin del programa. Se detectaron instrucciones despu�s de E, pero no se ejecutaron.");
						}
						else if (insActual.sTipo.equals("E")) {
							System.out.println("Fin del programa.");
						}
						else {	// El programa termin� de leer las instrucciones, pero no hab�a E
							System.out.println("Fin del programa. No se encontr� la instrucci�n E para terminar su programa.");
						}
						System.out.println( insActual.getInstruccion() );	// Mostrar instrucci�n E
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
							System.out.println("Ocurri� un error inesperado al intentar cerrar el documento.");	
						}
					}
				}
			}
			else {	//%a//Denegar archivos que no sean .txt
				System.out.println("El archivo debe ser un .txt para poder utilizarlo.\n");	//%a
			}
			
			System.out.print("Desea correr el programa nuevamente?(s/n) ");
			sRespuesta = scaEntrada.nextLine();
		} while (sRespuesta.contains("s") || sRespuesta.contains("S"));
																										////58
	}

}
