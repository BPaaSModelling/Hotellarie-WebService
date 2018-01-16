package ch.fhnw.bpaas.model.entropy;

import java.util.ArrayList;

public class EntropyHotelAttribute {
	
private String id;
private ArrayList<String> values;
private String domain;

public EntropyHotelAttribute() {
	this.values = new ArrayList<String>();
}

public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public ArrayList<String> getValues() {
	return values;
}
public void setValues(ArrayList<String> values) {
	this.values = values;
}
public String getDomain() {
	return domain;
}
public void setDomain(String domain) {
	this.domain = domain;
}

@Override
public String toString() {
	return "[id=" + id + ", values= "+ values + ", domain=" + domain + "]"+"\n";
}


}
