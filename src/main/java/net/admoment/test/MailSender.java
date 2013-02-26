package net.admoment.test;

import org.apache.log4j.*;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class MailSender implements MailService {
    static final String SDOMEN= "[a-z][a-z[0-9]\u005F\u002E\u002D]*[a-z||0-9]";
    static final Pattern P = Pattern.compile(SDOMEN + "@" + SDOMEN + "\u002E" + SDOMEN);

    private String host = "secure.emailsrvr.com";

    private String login;
    private String password;
    private Set<Address> recipients;
    private Message.RecipientType type;

    private static final Logger log = Logger.getLogger(MailSender.class);

    private MailSender(){

    }


    private Session getSession(){
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
        return session;
    }


    private MimeMessage getMessage(String subject, Set<String> recipients){
        MimeMessage message = null;
        String[] recipients_array = recipients.toArray(new String[recipients.size()]);

        try{
            Address[] array = new Address[checkNotNull(recipients_array).length];
            for (int i = 0; i < recipients_array.length; i++){
                Matcher m =  P.matcher(recipients_array[i]);
                checkArgument(m.matches());
                array[i] = new InternetAddress(recipients_array[i]);
            }
            message = new MimeMessage(this.getSession());
            message.setFrom(new InternetAddress(login));
            message.addRecipients(this.type, array);
            message.setSubject(subject);
        } catch (MessagingException mex) {
            mex.printStackTrace();
            log.error(mex);
        }   catch (NullPointerException e1){
            log.error(e1);
        }
        return message;
    }




    @Override
    public void send(String subject, String HTML, Set<String> recipients) {
        try {
            MimeMessage message = this.getMessage(subject, recipients);

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(checkNotNull(HTML), "text/html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);

            Transport.send(message);
            log.debug("message sent");
            }    catch (MessagingException mex) {
                log.error(mex);
            }
        }


    @Override
    public void send(String subject, String HTML, Set<String> recipients, Set<File> attachments) {
        try {
            MimeMessage message = this.getMessage(subject, recipients);

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(checkNotNull(HTML), "text/html");


            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);


            for(Iterator<File> i = checkNotNull(attachments).iterator(); i.hasNext(); ){
                File item = i.next();
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(item);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(item.getName());
                multipart.addBodyPart(messageBodyPart);
            }
            message.setContent(multipart);

            Transport.send(message);
            log.debug("message sent");
        }    catch (MessagingException mex) {
            log.error(mex);
        }    catch (NullPointerException e){
            log.error(e);
        }
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


    public Message.RecipientType getType() {
        return type;
    }

    public static Builder<MailSender> newBuilder(){
        return new MailBuilder(new MailSender());
    }

    public static class MailBuilder implements Builder<MailSender>{

        private String host = "secure.emailsrvr.com";
        private String login;
        private String password;
        private Message.RecipientType type = Message.RecipientType.TO;
        private final MailSender sender;

        public MailBuilder(MailSender sender){
            this.sender = sender;
        }

        @Override
        public MailSender build() {
            try{
                sender.login = checkNotNull(login);
                sender.password = checkNotNull(password);
                sender.type = checkNotNull(type);
                sender.host = checkNotNull(host);
            }catch (NullPointerException e){
                log.error(e);
            }

            log.debug("MailSender built");
            return this.sender;
        }

        public void withLogin(String login){
            try{
                this.login = checkNotNull(login);
            }   catch (NullPointerException e1){
                log.error(e1);
            }
        }

        public void withPassword(String password){
            try{
                this.password = checkNotNull(password);
            }catch (NullPointerException e1){
                log.error(e1);
            }
        }

        public void withHost(String hostname) {
            try{
                this.host = checkNotNull(hostname);
            }   catch (NullPointerException e1){
                log.error(e1);
            }
        }


        public enum Type{TO, CC, BCC};

        public void withRecipientType(Type type){
            try{
                checkNotNull(type);
                if (type.equals(Type.TO)){
                    this.type = Message.RecipientType.TO;
                } else if (type.equals(Type.CC)){
                    this.type = Message.RecipientType.CC;
                } else if (type.equals(Type.BCC)){
                    this.type = Message.RecipientType.BCC;
                }
            }catch (NullPointerException e1){
                log.error(e1);
            }
        }
    }
}
