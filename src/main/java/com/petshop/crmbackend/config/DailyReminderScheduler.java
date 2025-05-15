package com.petshop.crmbackend.config;

import com.petshop.crmbackend.entity.Appointment;
import com.petshop.crmbackend.entity.Notification;
import com.petshop.crmbackend.entity.User;
import com.petshop.crmbackend.repository.AppointmentRepository;
import com.petshop.crmbackend.repository.UserRepository;
import com.petshop.crmbackend.service.EmailService;
import com.petshop.crmbackend.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DailyReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(DailyReminderScheduler.class);

    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public DailyReminderScheduler(AppointmentRepository appointmentRepository,
                                  EmailService emailService,
                                  NotificationService notificationService,
                                  UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.emailService = emailService;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    /**
     * 每天上午8点发送当天所有预约的批量提醒邮件，并给员工生成站内信
     * cron 表达式：秒 分 时 日 月 周
     *
     * 本地测试时可用 "0 0/1 * * * *" （每分钟执行一次）
     * 生产环境改回每天 8 点："0 0 8 * * *"
     */
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional  // 确保 reminderSent 标记与通知保存都在同一事务内
    public void sendTodayAppointmentsReminder() {
        LocalDate today = LocalDate.now();

        // —— 一、给客户发邮件 ——
        List<Appointment> toRemind = appointmentRepository
                .findAllByAppointmentDateAndStatusAndReminderSentFalse(today, "已预约");
        log.info("[Scheduler] {} appointments to remind via email for {}", toRemind.size(), today);

        for (Appointment appt : toRemind) {
            String to      = appt.getCustomerEmail();
            String subject = String.format("[PetCRM] Reminder: your appointment is today %s at %s",
                    appt.getAppointmentDate(), appt.getAppointmentTime());
            String body    = String.format(
                    "Hello %s,\n\n" +
                            "This is a friendly reminder of your appointment (ID: %s) scheduled today at %s for \"%s\".\n\n" +
                            "Notes: %s\n\n" +
                            "See you soon!\n" +
                            "PetCRM Team",
                    appt.getCustomerName(),
                    appt.getAppointmentId(),
                    appt.getAppointmentTime(),
                    appt.getServiceType(),
                    appt.getNotes() == null ? "" : appt.getNotes()
            );

            try {
                emailService.sendAppointmentReminder(to, subject, body);
                appt.setReminderSent(true);
                appointmentRepository.save(appt);
                log.info("[Scheduler] Email reminder sent to {} and marked appointment {} as reminded",
                        to, appt.getAppointmentId());
            } catch (Exception e) {
                log.error("[Scheduler] Failed to send email reminder to {}: {}", to, e.getMessage(), e);
            }
        }

        // 2. 给所有在职员工在系统里发“站内信”
        List<User> staff = userRepository.findAllByStatus("active");
        for (User u : staff) {
            Notification notif = new Notification();
            notif.setTargetUserId(u.getUserId());
            notif.setTitle("Today's Appointments Reminder");

            // 拼一条 To-Do 样式的列表
            String content = appointmentRepository
                    .findByAppointmentDate(today).stream()
                    .map(a -> String.format(
                            "- %s at %s (ID: %s), Customer: %s",
                            a.getServiceType(),
                            a.getAppointmentTime(),
                            a.getAppointmentId(),
                            a.getCustomerName()
                    ))
                    .collect(Collectors.joining("\n"));
            notif.setContent(content);

            notif.setCreatedAt(LocalDateTime.now());
            notificationService.createNotification(notif);
        }

        log.info("[DailyReminderScheduler] Reminders and staff notifications sent.");
    }
}