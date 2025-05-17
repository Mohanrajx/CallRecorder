package com.example.callrecorder;

import android.content.Context;
import android.util.Log;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailSender {
    public static void sendEmail(Context context, String senderEmail, String appPassword, 
            String recipientEmail, String subject, String body, String attachmentPath) {
        
        new Thread(() -> {
            try {
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

                Session session = Session.getInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(senderEmail, appPassword);
                    }
                });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(senderEmail));
                message.setRecipients(Message.RecipientType.TO, 
                    InternetAddress.parse(recipientEmail));
                message.setSubject(subject);

                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText(body);

                MimeMultipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);

                if (attachmentPath != null) {
                    MimeBodyPart attachmentPart = new MimeBodyPart();
                    attachmentPart.attachFile(attachmentPath);
                    multipart.addBodyPart(attachmentPart);
                }

                message.setContent(multipart);
                Transport.send(message);

                if (attachmentPath != null) {
                    new File(attachmentPath).delete();
                }

                Log.d("EmailSender", "Email sent successfully");
            } catch (Exception e) {
                Log.e("EmailSender", "Error sending email", e);
            }
        }).start();
    }
}
