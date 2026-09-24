package com.bistro.reservations.controller;

import com.bistro.reservations.model.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationStatusResponse {

    private String reservationCode;
    private ReservationStatus status;
    private String customerName;
    private LocalDateTime reservationTime;
    private Integer partySize;
    private Long assignedTableId;
}
