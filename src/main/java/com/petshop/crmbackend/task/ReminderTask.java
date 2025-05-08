//package com.petshop.crmbackend.task;
//
//import com.amazonaws.services.sns.AmazonSNS;
//import com.amazonaws.services.sns.model.PublishRequest;
//import com.petshop.crmbackend.entity.Appointment;
//import com.petshop.crmbackend.entity.Pet;
//import com.petshop.crmbackend.repository.AppointmentRepository;
//import com.petshop.crmbackend.repository.PetRepository;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Optional;

//
//@Component
//public class ReminderTask {
//
//    private final AppointmentRepository appointmentRepository;
//    private final PetRepository petRepository;
//    private final AmazonSNS amazonSNS;
//
//    public ReminderTask(AppointmentRepository appointmentRepository,
//                        PetRepository petRepository,
//                        AmazonSNS amazonSNS) {
//        this.appointmentRepository = appointmentRepository;
//        this.petRepository = petRepository;
//        this.amazonSNS = amazonSNS;
//    }
//
//    /**
//     * 每天早上8点执行，提醒第二天的预约
//     */
//    @Scheduled(cron = "0 0 8 * * ?")
//    public void sendReminders() {
//        LocalDate tomorrow = LocalDate.now().plusDays(1);
//        List<Appointment> appointments = appointmentRepository.findByAppointmentDate(tomorrow);
//
//        for (Appointment appointment : appointments) {
//            Optional<Pet> petOpt = petRepository.findByPetId(appointment.getPetId());
//            String petName = petOpt.map(Pet::getPetName).orElse("您的宠物");
//
//            String message = String.format("您好，%s有一个预约服务，时间为 %s，请提前做好准备。",
//                    petName,
//                    appointment.getAppointmentTime().toString());
//
//            try {
//                PublishRequest publishRequest = new PublishRequest()
//                        .withMessage(message)
//                        .withPhoneNumber(appointment.getPhone()); // 必须是 E.164 格式，如 +64xxxxxx
//                amazonSNS.publish(publishRequest);
//            } catch (Exception e) {
//                System.err.println("发送短信失败: " + e.getMessage());
//            }
//        }
//    }
//}