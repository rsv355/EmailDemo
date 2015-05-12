package krishna.android.com.emaildemo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;

import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class MainActivity extends ActionBarActivity {

    private static final int CAMERA_REQUEST = 500;
    private static final int GALLERY_REQUEST = 300;
    final CharSequence[] items = { "Take Photo", "Choose from Gallery" };
    File imgPath;

private Button btn,btn2;
    private ImageView img;
    private TextView txt;
    private static final String username = "softeng.krishna@gmail.com";
    private static final String password = "rinkuamit";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button)findViewById(R.id.btn);
        btn2 = (Button)findViewById(R.id.btn2);
        txt = (TextView)findViewById(R.id.txt);
        img = (ImageView)findViewById(R.id.img);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMail("softeng.krishna@gmail.com","test","working");
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Upload Picture");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Take Photo")) {
                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(takePicture, CAMERA_REQUEST);
                            Log.e("Camera ","exit");

                        } else if (items[item].equals("Choose from Gallery")) {
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                            startActivityForResult(pickPhoto , GALLERY_REQUEST);
                        }
                    }
                });
                builder.show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST) {
            if (resultCode == RESULT_OK) {

                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                img.setImageBitmap(thumbnail);
                txt.setText(""+data.getExtras().get("data"));
            }
            else{
                Toast.makeText(MainActivity.this,"Error to load image from camera",Toast.LENGTH_LONG).show();
            }
        }else if(requestCode == GALLERY_REQUEST){
            if (resultCode == RESULT_OK) {

                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                txt.setText(""+picturePath);

                final Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                img.setImageBitmap(thumbnail);
                 imgPath = new File(picturePath);
            }
            else{
                Toast.makeText(MainActivity.this,"Error to load image from gallery",Toast.LENGTH_LONG).show();
            }
        }
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



        // for sending single attachments

        Multipart multipart = new MimeMultipart();

          MimeBodyPart messageBodyPart = new MimeBodyPart();
          DataSource source = new FileDataSource(new File(imgPath.getAbsolutePath()));
          messageBodyPart.setDataHandler(new DataHandler(source));
          messageBodyPart.setFileName("helpme.jpg");
          messageBodyPart.setDisposition(MimeBodyPart.ATTACHMENT);
          messageBodyPart.setHeader("Content-ID", "<vogue>");
          multipart.addBodyPart(messageBodyPart);

         message.setContent(multipart);



       // for sending multiple attachments

      /*  Multipart multipart = new MimeMultipart("mixed");

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(new File(imgPath.getAbsolutePath()));
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName("Img1.jpg");
            multipart.addBodyPart(messageBodyPart);

        MimeBodyPart messageBodyPart2 = new MimeBodyPart();
        DataSource source2 = new FileDataSource(new File(imgPath.getAbsolutePath()));
        messageBodyPart2.setDataHandler(new DataHandler(source2));
        messageBodyPart2.setFileName("Img2.jpg");
        multipart.addBodyPart(messageBodyPart2);

        message.setContent(multipart);*/











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
            progressDialog.setCancelable(false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            Toast.makeText(MainActivity.this,"Mail Sent",Toast.LENGTH_LONG).show();
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
