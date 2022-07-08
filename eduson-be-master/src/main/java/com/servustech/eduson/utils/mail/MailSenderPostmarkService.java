package com.servustech.eduson.utils.mail;

import com.servustech.eduson.features.account.User;
import com.servustech.eduson.features.account.users.data.IndividualService;
import com.servustech.eduson.features.account.users.data.LegalService;
import com.servustech.eduson.features.files.FileService;
import com.servustech.eduson.features.permissions.permissions.PaymentType;
import com.servustech.eduson.features.permissions.subscriptions.SubscriptionPeriodsService;
import com.servustech.eduson.features.permissions.permissions.Permission;
import com.servustech.eduson.features.permissions.transactions.Transaction;
import com.servustech.eduson.features.permissions.PermissionsService;

import com.wildbit.java.postmark.client.data.model.templates.TemplatedMessage;
import com.wildbit.java.postmark.client.exception.PostmarkException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.UnsupportedEncodingException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

import static com.wildbit.java.postmark.Postmark.getApiClient;

@Async
@Service
public class MailSenderPostmarkService {

    @Value("${email.from}")
    private String FROM;

    @Value("${email.postmark.apiToken}")
    private String POSTMARK_TOKEN;

    // @Value("${spring.frontend.domain}")
    // private String domain;

    public void sendTest(String email) {
        new MailPostmarkBuilder(email, "welcome").send();
    }

    public void sendRegisterConfirmationEmail(String emailTo, String token, String name, String username,
            boolean passwordToBeSet) {
        System.out.println(emailTo + " " + token);
        String emailToEncoded = emailTo;
        try {
            emailToEncoded = URLEncoder.encode(emailTo, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            System.out.println("UnsupportedEncodingException");
        }
        String domain = "http://18.197.26.150";
        System.out.println("emailTo " + name);
        new MailPostmarkBuilder(emailTo, "welcome-1").withField("name", name).withField("product_name", "Eduson")
                .withField("product_url", domain).withField("login_url", domain + "/authentication/login")
                .withField("username", username)
                .withField("action_url", domain + "/authentication/confirm-account?activationKey=" + token + "&email="
                        + emailToEncoded + "&passwordToBeSet=" + passwordToBeSet)
                .send();
    }
    public void sendPasswordResetEmail(String emailTo, String token, String name, String username) {
        System.out.println(emailTo + " " + token);
        String emailToEncoded = emailTo;
        try {
            emailToEncoded = URLEncoder.encode(emailTo, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            System.out.println("UnsupportedEncodingException");
        }
        String domain = "http://18.197.26.150";
        System.out.println("emailTo " + name);
        new MailPostmarkBuilder(emailTo, "welcome-4").withField("name", name).withField("product_name", "Eduson")
                .withField("product_url", domain).withField("login_url", domain + "/authentication/login")
                .withField("username", username)
                .withField("action_url", domain + "/authentication/lost-password?securityKey=" + token + "&email="
                        + emailToEncoded)
                .send();
    }

    private String getPeriodName(Long periodId, SubscriptionPeriodsService subscriptionPeriodsService) {
        if (periodId != null) {
          return subscriptionPeriodsService.getPeriod(periodId).getName();
        } else {
          return "Perioadă nedeterminată";
        }
      }
    
    public void sendInvoiceEmail(
        String status, User user, List<Permission> permissions, Transaction transaction,
        IndividualService individualService, LegalService legalService, 
        SubscriptionPeriodsService subscriptionPeriodsService, PermissionsService permissionsService,
        InputStream inputStream
    ) {
        var emailTo = user.getEmail();
        var firstName = user.getFirstName();
        var name = user.getFullName();
        System.out.println(emailTo + " (email to)");
        String emailToEncoded = emailTo;
        try {
            emailToEncoded = URLEncoder.encode(emailTo, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            System.out.println("UnsupportedEncodingException");
        }
        String domain = "http://18.197.26.150"; // TODO
        var individual = individualService.findById(user.getId()).orElse(null);
        var legal = legalService.findById(user.getId()).orElse(null);
        var isIndividual = user.getInvoiceAddressPersonal();

        var cnp = "";
        var address = "";
        var city = "";
        var zipCode = "";
        var companyName = "";
        var regCom = "";
        var cui = "";
        if (isIndividual) {
            if (individual != null) { // TODO correct it!
                cnp = individual.getCnp();
                address = individual.getAddress();
                city = individual.getCity();
                zipCode = individual.getZipCode();
            }
        } else {
            if (legal != null) {
                cui = legal.getCui();
                regCom = legal.getRegCom();
                address = legal.getAddress();
                city = legal.getCity();
                zipCode = legal.getZipCode();
            }
        }
        String orderTimestamp = transaction.getTimestamp().format(DateTimeFormatter.ofPattern("dd.MM.yyyy").withZone(ZoneId.of("Europe/Bucharest")));
        String timestamp = transaction.getPaidAt() == null
          ? orderTimestamp
          : transaction.getPaidAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy").withZone(ZoneId.of("Europe/Bucharest")));
        String orderId = "Comanda_" + transaction.getId();
        String id = status.equals("paid") ? "EDU " + transaction.getInvoiceId() : orderId;
        String stripeId = status.equals("paid") ? transaction.getTransactionId() : ""+transaction.getId();
        List<HashMap<String, Object>> products = new ArrayList<>();
        permissions.stream().forEach(p -> {
            HashMap<String, Object> pValue = new HashMap<String, Object>();
            pValue.put("period", getPeriodName(p.getPeriodId(), subscriptionPeriodsService));
            pValue.put("product", permissionsService.getProduct2(p.getProductType(),
                p.getProductId()).getName());
            pValue.put("amount", p.getValue() + " lei");
            products.add(pValue);
        });
        if (status.equals("paid")) {
            new MailPostmarkBuilder(emailTo, "welcome-2")
                    .withField("command_date",
                            timestamp)
                    .withField("command_id",
                            stripeId)
                    .withField("name",
                            name)
                    .withField("cnp",
                            cnp)
                    .withField("address",
                            address)
                    .withField("city",
                            city)
                    .withField("zip_code",
                            zipCode)
                    .withField("company_name",
                            companyName)
                    .withField("reg_com",
                            regCom)
                    .withField("cui",
                            cui)
                    .withField("first_name",
                            firstName)
                    .withField("product_name", "Eduson")
                    .withField("product_url", domain)
                    .withField("login_url", domain +
                            "/authentication/login")
                    .withField("products", products)
                    .withField("amount", transaction.getValue() / 100 + " lei")
                    .addAttachment(id + "_" + stripeId + ".pdf",
                            inputStream);
        } else if (status.equals("ordered")) {
            new MailPostmarkBuilder(emailTo, "welcome-3")
                    .withField("command_date",
                            orderTimestamp)
                    .withField("command_id",
                            stripeId)
                    .withField("first_name",
                            firstName)
                    .withField("product_name", "Eduson")
                    .withField("product_url", domain).withField("login_url", domain +
                            "/authentication/login")
                    .withField("transfer_intent", stripeId)
                    .withField("products", products)
                    .withField("amount", transaction.getValue() / 100 + " lei")
                    .addAttachment(id + "_" + stripeId + ".pdf",
                            inputStream);
        }
    }
    class MailPostmarkBuilder {
        TemplatedMessage message;
        HashMap<String, Object> model = new HashMap<>();

        MailPostmarkBuilder(User to, String templateAlias) {
            message = new TemplatedMessage(FROM, to.getEmail());
            message.setTemplateAlias(templateAlias);
            // this.withField("userName", to.getFullName());
        }

        MailPostmarkBuilder(String toEmail, String templateAlias) {
            message = new TemplatedMessage(FROM, toEmail);
            message.setTemplateAlias(templateAlias);
            // this.withField("userName", to.getFullName());
        }

        MailPostmarkBuilder withField(String name, String value) {
            this.model.put(name, value);
            return this;
        }

        MailPostmarkBuilder withField(String name, List<HashMap<String, Object>> value) {
            this.model.put(name, value);
            return this;
        }

        public void addAttachment(String fileName, InputStream inputStream) {
            try {
                this.message.addAttachment(fileName, inputStream.readAllBytes(), "application/octet-stream");
                message.setTemplateModel(model);
                getApiClient(POSTMARK_TOKEN).deliverMessageWithTemplate(message);
            } catch (PostmarkException | IOException e) {
                System.out.println(e.getMessage());
                // throw new InternalException(e);
            }
        }

        public void send() {
            try {
                message.setTemplateModel(model);
                getApiClient(POSTMARK_TOKEN).deliverMessageWithTemplate(message);
            } catch (PostmarkException | IOException e) {
                System.out.println(e.getMessage());
                // throw new InternalException(e);
            }
        }
    }
}
