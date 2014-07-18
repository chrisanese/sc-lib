package de.fu.mi.scuttle.lib.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Strings;

/**
 * A MailMessage which can be sent using {@link Mail}.
 * 
 * @author Julian Fleischer
 * @since 2013-11-17
 */
public class MailMessage implements Serializable {

    /**
     * Legacy version uid.
     */
    private static final long serialVersionUID = -7488172314897699311L;

    private String from = null;
    private final String subject;
    private final String text;
    private final String mimeType;

    private final List<String> recipients = new ArrayList<String>();
    private final List<String> ccRecipients = new ArrayList<String>();
    private final List<String> bccRecipients = new ArrayList<String>();

    /**
     * 
     * @param from
     *            The address to use as the sending address.
     * @param subject
     *            The subject of the message.
     * @param text
     *            The text of the message.
     * @param mimeType
     *            The mime type of the message.
     */
    public MailMessage(final String from, final String subject,
            final String text, final MailMimeType mimeType) {
        if (Strings.isNullOrEmpty(from)) {
            throw new IllegalArgumentException("from may not be null nor empty");
        }
        if (Strings.isNullOrEmpty(subject)) {
            throw new IllegalArgumentException(
                    "The subject may not be null nor empty");
        }
        if (text == null) {
            throw new IllegalArgumentException(
                    "The message text may not be null.");
        }

        this.setFrom(from);
        this.subject = subject;
        this.text = text;
        this.mimeType = mimeType.toString();
    }

    /**
     * Creates a plain text message.
     * 
     * @param from
     *            The address to use as the sending address.
     * @param subject
     *            The subject of the message.
     * @param text
     *            The text of the message.
     */
    public MailMessage(final String from, final String subject,
            final String text) {
        this(from, subject, text, null);
    }

    /**
     * Creates a plain text message.
     * 
     * @param subject
     *            The subject of the message.
     * @param text
     *            The text of the message.
     */
    public MailMessage(final String subject, final String text) {
        this(null, subject, text, null);
    }

    /**
     * Creates a message with the given mime type.
     * 
     * @param subject
     *            The subject of the message.
     * @param text
     *            The text of the message.
     * @param mimeType
     *            The mime type of this message.
     */
    public MailMessage(final String subject, final String text,
            final MailMimeType mimeType) {
        this(null, subject, text, mimeType);
    }

    /**
     * Get the sending address used to send this message with.
     * 
     * @return The from address.
     */
    public String getFrom() {
        return from;
    }

    public void setFrom(final String from) {
        this.from = from;
    }

    /**
     * Get the list of recipients.
     * 
     * @return An unmodifiable list.
     */
    public List<String> getToRecipients() {
        return Collections.unmodifiableList(recipients);
    }

    /**
     * Get the list of Cc recipients.
     * 
     * @return An unmodifiable list.
     */
    public List<String> getCcRecipients() {
        return Collections.unmodifiableList(ccRecipients);
    }

    /**
     * Get the list of Bcc recipients.
     * 
     * @return An unmodifiable list.
     */
    public List<String> getBccRecipients() {
        return Collections.unmodifiableList(bccRecipients);
    }

    /**
     * Get the subject of the message.
     * 
     * @return The subject of this message. This string is never null.
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Retrieves the message content of this message.
     * 
     * @return The body of this message. This string is never null.
     */
    public String getText() {
        return text;
    }

    /**
     * Retrieve the mime type of this message.
     * 
     * @return The mimeType of this message.
     */
    public String getMimeType() {
        return mimeType == null ? "text/plain" : mimeType;
    }

    /**
     * Adds mail addresses to the list to the To recipients.
     * 
     * @param to
     *            The addresses to add.
     * @return this (fluent interface)
     */
    public MailMessage addToRecipients(final String... to) {
        for (final String address : to) {
            recipients.add(address);
        }
        return this;
    }

    /**
     * Adds mail addresses to the list to the Cc recipients.
     * 
     * @param cc
     *            The addresses to add.
     * @return this (fluent interface)
     */
    public MailMessage addCcRecipients(final String... cc) {
        for (final String address : cc) {
            ccRecipients.add(address);
        }
        return this;
    }

    /**
     * Adds mail addresses to the list to the Bcc recipients.
     * 
     * @param bcc
     *            The addresses to add.
     * @return this (fluent interface)
     */
    public MailMessage addBccRecipients(final String... bcc) {
        for (final String address : bcc) {
            bccRecipients.add(address);
        }
        return this;
    }

    /**
     * Adds mail addresses to the list to the To recipients.
     * 
     * @param to
     *            The addresses to add.
     * @return this (fluent interface)
     */
    public MailMessage addToRecipients(final Collection<String> to) {
        recipients.addAll(to);
        return this;
    }

    /**
     * Adds mail addresses to the list to the Cc recipients.
     * 
     * @param cc
     *            The addresses to add to the list of Cc recipients.
     * @return this (fluent interface)
     */
    public MailMessage addCcRecipients(final Collection<String> cc) {
        ccRecipients.addAll(cc);
        return this;
    }

    /**
     * Adds mail addresses to the list to the Bcc recipients.
     * 
     * @param bcc
     *            The addresses to add to the list of Bcc recipients.
     * @return this (fluent interface)
     */
    public MailMessage addBccRecipients(final Collection<String> bcc) {
        bccRecipients.addAll(bcc);
        return this;
    }
}