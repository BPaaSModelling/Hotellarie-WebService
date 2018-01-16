package ch.fhnw.bpaas.model.questionnaire;

import java.util.ArrayList;

public class QuestionnaireModel {
	
	private boolean completed;
	private ArrayList<QuestionnaireItem> completedQuestionList;
	private int currentQuestionID;
	private ArrayList<Answer> selectedDomainList;
	
	public QuestionnaireModel(){
		completedQuestionList = new ArrayList<QuestionnaireItem>();
		currentQuestionID = 0;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public ArrayList<QuestionnaireItem> getCompletedQuestionList() {
		return completedQuestionList;
	}

	public void setCompletedQuestionList(ArrayList<QuestionnaireItem> completedQuestionList) {
		this.completedQuestionList = completedQuestionList;
	}

	public int getCurrentQuestionID() {
		return currentQuestionID;
	}

	public void setCurrentQuestionID(int currentQuestionID) {
		this.currentQuestionID = currentQuestionID;
	}

	public ArrayList<Answer> getSelectedDomainList() {
		return selectedDomainList;
	}

	public void setSelectedQuestionDomainList(ArrayList<Answer> selectedDomainList) {
		this.selectedDomainList = selectedDomainList;
	}

	@Override
	public String toString() {
		return "QuestionnaireModel [completed=" + completed + ", completedQuestionList=" + completedQuestionList
				+ ", currentQuestionID=" + currentQuestionID + ", selectedDomainList=" + selectedDomainList + "]";
	}
	
	

	


}
