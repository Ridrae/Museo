package com.g4.museo.mail;

import com.g4.museo.event.AlertReturnEvent;
import com.g4.museo.persistence.dto.ArtworkFull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MailSender {
    @Autowired
    private JavaMailSender emailSender;

    protected Logger log = LoggerFactory.getLogger(MailSender.class);

    private List<Integer> alreadySent = new ArrayList<>();

    @EventListener(AlertReturnEvent.class)
    private void sendAlert(AlertReturnEvent event){
        List<ArtworkFull> filteredList = ((List<ArtworkFull>)event.getSource()).stream().filter(a -> a.getReturnDate().isBefore(LocalDate.now().plusMonths(1)) || a.getReturnDate().isEqual(LocalDate.now().plusMonths(1))).collect(Collectors.toList());
        filteredList.forEach(a -> {
            if(!alreadySent.contains(a.getIdartwork())){
                log.info(a.toString());
                try {
                    MimeMessage message = emailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED, "utf-8");
                    String htmlMsg = "<img src='cid:myLogo'>"+
                            "<h3>Alerte de retour</h3>" +
                            "<p>L'oeuvre " + a.getName() + " doit être rendue bientôt : " + a.getReturnDate().format(DateTimeFormatter.ISO_LOCAL_DATE) + "</p>";
                    helper.setFrom("noreply@museo.com");
                    helper.setTo("ju.cordier21@gmail.com");
                    helper.setSubject("Alerte date de retour");
                    helper.setText(htmlMsg, true);
                    helper.addInline("myLogo", new ClassPathResource("images/logo.png"));
                    emailSender.send(message);
                    alreadySent.add(a.getIdartwork());
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
