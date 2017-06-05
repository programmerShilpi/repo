package bank;

public class AlertServiceImpl implements AlertService {

    /**
     * Send an alert.
     * 
     * @param message the alert message describing the problem
     */
    public void alert (String message){
    	System.out.println(message);
    }
}

