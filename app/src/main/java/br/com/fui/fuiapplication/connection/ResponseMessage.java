package br.com.fui.fuiapplication.connection;

/**
 * Created by guilherme on 19/07/17.
 */

public class ResponseMessage {

    private int code;
    private String message;
    private String body;

    /**
     * Creates a default ResponseMessage object
     */
    public ResponseMessage(){
        this.code = 0;
        this.message = "";
        this.body = "";
    }

    /**
     * Creates a ResponseMessage object with specified attributes
     * @param code      Response's code
     * @param message   Response's message
     * @param body      Response's body
     */
    public ResponseMessage(int code, String message, String body){
        this.code = code;
        this.message = message;
        this.body = body;
    }

    /**
     *
     * @return  Response's code
     */
    public int getCode(){
        return this.code;
    }

    /**
     *
     * @return Response's message
     */
    public String getMessage(){ return this.message; }

    /**
     *
     * @return Response's body
     */
    public String getBody(){
        return this.body;
    }

}
