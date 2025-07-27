package com.nus.oribital.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.Version;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailUtil {

    public static void sendMail(String from, String to, String subject, String body) {
        // String password = "your_email_password";

        Properties props = new Properties();
        props.put("mail.smtp.host", "localhost");
        props.put("mail.smtp.port", "8025");
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.starttls.enable", "true");

        /*Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });*/
        try {
            Session session = Session.getInstance(props);
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=utf-8");
            System.out.println("Email body: " + body);
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            //throw new RuntimeException(e);
        }
    }

    public static String renderInviteEmail(String inviter, String inviterEmail, String username, String budgetName, String inviteLink) {
        try {
            Configuration cfg = new Configuration(new Version("2.3.31"));
            cfg.setClassForTemplateLoading(EmailUtil.class, "/templates");
            Template template = cfg.getTemplate("invite_email.ftl");

            Map<String, Object> model = new HashMap<>();
            Map<String, Object> invitee = new HashMap<>();
            invitee.put("username", username);
            model.put("invitee", invitee);
            model.put("budgetName", budgetName);
            model.put("inviteLink", inviteLink);
            model.put("inviter", inviter);
            model.put("inviterEmail", inviterEmail);

            StringWriter out = new StringWriter();
            template.process(model, out);
            return out.toString();
        } catch (IOException | freemarker.template.TemplateException e) {
            e.printStackTrace();
            return "Error generating email body.";
        }
    }

}
