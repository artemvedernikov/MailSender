package net.admoment.test;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

public class MailSender implements MailService {

    private String host = "secure.emailsrvr.com";

    private String login;
    private String password;
    private String subject;
    private ArrayList<String> recipients;
    private String text;
    private String HTML;
    private ArrayList<File> attachments;


    private MailSender(){

    }

    @Override
    public void send() {
        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.auth", "true");

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                login, password);
                    }
                });

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(login));

            // Set To: header field of the header.
            Address[] array;
            array = new InternetAddress[recipients.length];
            for (int i = 0; i < recipients.length; i ++){
                array[i] = new InternetAddress(recipients[i]);
            }

            message.addRecipients(Message.RecipientType.BCC, array);  //no bcc but different

            // Set Subject: header field
            message.setSubject(this.subject);

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Fill the message
            if (this.text != null){
                messageBodyPart.setText(text);
            }
            if (this.HTML != null){
                messageBodyPart.setContent(this.HTML, "text/html");
            }


            // Create a multipar message
            Multipart multipart = new MimeMultipart();
            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            if (attachments != null){
                for (int i = 0; i < attachments.length; i++){
                    messageBodyPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(attachments[i]);
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(attachments[i].getName());
                    multipart.addBodyPart(messageBodyPart);
                }
            }


            // Send the complete message parts
            message.setContent(multipart);

            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }

    @Override
    public void addSubject(String string) {
        this.subject = subject;
    }

    @Override
    public void addRecipient(String... recipient) {
        String[]rec = new String[this.recipients.length + recipient.length];
        for (int i = 0; i < this.recipients.length; i ++) {
            rec[i] = this.recipients[i];
        }
        for (int i = 0; i < recipients.length; i ++){
            rec[i + this.recipients.length] = recipients[i];
        }
        this.recipients = rec;
    }

    @Override
    public void addBody(String text, boolean isHTML) {
        if (isHTML){
            if (this.HTML == null){
                this.HTML = text;
            } else {
                StringBuffer sbuf = new StringBuffer(this.HTML);
                this.HTML = new String(sbuf.append(text));
            }
        } else {
            if (this.text == null){
                this.text = text;
            } else {
                StringBuffer sbuf = new StringBuffer(this.text);
                this.text = new String(sbuf.append(text));
            }
         }
    }

    @Override
    public void addAttachment(File filename) {
        if (this.attachments == null) {
            this.attachments = new File[1];
            this.attachments[0] = filename;
        } else{
            File[] att = new File[this.attachments.length + 1];
            for (int i = 0; i < this.attachments.length; i++) {
                att[i] = this.attachments[i];
            }
            att[this.attachments.length] = filename;
            this.attachments = att;

        }

    }

    @Override
    public void withHost(String hostname) {
        this.host = hostname;
    }


    public String getHost() {
        return host;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getSubject() {
        return subject;
    }

    public ArrayList<String> getRecipients() {
        return recipients;
    }

    public String getText() {
        return text;
    }

    public String getHTML() {
        return HTML;
    }

    public ArrayList<File> getAttachments() {
        return attachments;
    }

    public static Builder<MailService> newBuilder(){
        return new MailBuilder(new MailSender());
    }

    public static class MailBuilder implements Builder<MailService>{

        private String login;
        private String password;
        private ArrayList<String> recipient;
        final MailSender sender;

        public MailBuilder(MailSender sender){
            this.sender = sender;
        }

        @Override
        public MailService build() {
            if ((login != null) && (password != null) && (recipient != null)) {
                return sender;
            }  else {
                throw new NullPointerException();
            }
        }

        public void withLogin(String login){
            this.login = login;
        }

        public void withPassword(String password){
            this.password = password;
        }

        public void withRecipient(String... recipient){
            this.recipient = this.recipient.add(recipient);

        }




    }
}
