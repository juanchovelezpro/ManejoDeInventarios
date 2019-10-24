package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import comparadores.OrdenarItemsPorVolumenPorcentaje;

public class Inventario {

	/**
	 * Lista de items.
	 */
	private ArrayList<Item> items;

	/**
	 * Archivo de excel.
	 */
	private XSSFWorkbook workbook;

	public Inventario() {

		items = new ArrayList<>();
		workbook = null;

	}

	public ArrayList<Item> getItems() {
		return items;
	}

	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}

	public XSSFWorkbook getWorkbook() {
		return workbook;
	}

	public void setWorkbook(XSSFWorkbook workbook) {
		this.workbook = workbook;
	}

	public ArrayList<Item> itemsOrganizadosPorVolumenPorcentaje() {

		ArrayList<Item> organizados = items;

		Collections.sort(organizados, new OrdenarItemsPorVolumenPorcentaje());

		return organizados;

	}

	public void calcularPorcentajesDeVolumenes() {

		double volumenTotal = 0.0;

		// Sumatoria de los volumenes de todos los items.
		for (int i = 0; i < items.size(); i++) {

			volumenTotal += items.get(i).volumen();

		}

		// Porcentajes de los volumenes de cada item. (Volumen de cada item dividido por
		// la sumatoria de los volumenes de todos los items).
		for (int i = 0; i < items.size(); i++) {

			double porcentaje = (items.get(i).volumen() / volumenTotal) * 100;

			items.get(i).setVolumenPorcentaje(porcentaje);

		}

	}

	public ArrayList<Double> asignarClasesItems() {
		
		ArrayList<Item> itemsOrganizados = itemsOrganizadosPorVolumenPorcentaje();
		
		ArrayList<Double> volumenesAcumulados = new ArrayList<>();
		
		volumenesAcumulados.add(itemsOrganizados.get(0).getVolumenPorcentaje());
		
		for(int i = 1; i<itemsOrganizados.size();i++) {
			
			volumenesAcumulados.add(volumenesAcumulados.get(i-1) + itemsOrganizados.get(i).getVolumenPorcentaje());
			
		}
		
		
		return volumenesAcumulados;
	}
	
	// Lee la primera hoja del excel para calcular CVD y sacar las graficas de
	// cantidad de item por periodo.

	public void obtenerItems() {

		XSSFSheet sheet = workbook.getSheetAt(0);

		Iterator<Row> rows = sheet.iterator();

		int x = 0;

		// Para empezar a obtener los datos desde la fila 3.
		while (x < 3) {

			rows.next();
			x++;

		}

		while (rows.hasNext()) {

			Row row = rows.next();

			Iterator<Cell> cells = row.cellIterator();

			int contadorCeldas = 0;
			Item item = new Item();

			while (cells.hasNext()) {

				Cell cell = cells.next();

				switch (contadorCeldas) {

				case 0:
					item.setCodigo((int) Double.parseDouble(cell.toString()));
					break;

				case 1:
					item.setDescripcion(cell.toString());
					break;

				default:
					item.getCantidades().add(Double.parseDouble(cell.toString()));
					break;

				}

				contadorCeldas++;

			}

			items.add(item);

		}

	}

	// Busca el item por el codigo.
	public Item buscarItem(int codigo) {

		Item item = null;

		for (int i = 0; i < items.size(); i++) {

			if (items.get(i).getCodigo() == codigo) {

				item = items.get(i);

			}

		}

		return item;

	}

	// Lee la segunda hoja del archivo excel para clasificar los items.
	public void itemsParaClasificacion() {

		XSSFSheet sheet = workbook.getSheetAt(1);

		Iterator<Row> rows = sheet.iterator();

		rows.next();

		while (rows.hasNext()) {

			Row row = rows.next();

			Iterator<Cell> cells = row.cellIterator();
			Item item = null;
			int contadorCeldas = 0;

			while (cells.hasNext()) {

				Cell cell = cells.next();

				switch (contadorCeldas) {

				case 0:

					item = buscarItem((int) Double.parseDouble(cell.toString()));

					break;

				case 1:

					item.getSalidasDeInventario().add((int) Double.parseDouble(cell.toString()));

					break;

				case 2:

					item.getCostosUnitarios().add(Double.parseDouble(cell.toString()));

					break;

				}

				contadorCeldas++;

			}

		}

	}

}
