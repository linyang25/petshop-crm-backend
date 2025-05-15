package com.petshop.crmbackend.config;

import com.petshop.crmbackend.entity.Appointment;
import com.petshop.crmbackend.repository.AppointmentRepository;
import com.petshop.crmbackend.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DailyReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(DailyReminderScheduler.class);

    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;

    public DailyReminderScheduler(AppointmentRepository appointmentRepository,
                                  EmailService emailService) {
        this.appointmentRepository = appointmentRepository;
        this.emailService = emailService;
    }

    /**
     * 每天上午8点发送当天所有预约的批量提醒邮件
     * cron 表达式：秒 分 时 日 月 周
     *
     * 本地测试时可用 "0 0/1 * * * *" （每分钟执行一次）
     * 生产环境改回每天 8 点："0 0 8 * * *"
     */
    @Scheduled(cron = "0 0/1 * * * *")
    public void sendTodayAppointmentsReminder() {
        LocalDate today = LocalDate.now();

        // 查询当日所有状态为“已预约”且 reminderSent = false 的记录
        List<Appointment> list = appointmentRepository
                .findAllByAppointmentDateAndStatusAndReminderSentFalse(today, "已预约");

        log.info("[Scheduler] Found {} appointments to remind for {}", list.size(), today);

        for (Appointment appt : list) {
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
                log.info("[Scheduler] Sending reminder to {}", to);
                emailService.sendAppointmentReminder(to, subject, body);

                // 标记为已发送，避免重复提醒
                appt.setReminderSent(true);
                appointmentRepository.save(appt);

                log.info("[Scheduler] Marked appointment {} as reminded", appt.getAppointmentId());
            } catch (Exception e) {
                log.error("[Scheduler] Failed to send daily reminder to {}: {}", to, e.getMessage(), e);
            }
        }
    }
}