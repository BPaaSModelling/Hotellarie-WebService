package ch.fhnw.bpaas.model.hotel;

import java.util.ArrayList;

public class HotelModel {
private String URI;
private String label;
private ArrayList<HotelElementModel> properties;
public String getURI() {
	return URI;
}
public void setURI(String URI) {
	this.URI = URI;
}
public String getLabel() {
	return label;
}
public void setLabel(String label) {
	this.label = label;
}
public ArrayList<HotelElementModel> getProperties() {
	return properties;
}
public void setProperties(ArrayList<HotelElementModel> properties) {
	this.properties = properties;
}
public HotelModel() {
	URI = "";
	label = "";
	properties = new ArrayList<HotelElementModel>();
}

}
