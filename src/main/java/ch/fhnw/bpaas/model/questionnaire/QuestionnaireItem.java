package ch.fhnw.bpaas.model.questionnaire;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class QuestionnaireItem {

	@Override
	public String toString() {
		return "QuestionnaireItem [questionLabel=" + questionLabel + ", questionURI=" + questionURI + ", questionID="
				+ questionID + ", answerList=" + answerList + ", answerDatatype=" + answerDatatype
				+ ", givenAnswerList=" + givenAnswerList + ", searchNamespace=" + searchNamespace
				+ ", comparisonOperationAnswers=" + comparisonOperationAnswers + ", comparisonAnswer="
				+ comparisonAnswer + ", searchOnClassesInsteadOfInstances=" + searchOnClassesInsteadOfInstances
				+ ", answerType=" + answerType + ", domainLabel=" + domainLabel + ", annotationRelation="
				+ annotationRelation + ", ruleToApply=" + ruleToApply + "]";
	}

	public void setComparisonAnswer(String comparisonAnswer) {
		this.comparisonAnswer = comparisonAnswer;
	}

	private String questionLabel;						//label
	private String questionURI;							//URI
	private int questionID;								//number of the question (supposed to be used on angular)
	private ArrayList<Answer> answerList;				//list of answer
	private String answerDatatype;						//type of answer (boolean, integer...)
	private ArrayList<Answer> givenAnswerList;			//answers after angular interface
	private String searchNamespace;						//points to the namespace
	private Set<Answer> comparisonOperationAnswers;		//set all the possible operators
	private String comparisonAnswer;					//defined the operator selected after angular 
	private Boolean searchOnClassesInsteadOfInstances; 	//define if the namespace has classes or instances to select
	private String answerType;							//type (multiselect, singleselect...)
	private String domainLabel;							//the label of the domain (performance, datasecurity, etc.)	
	private String annotationRelation;					//the uri of the CS property that the question refers
	private String ruleToApply;							//a rule to apply when perform an answer
	//private OntologyManager ontology = OntologyManager.getInstance();
	private boolean debug_properties = false;
	
	public String getSearchNamespace() {
		return searchNamespace;
	}

	public void setSearchNamespace(String searchNamespace) {
		this.searchNamespace = searchNamespace;
	}

	public QuestionnaireItem(){
		answerList = new ArrayList<Answer>();
		givenAnswerList = new ArrayList<Answer>();
		comparisonOperationAnswers = new HashSet<Answer>();
	}

	public String getDomainLabel() {
		return domainLabel;
	}

	public void setDomainLabel(String domainURI) {
		this.domainLabel = domainURI;
	}

	public void setQuestionLabel(String questionLabel) {
		this.questionLabel = questionLabel;
	}

	public void setQuestionURI(String questionURI) {
		this.questionURI = questionURI;
	}

	public void addAnswer(Answer answer) {
		answerList.add(answer);
	}

	public void setAnswerType(String answerType) {
		this.answerType = answerType;
	}
	
	public String getQuestionURI() {
		return questionURI;
	}
	
	public String getQuestionLabel() {
		return questionLabel;
	}
	
	public String getAnswerType() {
		return answerType;
	}
	
	public ArrayList<Answer> getAnswerList() {
		return answerList;
	}
	
	public void setAnswerList(ArrayList<Answer> answerList) {
		this.answerList = answerList;
	}
	
	
	public Boolean getSearchOnClassesInsteadOfInstances() {
		return searchOnClassesInsteadOfInstances;
	}

	public void setSearchOnClassesInsteadOfInstances(Boolean searchOnClassesInsteadOfInstances) {
		this.searchOnClassesInsteadOfInstances = searchOnClassesInsteadOfInstances;
	}

	// old
	//	public String toString(){
	//		StringBuilder sb = new StringBuilder();
	//		sb.append("==QuestionnaireItem==\n");
	//		sb.append("QuestionURI: \t" +getQuestionURI()+"\n");
	//		sb.append("questionID: \t" +getQuestionID()+"\n");
	//		sb.append("QuestionLabel: \t" +getQuestionLabel()+"\n");
	//		sb.append("AnswerType: \t" +getAnswerType()+"\n");
	//		sb.append("AnswerDataType: \t" +getAnswerDatatype()+"\n");
	//		sb.append("AnswerDataType: \t" +getAnswerDrilldownNamespace() +"\n");
	//		sb.append("comparisonOperationsAnswers: \t" +comparisonOperationAnswers.toString() +"\n");
	//		sb.append("comparisonAnswer: \t" +comparisonAnswer);
	//		
	//		for (Answer answerItem : answerList) {
	//			sb.append(answerItem.toString());
	//		}
	////		for (String gAnswer : givenAnswerList) {
	//			sb.append("given answers: " +givenAnswerList.toString() +"\n");
	////		}
	//		return sb.toString();
	//	}

	public void removeGivenAnswer(String answerID) {
		System.out.println("removed " +answerID);
		givenAnswerList.remove(answerID);
		System.out.println("new " +givenAnswerList.toString());
	}

	public String getAnswerDatatype() {
		return answerDatatype;
	}
	
	public void setAnswerDatatype(String answerDatatype) {
		this.answerDatatype = answerDatatype;
	}

	public ArrayList<Answer> getGivenAnswerList() {
		return givenAnswerList;
	}

	public void setQuestionID(int questionNumber) {
		this.questionID = questionNumber;
	}
	
	public int getQuestionID() {
		return questionID;
	}

	public void setAnswerSearchNamespace(String searchNamespace) {
		this.searchNamespace = searchNamespace;
	}
	
	private String getAnswerDrilldownNamespace() {
		return searchNamespace;
	}

	public void setValueInsertComparisonOperationAnswers(Set<Answer> comparisonOperations) {
		this.comparisonOperationAnswers = comparisonOperations;
	}

	public String getComparisonAnswer() {
		return comparisonAnswer;
	}
	
	public Set<Answer> getComparisonOperationAnswers() {
		return comparisonOperationAnswers;
	}

	public void setComparisonOperationAnswers(Set<Answer> comparisonOperationAnswers) {
		this.comparisonOperationAnswers = comparisonOperationAnswers;
	}

	public String getAnnotationRelation() {
		return annotationRelation;
	}

	public void setAnnotationRelation(String annotationRelation) {
		this.annotationRelation = annotationRelation;
	}

	public void setGivenAnswerList(ArrayList<Answer> givenAnswerList) {
		this.givenAnswerList = givenAnswerList;
	}

	public String getRuleToApply() {
		return ruleToApply;
	}

	public void setRuleToApply(String ruleToApply) {
		this.ruleToApply = ruleToApply;
	}

	
	
}
