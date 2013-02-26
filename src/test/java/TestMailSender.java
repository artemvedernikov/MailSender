import net.admoment.test.MailSender;
import org.junit.Test;

import javax.mail.Message;

import static junit.framework.Assert.assertTrue;


public class TestMailSender {

    @Test

    public void testBuild(){
        MailSender ms = MailSender.newBuilder().withLogin("artem@admoment.ru").withPassword("!QAZ1qaz").withRecipientType(MailSender.MailBuilder.Type.TO).build();
        assertTrue(ms.getLogin().equals("artem@admoment.ru"));
        assertTrue(ms.getPassword().equals("!QAZ1qaz"));
        assertTrue(ms.getHost().equals("secure.emailsrvr.com"));
        assertTrue(ms.getType().equals(Message.RecipientType.TO));
    }

}
