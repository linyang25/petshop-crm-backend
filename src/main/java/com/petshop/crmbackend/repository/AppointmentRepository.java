package com.petshop.crmbackend.repository;

import com.petshop.crmbackend.entity.Appointment;
import com.petshop.crmbackend.repository.projection.DayCountProjection;
import com.petshop.crmbackend.repository.projection.LabelValueProjection;
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


    List<Appointment> findAllByAppointmentDateAndStatusAndReminderSent(
            LocalDate date, String status, boolean reminderSent);

    List<Appointment> findAllByAppointmentDateAndStatus(LocalDate date, String status);

    List<Appointment> findAllByAppointmentDateAndStatusAndReminderSentFalse(LocalDate date, String status);


    /** 当日预约总数 未排除已取消预约**/
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.appointmentDate = :date")
    long countByAppointmentDate(@Param("date") LocalDate date);

    /** 当日取消总数 **/
    @Query("SELECT COUNT(a) FROM Appointment a " +
            " WHERE a.appointmentDate = :date AND a.status = 'Cancelled'")
    long countByAppointmentDateAndCancelled(@Param("date") LocalDate date);



    /** 按物种统计（排除已删除） **/
    @Query(
            "SELECT p.species AS label, COUNT(a) AS value " +
                    "FROM Appointment a, Pet p " +
                    "WHERE p.petId = a.petId " +
                    "  AND p.isDeleted = false " +
                    "GROUP BY p.species"
    )
    List<LabelValueProjection> countPetsBySpecies();

    /** 按品种统计（排除已删除） **/
    @Query(
            "SELECT p.breedName AS label, COUNT(a) AS value " +
                    "FROM Appointment a, Pet p " +
                    "WHERE p.petId = a.petId " +
                    "  AND p.isDeleted = false " +
                    "GROUP BY p.breedName"
    )
    List<LabelValueProjection> countPetsByBreed();


    /** 最近7天每日趋势，包括取消预约**/
    @Query(
            "SELECT FUNCTION('DAYNAME', a.appointmentDate) AS day, COUNT(a) AS count " +
                    "FROM Appointment a " +
                    "WHERE a.appointmentDate BETWEEN CURRENT_DATE - 6 AND CURRENT_DATE " +
                    "GROUP BY FUNCTION('DAYOFWEEK', a.appointmentDate) " +
                    "ORDER BY FUNCTION('DAYOFWEEK', a.appointmentDate)"
    )
    List<DayCountProjection> countPerDayLast7Days();


    /**
     * 热门服务排行榜（排除已取消）
     */
    @Query(
            "SELECT a.serviceType AS label, COUNT(a) AS value " +
                    "FROM Appointment a " +
                    "WHERE a.status <> 'Cancelled' " +
                    "GROUP BY a.serviceType " +
                    "ORDER BY COUNT(a) DESC"
    )
    List<LabelValueProjection> countByServiceType();






}


