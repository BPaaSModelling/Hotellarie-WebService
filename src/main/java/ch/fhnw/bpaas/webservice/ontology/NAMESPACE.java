package ch.fhnw.bpaas.webservice.ontology;

public enum NAMESPACE {
	RDFS(		"rdfs",							"http://www.w3.org/2000/01/rdf-schema#"),
	RDF(		"rdf", 							"http://www.w3.org/1999/02/22-rdf-syntax-ns#"),
	OWL(		"owl", 							"http://www.w3.org/2002/07/owl#"),
	ARCHI(		"archi", 						"http://ikm-group.ch/archiMEO/archimate#"),
	APQC(		ONTOLOGY.APQC.getPrefix(),		"http://ikm-group.ch/archimeo/apqc#"),
	MEDIATYPES(	"media-types", 					"http://www.iana.org/assignments/media-types#"),
	BPMN(		ONTOLOGY.BPMN.getPrefix(),		"http://ikm-group.ch/archiMEO/BPMN#"),
	HTL(		ONTOLOGY.HTL.getPrefix(),		"http://ikm-group.ch/archimeo/htl#"),
	HTLDATA(		ONTOLOGY.HTLDATA.getPrefix(),		"http://ikm-group.ch/archiMEO/htldata#"),
	EO(			ONTOLOGY.EO.getPrefix(), 		"http://ikm-group.ch/archiMEO/eo#"),
	FBPDO(		ONTOLOGY.FBPDO.getPrefix(),		"http://ikm-group.ch/archimeo/fbpdo#"),
	TOP(		ONTOLOGY.TOP.getPrefix(),		"http://ikm-group.ch/archiMEO/top#"),
	NCO(		ONTOLOGY.NCO.getPrefix(),		"http://ikm-group.ch/archiMEO/NCO#"),
	BMM(		ONTOLOGY.BMM.getPrefix(),		"http://ikm-group.ch/archiMEO/BMM#"),
	XSD(		"xsd",							"http://www.w3.org/2001/XMLSchema#"),
	questionnaire (ONTOLOGY.questionnaire.getPrefix(), "http://ikm-group.ch/archiMEO/questionnaire#"),
	questiondata (ONTOLOGY.questiondata.getPrefix(), "http://ikm-group.ch/archiMEO/questiondata#");
	
	private String prefix;
	private String url;
	
	NAMESPACE(String prefix, String url) {
		this.prefix = prefix;
		this.url = url;
	}

	public String getPrefix() {
		return prefix;
	}
	
	public String getURI() {
		return url;
	}
}