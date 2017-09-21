package com.amazonaws.lambda.farmee;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.SsmlOutputSpeech;
import com.amazonaws.util.StringUtils;

public class FarmeeSpeechlet implements Speechlet {
    private static final Logger log = LoggerFactory.getLogger(FarmeeSpeechlet.class);

	private static final Map<String,String> farmeeResponsesMap;

	static {
		farmeeResponsesMap = new HashMap<>();
		farmeeResponsesMap.put("help","You can ask like, what does a Cow say, or, you can say exit. Which Farm animal you would like to speak with?");
		farmeeResponsesMap.put("animalNotFound","uh oh, Could not find the animal you are looking for on the farm !! Which animal do you want to hear on the farm? A Cow, Dog, Pig, Cat, Sheep, Duck, Crow or a Chicken?");
		farmeeResponsesMap.put("bye","Good Bye !! Hope you had fun with the Farm Animals !!");
		farmeeResponsesMap.put("cow","<speak><s>The Cow says, </s> <emphasis level=\"strong\"><say-as interpret-as=\"interjection\">Moo, Moo, Moo</say-as></emphasis>,  ");
		farmeeResponsesMap.put("dog","<speak><s>The Dog says, </s> <emphasis level=\"strong\"><say-as interpret-as=\"interjection\">Woof Woof, Woof</say-as></emphasis>, ");
		farmeeResponsesMap.put("pig","<speak><s>The Pig says, </s> <say-as interpret-as=\"interjection\">Oink, Oink, Oink Oink</say-as>, ");
		farmeeResponsesMap.put("cat","<speak><s>The Cat says, </s> <emphasis level=\"strong\"><say-as interpret-as=\"interjection\">Meow, Meow, Meow</say-as></emphasis>, ");
		farmeeResponsesMap.put("sheep","<speak><s>The Sheep says, </s> <emphasis level=\"strong\"><say-as interpret-as=\"interjection\">Baa, Baa, Baa</say-as></emphasis>,  ");
		farmeeResponsesMap.put("duck","<speak><s>The Duck says, </s> <say-as interpret-as=\"interjection\">Quack Quack, Quack</say-as>, ");
		farmeeResponsesMap.put("crow","<speak><s>The Crow says, </s> <say-as interpret-as=\"interjection\">Caw, Caw, Caw</say-as>, ");
        farmeeResponsesMap.put("chicken","<speak><s>The Chicken says, </s> <say-as interpret-as=\"interjection\">Cluck Cluck, Cluck</say-as>,");
		farmeeResponsesMap.put(null,"Sorry, Could not find the animal you are looking for on the farm !! You can ask for a Cow, Dog, Pig, Cat, Sheep, Duck, Crow or a Chicken. Which animal would you like to find? ");
	}
	

	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException {
		log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;

        if ("FarmeeIntent".equals(intentName)) {
            return getFarmeeResponse(intent);
        } else if ("AMAZON.HelpIntent".equals(intentName)) {
            return getHelpResponse();
        } else if ("AMAZON.StopIntent".equals(intentName) || "AMAZON.CancelIntent".equals(intentName)) {
            PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText("Goodbye. See you real soon");
            return SpeechletResponse.newTellResponse(outputSpeech);
        }else {
            throw new SpeechletException("Invalid Intent");
        }
	}

	@Override
	public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
		log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        return getHelpResponse();

	}

	@Override
	public void onSessionEnded(SessionEndedRequest request, Session session) throws SpeechletException {
		log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

	}

	@Override
	public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
		 log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
	                session.getSessionId());

	}
	
	 /**
     * Gets a random new fact from the list and returns to the user.
     */
    private SpeechletResponse getFarmeeResponse(Intent currentIntent) {
         Slot animalSlot = currentIntent.getSlot("Animal");
         String animalName= animalSlot.getValue();
         
         if("help".equalsIgnoreCase(animalName)) {
        	 	getHelpResponse();
         }
        // Get a random space fact from the space facts list
        // Create speech output
        String speechText = farmeeResponsesMap.get(animalName);
        if(StringUtils.isNullOrEmpty(speechText)) {
        		speechText= farmeeResponsesMap.get("animalNotFound");
        		return getRepromptResponse(speechText);
        }
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Farm Animals");
        card.setContent(animalName);

        // Create the plain text output.
        SsmlOutputSpeech speechOutput= new SsmlOutputSpeech();
        speechOutput.setSsml(speechText+" Which farm animal would you like to hear next? </speak>");
        
        // Create reprompt
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText("Which farm animal would you like to hear next?");
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);
        return SpeechletResponse.newAskResponse(speechOutput, reprompt, card);
    }

    /**
     * Returns a response for the help intent.
     */
    private SpeechletResponse getHelpResponse() {

        String speechText="You can ask like, what does a Cow say. Here is the list of animals you can ask for: "
        		+ "Cow, Dog, Cat, Sheep, Duck, Chicken and Pig. Which animal would you like to hear?";
        return getRepromptResponse(speechText);
    }
    
    private SpeechletResponse getRepromptResponse(String speechText) {
    	    // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);
        
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Farm Animals");
        card.setContent(speechText);
        
        // Create reprompt
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);
        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

}
