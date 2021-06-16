package com.g4.museo.mail;

import com.g4.museo.event.AlertReturnEvent;
import com.g4.museo.persistence.dto.ArtworkFull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

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
        List<ArtworkFull> filteredList = ((List<ArtworkFull>)event.getSource()).stream().filter(a -> {
            return a.getReturnDate().isBefore(LocalDate.now().plusMonths(1)) || a.getReturnDate().isEqual(LocalDate.now().plusMonths(1));
        }).collect(Collectors.toList());
        filteredList.forEach(a -> {
            if(!alreadySent.contains(a.getIdartwork())){
                log.info(a.toString());
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("noreply@museo.com");
                message.setTo("ju.cordier21@gmail.com");
                message.setSubject("Return Date Alert");
                message.setText("The artwork named " + a.getName() + " needs to be returned soon : " + a.getReturnDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
                //emailSender.send(message);
                alreadySent.add(a.getIdartwork());
            }
        });
    }
}
