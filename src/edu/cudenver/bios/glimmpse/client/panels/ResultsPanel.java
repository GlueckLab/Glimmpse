package edu.cudenver.bios.glimmpse.client.panels;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.cudenver.bios.glimmpse.client.Glimmpse;
import edu.cudenver.bios.glimmpse.client.StudyDesignManager;

public class ResultsPanel extends WizardStepPanel
{
	protected StudyDesignManager manager;
	
	private static final int STATUS_CODE_OK = 200;
	private static final int STATUS_CODE_CREATED = 201;
	private static final String POWER_URL = "/webapps/power/power";
    private static final String SAMPLE_SIZE_URL = "/webapps/power/samplesize";
	private static final String EFFECT_SIZE_URL = "/webapps/power/difference";
	
    public ResultsPanel(StudyDesignManager manager)
    {
    	super(Glimmpse.constants.stepsLeftResults());
    	this.manager = manager;
        VerticalPanel panel = new VerticalPanel();
        
        initWidget(panel);
    }
    
    public void reset()
    {
    	// TODO:  clear the screen
    }

    @Override
    public void onEnter()
    {
    	reset();
    	sendPowerRequest();
    }
    
    private void showWorkingDialog()
    {
    	
    }
    
    private void hideWorkingDialog()
    {
    	
    }
    
    private void showError(String message)
    {
    	
    }
    
    private void showResults(String resultXML)
    {
    	
    }
    
    private void sendPowerRequest()
    {
    	showWorkingDialog();
    	String requestEntityBody = manager.getPowerRequestXML();
    	Window.alert(requestEntityBody);
    	RequestBuilder builder = null;
    	switch(manager.getSolvingFor())
    	{
    	case POWER:
    		builder = new RequestBuilder(RequestBuilder.POST, POWER_URL);
    		break;
    	case SAMPLE_SIZE:
    		builder = new RequestBuilder(RequestBuilder.POST, SAMPLE_SIZE_URL);
    		break;
    	case EFFECT_SIZE:
    		builder = new RequestBuilder(RequestBuilder.POST, EFFECT_SIZE_URL);
    		break;
    	}

    	try 
    	{
    		builder.setHeader("Content-Type", "text/xml");
    		builder.sendRequest(requestEntityBody, new RequestCallback() {

    			public void onError(Request request, Throwable exception) 
    			{
    				hideWorkingDialog();
    				showError("Calculation failed: " + exception.getMessage());	
    			}

    			public void onResponseReceived(Request request, Response response) 
    			{
    				hideWorkingDialog();
    				if (STATUS_CODE_OK == response.getStatusCode() ||
    						STATUS_CODE_CREATED == response.getStatusCode()) 
    				{
    					showResults(response.getText());
    				} 
    				else 
    				{
    					showError("Calculation failed: [HTTP STATUS " + 
    					        response.getStatusCode() + "] " + response.getText());
    				}
    			}
    		});
    	} 
    	catch (Exception e) 
    	{
			hideWorkingDialog();
			showError("Failed to send the request: " + e.getMessage());
    	}
    }    
    
}
