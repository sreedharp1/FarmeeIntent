package com.amazonaws.lambda.farmee;

import java.util.HashSet;
import java.util.Set;

import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletV2;
import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

public class FarmeeSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {

	private static final Set<String> supportedApplicationIds;

	static {
		/*
		 * This Id can be found on https://developer.amazon.com/edw/home.html#/ "Edit"
		 * the relevant Alexa Skill and put the relevant Application Ids in this Set.
		 */
		supportedApplicationIds = new HashSet<>();
		supportedApplicationIds.add("amzn1.ask.skill.d1dc75bd-7f94-47e9-8d5b-c4b013bf98a1");
	}
	
	public FarmeeSpeechletRequestStreamHandler() {
		super(new FarmeeSpeechlet(), supportedApplicationIds);
	}
	public FarmeeSpeechletRequestStreamHandler(SpeechletV2 speechlet, Set<String> supportedApplicationIds) {
		super(speechlet, supportedApplicationIds);
	}

	public FarmeeSpeechletRequestStreamHandler(Speechlet speechlet, Set<String> supportedApplicationIds) {
		super(speechlet, supportedApplicationIds);
	}

}
