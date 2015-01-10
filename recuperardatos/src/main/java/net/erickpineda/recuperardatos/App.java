package net.erickpineda.recuperardatos;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
 * @author Erick Pineda
 *
 */
public class App {

	private static String nombreFichero = "/datos.txt";
	private static InputStream ficheroALeer = null;
	private static BufferedReader inFile = null;
	private static BufferedReader inURL = null;
	private static String user = "";
	private static String pass = "";

	public static void main(String[] args) {
		leerFichero();
	}

	public static void leerFichero() {

		try {

			// Línea en fichero
			String linea;
			ficheroALeer = App.class.getResourceAsStream(nombreFichero);
			inFile = new BufferedReader(new InputStreamReader(ficheroALeer));

			if (inFile != null) {
				linea = inFile.readLine();

				while (linea != null) {
					String[] array = linea.split(":");

					switch (array.length) {
					case 2:
						user = array[0].replace(" ", "");
						pass = array[1].replace(" ", "");
						break;

					case 3:
						user = array[2].replace(" ", "");
						pass = array[0].replace(" ", "");
						break;
					default:
						break;
					}
					System.out.println(user + " " + pass);
					recuperarDatos(user, pass);
					linea = inFile.readLine();
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
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

	public static void recuperarDatos(String myUser, String myPass)
			throws IOException {

		CookieHandler.setDefault(new CookieManager(null,
				CookiePolicy.ACCEPT_ALL));

		String data = URLEncoder.encode("usuari", "UTF-8") + "="
				+ URLEncoder.encode("sala", "UTF-8");
		data += "&" + URLEncoder.encode("contrasenya", "UTF-8") + "="
				+ URLEncoder.encode("5057", "UTF-8");
		data += "&" + URLEncoder.encode("Entrar", "UTF-8") + "="
				+ URLEncoder.encode("Entrar", "UTF-8");

		URL url = new URL(
				"http://projectes.iescendrassos.net/entrada/checklogin.php");

		URLConnection conexion = url.openConnection();

		conexion.setDoOutput(true);

		OutputStreamWriter osr = new OutputStreamWriter(
				conexion.getOutputStream());
		osr.write(data);
		osr.flush();
		osr.close();
		conexion.connect();

		inURL = new BufferedReader(new InputStreamReader(
				conexion.getInputStream()));
		String linea;

		if (inURL != null) {
			linea = inURL.readLine();

			while (linea != null) {
				System.out.println(linea);
				linea = inURL.readLine();
			}
		}

	}

}
