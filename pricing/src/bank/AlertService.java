package bank;

public interface AlertService  {

    /**
     * Send an alert.
     * 
     * @param message the alert message describing the problem
     */
    void alert (String message);
}

