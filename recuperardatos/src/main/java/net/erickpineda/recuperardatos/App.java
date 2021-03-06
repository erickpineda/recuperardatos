package net.erickpineda.recuperardatos;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * @author Erick Pineda. Programa en Java que permite conectarse a una URL,
 *         rellenar los datos de un login y descargar la imágen de perfil para
 *         cada usuario.
 */
public class App {
	/**
	 * Fichero de texto, que tendrá los usuarios y contraseñas.
	 */
	private static String nombreFichero = "/datos.txt";
	/**
	 * URL para la conexión.
	 */
	private static String pathURL = "http://projectes.iescendrassos.net/entrada/checklogin.php";
	/**
	 * Entrada del fichero que se leerá.
	 */
	private static InputStream ficheroALeer = null;
	/**
	 * Leerá la secuencia del fichero de texto @param nombreFichero.
	 */
	private static BufferedReader inFile = null;
	/**
	 * Leerá la secuencia de la URL.
	 */
	private static BufferedReader inURL = null;
	/**
	 * Usuario del login.
	 */
	private static String user = "";
	/**
	 * Contraseña del usuario del login.
	 */
	private static String pass = "";

	/**
	 * Método principal.
	 */
	public static void main(String[] args) {
		leerFichero();
	}

	/**
	 * Método que lee el fichero de texto. Línea a línea va identificando
	 * usuario y contraseña para después iniciar sesión.
	 */
	public static void leerFichero() {

		try {

			String linea; // Línea en fichero
			ficheroALeer = App.class.getResourceAsStream(nombreFichero);
			inFile = new BufferedReader(new InputStreamReader(ficheroALeer));

			if (inFile != null) {
				linea = inFile.readLine();

				while (linea != null) {
					String[] array = linea.split(":");

					switch (array.length) {
					case 2:
						user = array[0].trim();
						pass = array[1].trim();
						break;

					case 3:
						user = array[2].trim();
						pass = array[0].trim();
						break;

					default:
						break;
					}

					System.out.println(" -> Usuario: " + user + " -> Contraseña: "
							+ pass);

					recuperarDatos(user, pass);
					linea = inFile.readLine();
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();

		} catch (MalformedURLException e) {
			System.out.println(" -> La url: " + pathURL + " ¡no es válida!");
			e.printStackTrace();

		} catch (FileNotFoundException e) {
			System.out.println(" -> El fichero: " + nombreFichero
					+ " ¡no existe!");
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			if (ficheroALeer != null)
				try {
					ficheroALeer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * Método que pasa por parámetro dos String <i>( usuario y contraseña )</i>,
	 * crea una conexión vía URL para rellenar los datos del login.
	 * 
	 * @param myUser
	 *            Parámetro que será el nombre del usuario.
	 * @param myPass
	 *            Parámetro que será la contraseña.
	 * @throws IOException
	 *             Errores de entrada y salida.
	 */
	public static void recuperarDatos(String myUser, String myPass)
			throws IOException {

		// Crear una sesión vía coockies
		CookieHandler.setDefault(new CookieManager(null,
				CookiePolicy.ACCEPT_ALL));

		// Datos del Formulario
		String data = URLEncoder.encode("usuari", "UTF-8") + "="
				+ URLEncoder.encode(myUser, "UTF-8");

		data += "&" + URLEncoder.encode("contrasenya", "UTF-8") + "="
				+ URLEncoder.encode(myPass, "UTF-8");

		data += "&" + URLEncoder.encode("Entrar", "UTF-8") + "="
				+ URLEncoder.encode("Entrar", "UTF-8");

		// Login a conectar
		URL url = new URL(pathURL);

		// Establecer la conexión
		URLConnection conexion = url.openConnection();

		// Activar la conectividad
		conexion.setDoOutput(true);

		// Escritor de datos
		OutputStreamWriter osr = new OutputStreamWriter(
				conexion.getOutputStream());

		// Escribe los datos en el formulario
		osr.write(data);

		// Limpiar la conexión
		osr.flush();

		// Cerrar el escritor
		osr.close();

		// Conecta
		conexion.connect();

		// Lectura de la URL
		inURL = new BufferedReader(new InputStreamReader(
				conexion.getInputStream()));

		// Línea en URL
		String linea;

		if (inURL != null) {
			linea = inURL.readLine();

			while (linea != null) {

				if (linea.contains("<img src="))
					lecturaDeImagen(linea);

				linea = inURL.readLine();
			}
		}
	}

	/**
	 * Método que pasa por parámetro el String de la línea leída en la URL,
	 * separará la ubicación de la imágen y el nombre de la misma.
	 * 
	 * @param lineaLeida
	 *            String que será la línea en la URL.
	 */
	public static void lecturaDeImagen(String lineaLeida) {

		String[] urlImg = lineaLeida.split("'", 0); // '(.*?)'
		String[] imagen = urlImg[1].split("/");

		descargarImagen(urlImg[1], imagen[1]);
	}

	/**
	 * Una vez leída la imágen, éste método pasa por parámetro dos String que
	 * serán: la ubicación de la imágen y nombre de ésta misma. Abre una
	 * conexión URL hacia el link absoluto y procederá a descargar la imágen.
	 * 
	 * @param pathImg
	 *            Ubicación de la imágen.
	 * @param nameImg
	 *            Nombre de la imágen.
	 */
	public static void descargarImagen(String pathImg, String nameImg) {

		try {

			// Link absoluto de la imágen
			// Ej: http://projectes.iescendrassos.net/entrada/pilaf-net.png
			URL url = new URL(pathURL.replace("checklogin.php", "") + pathImg);

			InputStream in = new BufferedInputStream(url.openStream());
			OutputStream out = new BufferedOutputStream(new FileOutputStream(
					"src/main/resources/" + nameImg));

			// Lee cada byte de la imágen hasta el final de la misma
			for (byte i = 0; (i = (byte) in.read()) != -1;) {
				out.write(i);
			}

			in.close();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
