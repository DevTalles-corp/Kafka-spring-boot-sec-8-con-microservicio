package com.bistro.reservations.repository;

import com.bistro.reservations.model.Reservation;
import com.bistro.reservations.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    boolean existsByAssignedTableIdAndReservationTimeAndStatus(
            Long assignedTableId,
            LocalDateTime reservationTime,
            ReservationStatus status);

    Optional<Reservation> findByReservationCode(String reservationCode);

    long countByStatus(ReservationStatus status);
}
