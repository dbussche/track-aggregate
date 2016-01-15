package nl.bikeprint.trackaggregate.shared;

import nl.bikeprint.trackaggregate.general.ColumnType;

public class Column {

	private String naam;
	private ColumnType columnType;
	
	public Column(String naam, ColumnType columnType) {
		this.setNaam(naam);
		this.setColumnType(columnType);
	}

	public String getNaam() {
		return naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	public ColumnType getColumnType() {
		return columnType;
	}

	public void setColumnType(ColumnType columnType) {
		this.columnType = columnType;
	}

}
