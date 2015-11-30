package trabajoFinal.cps;

import static java.lang.System.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
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
					
					// Leer y guardar información del archivo
					arcEntrada.setNombre(sRespuesta);
																												////14
//					double dAux;	//%m
//					if ((sRespuesta = bfrArchivoEntrada.readLine()) != null) {
//						dAux = Double.parseDouble(sRespuesta);	//%m
//					}
//					else {	// El dato no se encuentra en la primera línea
//						dAux = -2;	//%m
//					}
					
//					boolean bNumNegativos = ( dAux < 0 );	//%a
					boolean bNumNegativos = false;	//%a
					
					if (!bNumNegativos) {	//%m// Revisar que el Xk sea válido
						arcEntrada.dXk = 1;	//%m		///////////MODIFICAR
					
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
								ManejadorDeMemoria mdmSistemaOperativo = new mdmSistemaOperativo( 2048, 4096, 8 );
								
								
								// Continuar hasta que se indique fin de sección de instrucciones
								while ( !(insActual.sTipo.equals("F")) ) {
									if ( insActual.sTipo.equals("P") ) {	// Cargar proceso
										if ( mdmSistemaOperativo.cargarProceso( insActual.iValor1, insActual.iValor2 ) ) {	// Cargar y manejar errores de carga
											// SALIDA ESPERADA
										}
										else {	// Error. No se pudo cargar el proceso
											System.out.println("No fue posible cargar el proceso. Posiblemente ya estaba cargado a memoria.");
										}
									}
									else if ( insActual.sTipo.equals("A") ) {	// Accesar a proceso para leer o modificar
										if ( mdmSistemaOperativo.accesarProceso(insActual.iValor1, insActual.iValor2, insActual.iValor3) ) {	// Accesar y manejar errores
											// SALIDA ESPERADA
										}
										else {	// Error. No se pudo accesar al proceso
											System.out.println("No fue posible accesar al proceso. Tal vez haya faltado cargarlo a memoria.");
										}
									}
									//POR DESARROLLAR**************************
									else if ( insActual.sTipo.equals("L") ) {	// Liberar proceso de memoria
										
									}
								}
							}
						}
						else {	// Error. No hay instrucciones
							System.out.println("El archivo provisto no contiene instrucciones.");
						}
						
						if (!bNumNegativos) {	//%a// Evitar datos menores a 0
							if (arcEntrada.vecX.size() > 2) {	// Aceptar solo más de 2 parejas de datos
							////33
							// Mostrar resultados en pantalla
//							CalculosAumentados calValores = new CalculosAumentados(arcEntrada.vecX, arcEntrada.vecY, arcEntrada.dXk);	//%m
//							//%d=1
//								System.out.println("N\t= " + calValores.ObtenerN());
//								System.out.printf("xk\t= %.0f\n", calValores.ObtenerXk());
//								System.out.printf("r\t= %.5f\n", calValores.ObtenerR());
//								System.out.printf("r2\t= %.5f\n", calValores.ObtenerR2());
//								System.out.printf("b0\t= %.5f\n", calValores.ObtenerB0());
//								System.out.printf("b1\t= %.5f\n", calValores.ObtenerB1());
//								System.out.printf("yk\t= %.5f\n", calValores.ObtenerYk());
//								System.out.printf("sig\t= %.10f\n", calValores.obtenerSig());	//%a
//								System.out.printf("ran\t= %.5f\n", calValores.obtenerRan());	//%a
//								System.out.printf("LS\t= %.5f\n", calValores.obtenerLS());	//%a
//								System.out.printf("LI\t= %.5f\n", calValores.obtenerLI());	//%a
							}
							else {	// Si alguno de los datos no cumple las condiciones
								System.out.println("Para dar resultados acertados, es necesario que el archivo contenga al menos 3 pares de datos.\n");
							}
						}
						else {	//%a// Alguno de las parejas de datos contiene un número negativo
							System.out.println("El valor de todos los datos debe ser mayor o igual a 0.\n");	
						}
					}
					else {	// El dato Xk es menor a 0
						System.out.println("El valor de todos los datos debe ser mayor o igual a 0.\n");	
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
				System.out.println("El archivo debe ser un .txt para poder utilizarlo.\n");	//%a
			}
			
			System.out.print("Desea correr el programa nuevamente?(s/n) ");
			sRespuesta = scaEntrada.nextLine();
		} while (sRespuesta.contains("s") || sRespuesta.contains("S"));
																										////58
	}

}
