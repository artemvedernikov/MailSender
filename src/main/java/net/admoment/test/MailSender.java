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
    static final String SDOMEN= "[a-z][a-z[0-9]\\-\\.\\_]*[a-z||0-9]";
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


    private MimeMessage getMessage(String subject, Set<String> recipients) {
        MimeMessage message = null;
        String[] RecipientsArray = recipients.toArray(new String[recipients.size()]);

        try{
            Address[] array = new Address[checkNotNull(RecipientsArray).length];
            for (int i = 0; i < RecipientsArray.length; i++){
                Matcher m =  P.matcher(RecipientsArray[i]);
                checkArgument(m.matches());
                array[i] = new InternetAddress(RecipientsArray[i]);
            }
            message = new MimeMessage(this.getSession());
            message.setFrom(new InternetAddress(login));
            message.addRecipients(this.type, array);
            message.setSubject(subject);
        } catch (MessagingException mex) {
            throw new MailSenderException(mex);
        }   catch (NullPointerException e1){
            throw e1;
        }
        return message;
    }




    @Override
    public void send(String subject, String BodyHtml, Set<String> recipients) {
        try {
            MimeMessage message = this.getMessage(subject, recipients);

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(checkNotNull(BodyHtml), "text/html");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);

            Transport.send(message);
            log.debug("message sent");
            } catch (MessagingException mex) {
                throw new MailSenderException(mex);

            }
        }


    @Override
    public void send(String subject, String BodyHtml, Set<String> recipients, Set<File> attachments)  {
        try {
            MimeMessage message = this.getMessage(subject, recipients);

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(checkNotNull(BodyHtml), "text/html");


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
            throw new MailSenderException(mex);
        }    catch (NullPointerException e){
            throw e;

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
        return new MailBuilder();
    }

    public static class MailBuilder implements Builder<MailSender>{

        private String host = "secure.emailsrvr.com";
        private String login;
        private String password;
        private Message.RecipientType type = Message.RecipientType.TO;


        @Override
        public MailSender build() {
            MailSender sender = null;
            try{
                sender = new MailSender();
                sender.login = checkNotNull(login);
                sender.password = checkNotNull(password);
                sender.type = checkNotNull(type);
                sender.host = checkNotNull(host);
            }catch (NullPointerException e){
                throw e;
            }

            log.debug("MailSender built");
            return sender;
        }

        public MailBuilder withLogin(String login){
            try{
                this.login = checkNotNull(login);
            }   catch (NullPointerException e1){
                throw e1;
            }
            return this;
        }

        public MailBuilder withPassword(String password){
            try{
                this.password = checkNotNull(password);
            }catch (NullPointerException e1){
                throw e1;
            }
            return this;
        }

        public MailBuilder withHost(String hostname) {
            try{
                this.host = checkNotNull(hostname);
            }   catch (NullPointerException e1){
                throw e1;
            }
            return this;
        }


        public enum Type{TO, CC, BCC};

        public MailBuilder withRecipientType(Type type){
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
                throw e1;
            }
            return this;
        }
    }
}
