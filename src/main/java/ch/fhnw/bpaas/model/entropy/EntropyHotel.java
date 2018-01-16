package ch.fhnw.bpaas.model.entropy;

import java.util.ArrayList;

public class EntropyHotel {
	
private String id;
private String label;
private ArrayList<EntropyHotelAttribute> attributes;

public EntropyHotel() {
	this.attributes = new ArrayList<EntropyHotelAttribute>();
}

public String getId() {
	return id;
}

public void setId(String id) {
	this.id = id;
}

public String getLabel() {
	return label;
}

public void setLabel(String label) {
	this.label = label;
}

public ArrayList<EntropyHotelAttribute> getAttributes() {
	return attributes;
}

public void setAttributes(ArrayList<EntropyHotelAttribute> attributes) {
	this.attributes = attributes;
}

@Override
public String toString() {
	return "\nCS[id=" + id + ", label=" + label + ", attributes="+"\n" + attributes + "]";
}


}
