package net.admoment.test;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: artem
 * Date: 22.02.13
 * Time: 16:46
 * To change this template use File | Settings | File Templates.
 */
public interface MailService {

    void send();
    void addSubject(String string);
    void addRecipient(String ... recipient);
    void addBody(String text, boolean isHTML);
    void addAttachment(File filename);
    void withHost (String hostname);
}
