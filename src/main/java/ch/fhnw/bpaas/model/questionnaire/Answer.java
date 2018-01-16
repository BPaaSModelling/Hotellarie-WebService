package ch.fhnw.bpaas.model.questionnaire;

public class Answer {

	private String answerLabel;
	private String answerID;

	public Answer(String answerID, String answerLabel) {
		this.setAnswerID(answerID);
		this.setAnswerLabel(answerLabel);
	}

	public Answer() {
		// TODO Auto-generated constructor stub
	}

	public void setAnswerLabel(String answerLabel) {
		this.answerLabel = answerLabel;
	}

	public void setAnswerID(String answerID) {
		this.answerID = answerID;
	}
	
	public String getAnswerID() {
		return answerID;
	}
	
	public String getAnswerLabel() {
		return answerLabel;
	}

	@Override
	public String toString() {
		return "Answer [answerLabel=" + answerLabel + ", answerID=" + answerID + "]";
	}
	
	
//	public String toString(){
//		StringBuilder sb = new StringBuilder();
//		sb.append("=Answer=\n");
//		sb.append("AnswerID: \t" +getAnswerID()+"\n");
//		sb.append("AnswerLabel: \t" +getAnswerLabel()+"\n");
//		return sb.toString();
//	}
}
