package net.admoment.test;

import org.apache.log4j.*;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
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
    private String subject;
    private Set<Address> recipients;
    private String text;
    private String HTML;
    private Set<File> attachments;
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

    private MimeMessage getMessage(){
        MimeMessage message = null;
        try {
            // Create a default MimeMessage object.
            message = new MimeMessage(this.getSession());

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(login));

            // Set To: header field of the header.
            Address[] array = recipients.toArray(new Address[recipients.size()]);
            message.addRecipients(this.type, array);  //no bcc but different

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


            for(Iterator<File> i = attachments.iterator(); i.hasNext(); ){
                File item = i.next();
                messageBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(item);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(item.getName());
                multipart.addBodyPart(messageBodyPart);
            }

            // Send the complete message parts
            message.setContent(multipart);

        }catch (MessagingException mex) {
            mex.printStackTrace();
            log.error(mex);
        }
        return message;
    }

    @Override
    public void send() {
         try{
            // Send message
            Transport.send(this.getMessage());
            log.debug("message sent");
        } catch (MessagingException mex) {
            log.error(mex);
        }
    }

    @Override
    public void addSubject(String string) {
        try{
            this.subject = checkNotNull(subject);
        }   catch (NullPointerException e1){
            log.error(e1);
        }
    }

    @Override
    public void addRecipient(String... recipient) {
        try{
            for (int i = 0; i < checkNotNull(recipient).length; i++){
                Matcher m =  P.matcher(recipient[i]);
                checkArgument(m.matches());
                this.recipients.add(new InternetAddress(recipient[i]));
            }
        }  catch (AddressException e) {
            log.error(e);
        }   catch (NullPointerException e1){
            log.error(e1);
        }
    }

    @Override
    public void addBody(String text, boolean isHTML) {
        if (isHTML){
            if (this.HTML == null){
                this.HTML = checkNotNull(text);
            } else {
                StringBuffer sbuf = new StringBuffer(this.HTML);
                this.HTML = new String(sbuf.append(checkNotNull(text)));
            }
        } else {
            if (this.text == null){
                this.text = checkNotNull(text);
            } else {
                StringBuffer sbuf = new StringBuffer(this.text);
                this.text = new String(sbuf.append(checkNotNull(text)));
            }
         }
    }

    @Override
    public void addAttachment(File filename) {
        try{
            this.attachments.add(checkNotNull(filename));
        }catch (NullPointerException e1){
            log.error(e1);
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

    public String getSubject() {
        return subject;
    }

    public Set<Address> getRecipients() {
        return recipients;
    }

    public String getText() {
        return text;
    }

    public String getHTML() {
        return HTML;
    }

    public Set<File> getAttachments() {
        return attachments;
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
        private Set<Address> recipients;
        private Message.RecipientType type = Message.RecipientType.TO;
        final MailSender sender;

        public MailBuilder(MailSender sender){
            this.sender = sender;
        }

        @Override
        public MailSender build() {
            try{
                sender.login = checkNotNull(login);
                sender.password = checkNotNull(password);
                sender.recipients = checkNotNull(recipients);
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

        public void withRecipient(String... recipient){

            try{
                for (int i = 0; i < checkNotNull(recipient).length; i++){
                    Matcher m =  P.matcher(recipient[i]);
                    checkArgument(m.matches());
                    this.recipients.add(new InternetAddress(recipient[i]));
                }
            }  catch (AddressException e) {
               log.error(e);
            }   catch (NullPointerException e1){
               log.error(e1);
            }
        }

        public void withRecipientType(String type){
            try{
                if (checkNotNull(type).equals("TO")){
                    this.type = Message.RecipientType.TO;
                } else if (checkNotNull(type).equals("CC")){
                    this.type = Message.RecipientType.CC;
                } else if (checkNotNull(type).equals("BCC")){
                    this.type = Message.RecipientType.BCC;
                }
            }catch (NullPointerException e1){
                log.error(e1);
            }
        }
    }
}
