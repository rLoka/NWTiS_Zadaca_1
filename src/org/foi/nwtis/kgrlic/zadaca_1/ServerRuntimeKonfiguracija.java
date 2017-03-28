package org.foi.nwtis.kgrlic.zadaca_1;

/**
 *
 * @author rloka
 */
public class ServerRuntimeKonfiguracija {

    private String status;
    private static final ServerRuntimeKonfiguracija serverRuntimeKonfiguracija = new ServerRuntimeKonfiguracija();

    /**
     *
     */
    public ServerRuntimeKonfiguracija() {
        this.status = "STARTED";
    }
    
    /**
     *
     * @return
     */
    public static ServerRuntimeKonfiguracija getInstance(){
        return serverRuntimeKonfiguracija;
    }

    /**
     *
     * @return
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     */
    public void setStatus(String status) {
        if("STARTED".equals(status) || "PAUSED".equals(status) || "STOPPED".equals(status)){
            this.status = status;
        }        
    }
}
