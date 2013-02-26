package net.admoment.test;

import javax.mail.MessagingException;
import java.io.File;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: artem
 * Date: 22.02.13
 * Time: 16:46
 * To change this template use File | Settings | File Templates.
 */
public interface MailService {

    void send(String subject, String HTML, Set<String> recipients);
    void send(String subject, String HTML, Set<String> recipients, Set<File> attachments);
}
