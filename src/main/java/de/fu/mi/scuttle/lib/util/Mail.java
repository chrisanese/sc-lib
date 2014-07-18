package de.fu.mi.scuttle.lib.util;

import java.util.Collection;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Utility class for sending emails (a convenient wrapper for the javax.mail
 * API).
 * 
 * @author Julian Fleischer
 * @since 2013-11-17
 */
public final class Mail {

    private Mail() {
    }

    /**
     * Sends an email message.
     * 
     * @param host
     *            The host to send the mail from.
     * @param username
     *            The username used for authentication with the outgoing mail
     *            server.
     * @param password
     *            The password used for authentication with the outgoing server.
     * @param protocol
     *            The protocol used to connect with the mail server.
     * @param message
     *            The message to send.
     * @throws MessagingException
     *             If the message could not be delivered.
     */
    public static void sendMail(final String host, final String username,
            final String password,
            final MailProtocol protocol, final MailMessage message)
            throws MessagingException {

        sendMail(host, username, password, protocol, message.getFrom(),
                message.getSubject(), message.getText(),
                message.getToRecipients(), message.getCcRecipients(),
                message.getBccRecipients());
    }

    /**
     * Sends a plain text email message.
     * 
     * @param host
     *            The host to send the mail from.
     * @param username
     *            The username used for authentication with the outgoing mail
     *            server.
     * @param password
     *            The password used for authentication with the outgoing server.
     * @param protocol
     *            The protocol used to connect with the mail server.
     * @param from
     *            The mail address to use as the sending address.
     * @param subject
     *            The subject to use.
     * @param text
     *            The body of the plain text message.
     * @param tos
     *            The To recipients.
     * @param ccs
     *            The Cc recipients.
     * @param bccs
     *            The Bcc recipients.
     * @throws MessagingException
     *             If the message could not be delivered.
     */
    public static void sendMail(
            final String host,
            final String username,
            final String password,
            final MailProtocol protocol,
            final String from,
            final String subject,
            final String text,
            final Collection<String> tos,
            final Collection<String> ccs,
            final Collection<String> bccs) throws MessagingException {
        sendMail(host, username, password, protocol, from, subject, text, null,
                tos, ccs, bccs);
    }

    /**
     * Sends an email message.
     * 
     * @param host
     *            The host to send the mail from.
     * @param username
     *            The username used for authentication with the outgoing mail
     *            server.
     * @param password
     *            The password used for authentication with the outgoing server.
     * @param protocol
     *            The protocol used to connect with the mail server.
     * @param from
     *            The mail address to use as the sending address.
     * @param subject
     *            The subject to use.
     * @param text
     *            The body of the plain text message.
     * @param mimeType
     *            The mime type of the message.
     * @param tos
     *            The To recipients.
     * @param ccs
     *            The Cc recipients.
     * @param bccs
     *            The Bcc recipients.
     * @throws MessagingException
     *             If the message could not be delivered.
     */
    public static void sendMail(
            final String host,
            final String username,
            final String password,
            final MailProtocol protocol,
            final String from,
            final String subject,
            final String text,
            final String mimeType,
            final Collection<String> tos,
            final Collection<String> ccs,
            final Collection<String> bccs) throws MessagingException {

        final Properties properties = System.getProperties();
        properties.setProperty("mail." + protocol + ".host", host);
        properties.setProperty("mail." + protocol + ".auth", "true");
        properties.setProperty("mail." + protocol + ".localhost", host);

        final Session session = Session.getDefaultInstance(properties);
        final MimeMessage message = new MimeMessage(session);

        message.setFrom(new InternetAddress(from));
        for (final String to : tos) {
            message.addRecipient(
                    Message.RecipientType.TO,
                    new InternetAddress(to));
        }
        for (final String cc : ccs) {
            message.addRecipient(
                    Message.RecipientType.CC,
                    new InternetAddress(cc));
        }
        for (final String bcc : bccs) {
            message.addRecipient(
                    Message.RecipientType.BCC,
                    new InternetAddress(bcc));
        }
        message.setSubject(subject);
        if (mimeType == null) {
            message.setText(text);
        } else {
            message.setContent(text, mimeType);
        }

        final Transport t = session.getTransport(protocol.toString());
        try {
            t.connect(username, password);
            t.sendMessage(message, message.getAllRecipients());
        } finally {
            t.close();
        }
    }
}
