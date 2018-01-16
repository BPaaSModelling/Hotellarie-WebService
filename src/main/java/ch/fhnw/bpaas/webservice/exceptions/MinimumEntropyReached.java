package ch.fhnw.bpaas.webservice.exceptions;

public class MinimumEntropyReached extends Exception {

	public MinimumEntropyReached(String msg) {
		super(msg);
		System.out.println("Entropy is too low");
	}

}
