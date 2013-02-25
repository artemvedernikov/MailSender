package net.admoment.test;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;


public class Program {
    public static void main(String[] args) {
        // Recipient's email ID needs to be mentioned.
        String to0 = "vlad@admoment.ru";
        String to1 = "nikolay@admoment.ru";
        String to2 = "artem@admoment.ru";

        // Sender's email ID needs to be mentioned
        String from = "noreply@admoment.ru";

        // Assuming you are sending email from localhost
        String host = "secure.emailsrvr.com";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.auth", "true");

        // Get the default Session object.
        //Session session = Session.getDefaultInstance(properties);
        Session session = Session.getDefaultInstance(properties,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                "noreply@admoment.ru", "@ostok40x");
                    }
                });

        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            Address[] array;
            array = new InternetAddress[3];
            array[0] = new InternetAddress(to0);
            array[1] = new InternetAddress(to1);
            array[2] = new InternetAddress(to2);

            message.addRecipients(Message.RecipientType.BCC, array);

            // Set Subject: header field
            message.setSubject("Test github link");

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Fill the message
            messageBodyPart.setText("https://github.com/artemkorolev/MailSender/pull/1");

            // Create a multipar message
            Multipart multipart = new MimeMultipart();
            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            // Part two is attachment
            messageBodyPart = new MimeBodyPart();
            String filename = "/attachment.txt";
            String fullpath = Thread.currentThread().getClass().getResource(filename).getPath();

            DataSource source = new FileDataSource(fullpath);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filename);
            multipart.addBodyPart(messageBodyPart);

            // Send the complete message parts
            message.setContent(multipart);

            // Send message
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}