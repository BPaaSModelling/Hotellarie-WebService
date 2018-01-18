package ch.fhnw.bpaas.webservice.ontology;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.jena.graph.Triple;
import org.apache.jena.query.DatasetAccessor;
import org.apache.jena.query.DatasetAccessorFactory;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateProcessor;
import org.apache.jena.update.UpdateRequest;

import ch.fhnw.bpaas.webservice.persistence.GlobalVariables;
import ch.fhnw.bpaas.webservice.persistence.RuleParser;

public final class OntologyManager {

	private static OntologyManager INSTANCE;
	private Model rdfModel;
	
	private static String TRIPLESTOREENDPOINT 	= "http://localhost:3030/questionnaire";
	private static String UPDATEENDPOINT 		= TRIPLESTOREENDPOINT + "/update";
	private static String QUERYENDPOINT			= TRIPLESTOREENDPOINT + "/query";
	private static String READENDPOINT			= TRIPLESTOREENDPOINT + "/get";
	private static String DATAENDPOINT			= TRIPLESTOREENDPOINT + "/data";

	public static synchronized OntologyManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new OntologyManager();
		}
		return INSTANCE;
	}

	public OntologyManager() {
		
		rdfModel = ModelFactory.createDefaultModel();
		setNamespaces(rdfModel);
		//loadOntologyiesToModel();
		//applyReasoningRulesToMainModel(GlobalVariables.REASONING_RULESET);
	}

	private void applyReasoningRulesToMainModel(String ruleFile) {
		List<String> ruleSet = null;
		try {
			ruleSet = RuleParser.parseRules(this.getClass().getClassLoader().getResourceAsStream(ruleFile));
			for (String rule : ruleSet) {
					performConstructRule(new ParameterizedSparqlString(rule));	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public Model applyReasoningRulesToTempModel(Model tempModel, ParameterizedSparqlString constructQuery) {
		return performConstructRule(tempModel, constructQuery);	
	}

	public void setNamespaces(Model model) {
		for (NAMESPACE ns : NAMESPACE.values()) {
			model.setNsPrefix(ns.getPrefix(), ns.getURI());
		}
	}

	private void loadOntologyiesToModel() {
			rdfModel.read(READENDPOINT);
	}

	private void addNamespacesToQuery(ParameterizedSparqlString queryStr) {
		for (NAMESPACE ns : NAMESPACE.values()) {
			queryStr.setNsPrefix(ns.getPrefix(), ns.getURI());
		}
	}

	public Model performConstructRule(Model model, ParameterizedSparqlString query) {
	
		Model temp = ModelFactory.createOntologyModel();
		addNamespacesToQuery(query);
		System.out.println("### local performConstructRule: " + query.toString());
		QueryExecution qexec = QueryExecutionFactory.create(query.toString(), model);
		temp = qexec.execConstruct();
		model = model.union(temp);
		return model;
	}
	
	public void performConstructRule(ParameterizedSparqlString query) {
		
		addNamespacesToQuery(query);
		System.out.println("### online performConstructRule: " + query.toString());
		Query myQuery = QueryFactory.create(query.toString());
		QueryExecution qExec = QueryExecutionFactory.sparqlService(QUERYENDPOINT, myQuery);
		Model temp = ModelFactory.createOntologyModel();
		temp = qExec.execConstruct();
		DatasetAccessor accessor = DatasetAccessorFactory.createHTTP(DATAENDPOINT);
	    accessor.add(temp);
		qExec.close();

	}

	public void printModel(Model model, String fileName) {
		try {
			RDFDataMgr.write(new FileOutputStream(fileName), model, Lang.TURTLE);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		System.out.println("****************************************************");
	}

	public void printCurrentModel(String filename) {
		this.printModel(this.rdfModel, filename);
	}

	public QueryExecution query(ParameterizedSparqlString queryStr) {
		addNamespacesToQuery(queryStr);
		//System.out.println("***Performed query***\n" + queryStr.toString() + "***Performed query***\n");
		Query query = QueryFactory.create(queryStr.toString());
		QueryExecution qexec;
		qexec = QueryExecutionFactory.sparqlService(QUERYENDPOINT, query);
		
		return qexec;
	}
	
	public ResultSet query(Model model, ParameterizedSparqlString queryStr) {
		addNamespacesToQuery(queryStr);
		System.out.println(queryStr.toString());
		Query query = QueryFactory.create(queryStr.toString());
		QueryExecution qexec = QueryExecutionFactory.create(query, model);

		return qexec.execSelect();
	}

	public void insertQuery(Model model, ParameterizedSparqlString queryStr) {
		addNamespacesToQuery(queryStr);
		System.out.println(queryStr.toString());
		UpdateAction.parseExecute(queryStr.toString(), model);
		
	}

	public void insertQuery(ParameterizedSparqlString queryStr) {
		boolean online = false;
		if (online){
		addNamespacesToQuery(queryStr);
		UpdateRequest update = UpdateFactory.create(queryStr.toString());
		UpdateProcessor up = UpdateExecutionFactory.createRemote(update, UPDATEENDPOINT);
		up.execute();
		} else {
			addNamespacesToQuery(queryStr);
			Model temp1 = ModelFactory.createOntologyModel();
			Model temp2 = ModelFactory.createOntologyModel();
			temp1.read("htldata.ttl", "TTL");
			UpdateAction.parseExecute(queryStr.toString(), temp2);
			temp1.add(temp2);
			
			String fileName = "Hotel_Inserted.ttl";
			FileWriter out;
			try {
				out = new FileWriter( fileName );
				temp1.write(out, "TTL");
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}
	}

	public static String getREADENDPOINT() {
		return READENDPOINT;
	}

}
