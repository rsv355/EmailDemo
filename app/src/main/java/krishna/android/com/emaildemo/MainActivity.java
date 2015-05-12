package krishna.android.com.emaildemo;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class MainActivity extends ActionBarActivity {
private Button btn;
    private static final String username = "softeng.krishna@gmail.com";
    private static final String password = "rinkuamit";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button)findViewById(R.id.btn);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMail("softeng.krishna@gmail.com","test","working");
            }
        });
    }

    private void sendMail(String email, String subject, String messageBody) {
        Session session = createSessionObject();

        try {
            Message message = createMessage(email, subject, messageBody, session);
            new SendMailTask().execute(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private Message createMessage(String email, String subject, String messageBody, Session session) throws MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("softeng.krishna@gmail.com", "test attachment"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress("softeng.krishna@gmail.com"));
//        message.addRecipient(Message.RecipientType.TO, new InternetAddress("niravnvk@gmail.com"));
//        message.addRecipient(Message.RecipientType.TO, new InternetAddress("nkdroidworld@gmail.com"));
//        message.addRecipient(Message.RecipientType.TO, new InternetAddress("nksoftech@gmail.com"));

        message.setSubject(subject);
        message.setText(messageBody);
//        Multipart multipart = new MimeMultipart();
//
//
//        MimeBodyPart messageBodyPart = new MimeBodyPart();
//        DataSource source = new FileDataSource(new File(audiofile.getAbsolutePath()));
//        messageBodyPart.setDataHandler(new DataHandler(source));
//        messageBodyPart.setFileName("helpme.mp3");
//        messageBodyPart.setDisposition(MimeBodyPart.ATTACHMENT);
//        messageBodyPart.setHeader("Content-ID", "<vogue>");
//        multipart.addBodyPart(messageBodyPart);
//
//        message.setContent(multipart);

//        Transport.send(message);

        return message;
    }

    private Session createSessionObject() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        return Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    private class SendMailTask extends AsyncTask<Message, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this, "Please wait", "Sending mail", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
            } catch (MessagingException e) {
                Log.e("exc",e.toString());
                e.printStackTrace();
            }
            return null;
        }
    }


    //end of main class
}
