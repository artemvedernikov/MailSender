package net.admoment.test;


public class MailSenderException extends RuntimeException {

    public MailSenderException(){}

    public MailSenderException(Throwable cause){
        super(cause);
    }

    public MailSenderException(String message, Throwable cause){
        super(message, cause);
    }

}
