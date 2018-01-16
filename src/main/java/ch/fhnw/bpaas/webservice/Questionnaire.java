package ch.fhnw.bpaas.webservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import ch.fhnw.bpaas.model.entropy.EntropyHotel;
import ch.fhnw.bpaas.model.entropy.EntropyHotelAttribute;
import ch.fhnw.bpaas.model.hotel.HotelElementModel;
import ch.fhnw.bpaas.model.hotel.HotelModel;
import ch.fhnw.bpaas.model.questionnaire.Answer;
import ch.fhnw.bpaas.model.questionnaire.QuestionnaireItem;
import ch.fhnw.bpaas.model.questionnaire.QuestionnaireModel;
import ch.fhnw.bpaas.webservice.exceptions.DomainSelectionException;
import ch.fhnw.bpaas.webservice.exceptions.MinimumEntropyReached;
import ch.fhnw.bpaas.webservice.exceptions.NoDomainQuestionLeftException;
import ch.fhnw.bpaas.webservice.exceptions.NoResultsException;
import ch.fhnw.bpaas.webservice.ontology.OntologyManager;
import ch.fhnw.bpaas.webservice.persistence.EntropyCalculation;
import ch.fhnw.bpaas.webservice.persistence.GlobalVariables;




@Path("/questionnaire")
public class Questionnaire {

	private Gson gson = new Gson();
	private OntologyManager ontology = OntologyManager.getInstance();
	private boolean debug_properties = false;
	private String hotword_rule = "?value";


	@GET
	@Path("/getDomains")
	public Response getDomains() {
		
		ArrayList<Answer> result = new ArrayList<Answer>();

		try {
			result = queryDomains();
			if (debug_properties){
				for (int index = 0; index < result.size(); index++){
					System.out.println("Element "+index+": ");
					System.out.println("Label -->" + result.get(index).getAnswerLabel());
					System.out.println("ID Name -->" + result.get(index).getAnswerID());
					System.out.println("");
				}
			}
		} catch (NoResultsException e) {
			e.printStackTrace();
		}

		String json = gson.toJson(result);
		//System.out.println("\n####################<start>####################");
		//System.out.println("/search genereated json: " +json);
		//System.out.println("####################<end>####################");

		System.out.println("\n----------------------------      Domain List generated      -----------------------------------" );

		return Response.status(Status.OK).entity(json).build();
	}

	private ArrayList<Answer> queryDomains() throws NoResultsException{
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();
		ArrayList<Answer> allDomains = new ArrayList<Answer>();

		queryStr.append("SELECT ?domain ?label ?order WHERE {");
		queryStr.append("?domain rdfs:subClassOf questionnaire:Question .");
		queryStr.append("?domain rdfs:label ?label .");
		queryStr.append("OPTIONAL { ?domain questionnaire:hasOrderNumberForVisualization ?order  } .");
		queryStr.append("}");
		queryStr.append("ORDER BY DESC(?order)  ?label");

		QueryExecution qexec = ontology.query(queryStr);
		ResultSet results = qexec.execSelect();

		if (results.hasNext()) {
			while (results.hasNext()) {
				Answer domain = new Answer();

				QuerySolution soln = results.next();
				domain.setAnswerID(soln.get("?domain").toString());
				domain.setAnswerLabel(soln.get("?label").toString());

				allDomains.add(domain);
			}
		} else {
			throw new NoResultsException("nore more results");
		}
		qexec.close();
		return allDomains;
	}

	//	@GET
	//	@Path("/getQuestionsFromDomains")
	//	public Response getQuestionsFromDomains(@QueryParam("domains") String domains_received) {
	//		Gson gson = new Gson(); 
	//		Answer[] domains = gson.fromJson(domains_received, Answer[].class);
	//		System.out.println("\n####################<start>####################");
	//		System.out.println("/requested questions from domains" );
	//		System.out.println("/received " + domains_received + " domains" );
	//		System.out.println("/received " + domains.length + " domains" );
	//		System.out.println("####################<end>####################");
	//		ArrayList<QuestionnaireItem> result = new ArrayList<QuestionnaireItem>();
	//		
	//		try {
	//				result = queryQuestionsFromDomains(domains);
	//				if (debug_properties){
	//				for (int index = 0; index < result.size(); index++){
	//				System.out.println("Element "+index+": ");
	//				System.out.println("QuestionLabel -->" + result.get(index).getQuestionLabel());
	//				System.out.println("QuestionURI -->" + result.get(index).getQuestionURI());
	//				System.out.println("QuestionURI -->" + result.get(index).getAnswerType());
	//				System.out.println("");
	//				}
	//				}
	//		} catch (NoResultsException e) {
	//			e.printStackTrace();
	//		}
	//		
	//		String json = gson.toJson(result);
	//		System.out.println("\n####################<start>####################");
	//		System.out.println("/search genereated json: " +json);
	//		System.out.println("####################<end>####################");
	//		return Response.status(Status.OK).entity(json).build();
	//	}
	//
	//	private ArrayList<QuestionnaireItem> queryQuestionsFromDomains(Answer[] domain_received) throws NoResultsException{
	//		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();
	//		ArrayList<QuestionnaireItem> allQuestions = new ArrayList<QuestionnaireItem>();
	//		
	//		
	//			
	//			queryStr.append("SELECT ?question ?label ?qType ?relation ?datatype ?searchnamespace ?searchType ?dTypeLabel ?rule WHERE {");
	//			queryStr.append("?question rdfs:label ?label .");
	//			queryStr.append("?question rdf:type ?qType . ");
	//			queryStr.append("?qType rdfs:subClassOf* questionnaire:AnswerType .");
	//			queryStr.append("?question rdf:type ?dType .");
	//			queryStr.append("?dType rdfs:label ?dTypeLabel .");
	//			queryStr.append("?dType rdfs:subClassOf questionnaire:Question . ");
	//			queryStr.append("?question questionnaire:questionHasAnnotationRelation ?relation . ");
	//			queryStr.append("OPTIONAL {?question questionnaire:valueInsertAnswerTypeHasDatatype ?datatype .}");
	//			queryStr.append("OPTIONAL {?question questionnaire:searchSelectionHasSearchNamespace ?searchnamespace .}");
	//			queryStr.append("OPTIONAL {?question questionnaire:searchSelectionOnClassesInsteadOfInstances ?searchType .}");
	//			queryStr.append("OPTIONAL {?dType questionnaire:hasOrderNumberForVisualization ?orderD}");
	//			queryStr.append("OPTIONAL {?question questionnaire:hasOrderNumberForVisualization ?orderQ}");
	//			queryStr.append("OPTIONAL {?question questionnaire:questionHasRuleToApply ?rule}");
	//			String first_part = "FILTER (";
	//			String middle_part = "";
	//			String last_part = ")";
	//			for (int i = 0; i < domain_received.length; i++){
	//				if (middle_part != ""){
	//					middle_part =  middle_part + " || ";
	//				}
	//				middle_part = middle_part + "?dType = <" + domain_received[i].getAnswerID()+">";
	//			}
	//			queryStr.append(first_part + middle_part + last_part);
	//			queryStr.append("}");
	//			queryStr.append("ORDER BY DESC(?orderD) DESC(?orderQ)");
	//		
	//		QueryExecution qexec = ontology.query(queryStr);
	//		ResultSet results = qexec.execSelect();
	//		
	//		if (results.hasNext()) {
	//			while (results.hasNext()) {
	//				QuestionnaireItem question = new QuestionnaireItem();
	//				
	//				QuerySolution soln = results.next();
	//				question.setQuestionURI(soln.get("?question").toString());
	//				question.setQuestionLabel(soln.get("?label").toString());
	//				question.setAnswerType(soln.get("?qType").toString());
	//				question.setDomainLabel(soln.get("?dTypeLabel").toString());
	//				question.setAnnotationRelation(soln.get("?relation").toString());
	//				
	//				if (soln.get("?rule") != null ){
	//					question.setRuleToApply(soln.get("?rule").toString());
	//					}
	//				
	//				if (soln.get("?qType").toString().equals(GlobalVariables.ANSWERTYPE_SINGLE_SELECTION) || 
	//						soln.get("?qType").toString().equals(GlobalVariables.ANSWERTYPE_MULTI_SELECTION)){
	//					//Call for the answers
	//					question.setAnswerList(new ArrayList<Answer>(getAnswerList(soln.get("?question").toString())));
	//				}else if (soln.get("?qType").toString().equals(GlobalVariables.ANSWERTYPE_SEARCH_SELECTION)){
	//					//Call for the namespace
	//					question.setSearchNamespace(soln.get("?searchnamespace").toString());
	//					//System.out.println("=====================SEARCHTYPE: " + soln.get("?searchType"));
	//					//Call for the searchType (Classes of Instances)
	//					if (soln.get("?searchType").toString() == null ||
	//						soln.get("?searchType").toString().equals("") ||
	//						soln.get("?searchType").toString().equals(GlobalVariables.BOOLEAN_FALSE_URI)){
	//						question.setSearchOnClassesInsteadOfInstances(false);
	//					} else {
	//						question.setSearchOnClassesInsteadOfInstances(true);
	//					}	
	//				}else if (soln.get("?qType").toString().equals(GlobalVariables.ANSWERTYPE_VALUEINSERT)){
	//					//Call for the Datatype
	//					question.setAnswerDatatype(soln.get("?datatype").toString());
	//					question.setComparisonOperationAnswers(getComparisonOperations());
	//				}
	//				
	//				allQuestions.add(question);
	//			}
	//		} else {
	//			throw new NoResultsException("nore more results");
	//		}
	//		qexec.close();
	//		return allQuestions;
	//	}

	@GET
	@Path("/getSuitableHotels")
	public Response getSuitableHotels(@QueryParam("questionList") String questionList_received) {
		//System.out.println("ecco quante sono le domande: " + questionList_received);
		//System.out.println("\n####################<start> getSuitableHotels ####################");
		//System.out.println("/requested suitable hotels " );
		//		ArrayList<QuestionnaireItem> questionList = new ArrayList<QuestionnaireItem>();
		//		for (int i = 0; i < questionList_received.size(); i++){
		//			Gson gson = new Gson(); 
		//			questionList.add(gson.fromJson(questionList_received.get(i), QuestionnaireItem.class));
		//		}

		//QuestionnaireItem[] questionList = gson.fromJson(questionList_received, QuestionnaireItem[].class);
		Gson gson = new Gson(); 
		//QuestionnaireItem[] questionList = gson.fromJson(questionList_received, QuestionnaireItem[].class);
		QuestionnaireItem[] questionList = gson.fromJson(questionList_received, QuestionnaireItem[].class);
		//System.out.println("/received " + questionList_received );
		//System.out.println("/received " + questionList.length + " questions" );
		//System.out.println("####################<end> getSuitableHotels ####################");
		ArrayList<Answer> result = new ArrayList<Answer>();

		//System.out.println("Inside Response get Suitable Hotels , QuestionList received \n" +questionList.toString());
		try {

			result = querySuitableHotels(questionList);
			//System.out.println("Inside Response get Suitable Hotels , result \n" +result.toString());
			if (debug_properties){
				for (int index = 0; index < result.size(); index++){
					System.out.println("Htl number "+index+": ");
					System.out.println("HotelID -->" + result.get(index).getAnswerID());
					System.out.println("HotelLabel -->" + result.get(index).getAnswerLabel());
					System.out.println("");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String json = gson.toJson(result);
		//System.out.println("\n####################<start>####################");
		//System.out.println("/search genereated json: " +json);
		//System.out.println("####################<end>####################");
		return Response.status(Status.OK).entity(json).build();
	}

	private ArrayList<Answer> querySuitableHotels(QuestionnaireItem[] questions) throws NoResultsException{
		//System.out.println("----------------------------                                                                   Matching hotel list creation                 ----------------------------");

		ArrayList<Answer> hotels = new ArrayList<Answer>();
		//this method takes in input all the questions, it has to:
		//1. define which questions were answered

		ArrayList<QuestionnaireItem> answeredQuestion = new ArrayList<QuestionnaireItem>();
		for (int i = questions.length-1; i>=0 ; i--){

			if (questions[i].getGivenAnswerList().size() > 0){
				answeredQuestion.add(questions[i]);
				//System.out.println("=== ANSERED QUESTIONS n. "+ i+ " " +questions[i].getQuestionLabel() +"===");
			}
		}

		//2. apply rules to those answers and query the triplestore to find the hotels suitable
		//BEWARE: "?value" will be replace with the answerID of the answer!!
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();

		ArrayList<String> rules = new ArrayList<String>();
		ArrayList<String> filters = new ArrayList<String>();

		queryStr.append("SELECT ?hotel ?htlLabel WHERE {");
		queryStr.append("?hotel rdf:type bpaas:Hotel .");
		queryStr.append("?hotel rdfs:label ?htlLabel .");

		for (int i = 0; i < answeredQuestion.size(); i++){
			//System.out.println("SWITCH CASE = "+answeredQuestion.get(i).getAnswerType());
			switch (answeredQuestion.get(i).getAnswerType()){
			case GlobalVariables.ANSWERTYPE_MULTI_SELECTION:

				if (answeredQuestion.get(i).getRuleToApply()!=null){
					for (int j = 0; j < answeredQuestion.get(i).getGivenAnswerList().size(); j++){
						String id = UUID.randomUUID().toString();
						//rules.add("BIND (" + answeredQuestion.get(i).getRuleToApply().replaceAll(hotword_rule, answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID()) + ") AS " + id + ") .");
						queryStr.append("BIND (" + answeredQuestion.get(i).getRuleToApply().replaceAll(hotword_rule, answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID()) + ") AS " + id + ") .");

						//System.out.println("inside multi search answeredQuestion.get(i).getAnnotationRelation()"+answeredQuestion.get(i).getAnnotationRelation());
						queryStr.append("?hotel <" + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()) + "> " + id + " .");
						//System.out.println("inside multi search, almost done");
					}
				} else {
					try {
						//System.out.println("------------------------------------------------------------");
						//System.out.println("                 searchAnnotationRelation    " + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()));
						queryStr.append("?hotel <" + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()) + "> " + formatURIForQueries(answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID()) + " .");
					} catch (NoResultsException e) {
						e.printStackTrace();
					}
				}	
				//System.out.println("inside ANSWERTYPE_MULTI_SELECTION for "+ answeredQuestion.get(i).getQuestionURI()+"i-->"+i);
				break;
			case GlobalVariables.ANSWERTYPE_VALUEINSERT:

				if (answeredQuestion.get(i).getRuleToApply()!=null){
					String id = "?"+UUID.randomUUID().toString();
					id=id.replace("-", "");
					String id2 = "?"+UUID.randomUUID().toString();
					id2=id2.replace("-", "");

					try {
						//System.out.println("------------------------------------------------------------");
						//System.out.println("                 searchAnnotationRelation    " + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()));
						//queryStr.append("?hotel <" + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()) + "> " + formatURIForQueries(answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID()) + " .");
						queryStr.append("?hotel <" + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()) + "> ?value .");

					} catch (NoResultsException e) {
						e.printStackTrace();
					}
					//TODO: @STEFANO?
					//rules.add("BIND (" + answeredQuestion.get(i).getRuleToApply().replaceAll(hotword_rule, answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID()) + ") AS " + id2 + ") .");
					queryStr.append("BIND (" + answeredQuestion.get(i).getRuleToApply().replaceAll(hotword_rule, answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID()) + ") AS " + id2 + ") .");
					//filters.add("FILTER( " + id + " " + GlobalVariables.getComparisonOperatorString(answeredQuestion.get(i).getComparisonAnswer()) + id2 + " ) .");
					queryStr.append("FILTER( ?value " + GlobalVariables.getComparisonOperatorString(answeredQuestion.get(i).getComparisonAnswer()) + answeredQuestion.get(i).getGivenAnswerList().get(0)+ answeredQuestion.get(i).getAnswerDatatype() );
					System.out.println("answeredQuestion.get(i).getAnswerDatatype()"+answeredQuestion.get(i).getAnswerDatatype());


				} else {
					//System.out.println("inside ELSE for "+ answeredQuestion.get(i).getQuestionURI());
					//String id = "?"+UUID.randomUUID().toString();
					try {
						//System.out.println("------------------------------------------------------------");
						//System.out.println("                 searchAnnotationRelation    " + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()));
						queryStr.append("?hotel <" + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()) + "> ?value .");
					} catch (NoResultsException e) {
						e.printStackTrace();
					}

					//filters.add("FILTER( " + id + " " + GlobalVariables.getComparisonOperatorString(answeredQuestion.get(i).getComparisonAnswer()) + answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID() + ") .");
					queryStr.append("FILTER( ?value " + GlobalVariables.getComparisonOperatorString(answeredQuestion.get(i).getComparisonAnswer()) + answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerLabel()+")" );


				}
				//System.out.println("inside ANSWERTYPE_VALUEINSERT for "+ answeredQuestion.get(i).getQuestionURI()+"i-->"+i);
				break;

			case GlobalVariables.ANSWERTYPE_SEARCH_SELECTION:


				//System.out.println("answeredQuestion.get(i).getRuleToApply()="+answeredQuestion.get(i).getRuleToApply());
				if (answeredQuestion.get(i).getRuleToApply()!=null) {
					String id = UUID.randomUUID().toString();
					//rules.add("BIND (" + answeredQuestion.get(i).getRuleToApply().replaceAll(hotword_rule, answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID()) + ") AS " + id + ") .");
					queryStr.append("?hotel <" + answeredQuestion.get(i).getAnnotationRelation() + "> " + id + " .");
					System.out.println("BIND");
					queryStr.append("BIND (" + answeredQuestion.get(i).getRuleToApply().replaceAll(hotword_rule, answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID()) + ") AS " + id + ") .");


				} else {
					//System.out.println("No rule to apply");
					//System.out.println("answeredQuestion.get(i)"+"i="+i+" "+answeredQuestion.get(i).toString());
					//System.out.println("Get annotation relation" + answeredQuestion.get(i).getAnnotationRelation());
					//System.out.println(formatURIForQueries(answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID()));
					try {
						//System.out.println("------------------------------------------------------------");
						//System.out.println("                 searchAnnotationRelation    " + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()));
						queryStr.append("?hotel <" + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()) + "> " + formatURIForQueries(answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID()) + " .");
					} catch (NoResultsException e) {
						e.printStackTrace();
					}
				}
				//System.out.println("inside ANSWERTYPE_SEARCH_SELECTION for "+ answeredQuestion.get(i).getQuestionURI()+"i-->"+i);
				break;




			default: //single

				if (answeredQuestion.get(i).getAnswerList().get(0).getAnswerID().equals("SKIP")) {
					break;
				}
				
				if (answeredQuestion.get(i).getRuleToApply()!=null) {
					String id = UUID.randomUUID().toString();
					//rules.add("BIND (" + answeredQuestion.get(i).getRuleToApply().replaceAll(hotword_rule, answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID()) + ") AS " + id + ") .");
					try {
						//System.out.println("------------------------------------------------------------");
						//System.out.println("                 searchAnnotationRelation    " + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()));
						queryStr.append("?hotel <" + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()) + "> " + formatURIForQueries(answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID()) + " .");
					} catch (NoResultsException e) {
						e.printStackTrace();
					}
					queryStr.append("BIND (" + answeredQuestion.get(i).getRuleToApply().replaceAll(hotword_rule, answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID()) + ") AS " + id + ") .");


				} else {
					try {
						//System.out.println("------------------------------------------------------------");
						//System.out.println("                 searchAnnotationRelation    " + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()));
						queryStr.append("?hotel <" + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()) + "> " + formatURIForQueries(answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID()) + " .");
					} catch (NoResultsException e) {
						e.printStackTrace();
					}
				}
				//System.out.println("inside default SWITCH CASE= "+answeredQuestion.get(i).getAnswerType()+"i-->"+i);
				break;
			}
		}
		queryStr.append("}");
		queryStr.append("ORDER BY ?htlLabel");
		//System.out.println("query executed for query suitable\n"+queryStr);
		QueryExecution qexec = ontology.query(queryStr);
		ResultSet results = qexec.execSelect();

		while (results.hasNext()) {
			QuerySolution soln = results.next();
			hotels.add(new Answer(soln.get("?hotel").toString(), soln.get("?htlLabel").toString()));
		}
		qexec.close();
		//System.out.println("\n"+ hotels.toString()+"\n");
		System.out.println("----------------------------                                                                   Matching hotel list creation                 ----------------------------");

		return hotels;
	}

	private ArrayList<EntropyHotel> querySuitableHotels(ArrayList<QuestionnaireItem> answeredQuestion) throws NoResultsException{
		//System.out.println("----------------------------              CREATING SUITABLE HOTEL LIST                 ----------------------------");

		ArrayList<EntropyHotel> hotels = new ArrayList<EntropyHotel>();
		//this method takes in input all the questions, it has to:
		//1. define which questions were answered

		//		ArrayList<QuestionnaireItem> answeredQuestion = new ArrayList<QuestionnaireItem>();
		//		for (int i = questions.length-1; i>=0 ; i--){
		//			
		//			if (questions[i].getGivenAnswerList().size() > 0){
		//				answeredQuestion.add(questions[i]);
		//				System.out.println("=== ANSERED QUESTIONS n. "+ i+ " " +questions[i].getQuestionLabel() +"===");
		//			}
		//		}

		//2. apply rules to those answers and query the triplestore to find the hotels suitable
		//BEWARE: "?value" will be replace with the answerID of the answer!!
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();
		ArrayList<String> rules = new ArrayList<String>();
		ArrayList<String> filters = new ArrayList<String>();

		queryStr.append("SELECT ?hotel ?htlLabel WHERE {");
		queryStr.append("?hotel rdf:type bpaas:Hotel .");
		queryStr.append("?hotel rdfs:label ?htlLabel .");

		for (int i = 0; i < answeredQuestion.size(); i++){
			//System.out.println("SWITCH CASE = "+answeredQuestion.get(i).getAnswerType());
			switch (answeredQuestion.get(i).getAnswerType()){
			case GlobalVariables.ANSWERTYPE_MULTI_SELECTION:

				if (answeredQuestion.get(i).getRuleToApply()!=null){
					for (int j = 0; j < answeredQuestion.get(i).getGivenAnswerList().size(); j++){
						String id = UUID.randomUUID().toString();
						//rules.add("BIND (" + answeredQuestion.get(i).getRuleToApply().replaceAll(hotword_rule, answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID()) + ") AS " + id + ") .");
						queryStr.append("BIND (" + answeredQuestion.get(i).getRuleToApply().replaceAll(hotword_rule, answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID()) + ") AS " + id + ") .");

						//System.out.println("inside multi search answeredQuestion.get(i).getAnnotationRelation()"+answeredQuestion.get(i).getAnnotationRelation());
						queryStr.append("?hotel <" + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()) + "> " + id + " .");
						//System.out.println("inside multi search, almost done");
					}
				} else {
					try {
						//System.out.println("------------------------------------------------------------");
						//System.out.println("                 searchAnnotationRelation    " + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()));
						queryStr.append("?hotel <" + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()) + "> " + formatURIForQueries(answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID()) + " .");
					} catch (NoResultsException e) {
						e.printStackTrace();
					}
				}	
				//System.out.println("inside ANSWERTYPE_MULTI_SELECTION for "+ answeredQuestion.get(i).getQuestionURI()+"i-->"+i);
				break;
			case GlobalVariables.ANSWERTYPE_VALUEINSERT:
				
				if (answeredQuestion.get(i).getRuleToApply()!=null){
					String id = "?"+UUID.randomUUID().toString();
					id=id.replace("-", "");
					String id2 = "?"+UUID.randomUUID().toString();
					id2=id2.replace("-", "");

					try {
						//System.out.println("------------------------------------------------------------");
						//System.out.println("                 searchAnnotationRelation    " + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()));
						//queryStr.append("?hotel <" + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()) + "> " + formatURIForQueries(answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID()) + " .");
						queryStr.append("?hotel <" + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()) + "> ?value .");
						
					} catch (NoResultsException e) {
						e.printStackTrace();
					}
					//TODO: @STEFANO?
					//rules.add("BIND (" + answeredQuestion.get(i).getRuleToApply().replaceAll(hotword_rule, answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID()) + ") AS " + id2 + ") .");
					queryStr.append("BIND (" + answeredQuestion.get(i).getRuleToApply().replaceAll(hotword_rule, answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID()) + ") AS " + id2 + ") .");
					//filters.add("FILTER( " + id + " " + GlobalVariables.getComparisonOperatorString(answeredQuestion.get(i).getComparisonAnswer()) + id2 + " ) .");
					queryStr.append("FILTER( ?value " + GlobalVariables.getComparisonOperatorString(answeredQuestion.get(i).getComparisonAnswer()) + answeredQuestion.get(i).getGivenAnswerList().get(0)+ answeredQuestion.get(i).getAnswerDatatype() );
					System.out.println("answeredQuestion.get(i).getAnswerDatatype()"+answeredQuestion.get(i).getAnswerDatatype());
					

				} else {
					//System.out.println("inside ELSE for "+ answeredQuestion.get(i).getQuestionURI());
					//String id = "?"+UUID.randomUUID().toString();
					try {
						//System.out.println("------------------------------------------------------------");
						//System.out.println("                 searchAnnotationRelation    " + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()));
						queryStr.append("?hotel <" + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()) + "> ?value .");
					} catch (NoResultsException e) {
						e.printStackTrace();
					}

					//filters.add("FILTER( " + id + " " + GlobalVariables.getComparisonOperatorString(answeredQuestion.get(i).getComparisonAnswer()) + answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID() + ") .");
					queryStr.append("FILTER( ?value " + GlobalVariables.getComparisonOperatorString(answeredQuestion.get(i).getComparisonAnswer()) + answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerLabel()+")" );
					
					
				}
				//System.out.println("inside ANSWERTYPE_VALUEINSERT for "+ answeredQuestion.get(i).getQuestionURI()+"i-->"+i);
				break;

			case GlobalVariables.ANSWERTYPE_SEARCH_SELECTION:

				//System.out.println("answeredQuestion.get(i).getRuleToApply()="+answeredQuestion.get(i).getRuleToApply());
				if (answeredQuestion.get(i).getRuleToApply()!=null) {
					String id = UUID.randomUUID().toString();
					//rules.add("BIND (" + answeredQuestion.get(i).getRuleToApply().replaceAll(hotword_rule, answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID()) + ") AS " + id + ") .");
					try {
						//System.out.println("------------------------------------------------------------");
						//System.out.println("                 searchAnnotationRelation    " + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()));
						queryStr.append("?hotel <" + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()) + "> " + formatURIForQueries(answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID()) + " .");
					} catch (NoResultsException e) {
						e.printStackTrace();
					}

					System.out.println("BIND");
					queryStr.append("BIND (" + answeredQuestion.get(i).getRuleToApply().replaceAll(hotword_rule, answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID()) + ") AS " + id + ") .");


				} else {
					//System.out.println("No rule to apply");
					//System.out.println("answeredQuestion.get(i)"+"i="+i+" "+answeredQuestion.get(i).toString());
					//System.out.println("Get annotation relation" + answeredQuestion.get(i).getAnnotationRelation());
					//System.out.println(formatURIForQueries(answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID()));
					try {
						//System.out.println("------------------------------------------------------------");
						//System.out.println("                 searchAnnotationRelation    " + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()));
						queryStr.append("?hotel <" + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()) + "> " + formatURIForQueries(answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID()) + " .");
					} catch (NoResultsException e) {
						e.printStackTrace();
					}
				}
				//System.out.println("inside ANSWERTYPE_SEARCH_SELECTION for "+ answeredQuestion.get(i).getQuestionURI()+"i-->"+i);
				break;
				
		
	

			default: //single

				if (answeredQuestion.get(i).getAnswerList().get(0).getAnswerID().equals("SKIP")) {
					break;
				}
				
				if (answeredQuestion.get(i).getRuleToApply()!=null) {
					String id = UUID.randomUUID().toString();
					//rules.add("BIND (" + answeredQuestion.get(i).getRuleToApply().replaceAll(hotword_rule, answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID()) + ") AS " + id + ") .");
					try {
						//System.out.println("------------------------------------------------------------");
						//System.out.println("                 searchAnnotationRelation    " + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()));
						queryStr.append("?hotel <" + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()) + "> " + formatURIForQueries(answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID()) + " .");
					} catch (NoResultsException e) {
						e.printStackTrace();
					}
					queryStr.append("BIND (" + answeredQuestion.get(i).getRuleToApply().replaceAll(hotword_rule, answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID()) + ") AS " + id + ") .");


				} else {
					try {
						//System.out.println("------------------------------------------------------------");
						//System.out.println("                 searchAnnotationRelation    " + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()));
						queryStr.append("?hotel <" + searchAnnotationRelation(answeredQuestion.get(i).getAnnotationRelation(),answeredQuestion.get(i).getQuestionURI()) + "> " + formatURIForQueries(answeredQuestion.get(i).getGivenAnswerList().get(0).getAnswerID()) + " .");
					} catch (NoResultsException e) {
						e.printStackTrace();
					}
				}
				//System.out.println("inside default SWITCH CASE= "+answeredQuestion.get(i).getAnswerType()+"i-->"+i);
				break;
			}
		}
		queryStr.append("}");
		queryStr.append("ORDER BY ?htlLabel");
		//System.out.println("Query execute to create the ArrayList of htl\n"+queryStr);
		QueryExecution qexec = ontology.query(queryStr);
		ResultSet results = qexec.execSelect();

		if(results.hasNext()) {
			while (results.hasNext()) {
				QuerySolution soln = results.next();
				EntropyHotel e= new EntropyHotel();
				e.setId(soln.get("?hotel").toString());
				e.setLabel(soln.get("?htlLabel").toString());

				hotels.add(e);
			}}else {
				System.out.println("no hotel matching");
				//throw new NoResultsException("nore more results");
			}
		qexec.close();
		//System.out.println("\nhtl suitable\n"+ hotels.toString()+"\n");
		//System.out.println("----------------------------       end of checking htl suitable               ----------------------------");

		return hotels;
	}


	private Set<Answer> getComparisonOperations() {
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();
		queryStr.append("SELECT ?operation ?label WHERE {");
		queryStr.append("?operation rdf:type ?type .");
		queryStr.append("?type rdfs:subClassOf* questionnaire:LogicalOperators .");
		queryStr.append("?operation rdfs:label ?label .");
		queryStr.append("}");

		QueryExecution qexec = ontology.query(queryStr);
		ResultSet results = qexec.execSelect();

		Set<Answer> comparisonOps = new HashSet<Answer>();

		while (results.hasNext()) {
			QuerySolution soln = results.next();
			comparisonOps.add(new Answer(soln.get("?operation").toString(), soln.get("?label").toString()));
		}
		qexec.close();
		return comparisonOps;
	}

	private ArrayList<Answer> getAnswerList(String element_URI) throws NoResultsException {
		ArrayList<Answer> answers= new ArrayList<Answer>();

		element_URI=element_URI.replace("#", ":");
		element_URI=element_URI.replace("http://ikm-group.ch/archiMEO/", "");
		//System.out.println("elementUri: "+ element_URI);

		//SELECT ?answer ?label WHERE { questiondata:Select_your_preferred_payment_plan questionnaire:questionHasAnswers ?answer .?answer rdfs:label ?label .}
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();

		queryStr.append("SELECT DISTINCT ?answer ?label WHERE {");
		queryStr.append(""+element_URI +" questionnaire:questionHasAnswers ?answer .");
		queryStr.append("?answer rdfs:label ?label .");
		queryStr.append("}");


		QueryExecution qexec2 = ontology.query(queryStr);
		ResultSet results = qexec2.execSelect();

		//System.out.println(element_URI);
		if (results.hasNext()) {
			while (results.hasNext()) {

				Answer answerN= new Answer();
				String id ="";
				String label ="";

				QuerySolution soln = results.next();

				id= soln.get("?answer").toString();
				label= soln.get("?label").toString();

				answerN.setAnswerID(id);
				answerN.setAnswerLabel(label);
				answers.add(answerN);
			}
		} else {
			throw new NoResultsException("nore more results");
		}
		qexec2.close();


		return answers;
	}

	private String formatURIForQueries(String URI){
		if (URI.startsWith("http://")){
			return "<"+URI+">";
		}else{
			return URI;
		}
	}

	@POST		
	@Produces({MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN})		
	@Consumes(MediaType.APPLICATION_JSON)		
	@Path("/getNextQuestion")		
	public Response getFunctionalQuestions(String parsed_json) throws NoResultsException {		
		Gson gson = new Gson(); 		
		//System.out.println("\n####################<start>####################");		
		//System.out.println("------------------------             Received request for next question        -------------------------" );		
		//System.out.println("####################<end>####################");		

		//System.out.println("Questionnaire received: " +parsed_json);		

		QuestionnaireModel qm = gson.fromJson(parsed_json, QuestionnaireModel.class);		
		QuestionnaireItem result = new QuestionnaireItem();		

		
		if (!qm.getCompletedQuestionList().isEmpty()) {
			if (qm.getCompletedQuestionList().size()>1)	{
				int lastQ= qm.getCompletedQuestionList().size()-1;
				
				QuestionnaireItem lastQuestion= qm.getCompletedQuestionList().get((lastQ));
				QuestionnaireItem lastLastQuestion= qm.getCompletedQuestionList().get((lastQ)-1);
				
				if(lastQuestion.getQuestionURI().equals("Empty")) {
					String json = gson.toJson(result);		
					return Response.status(Status.NOT_ACCEPTABLE).entity(json).build();	
				}else {
					
//					try {
//						result = detectNextQuestion(qm);
//					} catch (MinimumEntropyReached e) {
//						e.printStackTrace();
//					}
//					System.out.println(result.toString());
					
					if(lastQuestion.getQuestionURI().equals("End")) {
						String json = gson.toJson(result);		
						//System.out.println(json.toString());
						return Response.status(Status.NOT_ACCEPTABLE).entity(json).build();	
					}
				}
			}
		}
		
		try {
			result = detectNextQuestion(qm);
		} catch (MinimumEntropyReached e) {
			e.printStackTrace();
		}
		
		String json = gson.toJson(result);		
		//System.out.println("\n####################<start>####################");		
		//System.out.println("/search genereated json: " +json);		
		//System.out.println("####################<end>####################");		
		return Response.status(Status.OK).entity(json).build();		
	}

	public QuestionnaireItem detectNextQuestion(QuestionnaireModel qm) throws MinimumEntropyReached, NoResultsException {
		QuestionnaireItem pickedQuestion = new QuestionnaireItem();

		//Generate Attribute Map
		//System.out.println("questionnaire model" +qm.toString());

		//ArrayList<EntropyHotel> ehtls = createTestAttributeMap();

		if (qm.getCompletedQuestionList().size() >2){
			//System.out.println("####################qm.getCompletedQuestionList().size() inside if --->"+qm.getCompletedQuestionList().size()+"####################");
			try {
				pickedQuestion=getNonFunctionalQuestion(qm);
			} catch (NoResultsException e) {
				e.printStackTrace();
			}
			System.out.println("\n|----------------------------------------|\n#### Picked Non functional Question ---> "+ pickedQuestion.getQuestionLabel());
		} else {

			pickedQuestion=getFunctionalQuestion(qm);	
			System.out.println("\n|----------------------------------------|\n#### Picked functional Question ---> "+ pickedQuestion.getQuestionLabel());
		}
		return pickedQuestion;
	}


	public HashMap<String, Float> getEntropyMap(HashMap<String, HashMap<String, Integer>> attributeMap, Integer tot) {

		HashMap<String, Float> entropyMap = new HashMap<String, Float>();

		//System.out.println("\n|------------------------------------");
		//System.out.println("|EntropyMap Calculation on a total of :"+ tot +" hotels");
		//System.out.println("|------------------------------------");
		for (Map.Entry<String, HashMap<String, Integer>> entry : attributeMap.entrySet()) {
			HashMap<String, Integer> attributeImap = entry.getValue();

			//System.out.println(entry.getKey()+": "+ attributeImap.toString());

			Float entropyI=(float) 0;
			//System.out.println("\n|------------------------------------");	
			//System.out.println("|    Entropy of: "+ entry.getKey()+ "': "+ " " +entropyI);	
			//System.out.println("|------------------------------------\n\n");	
			for (Map.Entry<String, Integer> entry1 : attributeImap.entrySet()) {

				Integer count=entry1.getValue();

				Float entropyJ= entropyCalculation(count, tot);
				//System.out.println("    Entropy of '"+ entry1.getKey()+ "': " +entropyJ);
				//	System.out.println("    count: "+count);
				//	System.out.println("    Previous total entropy of: "+ entry.getKey()+ "': "+ " " +entropyI);
				entropyI=entropyI+entropyJ;
				//	System.out.println("------------------------------------");
				//	System.out.println("New entropy for "+entry.getKey()+": "+entropyI);
				//	System.out.println("------------------------------------");


				entropyMap.put(entry.getKey(), entropyI);
			}
		}
		//	System.out.println("\n|-----------------------------------------------------------");
		//	System.out.println("|     entropyMap generated by getEntropyMap() ");
		//	System.out.println("|-----------------------------------------------------------");	
		//	System.out.println("|\n|"+ entropyMap.toString()+"\n|");		
		//	System.out.println("|-----------------------------------------------------------\n");
		return entropyMap;
	}

	public HashMap<String, HashMap<String, Integer>> getAttributeMap(ArrayList<EntropyHotel> ehtls, ArrayList<String> blackListed) throws NoResultsException {

		ArrayList<String> tmpList= new ArrayList<String>();

		for (int i = 0; i<blackListed.size(); i++) {

			if (blackListed.get(i).contains("#")) {
				String tmp=blackListed.get(i);
				tmp=tmp.replace("http://ikm-group.ch/archiMEO/","");
				tmp=tmp.replace("http://ikm-group.ch/archimeo/","");
				tmp=tmp.replace("#",":");
				tmpList.add(tmp);
			}
		}
		blackListed.addAll(tmpList);

		//System.out.println("\n\nBlackListed answers:" +blackListed);
		
		HashMap<String, HashMap<String, Integer>> attributeValueListAndEntropyTotal = new HashMap<String, HashMap<String, Integer>>();

		Integer htlCount=ehtls.size();
		//System.out.println(ehtls.toString());

		//ArrayList<EntropyHotel> ehtlsOld =ehtls;

		ehtls= createAttributeMapFromList(ehtls, blackListed );

		//System.out.println("\n\nCount of Hotels: "+ htlCount.toString());

		for (int i = 0; i < htlCount; i++) {
			ArrayList<EntropyHotelAttribute> attributeListI = ehtls.get(i).getAttributes();
			//			System.out.println("hotel n"+(i+1) +"    id): "+ ehtls.get(i).getId().toString());
			//			System.out.println("ehtls.get("+i+").getAttributes(): ------->"+ ehtls.get(i).getAttributes().toString());
			Integer attributesCount = ehtls.get(i).getAttributes().size(); 

			for (int j = 0; j < attributesCount; j++) {
				//				System.out.println("attributes scanned ["+ (j+1) +"/"+ attributesCount);


				EntropyHotelAttribute attributeJ = attributeListI.get(j);
				//					System.out.println("     attributeJ: "+ attributeJ.toString());
				ArrayList<String> possibleValueList = attributeJ.getValues();
				//					System.out.println("     value: "+ possibleValueList.toString());

				//					System.out.println("          attributeValueListAndEntropyTotal.containsKey("+attributeJ.getId()+") "+ attributeValueListAndEntropyTotal.containsKey(attributeJ.getId()));
				if (!attributeValueListAndEntropyTotal.containsKey(attributeJ.getId())) {

					HashMap<String, Integer> attributeJmap = new HashMap<String, Integer>();

					for (int k = 0; k < possibleValueList.size(); k++) {
						//	System.out.println("          k: "+ k);

						String possibleValueK= possibleValueList.get(k);


						//							System.out.println("          Value "+ k + ":"+ possibleValueK);
						//							System.out.println("         	 attributeJmap: "+ attributeJmap.toString());
						//							System.out.println("                  attributeJmap.containsKey("+possibleValueK+"): "+ attributeJmap.containsKey(possibleValueK));
						if (!attributeJmap.containsKey(possibleValueK)) {
							//	System.out.println("                         adding "+possibleValueK);			
							attributeJmap.put(possibleValueK, 1);
							//								System.out.println("          attributeJmap: "+ attributeJmap.toString());
						} //for every new possible value K starts his count to 0


						attributeValueListAndEntropyTotal.put(attributeJ.getId(), attributeJmap);
						//							System.out.println("                           attributeValueListAndEntropyTotal.toString()"+attributeValueListAndEntropyTotal.toString());
					}
				} else {

					HashMap<String, Integer> attributeJmap = attributeValueListAndEntropyTotal.get(attributeJ.getId());
					//						System.out.println("          attributeValueListAndEntropyTotal.get(attributeJ) "+ attributeValueListAndEntropyTotal.get(attributeJ));
					for (int k = 0; k < possibleValueList.size(); k++) {
						String possibleValueK= possibleValueList.get(k);

						//							System.out.println("          Value "+ k + ":"+ possibleValueK);
						//							System.out.println("         	 attributeJmap: "+ attributeJmap.toString());

						if (!attributeJmap.containsKey(possibleValueK)) {
							attributeJmap.put(possibleValueK, 1);
						}else {
							int actualValue= attributeJmap.get(possibleValueK);
							attributeJmap.put(possibleValueK, actualValue+1);
							//	System.out.println("                         increasing count of "+possibleValueK+" to "+(actualValue+1));			


						} //for every new possible value K starts his count to 0, otherwise increment his count by 1
					}
				}
			}

		}
		//System.out.println("\n|-----------------------------------------------------------");
		//System.out.println("|     attributeValueListAndEntropyTotal generated by getAttributeMap() ");
		//System.out.println("|-----------------------------------------------------------\n");
		//System.out.println(attributeValueListAndEntropyTotal.toString());
		return attributeValueListAndEntropyTotal;
	}

	private static float entropyCalculation(Integer count, Integer total) {

		float prob = count/(float) total;
		//System.out.println("\n entropyCalc\n");
		//System.out.println("count:"+ count+" prob: "+ prob);
		float entropy = - (float)  (prob * Math.log(prob) / Math.log(2.0)) ;


		//System.out.println("entropy: "+ entropy);

		return entropy;
	}

	private Boolean checkAttrMinEntropy(Float float1) throws MinimumEntropyReached {

		Boolean flag=true;
		Float minEntropyAccepted= (float) 0.3;

		if (float1< minEntropyAccepted) {
			flag=false;
		}
		//System.out.println("entropy: "+ entropy);

		return flag;
	}

	public String getMaxEntropyAttribute(HashMap<String, Float> entropyMap, ArrayList<String> blackListed) throws NoResultsException {

		String maxEntropyAttr="";
		Float max=0.0f;
		ArrayList<String> tmpList= new ArrayList<String>();

		for (int i = 0; i<blackListed.size(); i++) {

			if (blackListed.get(i).contains("#")) {
				String tmp=blackListed.get(i);
				tmp=tmp.replace("http://ikm-group.ch/archiMEO/","");
				tmp=tmp.replace("http://ikm-group.ch/archimeo/","");
				tmp=tmp.replace("#",":");
				tmpList.add(tmp);
			}
		}
		blackListed.addAll(tmpList);

		//System.out.println("\n\nBlackListed answers:" +blackListed);
		
		for (Map.Entry<String,Float> entry : entropyMap.entrySet()) {
			
			//	System.out.println("OLD ANSWER CONTAINS THE VALUE? "+answers.contains(entry.getKey()));

			if (!blackListed.contains((entry.getKey()))) {

				if (entry.getValue()>=max) {
					maxEntropyAttr=entry.getKey();
					//System.out.println("New max entropy attribute is: "+entry.getKey() + " => " + entry.getValue());
					max=entry.getValue();
				}else {
					//System.out.println("Entropy attribute "+entry.getKey() + " => " + entry.getValue()+"is lower than "+maxEntropyAttr );
				}
			}
		}
		if (maxEntropyAttr=="") {
			System.out.println("All the questions available have been answered");
			maxEntropyAttr="End";
			//throw new NoResultsException("All the questions available have been answered");
		}


		//		potentially a conceptual error, removed for the moment
		//		try {
		//			checkAttrMinEntropy(entropyMap.get(maxEntropyAttr));
		//		} catch (MinimumEntropyReached e) {
		//			e.printStackTrace();
		//		}

		System.out.println("\n|-----------------------------------------------------------");
		System.out.println("            maxEntropyAttr: "+maxEntropyAttr );
		System.out.println("|------------------------------------------------------------\n");


		return maxEntropyAttr;
	}






	public String getQuestionFromAttribute(String attr) throws NoResultsException {
		String pickedQuestion ="Empty";

		//System.out.println("####################  EXECUTION OF getQuestionFromAttribute("+ attr+")         ####################");
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();
		//
		//		SELECT ?question ?label ?qType ?relation ?datatype ?searchnamespace ?searchType ?dTypeLabel ?rule WHERE {
		//			?question rdfs:label ?label .
		//			?question rdf:type ?qType . 
		//			?qType rdfs:subClassOf* questionnaire:AnswerType .
		//			?question rdf:type ?dType .
		//			?dType rdfs:label ?dTypeLabel .
		//			?dType rdfs:subClassOf questionnaire:Question . 
		//			?question questionnaire:questionHasAnnotationRelation ?relation . 
		//			OPTIONAL {?question questionnaire:valueInsertAnswerTypeHasDatatype ?datatype .}
		//			OPTIONAL {?question questionnaire:searchSelectionHasSearchNamespace ?searchnamespace .}
		//			OPTIONAL {?question questionnaire:searchSelectionOnClassesInsteadOfInstances ?searchType .}
		//			OPTIONAL {?dType questionnaire:hasOrderNumberForVisualization ?orderD}
		//			OPTIONAL {?question questionnaire:hasOrderNumberForVisualization ?orderQ}
		//			OPTIONAL {?question questionnaire:questionHasRuleToApply ?rule}
		//			FILTER (?question = questiondata:What_would_you_like_to_upload )
		//			} 

		queryStr.append("SELECT ?question ?label ?qType ?relation ?datatype ?searchnamespace ?searchType ?dTypeLabel ?rule WHERE {");
		queryStr.append("?question rdfs:label ?label .");
		queryStr.append("?question rdf:type ?qType . ");
		queryStr.append("?qType rdfs:subClassOf* questionnaire:AnswerType .");
		queryStr.append("?question rdf:type ?dType .");
		queryStr.append("?dType rdfs:label ?dTypeLabel .");
		queryStr.append("?dType rdfs:subClassOf questionnaire:Question . ");
		queryStr.append("?question questionnaire:questionHasAnnotationRelation ?relation . ");

		if (attr.contains("#")) {
			attr= attr.replace("http://ikm-group.ch/archiMEO/", "");
			attr=attr.replace("#", ":");
			System.out.println("attr formatted");
		};
		queryStr.append("FILTER (?relation = " + attr + ")}");


		QueryExecution qexec = ontology.query(queryStr);
		ResultSet results = qexec.execSelect();
		//System.out.println(queryStr);

		if (results.hasNext()) {
			while (results.hasNext()) {
				QuerySolution soln = results.next();

				pickedQuestion=soln.get("?question").toString();

			}
		} else {
			System.out.println("The question is not available in the triple store for the annotation relation: "+attr+" Query executed:\n"+queryStr+"\n");
			return pickedQuestion="Empty";
			//throw new NoResultsException("No matching question for "+ attr);
		}
		qexec.close();	


		//System.out.println("####################  end EXECUTION OF getQuestionFromAttribute("+ attr+")         ####################");
		//System.out.println("!!!!   THE QUESITON IS:  "+pickedQuestion);

		return pickedQuestion;

	}


	private QuestionnaireItem getNonFunctionalQuestion(QuestionnaireModel qm) throws NoResultsException {
		QuestionnaireItem pickedQuestion = new QuestionnaireItem();
		
		//ArrayList<EntropyHotel> ehtls = getHotelList(qm);
		ArrayList<EntropyHotel> ehtls=querySuitableHotels(qm.getCompletedQuestionList());

		

		ArrayList<String> oldAnswers=new ArrayList<String>();
		for (int i=0;i<qm.getCompletedQuestionList().size();i++) {
			String oldAnswer=qm.getCompletedQuestionList().get(i).getQuestionURI();
			oldAnswer=oldAnswer.replace("http://ikm-group.ch/archiMEO/", "");
			oldAnswer=oldAnswer.replace("#", ":");

			String getOldAnswer=qm.getCompletedQuestionList().get(i).getAnnotationRelation();
			if (getOldAnswer!=null) {
				getOldAnswer=getOldAnswer.replace("http://ikm-group.ch/archiMEO/", "");
				getOldAnswer=getOldAnswer.replace("#", ":");
			}

			oldAnswers.add(searchAnnotationRelation(getOldAnswer,oldAnswer));
			//System.out.println(oldAnswers.toString());
		}

		String maxEntropyAttribute="";

		ArrayList<Answer> selectedDomainList=qm.getSelectedDomainList();
		ArrayList<String> questionsOutOfDomain= new ArrayList<String>();

		questionsOutOfDomain=getQuestionsOutOfDomain(selectedDomainList);
		ArrayList<String> blackListedQuestion= new ArrayList<String>();
		blackListedQuestion.add("bpaas:hotelHasDescription");
		blackListedQuestion.addAll(oldAnswers);
		blackListedQuestion.addAll(questionsOutOfDomain);
		//System.out.println(blackListedQuestion.toString());
		
				
		if (ehtls.size()==0) {
			
			System.out.println("No Hotel matching based on the previous question's answers");
			//throw new NoResultsException("only 1 matching Hotel, no more questions are needed");
			
			// TODO: TO BE DISCUSSED: IF THE ATTRIBUTE HAS NOT A QUESTION, SHOW THE MESSAGE INSTEAD OF CRASH
			ArrayList<Answer> answerListTmp= new ArrayList<Answer>();
			Answer a1= new Answer();
			a1.setAnswerID("SKIP");
			a1.setAnswerLabel("Please, press back to try another answer of the previous question OR start a new questionnaire, clicking next won't generate a new question");
			
			answerListTmp.add(a1);
			
			pickedQuestion.setAnswerList(answerListTmp);
			pickedQuestion.setQuestionLabel("There are no matching Hotel");
			pickedQuestion.setAnnotationRelation("SKIP");
			pickedQuestion.setAnswerType("http://ikm-group.ch/archiMEO/questionnaire#SingleSelection");
			pickedQuestion.setQuestionURI("Empty");
			return pickedQuestion;
						
		}
		
		//TODO: USE BLACKLIST ON ATTRIBUTE MAP, INSTEAD OF GET MAX ENTROPY ATTR
		HashMap<String, HashMap<String, Integer>> attributeMap= getAttributeMap(ehtls, blackListedQuestion);
		//System.out.println("attributeMap"+attributeMap.toString());		

		HashMap<String, Float> entropyMap = getEntropyMap(attributeMap, ehtls.size());
		//System.out.println("entropyMap"+entropyMap.toString());

		maxEntropyAttribute = getMaxEntropyAttribute(entropyMap, blackListedQuestion);	
		
		if (maxEntropyAttribute=="End") {
			//TODO: NEW APPROACH TESTING, TO BE DISCUSSED
						
			System.out.println("All questions for the selected domain have been answered");
			//throw new NoResultsException("only 1 matching Hotel, no more questions are needed");
			
			// TODO: TO BE DISCUSSED: IF THE ATTRIBUTE HAS NOT A QUESTION, SHOW THE MESSAGE INSTEAD OF CRASH
			ArrayList<Answer> answerListTmp= new ArrayList<Answer>();
			Answer a1= new Answer();
			a1.setAnswerID("SKIP");
			a1.setAnswerLabel("Please, press back to try another answer of the previous questions OR start a new questionnaire, clicking next won't generate a new question");
			
			answerListTmp.add(a1);
						
			pickedQuestion.setAnswerList(answerListTmp);
			pickedQuestion.setQuestionLabel("All questions for the selected domain have been answered");
			pickedQuestion.setAnnotationRelation("SKIP");
			pickedQuestion.setAnswerType("http://ikm-group.ch/archiMEO/questionnaire#SingleSelection");
			pickedQuestion.setQuestionURI("Empty");
			return pickedQuestion;
		}
		
		String questionID =getQuestionFromAttribute(maxEntropyAttribute);
		
		// TODO: TO BE DISCUSSED: IF THE ATTRIBUTE HAS NOT A QUESTION, SHOW THE MESSAGE INSTEAD OF CRASH
		if (questionID=="Empty") {
			//TODO: NEW APPROACH TESTING, TO BE DISCUSSED
						
			ArrayList<Answer> answerListTmp= new ArrayList<Answer>();
			Answer a1= new Answer();
			a1.setAnswerID("SKIP");
			a1.setAnswerLabel("Please, press back to try another answer of the previous questions OR next to ignore the question");

			answerListTmp.add(a1);
			
			pickedQuestion.setAnswerList(answerListTmp);
			pickedQuestion.setQuestionLabel("Question for "+maxEntropyAttribute + " not available, try with another answer");
			pickedQuestion.setAnnotationRelation(maxEntropyAttribute);
			pickedQuestion.setAnswerType("http://ikm-group.ch/archiMEO/questionnaire#SingleSelection");
			pickedQuestion.setQuestionURI("skippable");
			
			return pickedQuestion;
		}
		//end  


		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();

		queryStr.append("SELECT ?question ?label ?qType ?relation ?datatype ?searchnamespace ?searchType ?dTypeLabel ?rule WHERE {");
		queryStr.append("?question rdfs:label ?label .");
		queryStr.append("?question rdf:type ?qType . ");
		queryStr.append("?qType rdfs:subClassOf* questionnaire:AnswerType .");
		queryStr.append("?question rdf:type ?dType .");
		queryStr.append("?dType rdfs:label ?dTypeLabel .");
		queryStr.append("?dType rdfs:subClassOf questionnaire:Question . ");
		//queryStr.append("?question questionnaire:questionHasAnnotationRelation ?relation . ");
		queryStr.append("OPTIONAL {?question questionnaire:valueInsertAnswerTypeHasDatatype ?datatype .}");
		queryStr.append("OPTIONAL {?question questionnaire:searchSelectionHasSearchNamespace ?searchnamespace .}");
		queryStr.append("OPTIONAL {?question questionnaire:searchSelectionOnClassesInsteadOfInstances ?searchType .}");
		queryStr.append("OPTIONAL {?dType questionnaire:hasOrderNumberForVisualization ?orderD}");
		queryStr.append("OPTIONAL {?question questionnaire:hasOrderNumberForVisualization ?orderQ}");
		queryStr.append("OPTIONAL {?question questionnaire:questionHasRuleToApply ?rule}");

		queryStr.append("FILTER (?question = <"+ questionID+"> )");
		queryStr.append("}");

		queryStr.append("ORDER BY DESC(?orderD) DESC(?orderQ)");
		
		System.out.println("query for non functional question\n"+queryStr);

		QueryExecution qexec = ontology.query(queryStr);
		ResultSet results = qexec.execSelect();

		if (results.hasNext()) {
			while (results.hasNext()) {
				QuestionnaireItem question = new QuestionnaireItem();

				QuerySolution soln = results.next();
				question.setQuestionURI(soln.get("?question").toString());
				question.setQuestionLabel(soln.get("?label").toString());
				question.setAnswerType(soln.get("?qType").toString());
				question.setDomainLabel(soln.get("?dTypeLabel").toString());
				//question.setAnnotationRelation(soln.get("?relation").toString());

				if (soln.get("?rule") != null ){
					question.setRuleToApply(soln.get("?rule").toString());
				}

				if (soln.get("?qType").toString().equals(GlobalVariables.ANSWERTYPE_SINGLE_SELECTION) || 
						soln.get("?qType").toString().equals(GlobalVariables.ANSWERTYPE_MULTI_SELECTION)){
					//Call for the answers
					question.setAnswerList(new ArrayList<Answer>(getAnswerList(soln.get("?question").toString())));
				}else if (soln.get("?qType").toString().equals(GlobalVariables.ANSWERTYPE_SEARCH_SELECTION)){
					//Call for the namespace
					question.setSearchNamespace(soln.get("?searchnamespace").toString());
					//System.out.println("=====================SEARCHTYPE: " + soln.get("?searchType"));
					//Call for the searchType (Classes of Instances)
					if (soln.get("?searchType").toString() == null ||
							soln.get("?searchType").toString().equals("") ||
							soln.get("?searchType").toString().equals(GlobalVariables.BOOLEAN_FALSE_URI)){
						question.setSearchOnClassesInsteadOfInstances(false);
					} else {
						question.setSearchOnClassesInsteadOfInstances(true);
					}	
				}else if (soln.get("?qType").toString().equals(GlobalVariables.ANSWERTYPE_VALUEINSERT)){
					//Call for the Datatype
					question.setAnswerDatatype(soln.get("?datatype").toString());
					question.setComparisonOperationAnswers(getComparisonOperations());
				}

				pickedQuestion = question;
			}
		} else {
			throw new NoResultsException("no more results");
		}
		qexec.close();

		return pickedQuestion;
	}



	private ArrayList<String> getQuestionsOutOfDomain(ArrayList<Answer> domain_received) throws NoResultsException {
		ArrayList<String> removedQuestion= new ArrayList<String>();
		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();

		queryStr.append("SELECT ?question ?label ?qType ?relation ?datatype ?searchnamespace ?searchType ?dTypeLabel ?rule WHERE {");
		queryStr.append("?question rdfs:label ?label .");
		queryStr.append("?question rdf:type ?qType . ");
		queryStr.append("?qType rdfs:subClassOf* questionnaire:AnswerType .");
		queryStr.append("?question rdf:type ?dType .");
		queryStr.append("?dType rdfs:label ?dTypeLabel .");
		queryStr.append("?dType rdfs:subClassOf questionnaire:Question . ");
		queryStr.append("?question questionnaire:questionHasAnnotationRelation ?relation . ");
		queryStr.append("OPTIONAL {?question questionnaire:valueInsertAnswerTypeHasDatatype ?datatype .}");
		queryStr.append("OPTIONAL {?question questionnaire:searchSelectionHasSearchNamespace ?searchnamespace .}");
		queryStr.append("OPTIONAL {?question questionnaire:searchSelectionOnClassesInsteadOfInstances ?searchType .}");
		queryStr.append("OPTIONAL {?dType questionnaire:hasOrderNumberForVisualization ?orderD}");
		queryStr.append("OPTIONAL {?question questionnaire:hasOrderNumberForVisualization ?orderQ}");
		queryStr.append("OPTIONAL {?question questionnaire:questionHasRuleToApply ?rule}");

		String first_part = "FILTER (";
		String middle_part = "";
		String last_part = ")";
		for (int i = 0; i < domain_received.size(); i++){
			if (middle_part != ""){
				middle_part =  middle_part + " && ";
			}
			middle_part = middle_part + "?dType != <" + domain_received.get(i).getAnswerID()+">";
		}
		queryStr.append(first_part + middle_part + last_part);

		queryStr.append("}");
		queryStr.append("ORDER BY DESC(?orderD) DESC(?orderQ)");

		QueryExecution qexec = ontology.query(queryStr);
		ResultSet results = qexec.execSelect();

		if (results.hasNext()) {
			while (results.hasNext()) {

				QuerySolution soln = results.next();

				String question =(soln.get("?relation").toString());
				//System.out.println(question);

				question=question.replace("http://ikm-group.ch/archiMEO/","");
				question=question.replace("http://ikm-group.ch/archimeo/","");
				question=question.replace("#",":");
				//System.out.println("question fixed "+question);
				removedQuestion.add(question);
			}
		} else {
			throw new NoResultsException("No quesiton for the domain");
		}
		qexec.close();		
		//System.out.println("query executed\n"+queryStr);
		//System.out.println(removedQuestion.toString());

		return removedQuestion;
	}

	private QuestionnaireItem getFunctionalQuestion(QuestionnaireModel qm) throws NoResultsException {
		QuestionnaireItem pickedQuestion = new QuestionnaireItem();

		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();

		queryStr.append("SELECT ?question ?label ?qType ?relation ?datatype ?searchnamespace ?searchType ?dTypeLabel ?rule WHERE {");
		queryStr.append("?question rdfs:label ?label .");
		queryStr.append("?question rdf:type ?qType . ");
		queryStr.append("?qType rdfs:subClassOf* questionnaire:AnswerType .");
		queryStr.append("?question rdf:type ?dType .");
		queryStr.append("?dType rdfs:label ?dTypeLabel .");
		queryStr.append("?dType rdfs:subClassOf questionnaire:Question . ");
		//queryStr.append("?question questionnaire:questionHasAnnotationRelation ?relation . ");
		queryStr.append("OPTIONAL {?question questionnaire:valueInsertAnswerTypeHasDatatype ?datatype .}");
		queryStr.append("OPTIONAL {?question questionnaire:searchSelectionHasSearchNamespace ?searchnamespace .}");
		queryStr.append("OPTIONAL {?question questionnaire:searchSelectionOnClassesInsteadOfInstances ?searchType .}");
		queryStr.append("OPTIONAL {?dType questionnaire:hasOrderNumberForVisualization ?orderD}");
		queryStr.append("OPTIONAL {?question questionnaire:hasOrderNumberForVisualization ?orderQ}");
		queryStr.append("OPTIONAL {?question questionnaire:questionHasRuleToApply ?rule}");

		if (qm.getCompletedQuestionList().size() == 1){
			queryStr.append("FILTER (?label = \"Which Object does reflect the functional requirement you want to express?\")");
		}else if (qm.getCompletedQuestionList().size() == 0){
			queryStr.append("FILTER (?label = \"Which Action does reflect the functional requirement you want to express?\")");
		}else if (qm.getCompletedQuestionList().size()== 2){
			queryStr.append("FILTER (?label = \"Which APQC category does reflect the functional requirement you want to express?\")");
		}
		queryStr.append("}");
		queryStr.append("ORDER BY DESC(?orderD) DESC(?orderQ)");

		//System.out.println("Get functional question query:\n"+ queryStr);
		QueryExecution qexec = ontology.query(queryStr);
		ResultSet results = qexec.execSelect();

		if (results.hasNext()) {
			while (results.hasNext()) {
				QuestionnaireItem question = new QuestionnaireItem();

				QuerySolution soln = results.next();
				question.setQuestionURI(soln.get("?question").toString());
				question.setQuestionLabel(soln.get("?label").toString());
				question.setAnswerType(soln.get("?qType").toString());
				question.setDomainLabel(soln.get("?dTypeLabel").toString());
				//question.setAnnotationRelation(soln.get("?relation").toString());

				if (soln.get("?rule") != null ){
					question.setRuleToApply(soln.get("?rule").toString());
				}

				if (soln.get("?qType").toString().equals(GlobalVariables.ANSWERTYPE_SINGLE_SELECTION) || 
						soln.get("?qType").toString().equals(GlobalVariables.ANSWERTYPE_MULTI_SELECTION)){
					//Call for the answers
					question.setAnswerList(new ArrayList<Answer>(getAnswerList(soln.get("?question").toString())));
				}else if (soln.get("?qType").toString().equals(GlobalVariables.ANSWERTYPE_SEARCH_SELECTION)){
					//Call for the namespace
					question.setSearchNamespace(soln.get("?searchnamespace").toString());
					//System.out.println("=====================SEARCHTYPE: " + soln.get("?searchType"));
					//Call for the searchType (Classes of Instances)
					if (soln.get("?searchType").toString() == null ||
							soln.get("?searchType").toString().equals("") ||
							soln.get("?searchType").toString().equals(GlobalVariables.BOOLEAN_FALSE_URI)){
						question.setSearchOnClassesInsteadOfInstances(false);
					} else {
						question.setSearchOnClassesInsteadOfInstances(true);
					}	
				}else if (soln.get("?qType").toString().equals(GlobalVariables.ANSWERTYPE_VALUEINSERT)){
					//Call for the Datatype
					question.setAnswerDatatype(soln.get("?datatype").toString());
					question.setComparisonOperationAnswers(getComparisonOperations());
				}

				pickedQuestion = question;
			}
		} else {
			throw new NoResultsException("no more results");
		}
		qexec.close();		

		return pickedQuestion;
	}


	//	@GET
	//	@Path("/testEntropy")
	//	public Response  getHotelList(){
	public ArrayList<EntropyHotel> getHotelList(QuestionnaireModel qm){

		String tempStrForDomain = "";

		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();
		queryStr.append("SELECT ?q ?dType ?annotationRelation ?htl ?htlLabel ?value WHERE {");
		//queryStr.append("SELECT ?htl ?htlLabel ?value WHERE {");

		queryStr.append("?q rdf:type ?dType .");
		queryStr.append("?dType rdfs:subClassOf questionnaire:Question . ");
		queryStr.append("?q questionnaire:questionHasAnnotationRelation ?annotationRelation .");
		queryStr.append("?htl rdf:type bpaas:Hotel .");
		queryStr.append("?htl rdfs:label ?htlLabel .");
		queryStr.append("?htl ?annotationRelation ?value . ");
		//generate filter based on domains

		for (int i = 0; i < qm.getSelectedDomainList().size(); i++){
			if (i != 0){
				tempStrForDomain = tempStrForDomain + " || ";
			}
			tempStrForDomain = tempStrForDomain + "?dType = <" + qm.getSelectedDomainList().get(i).getAnswerID()+">";
		}
		queryStr.append("FILTER (" + tempStrForDomain + ")");
		//queryStr.append("FILTER (?dType = questionnaire:DataSecurity || ?dType = questionnaire:Payment)");
		queryStr.append("} ORDER BY ?htlLabel ?annotationRelation ?value");

		QueryExecution qexec = ontology.query(queryStr);
		ResultSet results = qexec.execSelect();

		System.out.println(queryStr);

		ArrayList<EntropyHotel> ehtls = new ArrayList<EntropyHotel>();
		EntropyHotel currentHTL = null;
		EntropyHotelAttribute currentHtlAttribute = null;
		ArrayList<String> currentAttributeValues = null;
		String currentHtlName = "";
		String currentHtlAttributeName = "";

		while (results.hasNext()) {

			QuerySolution soln = results.next();
			//if the hotel that I am parsing is different from the previous one
			if (!soln.get("?htl").toString().equals(currentHtlName)){

				//I create a new one
				if (!currentHtlName.equals("")){
					//but first, I check if it's the first
					ehtls.add(currentHTL);
				}
				currentHTL = new EntropyHotel();
				currentHTL.setId(soln.get("?htl").toString());
				currentHTL.setLabel(soln.get("?htlLabel").toString());
				currentHtlName = soln.get("?htl").toString();
			}
			//I parse attributes
			//I check if I am parsing a new attribute
			if (!currentHtlAttributeName.equals(soln.get("?annotationRelation").toString())){

				if (currentAttributeValues != null){
					//if it is not the initial null array, then I add those values to the current property
					currentHtlAttribute.setValues(currentAttributeValues);
					currentHTL.getAttributes().add(currentHtlAttribute);
				}
				//I create a new array to hold all the values for the next attribute
				currentAttributeValues = new ArrayList<String>();
				//I set the new current attribute for matching
				currentHtlAttributeName = soln.get("?annotationRelation").toString();



				//And I create a new Attribute
				currentHtlAttribute = new EntropyHotelAttribute();
				currentHtlAttribute.setId(soln.get("?annotationRelation").toString());
				currentHtlAttribute.setDomain(soln.get("?dType").toString());
			}
			currentAttributeValues.add(soln.get("?value").toString());	
		}
		//System.out.println("debug prop" + debug_properties);

		if (debug_properties){
			for (int i = 0; i < ehtls.size(); i++){
				System.out.println("=======");
				System.out.println(ehtls.get(i).getId()+ " - " + ehtls.get(i).getLabel());
				for (int j = 0; j < ehtls.get(i).getAttributes().size(); j++){
					System.out.println("----> " + ehtls.get(i).getAttributes().get(j).getId() + " - " + ehtls.get(i).getAttributes().get(j).getDomain());
					for (int k = 0; k < ehtls.get(i).getAttributes().get(j).getValues().size(); k++){
						System.out.println("---------> " + ehtls.get(i).getAttributes().get(j).getValues().get(k));
					}
				}
			}
		}
		qexec.close();

		//System.out.println("---------------------------------json--------------------------------");
		//Gson gson = new Gson();
		//String json = gson.toJson(ehtls);
		//System.out.println(json.toString());

		//System.out.println("\n--------------------------------- ehtls.toString -----------------------\n"+ehtls.toString()+"\n");
		//return Response.status(Status.OK).entity(json).build();
		return ehtls;

	}

	private String searchAnnotationRelation(String annotationRelation, String questionURI) throws NoResultsException {


		if (annotationRelation!=null) {
			return annotationRelation;
		}else {

			//			SELECT ?relation WHERE {
			//			?question rdfs:label ?label .
			//			?question rdf:type ?qType . 
			//			?qType rdfs:subClassOf* questionnaire:AnswerType .
			//			?question rdf:type ?dType .
			//			?dType rdfs:label ?dTypeLabel .
			//			?dType rdfs:subClassOf questionnaire:Question . 
			//			  ?question questionnaire:questionHasAnnotationRelation ?relation .
			//			FILTER (?question =questionURI )
			//			}
			String questionN= questionURI.replace("http://ikm-group.ch/archiMEO/", "");
			questionN=questionN.replace("#", ":");
			ParameterizedSparqlString queryStr = new ParameterizedSparqlString();

			queryStr.append("SELECT ?relation WHERE {");
			queryStr.append("?question rdfs:label ?label .");
			queryStr.append("?question rdf:type ?qType . ");
			queryStr.append("?qType rdfs:subClassOf* questionnaire:AnswerType .");
			queryStr.append("?question rdf:type ?dType .");
			queryStr.append("?dType rdfs:label ?dTypeLabel .");
			queryStr.append("?dType rdfs:subClassOf questionnaire:Question . ");
			queryStr.append("?question questionnaire:questionHasAnnotationRelation ?relation .");
			queryStr.append("FILTER (?question = "+questionN+"" );
			queryStr.append(")}");


			QueryExecution qexec = ontology.query(queryStr);
			ResultSet results = qexec.execSelect();

			if (results.hasNext()) {
				while (results.hasNext()) {

					QuerySolution soln = results.next();
					annotationRelation=soln.get("?relation").toString();

				}
			} else {
				throw new NoResultsException("no more results");
			}
			qexec.close();

			return annotationRelation;
		}


	}

	private ArrayList<EntropyHotel> createAttributeMapFromList(ArrayList<EntropyHotel> ehtls, ArrayList<String> blackListed) throws NoResultsException {

		ArrayList<String> htlList=new ArrayList<String>();
		//System.out.println("creating list of hotels for attribute map");
		for (int i = 0; i < ehtls.size(); i++){
			if (!htlList.contains(ehtls.get(i).getId())) {
				htlList.add(ehtls.get(i).getId());
				String temp = ehtls.get(i).getId();
				temp=temp.replace("http://ikm-group.ch/archiMEO/", "");
				temp=temp.replace("#", ":");
				//System.out.println("added "+temp+" to the hotel list");
			}		

		}
		//System.out.println("the htlList is" +htlList.toString());

		ArrayList<EntropyHotel> attributeMap= new ArrayList<EntropyHotel>();

		for (int i = 0; i < htlList.size(); i++){
			//System.out.println("i--------> "+i);
			String htlIid=htlList.get(i);
			//System.out.println("htlIid ---->"+ htlIid);
			//System.out.println("htlList.get(i) "+htlList.get(i));
			htlIid=htlIid.replace("http://ikm-group.ch/archiMEO/", "");
			htlIid=htlIid.replace("#", ":");
			//System.out.println("htlIid ---->"+ htlIid);
			EntropyHotel htlI= queryHotel(htlIid, blackListed);
			attributeMap.add(htlI);
		}


		//System.out.println(" attributeMap from createAttributeMapFromList(ArrayList<EntropyHotel> ehtls)\n"+attributeMap.toString());
		return attributeMap;

	}

	private EntropyHotel queryHotel(String htlId, ArrayList<String> blackListed) throws NoResultsException {

		EntropyHotel htl= new EntropyHotel();

		ArrayList<String> properties = new ArrayList<String>();
		//System.out.println("htlId ---->"+ htlId+"\n before queryHotelProperties(htlId)");
		
		properties= queryHotelProperties(htlId, blackListed );
		//System.out.println("properties"+properties);

		//System.out.println("post queryHotelProperties(htlId)");
		ArrayList<EntropyHotelAttribute> attributeList= new ArrayList<EntropyHotelAttribute>();

		for (int i = 0; i < properties.size(); i++){
			EntropyHotelAttribute attributeI = new EntropyHotelAttribute();

			String attributeIid= properties.get(i);
			ArrayList<String> attributeIvalues= new ArrayList<String>();

			//System.out.println("htlId "+htlId + "\n attributeIid"+attributeIid);
			attributeIid=attributeIid.replace("http://ikm-group.ch/archiMEO/", "");
			attributeIid=attributeIid.replace("#", ":");

			attributeIvalues= queryHotelAttributeValues(htlId, attributeIid);

			attributeI.setId(attributeIid);
			attributeI.setValues(attributeIvalues);

			attributeList.add(attributeI);

		}

		htl.setAttributes(attributeList);
		htl.setId(htlId);

		//System.out.println(" htl from queryHotel(String htlId)\n"+htl.toString());
		return htl;
	}

	private ArrayList<String> queryHotelAttributeValues(String htlId, String attributeId) throws NoResultsException {

		ArrayList<String> attributeValues= new ArrayList<String>();
		htlId=htlId.replace("http://ikm-group.ch/archiMEO/", "");
		htlId=htlId.replace("#", ":");

		attributeId=attributeId.replace("http://ikm-group.ch/archimeo/", "");
		attributeId=attributeId.replace("#", ":");

		//		SELECT  ?value WHERE {
		//			?hotel rdf:type bpaas:Hotel .
		//			 ?property rdfs:domain bpaas:Hotel .
		//			  ?hotel bpaas:hotelHasPaymentPlan ?value
		//			FILTER (?hotel = bdata:InvoiceNinja ).
		//			FILTER (?property = bpaas:hotelHasPaymentPlan)}

		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();

		queryStr.append("SELECT ?hotel ?value WHERE {");
		queryStr.append("?hotel rdf:type bpaas:Hotel .");
		queryStr.append("?property rdfs:domain bpaas:Hotel .");
		queryStr.append("?hotel "+ attributeId+ " ?value ");
		queryStr.append("FILTER (?hotel = " + htlId + " ).");
		queryStr.append("FILTER (?property = "+ attributeId +")}");

		QueryExecution qexec = ontology.query(queryStr);
		ResultSet results = qexec.execSelect();

		if (results.hasNext()) {
			while (results.hasNext()) {
				String attributeValue ="";

				QuerySolution soln = results.next();

				attributeValue= soln.get("?value").toString();
				attributeValue.replace("http://ikm-group.ch/archimeo/", "");
				attributeValue.replace("#", ":");
				//System.out.println("attributeValue--->  "+ attributeValue);

				attributeValues.add(attributeValue);
			}
		}
		qexec.close();
		//System.out.println(" attributeValues from queryHotel(String htlId)\n"+attributeValues.toString());
		return attributeValues;
	}

	private ArrayList<String> queryHotelProperties(String htlId, ArrayList<String> blackListed) throws NoResultsException {
		ArrayList<String> properties = new ArrayList<String>();

		//		SELECT ?hotel ?property WHERE {
		//			?hotel rdf:type bpaas:Hotel .
		//			?property rdfs:domain bpaas:Hotel .
		//			FILTER (?hotel = bdata:InvoiceNinja).} 

		htlId=htlId.replace("http://ikm-group.ch/archiMEO/", "");
		htlId=htlId.replace("http://ikm-group.ch/archimeo/", "");
		htlId=htlId.replace("#", ":");

		ParameterizedSparqlString queryStr = new ParameterizedSparqlString();

		queryStr.append("SELECT DISTINCT ?hotel ?property WHERE {");
		queryStr.append("?hotel rdf:type bpaas:Hotel .");
		queryStr.append("?property rdfs:domain bpaas:Hotel .");
		queryStr.append("FILTER (?hotel = "+ htlId+" ).}");


		QueryExecution qexec = ontology.query(queryStr);
		ResultSet results = qexec.execSelect();

		if (results.hasNext()) {
			while (results.hasNext()) {
				String property ="";

				QuerySolution soln = results.next();

				property= soln.get("?property").toString();

				//System.out.println("property--->  "+ property);
				property= property.replace("http://ikm-group.ch/archimeo/", "");
				property= property.replace("#", ":");
				//System.out.println("new property--->  "+ property);

				properties.add(property);
			}
		} else {
			throw new NoResultsException("no more results");
		}
		qexec.close();

		//System.out.println("-----------------------------------------------------\n Properties of hotels are:" +properties.toString() + "\nthe blacklisted attributes are \n" +blackListed.toString());
		properties.removeAll(blackListed);
		//System.out.println("Resulting properties are:" +properties.toString());

		return properties;
	}
}

