package com.petshop.crmbackend.repository;

import com.petshop.crmbackend.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

//public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
public interface AppointmentRepository
        extends JpaRepository<Appointment, Long>,
        JpaSpecificationExecutor<Appointment> {
    boolean existsByPetIdAndAppointmentDateAndAppointmentTimeAndIdNot(
            Long petId,
            java.time.LocalDate appointmentDate,
            java.time.LocalTime appointmentTime,
            Long id
    );
    boolean existsByPetIdAndAppointmentDateAndAppointmentTime(Long petId, LocalDate appointmentDate, LocalTime appointmentTime);
    boolean existsByPetId(Long petId);

    Appointment findTopByPetIdOrderByAppointmentDateDescAppointmentTimeDesc(Long petId);
    List<Appointment> findByAppointmentDate(LocalDate appointmentDate);

    Optional<Appointment> findByPetIdAndAppointmentDateAndAppointmentTime(
            Long petId, LocalDate appointmentDate, LocalTime appointmentTime);

//
//    boolean findByAppointmentId(String appointmentId);
//    boolean existsByAppointmentId(String appointmentId);
    /**
     * 根据业务预约号查询
     */
    Optional<Appointment> findByAppointmentId(String appointmentId);

    /**
     * 如果你还想要一个 exists 查询，也可以保留
     */
    boolean existsByAppointmentId(String appointmentId);



    // 累计总数、今日新增、取消、未到访
    @Query("SELECT COUNT(a) FROM Appointment a")
    long countTotal();

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.createdAt >= :startOfToday")
    long countTodayNew(@Param("startOfToday") LocalDateTime startOfToday);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.status = '已取消'")
    long countCancelled();

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.status = '未到访'")
    long countNoShow();

    // 计算平均提前天数
    @Query("SELECT AVG(DATEDIFF(a.appointmentDate, DATE(a.createdAt))) FROM Appointment a")
    Double avgLeadTimeDays();

    // 时段分布（按小时分组）
    @Query("SELECT FUNCTION('DATE_FORMAT', a.appointmentTime, '%H:00') AS slot, COUNT(a) "
            + "FROM Appointment a "
            + "GROUP BY slot")
    List<Object[]> groupByTimeSlot();

    // 热门服务排行榜
    @Query("SELECT a.serviceType, COUNT(a) FROM Appointment a GROUP BY a.serviceType ORDER BY COUNT(a) DESC")
    List<Object[]> groupByService();
}


